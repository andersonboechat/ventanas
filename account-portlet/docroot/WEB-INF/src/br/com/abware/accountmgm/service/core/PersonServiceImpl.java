package br.com.abware.accountmgm.service.core;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import br.com.abware.accountmgm.persistence.manager.PersonManagerImpl;
import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.accountmgm.util.DomainPredicate;
import br.com.abware.accountmgm.util.MembershipPredicate;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Role;
import br.com.abware.jcondo.core.model.RoleName;
import br.com.abware.jcondo.core.service.PersonService;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.PersistenceException;

public class PersonServiceImpl implements PersonService {
	
	private static PersonManagerImpl personManager = new PersonManagerImpl();
	
	private static SecurityManagerImpl securityManager = new SecurityManagerImpl();

	@Override
	public List<Person> getPeople(Domain domain) throws ApplicationException {
		List<Person> people = null;

		try {
			people = personManager.findPeople(domain);

//			while (people.iterator().hasNext()) {
//				try {
//					Person person = people.iterator().next();
//					if (!securityManager.hasPermission(person, Permission.VIEW)) {
//						people.remove(person);
//					}
//				} catch (ApplicationException e) {
//					// Do nothing
//				}
//			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return people;
	}

	@Override
	public Person getPerson() throws ApplicationException {
		return personManager.getLoggedPerson();
	}

	@Override
	public File getPortrait() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPermission(Person person, Permission permission) throws ApplicationException {
		return securityManager.hasPermission(person, permission);
	}

	/**
	 * Papeis de Apartamento: Proprietario, Morador, Dependente, Convidado, Visitante
	 * 
	 * Papeis de Fornecedor: Gerente, Funcionario
	 * 
	 * Papeis de membro do condomínio: Alugador (Aluguel de Espaços), 
	 * 								   Reclamador (livro de reclamações), 
	 * 								   Comentarista (forum), 
	 * 								   Provedor de Acesso 
	 * 
	 * Se for morador, associa aos papeis: Morador, Alugador, Reclamador, Comentarista, Provedor de Acesso
	 * Se for dependente, associa aos papeis: Dependente, Provedor de Acesso
	 * Se for convidado, associa aos papeis: Convidado
	 * Se for visitante, associa aos papeis: Visitante
	 * 
	 * Se for proprietário
	 * 		Se não existir Locatarios, associa aos papeis: Proprietário, Alugador, Reclamador, Comentarista, Provedor de Acesso
	 * 		Se existir Locatarios, associa aos papeis: Proprietário
	 * 
	 * Se for locatário, associa aos papeis: Locatário, Alugador, Reclamador, Comentarista, Provedor de Acesso
	 * 				   , associa os proprietários aos papeis: Proprietário
	 */
	@Override
	public Person register(Person person) throws ApplicationException {
		Person p = null;
		boolean exists = personManager.exists(person);

		if ((!exists && securityManager.hasPermission(person, Permission.ADD_USER)) ||
				(exists && securityManager.hasPermission(person, Permission.UPDATE_PERSON))) {

			p = personManager.save(person);

			Set<Domain> domains = new HashSet<Domain>();
			List<Membership> newMemberships = person.getMemberships();
			List<Membership> oldMemberships = p.getMemberships();

			/* Removendo pepeis */
			for (Membership membership : oldMemberships) {
				Domain domain = membership.getDomain();
				Role role = membership.getRole();

				if (!CollectionUtils.exists(newMemberships, new MembershipPredicate(domain, role))) {
					if (securityManager.hasPermission(domain, Permission.DELETE_PERSON) &&
							securityManager.hasPermission(role, Permission.DELETE_PERSON)) {
						securityManager.removeRole(p, domain, role);
					} else {
						throw new ApplicationException(null, "no permission to remove a person with this role from this domain");
					}
				}

				domains.add(domain);
			}

			/* Removendo dominios */
			for(Domain domain : domains) {
				if (!CollectionUtils.exists(newMemberships, new DomainPredicate(domain))) {
					if (securityManager.hasPermission(domain, Permission.DELETE_PERSON)) {
						personManager.removeDomain(person, domain);
					}
				}
			}

			domains.clear();

			/* Incluindo pepeis */
			for (Membership membership : newMemberships) {
				Domain domain = membership.getDomain();
				Role role = membership.getRole();

				/* usuário nao tem esse papel nesse dominio */
				if (!CollectionUtils.exists(oldMemberships, new MembershipPredicate(domain, role))) {
					if (securityManager.hasPermission(domain, Permission.ADD_USER) &&
							securityManager.hasPermission(role, Permission.ADD_USER)) {

						securityManager.addRole(person, domain, role);

						// Configurando papeis de membro do condomínio de acordo com o papel de apartamento
						if (domain instanceof Flat) {
							if (role.getName() == RoleName.OWNER) {
								List<Person> renters = getRenters((Flat) domain);
		
								if (renters.isEmpty()) {
									securityManager.addRole(person, domain, securityManager.getRole(domain, RoleName.LESSEE));
									securityManager.addRole(person, domain, securityManager.getRole(domain, RoleName.DEBATER));
									securityManager.addRole(person, domain, securityManager.getRole(domain, RoleName.HABITANT));
								}
							}

							if (role.getName() == RoleName.RENTER) {
								List<Person> owners = getOwners((Flat) domain);

								for (Person owner : owners) {
									securityManager.removeRole(owner, domain, securityManager.getRole(domain, RoleName.LESSEE));
									securityManager.removeRole(owner, domain, securityManager.getRole(domain, RoleName.DEBATER));
									securityManager.removeRole(owner, domain, securityManager.getRole(domain, RoleName.HABITANT));
								}
							}

							if (role.getName() == RoleName.RESIDENT || role.getName() == RoleName.RENTER) {
								securityManager.addRole(person, domain, securityManager.getRole(domain, RoleName.LESSEE));
								securityManager.addRole(person, domain, securityManager.getRole(domain, RoleName.DEBATER));
							} 

							if (role.getName() == RoleName.RESIDENT || 
									role.getName() == RoleName.RENTER || role.getName() == RoleName.DEPENDENT) {
								securityManager.addRole(person, domain, securityManager.getRole(domain, RoleName.HABITANT));
							}
						}
					} else {
						throw new ApplicationException(null, "no permission to add a person with this role in this domain");
					}
				}

				domains.add(domain);
			}

			/* Incluindo dominios */
			for(Domain domain : domains) {
				if (!CollectionUtils.exists(oldMemberships, new DomainPredicate(domain))) {
					if (securityManager.hasPermission(domain, Permission.ADD_USER)) {
						personManager.addDomain(person, domain);
					}
				}
			}
		} else {
			throw new ApplicationException(null, "no permission to add or update people");
		}

		return p;
	}
	
	public void inactive(Person person) throws ApplicationException {
		Person p = personManager.findById(person.getId());

		while (p.getMemberships().iterator().hasNext()) {
			Membership membership = p.getMemberships().iterator().next();
			Domain domain = membership.getDomain();
			Role role = membership.getRole();

			/* Verifica se tem permissao para excluir o usuario, desse dominio e com esse papel */
			if (securityManager.hasPermission(domain, Permission.DELETE_PERSON) &&
					securityManager.hasPermission(role, Permission.DELETE_PERSON)) {
				p.getMemberships().remove(membership);

				if (domain instanceof Flat) {
					if (role.getName() == RoleName.RENTER) {
						List<Person> owners = getOwners((Flat) domain);
	
						for (Person owner : owners) {
							securityManager.addRole(owner, domain, securityManager.getRole(domain, RoleName.LESSEE));
							securityManager.addRole(owner, domain, securityManager.getRole(domain, RoleName.DEBATER));
							securityManager.addRole(owner, domain, securityManager.getRole(domain, RoleName.HABITANT));
						}
					}
				}
			}
		}

		if (securityManager.hasPermission(person, Permission.DELETE_PERSON)) {
			if (!p.getMemberships().isEmpty()) {
				personManager.delete(person);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean hasMembership(List<Membership> memberships, Membership membership) {
		Collection<Membership> c = CollectionUtils.select(memberships, 
														  new MembershipPredicate(membership.getDomain(), membership.getRole()));

		return !c.isEmpty();
	}

	public void delete(Person person) throws ApplicationException {
		inactive(person);
	}
	
	@Override
	public void setPortrait(File arg0) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	public List<Person> getOwners(Flat flat) {
		List<Person> people;
		try {
			people = personManager.findPeople(flat);
			for (Person p : people) {
				Collection<Membership> c;
				c = CollectionUtils.select(p.getMemberships(), 
										  new MembershipPredicate(flat, securityManager.getRole(flat, RoleName.OWNER)));
				if (c.isEmpty()) {
					people.remove(p);
				}
			}
		} catch (ApplicationException e) {
			e.printStackTrace();
			people = new ArrayList<Person>();
		}

		return people;
	}	

	@SuppressWarnings("unchecked")
	public List<Person> getRenters(Flat flat) {
		List<Person> people;
		try {
			people = personManager.findPeople(flat);
			for (Person p : people) {
				Collection<Membership> c; 
				c = CollectionUtils.select(p.getMemberships(), 
										   new MembershipPredicate(flat, securityManager.getRole(flat, RoleName.RENTER)));
				if (c.isEmpty()) {
					people.remove(p);
				}
			}
		} catch (ApplicationException e) {
			e.printStackTrace();
			people = new ArrayList<Person>();
		}

		return people;
	}	

}

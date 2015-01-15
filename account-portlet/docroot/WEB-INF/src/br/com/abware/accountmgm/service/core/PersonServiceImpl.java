package br.com.abware.accountmgm.service.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import br.com.abware.accountmgm.persistence.manager.PersonManagerImpl;
import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.accountmgm.util.MembershipPredicate;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Group;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Role;
import br.com.abware.jcondo.core.service.PersonService;
import br.com.abware.jcondo.exception.ApplicationException;

public class PersonServiceImpl implements PersonService {
	
	private PersonManagerImpl personManager = new PersonManagerImpl();
	
	private SecurityManagerImpl securityManager = new SecurityManagerImpl();

	@Override
	public List<Person> getPeople(Group group) throws ApplicationException {
		if (securityManager.hasPermission(group, Permission.ADD_USER)) {
			return personManager.findPeople(group);
		}
		
		return null;
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

	@SuppressWarnings("unchecked")
	@Override
	public Person register(Person person) throws ApplicationException {
		Person p = null;

		if (securityManager.hasPermission(person, Permission.ADD_USER) ||
				securityManager.hasPermission(person, Permission.UPDATE_PERSON)) {

			p = personManager.save(person);

			List<Membership> newMemberships = person.getMemberships();
			List<Membership> oldMemberships = p.getMemberships();

			/* Removendo pepeis */			
			for (Membership membership : oldMemberships) {
				Group group = membership.getGroup();
				Role role = membership.getRole();

				Collection<Membership> c = CollectionUtils.select(newMemberships, new MembershipPredicate(group, role));

				if (c.isEmpty()) {
					securityManager.removeRole(p, group, role);
				}
			}

			// Configurando papeis de acordo com o tipo de usuário
			/*
			 * Papeis de Apartamento: Proprietario, Morador, Dependente, Convidado, Visitante
			 * 
			 * Papeis de Fornecedor: Gerente, Funcionario
			 * 
			 * Papeis de membro do condomínio: Alugador (Aluguel de Espaços), 
			 * 								   Reclamador (livro de reclamações), 
			 * 								   Comentarista (forum), 
			 * 								   Provedor de Acesso
			 *
			 */
			/* Incluindo pepeis */
			for (Membership membership : newMemberships) {
				Group group = membership.getGroup();
				Role role = membership.getRole();

				Collection<Membership> c = CollectionUtils.select(oldMemberships, new MembershipPredicate(group, role));
				
				/* usuário já tem esse papel nesse grupo */
				if (!c.isEmpty()) {
					continue;
				}

				if (group instanceof Flat) {
					/*
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
					 * 
					 * */
					securityManager.addRole(person, group, role);
					
					if (role == Role.OWNER) {
						List<Person> renters = getRenters((Flat) group);

						if (renters.isEmpty()) {
							securityManager.addRole(person, group, Role.LESSEE);
							securityManager.addRole(person, group, Role.DEBATER);
							securityManager.addRole(person, group, Role.HABITANT);
						}
					} 

					if (role == Role.RENTER) {
						List<Person> owners = getOwners((Flat) group);

						for (Person owner : owners) {
							securityManager.removeRole(owner, group, Role.LESSEE);
							securityManager.removeRole(owner, group, Role.DEBATER);
							securityManager.removeRole(owner, group, Role.HABITANT);
						}
					}

					if (role == Role.RESIDENT || role == Role.RENTER) {
						securityManager.addRole(person, group, Role.LESSEE);
						securityManager.addRole(person, group, Role.DEBATER);
					} 

					if (role != Role.GUEST && role != Role.VISITOR && role != Role.OWNER) {
						securityManager.addRole(person, group, Role.HABITANT);
					}
				}
			}
		}

		return p;
	}
	
	public void inactive(Person person) throws ApplicationException {
		if (securityManager.hasPermission(person, Permission.DELETE_PERSON)) {
			//p = personManager.inactive(person);

			for (Membership membership : person.getMemberships()) {
				Group group = membership.getGroup();
				Role role = membership.getRole();

				if (group instanceof Flat) {
					securityManager.addRole(person, group, role);
					
					if (role == Role.RENTER) {
						List<Person> owners = getOwners((Flat) group);

						for (Person owner : owners) {
							securityManager.addRole(owner, group, Role.LESSEE);
							securityManager.addRole(owner, group, Role.DEBATER);
						}
					} 
				}
			}
		}
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
				Collection<Membership> c = CollectionUtils.select(p.getMemberships(), new MembershipPredicate(flat, Role.OWNER));
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
				Collection<Membership> c = CollectionUtils.select(p.getMemberships(), new MembershipPredicate(flat, Role.RENTER));
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

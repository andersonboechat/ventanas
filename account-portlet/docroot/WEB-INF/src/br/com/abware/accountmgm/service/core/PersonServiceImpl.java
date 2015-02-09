package br.com.abware.accountmgm.service.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.accountmgm.persistence.manager.NewPersonManagerImpl;
import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.accountmgm.util.DomainPredicate;
import br.com.abware.accountmgm.util.MembershipPredicate;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Role;
import br.com.abware.jcondo.core.model.RoleName;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.PersistenceException;

public class PersonServiceImpl  {

	private static final Condominium CONDOMINIUM = new Condominium();

	private static NewPersonManagerImpl personManager = new NewPersonManagerImpl();

	private static SecurityManagerImpl securityManager = new SecurityManagerImpl();

	private FlatServiceImpl flatService = new FlatServiceImpl();
	
	
	public PersonServiceImpl() {
		CONDOMINIUM.setDomainId(10179);
	}

	public List<Person> getPeople(Person person) throws Exception {
		List<Person> people = new ArrayList<Person>();

		try {
			for (Flat flat : flatService.getFlats(person)) {
				people.addAll(personManager.findPeople(flat));
			}

			people.addAll(getPeople(CONDOMINIUM));
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return people;
	}
	
	public List<Person> getPeople(Domain domain) throws Exception {
		List<Person> people = new ArrayList<Person>();

		try {
//			if (!securityManager.hasPermission(domain, Permission.VIEW)) {
				people = personManager.findPeople(domain);
//			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return people;
	}

	public Person getPerson(String identity) {
		return null;
	}
	
	
	public Person getPerson() throws Exception {
		return personManager.getPerson();
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
	public Person register(Person person) throws Exception {
//		if (!securityManager.hasPermission(person, Permission.ADD_USER)) {
//			throw new Exception("sem permissao para cadastrar usuario");
//		}

		if (StringUtils.isEmpty(person.getIdentity())) {
			throw new ApplicationException(null, "identidade não fornecida");
		}

		if (getPerson(person.getIdentity()) != null) {
			throw new ApplicationException(null, "existe usuario cadastrado com a identidade " + person.getIdentity());
		}

		return personManager.save(person);
	}

	public List<Membership> getMemberships(Person person) throws Exception {
		List<Membership> memberships = new ArrayList<Membership>();
		List<Flat> flats = flatService.getFlats(person);

		for (Flat flat : flats) {
			for (Role role : securityManager.getRoles(person, flat)) {
				memberships.add(new Membership(role, flat));
			}
		}

//		for (Role role : securityManager.getRoles(person, CONDOMINIUM)) {
//			memberships.add(new Membership(role, CONDOMINIUM));
//		}

		return memberships;
	}

	public void updateMemberships(Person person, List<Membership> memberships) throws Exception {
		Set<Domain> domains = new HashSet<Domain>();
		List<Membership> oldMemberships = getMemberships(person);
		
		/* Removendo pepeis */
		for (Membership membership : oldMemberships) {
			Domain domain = membership.getDomain();
			Role role = membership.getRole();

			if (!CollectionUtils.exists(memberships, new MembershipPredicate(domain, role))) {
//				if (securityManager.hasPermission(role, Permission.DELETE_PERSON)) {
					securityManager.removeRole(person, domain, role);
//				} else {
//					throw new ApplicationException(null, "no permission to remove a person with this role from this domain");
//				}
			}

			domains.add(domain);
		}

		/* Removendo dominios */
		for(Domain domain : domains) {
			if (!CollectionUtils.exists(memberships, new DomainPredicate(domain))) {
//				if (securityManager.hasPermission(domain, Permission.DELETE_PERSON)) {
					personManager.removeDomain(person, domain);
//				}
			}
		}

		domains.clear();

		/* Incluindo pepeis */
		for (Membership membership : memberships) {
			Domain domain = membership.getDomain();
			Role role = membership.getRole();

			/* usuário nao tem esse papel nesse dominio */
			if (!CollectionUtils.exists(oldMemberships, new MembershipPredicate(domain, role))) {
//				if (securityManager.hasPermission(role, Permission.ADD_USER)) {
					securityManager.addRole(person, domain, role);
//				} else {
//					throw new ApplicationException(null, "no permission to add a person with this role in this domain");
//				}
			}

			domains.add(domain);
		}

		/* Incluindo dominios */
		for(Domain domain : domains) {
			if (!CollectionUtils.exists(oldMemberships, new DomainPredicate(domain))) {
//				if (securityManager.hasPermission(domain, Permission.ADD_USER)) {
					personManager.addDomain(person, domain);
//				}
			}
		}
		
	}



	public void inactive(Person person) throws Exception {
		if (!securityManager.hasPermission(person, Permission.DELETE)) {
			throw new Exception("sem permissao para excluir este usuario");
		}
		securityManager.removeAllRoles(person);
		personManager.delete(person);
	}

	public void delete(Person person) throws Exception {
		inactive(person);
	}
	
	@SuppressWarnings("unchecked")
	public List<Person> getOwners(Flat flat) throws Exception {
		List<Person> people;
		try {
			Role owner = securityManager.getRole(flat, RoleName.OWNER);
			people = personManager.findPeople(flat);
			for (Person p : people) {
				Collection<Membership> c = CollectionUtils.select(p.getMemberships(), new MembershipPredicate(flat, owner));
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
	public List<Person> getRenters(Flat flat) throws Exception {
		List<Person> people;
		try {
			Role renter = securityManager.getRole(flat, RoleName.RENTER);
			people = personManager.findPeople(flat);
			for (Person p : people) {
				Collection<Membership> c = CollectionUtils.select(p.getMemberships(), new MembershipPredicate(flat, renter));
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

package br.com.atilo.jcondo.core.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.abware.jcondo.exception.PersistenceException;

import br.com.atilo.jcondo.commons.collections.MembershipPredicate;
import br.com.atilo.jcondo.core.persistence.manager.PersonManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;

public class PersonServiceImpl  {

	private static final Condominium CONDOMINIUM = new Condominium();

	private PersonManagerImpl personManager = new PersonManagerImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();
	
	private AdministrationServiceImpl adminService = new AdministrationServiceImpl();

	private FlatServiceImpl flatService = new FlatServiceImpl();

	public PersonServiceImpl() {
		CONDOMINIUM.setRelatedId(10179);
	}

	public List<PersonType> getTypes(Domain domain) throws Exception {
		List<PersonType> types = new ArrayList<PersonType>();
		PersonType[] ts;

		if (domain instanceof Flat) {
			ts = PersonType.FLAT_TYPES;
		} else if (domain instanceof Supplier) {
			ts = PersonType.SUPPLIER_TYPES;
		} else if (domain instanceof Administration) {
			ts = PersonType.ADMIN_TYPES;
		} else {
			throw new ApplicationException("psn.domain.unknown", domain);
		}

		for (PersonType type : ts) {
			if (securityManager.hasPermission(new Membership(type, domain), Permission.VIEW)) {
				types.add(type);
			}
		}

		return types;
	}

	public List<Person> getPeople(String name) throws Exception {
		return personManager.findPeople(name);
	}

	public List<Person> getPeople(Person person) throws Exception {
		Set<Person> people = new HashSet<Person>(); 

		try {
			for (Flat flat : flatService.getFlats(person)) {
				people.addAll(personManager.findPeople(flat));
			}

			for (Administration administration : adminService.getAdministrations(person)) {
				people.addAll(personManager.findPeople(administration));
			}
		} catch (PersistenceException e) {
			throw new ApplicationException(e, "psn.get.person.people.fail");
		}

		return new ArrayList<Person>(people);
	}

	public List<Person> getPeople(Domain domain) throws Exception {
		List<Person> people = new ArrayList<Person>();

		try {
			if (domain != null && securityManager.hasPermission(domain, Permission.VIEW)) {
				people = personManager.findPeople(domain);
			}
		} catch (PersistenceException e) {
			throw new ApplicationException(e, "psn.get.domain.people.fail");
		}

		return people;
	}

	public Person getPerson(String identity) {
		return null;
	}
	
	
	public Person getPerson() throws Exception {
		return personManager.findPerson();
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
		try {
			if (!securityManager.hasPermission(person, Permission.ADD)) {
				throw new BusinessException("psn.create.denied");
			}
	
			if (StringUtils.isEmpty(person.getIdentity())) {
				throw new BusinessException("psn.identity.empty");
			}
	
			if (getPerson(person.getIdentity()) != null) {
				throw new BusinessException("psn.identity.exists", person.getIdentity());
			}

			validateDomains(person);
			validateMemberships(person);
	
			Person p = personManager.save(person);
	
			for (Membership membership : person.getMemberships()) {
				securityManager.addMembership(p, membership);
			}
	
			return p;
		} catch (PersistenceException e) {
			throw new ApplicationException(e, "psn.register.fail");
		}
	}

	public void updatePassword(Person person, String password, String newPassword) throws Exception {
//		Person p = getPerson(person.getIdentity());
//
//		if (p == null) {
//			throw new Exception("Person not found");
//		}

		securityManager.updatePassword(person, password, newPassword);
	}
	
	@SuppressWarnings("unchecked")
	public Person update(Person person) throws Exception {
		try {
			if (!securityManager.hasPermission(person, Permission.UPDATE)) {
				throw new BusinessException("psn.update.denied");
			}
	
			if (StringUtils.isEmpty(person.getIdentity())) {
				throw new BusinessException("psn.identity.empty");
			}
	
			Person p = personManager.findById(person.getId());
			if (p == null) {
				throw new BusinessException("psn.not.found");
			}

			validateDomains(person);
			validateMemberships(person);
	
			if (p.getMemberships() == null) {
				p.setMemberships(new ArrayList<Membership>());
			}
	
			List<Membership> memberships = (List<Membership>) CollectionUtils.subtract(p.getMemberships(), person.getMemberships());
	
			for (Membership membership : memberships) {
				securityManager.removeMembership(person, membership);
			}
	
			memberships = (List<Membership>) CollectionUtils.subtract(person.getMemberships(), p.getMemberships());
	
			for (Membership membership : memberships) {
				securityManager.addMembership(person, membership);
			}
	
			return personManager.save(person);
		} catch (PersistenceException e) {
			throw new ApplicationException(e, "psn.update.fail");
		}
	}	

	private void validateDomains(Person person) throws Exception {
		// TODO Auto-generated method stub
		
	}

	private void validateMemberships(Person person) throws Exception {
		List<Membership> oldMemberships;
		List<Membership> memberships = person.getMemberships();
		
		Person p = personManager.findById(person.getId());

		if (p != null) {
			oldMemberships = p.getMemberships();
		} else {
			oldMemberships = new ArrayList<Membership>();
			memberships = new ArrayList<Membership>();
		}

		for (Membership membership : oldMemberships) {
			Domain domain = membership.getDomain();
			PersonType type = membership.getType();

			/* usuário deixou de ter esse papel nesse dominio */
			if (!CollectionUtils.exists(person.getMemberships(), new MembershipPredicate(domain, type))) {
				if (!securityManager.hasPermission(membership, Permission.REMOVE_MEMBER)) {
					throw new BusinessException("psn.membership.remove.member.denied");
				}
			}
		}

		for (Membership membership : memberships) {
			Domain domain = membership.getDomain();
			PersonType type = membership.getType();

			/* usuário nao tem esse papel nesse dominio */
			if (!CollectionUtils.exists(oldMemberships, new MembershipPredicate(domain, type))) {
				if (!securityManager.hasPermission(membership, Permission.ASSIGN_MEMBER)) {
					throw new BusinessException("psn.membership.assign.member.denied");
				}
			}
		}
	}
	
	public void delete(Person person) throws Exception {
		try {
			if (!securityManager.hasPermission(person, Permission.DELETE)) {
				throw new BusinessException("psn.delete.denied");
			}
	
			personManager.delete(person);
		} catch (PersistenceException e) {
			throw new ApplicationException(e, "psn.delete.fail");
		}
	}

	public boolean authenticate(Person person, String password) {
		try {
			return personManager.authenticate(person, password);
		} catch (Exception e) {
			// TODO log it!
			return false;
		}
	}

}

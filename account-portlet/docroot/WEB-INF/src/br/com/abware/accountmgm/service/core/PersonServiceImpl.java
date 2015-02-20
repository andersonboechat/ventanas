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
import br.com.abware.accountmgm.util.MembershipPredicate;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.PersistenceException;

public class PersonServiceImpl  {

	private static final Condominium CONDOMINIUM = new Condominium();

	private static NewPersonManagerImpl personManager = new NewPersonManagerImpl();

	private static SecurityManagerImpl securityManager = new SecurityManagerImpl();

	private FlatServiceImpl flatService = new FlatServiceImpl();
	
	public PersonServiceImpl() {
		CONDOMINIUM.setRelatedId(10179);
	}

	public List<Person> getPeople(Person person) throws Exception {
		Set<Person> people = new HashSet<Person>(); 

		try {
			for (Flat flat : flatService.getFlats(person)) {
				people.addAll(getPeople(flat));
			}

			people.addAll(getPeople(CONDOMINIUM));
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<Person>(people);
	}

	public List<Person> getPeople(Domain domain) throws Exception {
		List<Person> people = new ArrayList<Person>();

		try {
			if (securityManager.hasPermission(domain, Permission.VIEW)) {
				people = personManager.findPeople(domain);
			}
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
		if (!securityManager.hasPermission(person, Permission.ADD)) {
			throw new Exception("sem permissao para cadastrar usuario");
		}

		if (StringUtils.isEmpty(person.getIdentity())) {
			throw new ApplicationException(null, "identidade não fornecida");
		}

		if (getPerson(person.getIdentity()) != null) {
			throw new ApplicationException(null, "existe usuario cadastrado com a identidade " + person.getIdentity());
		}

		validateMemberships(person);

		Person p = personManager.save(person);

		for (Membership membership : person.getMemberships()) {
			securityManager.addMembership(p, membership);
			personManager.addDomain(person, membership.getDomain());
		}

		return p;
	}

	@SuppressWarnings("unchecked")
	public Person update(Person person) throws Exception {
		if (!securityManager.hasPermission(person, Permission.UPDATE)) {
			throw new Exception("sem permissao para alterar usuario");
		}

		if (StringUtils.isEmpty(person.getIdentity())) {
			throw new ApplicationException(null, "identidade não fornecida");
		}

		Person p = personManager.findById(person.getId());
		if (p == null) {
			throw new ApplicationException(null, "usuario nao cadastrado");
		}

		validateMemberships(person);

		if (p.getMemberships() == null) {
			p.setMemberships(new ArrayList<Membership>());
		}

		List<Membership> memberships = (List<Membership>) CollectionUtils.subtract(p.getMemberships(), person.getMemberships());

		for (Membership membership : memberships) {
			securityManager.removeMembership(person, membership);
			personManager.removeDomain(person, membership.getDomain());
		}

		memberships = (List<Membership>) CollectionUtils.subtract(person.getMemberships(), p.getMemberships());

		for (Membership membership : memberships) {
			securityManager.addMembership(person, membership);
			personManager.addDomain(person, membership.getDomain());
		}

		return personManager.save(person);
	}	

	private void validateMemberships(Person person) throws Exception {
		List<Membership> oldMemberships;
		List<Membership> memberships = person.getMemberships();

		Person p = getPerson(person.getIdentity());

		if (p != null) {
			oldMemberships = p.getMemberships();
		} else {
			oldMemberships = new ArrayList<Membership>();
		}

		for (Membership membership : oldMemberships) {
			Domain domain = membership.getDomain();
			PersonType type = membership.getType();

			/* usuário deixou de ter esse papel nesse dominio */
			if (!CollectionUtils.exists(person.getMemberships(), new MembershipPredicate(domain, type))) {
				if (!securityManager.hasPermission(membership, Permission.REMOVE_MEMBER)) {
					throw new Exception("");
				}
			}
		}

		/* Incluindo pepeis */
		for (Membership membership : memberships) {
			Domain domain = membership.getDomain();
			PersonType type = membership.getType();

			/* usuário nao tem esse papel nesse dominio */
			if (!CollectionUtils.exists(oldMemberships, new MembershipPredicate(domain, type))) {
				if (!securityManager.hasPermission(membership, Permission.ASSIGN_MEMBER)) {
					throw new Exception("");
				}
			}
		}
	}
	
	public void delete(Person person) throws Exception {
		if (!securityManager.hasPermission(person, Permission.DELETE)) {
			throw new Exception("sem permissao para excluir este usuario");
		}

		personManager.delete(person);
	}

	@SuppressWarnings("unchecked")
	public List<Person> getOwners(Flat flat) throws Exception {
		List<Person> people;
		try {
			people = personManager.findPeople(flat);
			for (Person p : people) {
				Collection<Membership> c = CollectionUtils.select(p.getMemberships(), new MembershipPredicate(flat, PersonType.OWNER));
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
			people = personManager.findPeople(flat);
			for (Person p : people) {
				Collection<Membership> c = CollectionUtils.select(p.getMemberships(), new MembershipPredicate(flat, PersonType.RENTER));
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

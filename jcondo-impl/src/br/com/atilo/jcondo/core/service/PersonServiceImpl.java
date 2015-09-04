package br.com.atilo.jcondo.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;

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
import br.com.abware.jcondo.exception.ModelExistException;
import br.com.abware.jcondo.exception.PersistenceException;

import br.com.atilo.jcondo.commons.collections.MembershipPredicate;
import br.com.atilo.jcondo.commons.collections.PersonTypePredicate;
import br.com.atilo.jcondo.commons.collections.PersonTypeTransformer;
import br.com.atilo.jcondo.core.persistence.manager.PersonManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;
import br.com.caelum.stella.validation.CPFValidator;

public class PersonServiceImpl  {

	private static final Condominium CONDOMINIUM = new Condominium();

	private PersonManagerImpl personManager = new PersonManagerImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();
	
	private AdministrationServiceImpl adminService = new AdministrationServiceImpl();

	private FlatServiceImpl flatService = new FlatServiceImpl();

	public PersonServiceImpl() {
		CONDOMINIUM.setRelatedId(10179);
	}

	public boolean isAccessAuthorized(Person person) throws Exception {
		if (person == null || person.getMemberships() == null) {
			return false;
		}
		
		boolean isAccessAuthorized = false;

		for (Membership membership : person.getMemberships()) {
			PersonType type = membership.getType();
			if (type == PersonType.RENTER || type == PersonType.RESIDENT || 
					type == PersonType.DEPENDENT ||	type == PersonType.GUEST || type == PersonType.EMPLOYEE) {
				isAccessAuthorized = true;
				break;
			} else if (type == PersonType.OWNER) {
				List<Person> renters = personManager.findPeopleByType(membership.getDomain(), PersonType.RENTER);
				if (renters.isEmpty()) {
					isAccessAuthorized = true;
					break;
				}
			}
		}

		return isAccessAuthorized;
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

	public Person getPerson(String identity) throws PersistenceException {
		return personManager.findPerson(identity);
	}
	
	public Person getPerson() throws Exception {
		return personManager.findPerson();
	}
	
	/**

	 * Papeis de Apartamento: Proprietario, Morador, Dependente, Convidado, Visitante
	 * 
	 * Papeis de Fornecedor: Gerente, Funcionario
	 * 
	 * Papeis de membro do condom�nio: Alugador (Aluguel de Espa�os), 
	 * 								   Reclamador (livro de reclama��es), 
	 * 								   Comentarista (forum), 
	 * 								   Provedor de Acesso 
	 * 
	 * Se for morador, associa aos papeis: Morador, Alugador, Reclamador, Comentarista, Provedor de Acesso
	 * Se for dependente, associa aos papeis: Dependente, Provedor de Acesso
	 * Se for convidado, associa aos papeis: Convidado
	 * Se for visitante, associa aos papeis: Visitante
	 * 
	 * Se for propriet�rio
	 * 		Se n�o existir Locatarios, associa aos papeis: Propriet�rio, Alugador, Reclamador, Comentarista, Provedor de Acesso
	 * 		Se existir Locatarios, associa aos papeis: Propriet�rio
	 * 
	 * Se for locat�rio, associa aos papeis: Locat�rio, Alugador, Reclamador, Comentarista, Provedor de Acesso
	 * 				   , associa os propriet�rios aos papeis: Propriet�rio
	 */
	public Person register(Person person) throws Exception {
		try {
			if (!securityManager.hasPermission(person, Permission.ADD)) {
				throw new BusinessException("psn.create.denied");
			}

			if (StringUtils.isEmpty(person.getIdentity())) {
				throw new BusinessException("psn.identity.empty");
			} else {
				try {
					new CPFValidator().assertValid(person.getIdentity().replaceAll("[^0-9]+", ""));
				} catch (Exception e) {
					throw new BusinessException("psn.identity.not.valid", person.getIdentity());
				}

				if (getPerson(person.getIdentity()) != null) {
					throw new ModelExistException(null, "psn.identity.exists", person.getIdentity());
				}
			}

			if (person.getBirthday() == null) {
				person.setBirthday(new Date());
			}
			
			if (person.getEmailAddress() == null) {
				person.setEmailAddress("");
			}

			validateDomains(person);
			validateMemberships(person);

			Person p = personManager.save(person);

			try {
				for (Membership membership : person.getMemberships()) {
					securityManager.addMembership(p, membership);
				}
				
				handleAccountAccess(p, new ArrayList<Membership>());
			} catch (Exception e) {
				// TODO: Log it!
				e.printStackTrace();
			}

			return p;
		} catch (PersistenceException e) {
			throw new ApplicationException(e, "psn.register.fail");
		}
	}

	@SuppressWarnings("unchecked")
	private void handleAccountAccess(Person person, List<Membership> memberships) throws Exception {
		User user = UserLocalServiceUtil.getUser(person.getUserId());
		List<PersonType> userTypes = Arrays.asList(PersonType.OWNER, PersonType.RENTER, PersonType.RESIDENT, 
												   PersonType.DEPENDENT, PersonType.ADMIN_ASSISTANT); 
		List<PersonType> originalTypes = (List<PersonType>) CollectionUtils.collect(memberships, new PersonTypeTransformer());
		List<PersonType> updatedTypes = (List<PersonType>) CollectionUtils.collect(person.getMemberships(), new PersonTypeTransformer());

		if (CollectionUtils.containsAny(updatedTypes, userTypes) &&
				!CollectionUtils.containsAny(originalTypes, userTypes)) {
			UserLocalServiceUtil.updateStatus(person.getUserId(), WorkflowConstants.STATUS_APPROVED);

			if (!user.getAgreedToTermsOfUse()) {
				notifyUserAccountCreation(person);	
			}
		} 

		if (!CollectionUtils.containsAny(updatedTypes, userTypes) && 
				(originalTypes.isEmpty() || CollectionUtils.containsAny(originalTypes, userTypes))) {
			UserLocalServiceUtil.updateStatus(person.getUserId(), WorkflowConstants.STATUS_DENIED);
		}
	}	

	private boolean isExternalPerson(Person person) {
		boolean isVisitor = CollectionUtils.exists(person.getMemberships(), new PersonTypePredicate(PersonType.VISITOR));
		boolean isGuest = CollectionUtils.exists(person.getMemberships(), new PersonTypePredicate(PersonType.GUEST));
		return isVisitor || isGuest;
	}
	
 	private void notifyUserAccountCreation(Person p) throws Exception {
		ServiceContext sc = new ServiceContext();
		sc.setAttribute("autoPassword", true);
		sc.setAttribute("sendEmail", true);
		UserLocalServiceUtil.completeUserRegistration(UserLocalServiceUtil.getUser(p.getUserId()), sc);
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

			Person p;

			if (StringUtils.isEmpty(person.getIdentity())) {
				throw new BusinessException("psn.identity.empty");
			} else {
				try {
					new CPFValidator().assertValid(person.getIdentity().replaceAll("[^0-9]+", ""));
				} catch (Exception e) {
					throw new BusinessException("psn.identity.not.valid", person.getIdentity());
				}

				p = getPerson(person.getIdentity());
				if (p != null && !p.equals(person)) {
					throw new ModelExistException(null, "psn.identity.exists", person.getIdentity());
				}
			}

			p = personManager.findById(person.getId());
			if (p == null) {
				throw new BusinessException("psn.not.found");
			}

			if (person.getBirthday() == null) {
				person.setBirthday(p.getBirthday());
			}

			if (person.getEmailAddress() == null) {
				person.setEmailAddress(p.getEmailAddress());
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

			memberships = p.getMemberships();

			p = personManager.save(person);

			try {
				handleAccountAccess(p, memberships);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return p;
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

			/* usu�rio deixou de ter esse papel nesse dominio */
			if (!CollectionUtils.exists(person.getMemberships(), new MembershipPredicate(domain, type))) {
				if (!securityManager.hasPermission(membership, Permission.REMOVE_MEMBER)) {
					throw new BusinessException("psn.membership.remove.member.denied");
				}
			}
		}

		for (Membership membership : memberships) {
			Domain domain = membership.getDomain();
			PersonType type = membership.getType();

			/* usu�rio nao tem esse papel nesse dominio */
			if (!CollectionUtils.exists(oldMemberships, new MembershipPredicate(domain, type))) {
				if (!securityManager.hasPermission(membership, Permission.ASSIGN_MEMBER)) {
					throw new BusinessException("psn.membership.assign.member.denied");
				}
			}
		}

		int residents = CollectionUtils.countMatches(memberships, new PersonTypePredicate(PersonType.RESIDENT));

		// Uma pessoa pode ser morador em somente um apartamento			
		if (residents > 1) {
			throw new BusinessException("psn.membership.assign.member.denied");
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

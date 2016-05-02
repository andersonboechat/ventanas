package br.com.atilo.jcondo.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.KinType;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Phone;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.abware.jcondo.exception.ModelExistException;
import br.com.abware.jcondo.exception.PersistenceException;
import br.com.atilo.jcondo.commons.collections.DomainPredicate;
import br.com.atilo.jcondo.commons.collections.MembershipPredicate;
import br.com.atilo.jcondo.commons.collections.PersonTypePredicate;
import br.com.atilo.jcondo.commons.collections.PersonTypeTransformer;
import br.com.atilo.jcondo.core.listener.PersonServiceListener;
import br.com.atilo.jcondo.core.persistence.manager.PersonManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;
import br.com.caelum.stella.validation.CPFValidator;

import com.liferay.portal.DuplicateUserEmailAddressException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;

public class PersonServiceImpl extends Observable  {

	private static final Condominium CONDOMINIUM = new Condominium();

	private PersonManagerImpl personManager = new PersonManagerImpl();
	
	private MembershipServiceImpl membershipService = new MembershipServiceImpl();
	
	private KinshipServiceImpl kinshipService = new KinshipServiceImpl();
	
	private PhoneServiceImpl phoneService = new PhoneServiceImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();
	
	private AdministrationServiceImpl adminService = new AdministrationServiceImpl();

	private FlatServiceImpl flatService = new FlatServiceImpl();

	public PersonServiceImpl() {
		CONDOMINIUM.setRelatedId(10179);
		addObserver(new PersonServiceListener());
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

	public Person getPerson(long id) throws Exception {
		return personManager.findById(id);
	}
	
	public Person getPerson() throws Exception {
		return personManager.findPerson();
	}
	
	public Person register(Person person) throws Exception {
		try {
			if (!securityManager.hasPermission(person, Permission.ADD)) {
				throw new BusinessException("psn.create.denied");
			}

			if (StringUtils.isEmpty(person.getIdentity())) {
				if (!CollectionUtils.exists(person.getMemberships(), new PersonTypePredicate(PersonType.RESIDENT))) {
					throw new BusinessException("psn.identity.empty");
				}
			} else {
				try {
					new CPFValidator().assertValid(person.getIdentity().replaceAll("[^0-9]+", ""));
				} catch (Exception e) {
					throw new BusinessException("psn.identity.not.valid", person.getIdentity());
				}

				if (getPerson(person.getIdentity()) != null) {
					throw new ModelExistException(null, "psn.identity.exists", 
												  person.getPicture().getPath(), person.getFirstName(), 
												  person.getLastName(), person.getIdentity());
				}
			}

			if (person.getBirthday() == null) {
				person.setBirthday(new Date());
			}
			
			if (person.getEmailAddress() == null) {
				person.setEmailAddress("");
			}

			Person p = personManager.save(person);

			List<Membership> newMemberships = person.getMemberships();
			List<Membership> oldMemberships = membershipService.getMemberships(p);			

			handleMemberships(p, newMemberships, oldMemberships);
			
			p.setMemberships(membershipService.getMemberships(p));

			try {
				if (!StringUtils.isEmpty(person.getEmailAddress())) {
					notifyUserAccountCreation(person);	
				}

				setChanged();
				notifyObservers(p);
			} catch (Exception e) {
				// TODO: Log it!
				e.printStackTrace();
			}

			return p;
		} catch (DuplicateUserEmailAddressException e) {
			throw new BusinessException("psn.email.already.exists", person.getEmailAddress());
		} catch (PersistenceException e) {
			throw new ApplicationException(e, "psn.register.fail");
		}
	}

	@SuppressWarnings("unchecked")
	private void handleAccountAccess(Person person, List<Membership> newMemberships, List<Membership> oldMemberships) throws Exception {
		User user = UserLocalServiceUtil.getUser(person.getUserId());
		List<PersonType> userTypes = Arrays.asList(PersonType.OWNER, PersonType.RENTER, PersonType.RESIDENT, 
												   PersonType.DEPENDENT, PersonType.ADMIN_ASSISTANT); 
		List<PersonType> originalTypes = (List<PersonType>) CollectionUtils.collect(oldMemberships, new PersonTypeTransformer());
		List<PersonType> updatedTypes = (List<PersonType>) CollectionUtils.collect(newMemberships, new PersonTypeTransformer());

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

	@SuppressWarnings("unchecked")
	private void handleAccountAccess(Person person, Membership membership) throws Exception {
		User user = UserLocalServiceUtil.getUser(person.getUserId());
		List<Membership> memberships = membershipService.getMemberships(person);
		List<PersonType> userTypes = Arrays.asList(PersonType.OWNER, PersonType.RENTER, PersonType.RESIDENT, 
												   PersonType.DEPENDENT, PersonType.ADMIN_ASSISTANT);
		List<PersonType> originalTypes = (List<PersonType>) CollectionUtils.collect(memberships, new PersonTypeTransformer());

		if (userTypes.contains(membership.getType()) &&
				!CollectionUtils.containsAny(originalTypes, userTypes)) {
			UserLocalServiceUtil.updateStatus(person.getUserId(), WorkflowConstants.STATUS_APPROVED);

			if (!user.getAgreedToTermsOfUse()) {
				notifyUserAccountCreation(person);	
			}
		} 

		if (!userTypes.contains(membership.getType()) && 
				(originalTypes.isEmpty() || CollectionUtils.containsAny(originalTypes, userTypes))) {
			UserLocalServiceUtil.updateStatus(person.getUserId(), WorkflowConstants.STATUS_DENIED);
		}
		
	}
	
 	private void notifyUserAccountCreation(Person p) throws Exception {
 		User user = UserLocalServiceUtil.getUser(p.getUserId());
		if (user.getAgreedToTermsOfUse()) {
			return;
		}

		ServiceContext sc = new ServiceContext();
		sc.setAttribute("autoPassword", true);
		sc.setAttribute("sendEmail", true);
		UserLocalServiceUtil.completeUserRegistration(user, sc);
	}

	public void updatePassword(Person person, String password, String newPassword) throws Exception {
//		Person p = getPerson(person.getIdentity());
//
//		if (p == null) {
//			throw new Exception("Person not found");
//		}

		securityManager.updatePassword(person, password, newPassword);
	}
	
	public Person add(String identity, String firstName, String lastName, 
						   Gender gender, Date birthday, String email, Phone phone, 
						   Domain domain, PersonType personType, KinType kintype) throws Exception {
		Person person = new Person();

		if (!securityManager.hasPermission(person, Permission.ADD)) {
			throw new BusinessException("psn.create.denied");
		}

		if (StringUtils.isEmpty(identity)) {
			if (personType != PersonType.RESIDENT) {
				throw new BusinessException("psn.identity.empty");
			}
		} else {
			try {
				new CPFValidator().assertValid(identity.replaceAll("[^0-9]+", ""));
			} catch (Exception e) {
				throw new BusinessException("psn.identity.not.valid", person.getIdentity());
			}

			if (getPerson(identity) != null) {
				throw new ModelExistException(null, "psn.identity.exists", person.getIdentity());
			}
		}

		//TODO: verificar se o dominio existe
		
		person.setIdentity(identity);
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setGender(gender);
		person.setBirthday(birthday);
		person.setEmailAddress(email);		

		person = personManager.save(person);

		Membership membership = membershipService.add(domain, person, personType);

		if (kintype != null) {
			kinshipService.add(getPerson(), person, kintype);
		}

		if (phone != null) {
			phoneService.add(person, phone.getExtension(), phone.getNumber(), phone.getType(), phone.isPrimary());
		}

		try {
			handleAccountAccess(person, membership);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return person;
	}

	public Person update(long id, String identity, String firstName, String lastName, 
						 Gender gender, Date birthday, String email, Phone phone, 
						 Domain domain, PersonType personType, KinType kintype) throws Exception {
		Person person = personManager.findById(id);

		if (person == null) {
			throw new BusinessException("psn.not.found");
		}			

		if (!securityManager.hasPermission(person, Permission.UPDATE)) {
			throw new BusinessException("psn.update.denied");
		}

		if (!person.getIdentity().equals(identity)) {
			if (!StringUtils.isEmpty(person.getIdentity())) {
				throw new BusinessException("psn.identity.change.denied");
			} else {
				try {
					new CPFValidator().assertValid(person.getIdentity().replaceAll("[^0-9]+", ""));
				} catch (Exception e) {
					throw new BusinessException("psn.identity.not.valid", person.getIdentity());
				}

				person = getPerson(person.getIdentity());
				if (person != null && !person.equals(person)) {
					throw new ModelExistException(null, "psn.identity.exists", person.getIdentity());
				}
			}
		} else if (StringUtils.isEmpty(identity)) {
			if (personType == null) {
				throw new BusinessException("psn.person.type.empty");
			} else if (personType != PersonType.RESIDENT) {
				throw new BusinessException("psn.identity.empty");
			}
		}

		//TODO: verificar se o dominio existe
		
		person.setIdentity(identity);
		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setGender(gender);
		person.setBirthday(birthday);
		person.setEmailAddress(email);		

		person = personManager.save(person);

		Membership membership = membershipService.add(domain, person, personType);

		kinshipService.update(getPerson(), person, kintype);

		if (phone != null) {
			phoneService.update(phone.getId(), phone.getExtension(), 
								phone.getNumber(), phone.getType(), phone.isPrimary());
		}

		try {
			handleAccountAccess(person, membership);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return person;
	}
	
	public Person update(Person person) throws Exception {
		try {
			if (!securityManager.hasPermission(person, Permission.UPDATE)) {
				throw new BusinessException("psn.update.denied");
			}

			Person dbPerson = personManager.findById(person.getId());

			if (dbPerson == null) {
				throw new BusinessException("psn.not.found");
			}			

			if (!StringUtils.equals(StringUtils.trimToNull(person.getIdentity()), 
									StringUtils.trimToNull(dbPerson.getIdentity()))) {
				if (!StringUtils.isEmpty(dbPerson.getIdentity())) {
					throw new BusinessException("psn.identity.change.denied");
				} else {
					try {
						new CPFValidator().assertValid(person.getIdentity().replaceAll("[^0-9]+", ""));
					} catch (Exception e) {
						throw new BusinessException("psn.identity.not.valid", person.getIdentity());
					}

					Person p = getPerson(person.getIdentity());
					if (p != null && !p.equals(person)) {
						throw new ModelExistException(null, "psn.identity.exists", person.getIdentity());
					}
				}
			}

			if (person.getBirthday() == null) {
				person.setBirthday(dbPerson.getBirthday());
			}

			Person p = personManager.save(person);

			List<Membership> newMemberships = person.getMemberships();
			List<Membership> oldMemberships = membershipService.getMemberships(person);

			handleMemberships(p, newMemberships, oldMemberships);

			p.setMemberships(membershipService.getMemberships(p));

			try {
				if (!StringUtils.isEmpty(person.getEmailAddress()) &&
						!StringUtils.equals(dbPerson.getEmailAddress(), person.getEmailAddress())) {
					notifyUserAccountCreation(person);	
				}
				
				setChanged();
				notifyObservers(p);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return p;
		} catch (DuplicateUserEmailAddressException e) {
			throw new BusinessException("psn.email.already.exists", person.getEmailAddress());
		} catch (PersistenceException e) {
			throw new ApplicationException(e, "psn.update.fail");
		}
	}

	@SuppressWarnings("unchecked")
	private void handleMemberships(Person person, List<Membership> newMemberships, List<Membership> oldMemberships) throws Exception {
		if (oldMemberships == null) {
			oldMemberships = new ArrayList<Membership>();
		}		

		List<Membership> toRemove = (List<Membership>) CollectionUtils.subtract(oldMemberships, newMemberships);
		
		List<Membership> ms = new ArrayList<Membership>(newMemberships); 
		ms.removeAll(toRemove);

		// Somente um membership por dominio
		for (Membership m : ms) {
			if (CollectionUtils.countMatches(ms, new DomainPredicate(m.getDomain())) > 1) {
				throw new BusinessException("psn.membership.already.exists", m.getDomain());
			}
		}

		// Uma pessoa pode ser morador em somente um apartamento			
		if (CollectionUtils.countMatches(ms, new PersonTypePredicate(PersonType.RESIDENT)) > 1) {
			throw new BusinessException("psn.membership.already.resident");
		}

		for (Membership membership : toRemove) {
			if (!securityManager.hasPermission(membership, Permission.REMOVE_MEMBER)) {
				throw new BusinessException("psn.membership.remove.member.denied");
			}
		}

		List<Membership> toAdd = (List<Membership>) CollectionUtils.subtract(newMemberships, oldMemberships);
		
		for (Membership membership : toAdd) {
			if (!securityManager.hasPermission(membership, Permission.ASSIGN_MEMBER)) {
				throw new BusinessException("psn.membership.assign.member.denied");
			}
		}
		
		for (Membership membership : toRemove) {
			membershipService.remove(membership.getDomain(), person, membership.getType());
		}

		for (Membership membership : toAdd) {
			membershipService.add(membership.getDomain(), person, membership.getType());
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void validateMemberships(Person person) throws Exception {
		List<Membership> oldMemberships;
		List<Membership> memberships = person.getMemberships();
		
		Person p = personManager.findById(person.getId());

		if (p != null) {
			oldMemberships = p.getMemberships();
			List<Membership> ms = new ArrayList<Membership>(person.getMemberships()); 
			ms.removeAll((List<Membership>) CollectionUtils.subtract(oldMemberships, memberships));

			// Somente um membership por dominio
			for (Membership m : ms) {
				if (CollectionUtils.countMatches(ms, new DomainPredicate(m.getDomain())) > 1) {
					throw new BusinessException("psn.membership.already.exists", m.getDomain());
				}
			}

			// Uma pessoa pode ser morador em somente um apartamento			
			if (CollectionUtils.countMatches(ms, new PersonTypePredicate(PersonType.RESIDENT)) > 1) {
				throw new BusinessException("psn.membership.already.resident");
			}
			
		} else {
			oldMemberships = new ArrayList<Membership>();
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

	public boolean hasPermission(Person person, Permission permission) throws Exception {
		if (personManager.findPerson().equals(person)) {
			return true;
		}

		if (securityManager.hasPermission(person, permission)) {
			boolean hasPersonTypePermission = true;
			Permission p;
			
			if (permission == Permission.UPDATE || permission == Permission.ADD) {
				p = Permission.ASSIGN_MEMBER;
			} else if (permission == Permission.DELETE) {
				p = Permission.REMOVE_MEMBER;
			} else {
				p = Permission.VIEW;
			}

			for (Membership membership : person.getMemberships()) {
				hasPersonTypePermission = hasPersonTypePermission && securityManager.hasPermission(membership, p);
			}

			return hasPersonTypePermission;
		}
		
		return false;
	}

}

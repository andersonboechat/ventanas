package br.com.abware.accountmgm.persistence.manager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.accountmgm.persistence.entity.PersonEntity;
import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Role;
import br.com.abware.jcondo.core.model.RoleName;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.PersistenceException;
import br.com.abware.jcondo.exception.SystemException;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroupRole;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.persistence.UserGroupRolePK;

public class NewPersonManagerImpl extends JCondoManager<PersonEntity, Person> {
	
	@Override
	protected Class<PersonEntity> getEntityClass() {
		return PersonEntity.class;
	}

	@Override
	protected Class<Person> getModelClass() {
		return Person.class;
	}

	public Person save(Person person) throws PersistenceException {
		try {	
			User user = null;

			try {
				user = UserLocalServiceUtil.getUserById(person.getId());
			} catch (NoSuchUserException e) {
			}

			Calendar c = Calendar.getInstance();
			c.setTime(person.getBirthday());

			int birthdayDay = c.get(Calendar.DAY_OF_MONTH);
			int birthdayMonth = c.get(Calendar.MONTH);
			int birthdayYear = c.get(Calendar.YEAR);

			boolean isMale = person.getGender().equals(Gender.MALE);

			if (user == null) {
				user = UserLocalServiceUtil.addUser(helper.getUser().getUserId(), helper.getCompanyId(), true, 
											 		StringUtils.EMPTY, StringUtils.EMPTY, true, StringUtils.EMPTY, 
											 		person.getEmailAddress(), 0, StringUtils.EMPTY, helper.getUser().getLocale(), 
											 		person.getFirstName(), StringUtils.EMPTY, person.getLastName(), 0, 0, 
											 		isMale, birthdayMonth, birthdayDay, birthdayYear, 
											 		StringUtils.EMPTY, null, null, null, 
											 		null, false, new ServiceContext());
			} else {
				user = UserLocalServiceUtil.updateUser(person.getId(), user.getPassword(), StringUtils.EMPTY, StringUtils.EMPTY, false, 
													   user.getReminderQueryQuestion(), user.getReminderQueryAnswer(), user.getScreenName(), 
													   person.getEmailAddress(), user.getFacebookId(), user.getOpenId(), user.getLanguageId(), 
													   user.getTimeZoneId(), user.getGreeting(), user.getComments(), person.getFirstName(), 
													   StringUtils.EMPTY, person.getLastName(), 0, 0, isMale, birthdayMonth, birthdayDay, 
													   birthdayYear, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, 
													   StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, 
													   StringUtils.EMPTY, StringUtils.EMPTY, user.getJobTitle(), null, null, 
													   null, null, null, new ServiceContext());

			}

			try {
				if (person.getPicture() != null && 
						!person.getPicture().equalsIgnoreCase(user.getPortraitURL(helper.getThemeDisplay()))) {
					File file = new File(new URL(person.getPicture()).toURI());
					user = UserLocalServiceUtil.updatePortrait(person.getId(),	FileUtils.readFileToByteArray(file));
				}
			} catch (Exception e) {
				// TODO Log it!
			}

			return getModel(user);
		} catch (Exception e) {
			throw new PersistenceException("");
		}
	}

	public void updatePersonDomains(Person person, List<Domain> domains) {
		long[] groupIds = new long[0];

		try {
			for (Domain domain : domains) {
				groupIds = ArrayUtils.add(groupIds, domain.getId());
			}

			if (groupIds.length > 0) {
				UserLocalServiceUtil.updateGroups(person.getId(), groupIds, new ServiceContext());
			}
		} catch (Exception e) {
			// TODO log it!
		}
	}	

	public void delete(Person person) throws PersistenceException {
		try {
			if (person.getId() != 0) {
				UserLocalServiceUtil.updateStatus(person.getId(), WorkflowConstants.STATUS_INACTIVE);
			}
		} catch (NoSuchUserException e) {
			throw new PersistenceException("usuario nao cadastrado");
		} catch (Exception e) {
			throw new PersistenceException("");
		}
	}

	public List<Person> findPeople(Domain domain) throws PersistenceException {
		try {
			List<PersonEntity> people = new ArrayList<PersonEntity>();

			for (User user : UserLocalServiceUtil.getGroupUsers(domain.getId())) {
				people.add(new PersonEntity());
			}

			return getModels(people);
		} catch (Exception e) {
			throw new PersistenceException("");
		}
	}

	public Person getLoggedPerson() throws PersistenceException {
		try {
			return getModel(helper.getUser());
		} catch (Exception e) {
			throw new PersistenceException("");
		}	
	}

	@Override
	protected Person getModel(PersonEntity entity) throws PersistenceException {
		try {
			Person person = super.getModel(entity);

			List<Membership> memberships = new ArrayList<Membership>();
			List<Group> groups = GroupLocalServiceUtil.getUserGroups(entity.getId());

			for (Group group : groups) {
				if (group.getSite()) {
					List<UserGroupRole> roles =  UserGroupRoleLocalServiceUtil.getUserGroupRoles(entity.getId(), group.getGroupId());
					for (UserGroupRole ugr : roles) {
						Role role = new Role(ugr.getRoleId(), RoleName.parse(ugr.getRole().getName()), ugr.getRole().getTitle());
						memberships.add(new Membership(role, new Condominium(group.getGroupId(), group.getDescriptiveName())));
					}
				} else {
					
				}
			}

			person.setMemberships(memberships);
			//person.setStatus(PersonStatus.parseStatus(user.getStatus()));

			person.setGender(user.isMale() ? Gender.MALE : Gender.FEMALE);
			person.setPicture(user.getPortraitURL(helper.getThemeDisplay()));
			person.setId(user.getUserId());

			return person;
		} catch (Exception e) {
			throw new PersistenceException(e, "");
		}
	}
	
	public Person findById(Object id) throws PersistenceException {
		try {
			return getModel(UserLocalServiceUtil.getUser((Long) id));
		} catch (Exception e) {
			throw new PersistenceException("");
		}
	}
	
	public boolean exists(Person person) throws Exception {
		return getEntity(person) != null;
	}

	public List<Person> findAll() throws PersistenceException {
		try {
			List<User> users = UserLocalServiceUtil.getUsers(-1, -1);
			return getModels(users);
		} catch (Exception e) {
			throw new PersistenceException("");
		}
	}

	public void removeDomain(Person person, Domain domain) {
		// TODO Auto-generated method stub
		
	}

	public void addDomain(Person person, Domain domain) {
		// TODO Auto-generated method stub
	}

}

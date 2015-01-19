package br.com.abware.accountmgm.persistence.manager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonStatus;
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

import com.liferay.faces.portal.context.LiferayPortletHelper;
import com.liferay.faces.portal.context.LiferayPortletHelperImpl;
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
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

public class PersonManagerImpl extends AbstractManager<User, Person> {
	
	private static final String PERSON_TYPE = "TYPE";
	
	private LiferayPortletHelper helper = new LiferayPortletHelperImpl();	

	private String getCustomField(String fieldName, User user) throws Exception {
		return (String) ExpandoValueLocalServiceUtil.getData(user.getCompanyId(), 
															 user.getClass().getName(), 
														     ExpandoTableConstants.DEFAULT_TABLE_NAME, 
														     fieldName, 
														     user.getUserId());
	}	

	private void saveCustomField(String fieldName, String fieldValue, User user) throws Exception {
		ExpandoValue value = ExpandoValueLocalServiceUtil.getValue(user.getCompanyId(), 
																   user.getClass().getName(), 
																   ExpandoTableConstants.DEFAULT_TABLE_NAME, 
																   fieldName, 
																   user.getUserId());

		if(value == null) {
			ExpandoValueLocalServiceUtil.addValue(user.getCompanyId(), 
												  user.getClass().getName(), 
												  ExpandoTableConstants.DEFAULT_TABLE_NAME, 
												  fieldName, 
												  user.getUserId(),
												  fieldValue);
		} else {
			value.setData(fieldValue);
			ExpandoValueLocalServiceUtil.updateExpandoValue(value);
		}
	}	

	@Override
	protected User getEntity(Person person) throws PersistenceException {
		User user;

		try {
			try {
				user = UserLocalServiceUtil.getUserById(person.getId());
			} catch (NoSuchUserException e) {
				return null;
			}

			//BeanUtils.copyProperties(user, person);
		} catch (Exception e) {
			throw new PersistenceException("");
		}

		return user;
	}

	@Override
	protected Class<Person> getModelClass() {
		return Person.class;
	}

	public Person save(Person person) throws PersistenceException {
		try {	
			User user = getEntity(person);
			Calendar c = Calendar.getInstance();
			c.setTime(person.getBirthday());

			int birthdayDay = c.get(Calendar.DAY_OF_MONTH);
			int birthdayMonth = c.get(Calendar.MONTH);
			int birthdayYear = c.get(Calendar.YEAR);

			boolean isMale = person.getGender().equals(Gender.MALE);
			boolean isMember = false;
			long[] organizationIds = new long[0];
			long[] groupIds = new long[0];
			List<UserGroupRole> userGroupRoles = new ArrayList<UserGroupRole>();

			for (Membership m : person.getMemberships()) {
				if (m.getDomain() instanceof Condominium) {
					groupIds = ArrayUtils.add(groupIds, m.getDomain().getId());
				} else {
					organizationIds = ArrayUtils.add(organizationIds, m.getDomain().getId());
					userGroupRoles.add(UserGroupRoleLocalServiceUtil.createUserGroupRole(new UserGroupRolePK(user.getUserId(), 
																											 m.getDomain().getId(), 
																											 m.getRole().getId())));
				}

				if (m.getRole().getName() != RoleName.GUEST ||
						m.getRole().getName() != RoleName.VISITOR ||
						m.getRole().getName() != RoleName.THIRD_PARTY) {
					isMember = true;
				}
			}

			if (user == null) {
				User creatorUser = helper.getUser();
				user = UserLocalServiceUtil.addUser(creatorUser.getUserId(), creatorUser.getCompanyId(), true, 
											 		StringUtils.EMPTY, StringUtils.EMPTY, true, StringUtils.EMPTY, 
											 		person.getEmailAddress(), 0, StringUtils.EMPTY, creatorUser.getLocale(), 
											 		person.getFirstName(), StringUtils.EMPTY, person.getLastName(), 0, 0, 
											 		isMale, birthdayMonth, birthdayDay, birthdayYear, 
											 		StringUtils.EMPTY, groupIds, organizationIds, null, 
											 		null, isMember, new ServiceContext());

				if (!isMember) {
					UserLocalServiceUtil.updateLockout(user, true);
				}
			} else {
				boolean wasMember = user.isLockout();
				user = UserLocalServiceUtil.updateUser(person.getId(), user.getPassword(), StringUtils.EMPTY, StringUtils.EMPTY, false, 
													   user.getReminderQueryQuestion(), user.getReminderQueryAnswer(), user.getScreenName(), 
													   person.getEmailAddress(), user.getFacebookId(), user.getOpenId(), user.getLanguageId(), 
													   user.getTimeZoneId(), user.getGreeting(), user.getComments(), person.getFirstName(), 
													   StringUtils.EMPTY, person.getLastName(), 0, 0, isMale, birthdayMonth, birthdayDay, 
													   birthdayYear, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, 
													   StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, 
													   StringUtils.EMPTY, StringUtils.EMPTY, user.getJobTitle(), groupIds, organizationIds, 
													   null, userGroupRoles, null, new ServiceContext());
				if (!isMember) {
					UserLocalServiceUtil.updateLockout(user, true);
				} else if (!wasMember) {
					UserLocalServiceUtil.updateLockout(user, false);
					UserLocalServiceUtil.sendPassword(user.getCompanyId(), user.getEmailAddress(), StringUtils.EMPTY, StringUtils.EMPTY, 
													  StringUtils.EMPTY, StringUtils.EMPTY, new ServiceContext());
				}

			}

			try {
				if (person.getPicture() != null) {
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
		long[] organizationIds = new long[0];
		long[] groupIds = new long[0];
		try {
			for (Domain domain : domains) {
				if (domain instanceof Flat || domain instanceof Supplier) {
					List<Organization> orgs = OrganizationLocalServiceUtil.getGroupOrganizations(domain.getId());
					organizationIds = ArrayUtils.add(organizationIds, domain.getId());
				} else {
					groupIds = ArrayUtils.add(groupIds, domain.getId());
				}
			}
	
			if (organizationIds.length > 0) {
				UserLocalServiceUtil.updateOrganizations(person.getId(), organizationIds, new ServiceContext());
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
		} catch (Exception e) {
			throw new PersistenceException("");
		}
	}

	public List<Person> findPeople(Domain domain) throws PersistenceException {
		try {
			List<User> users;

			if (domain instanceof Condominium) {
				users = UserLocalServiceUtil.getGroupUsers(domain.getId());
			} else {
				users = UserLocalServiceUtil.getOrganizationUsers(domain.getId()); 
			}

			return getModels(users);
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
	protected Person getModel(User user) throws PersistenceException {
		try {
			Person person = super.getModel(user);

			List<Membership> memberships = new ArrayList<Membership>();
			List<Organization> organizations = OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId());

			for (Organization organization : organizations) {
				Domain domain;
				if (organization.getType().equalsIgnoreCase("regular-organization")) {
					String[] name = organization.getName().split("/");
					domain = new Flat(organization.getOrganizationId(), Long.parseLong(name[0]), Long.parseLong(name[1]));
				} else if (organization.getType().equalsIgnoreCase("supplier")) {
					domain = new Supplier(organization.getOrganizationId(), organization.getName());
				} else {
					throw new SystemException("Organization not supported");
				}

				List<UserGroupRole> roles =  UserGroupRoleLocalServiceUtil.getUserGroupRoles(user.getUserId(), organization.getGroupId());
				for (UserGroupRole ugr : roles) {
					Role role = new Role(ugr.getRoleId(), RoleName.parse(ugr.getRole().getName()), ugr.getRole().getTitle());
					memberships.add(new Membership(role, domain));
				}
			}

			List<Group> groups = GroupLocalServiceUtil.getUserGroups(user.getUserId());
			
			for (Group group : groups) {
				if (group.getSite()) {
					List<UserGroupRole> roles =  UserGroupRoleLocalServiceUtil.getUserGroupRoles(user.getUserId(), group.getGroupId());
					for (UserGroupRole ugr : roles) {
						Role role = new Role(ugr.getRoleId(), RoleName.parse(ugr.getRole().getName()), ugr.getRole().getTitle());
						memberships.add(new Membership(role, new Condominium(group.getGroupId(), group.getDescriptiveName())));
					}
				}
			}
			
			person.setStatus(PersonStatus.parseStatus(user.getStatus()));
			person.setGender(user.isMale() ? Gender.MALE : Gender.FEMALE);
			person.setPicture(user.getPortraitURL(helper.getThemeDisplay()));

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
	
	public boolean exists(Person person) throws PersistenceException {
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

}

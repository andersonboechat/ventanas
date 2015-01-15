package br.com.abware.accountmgm.persistence.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Group;
import br.com.abware.jcondo.core.model.GroupType;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Role;
import br.com.abware.jcondo.exception.PersistenceException;

import com.liferay.faces.portal.context.LiferayPortletHelper;
import com.liferay.faces.portal.context.LiferayPortletHelperImpl;
import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.NoSuchUserGroupRoleException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.persistence.UserGroupRolePK;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

public class PersonManagerImpl extends AbstractManager<User, Person> {
	
	private static final long GUEST_ROLE_ID = 0;
	
	private static final String HOME = "RESIDENCIA";
	
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
				user = UserLocalServiceUtil.createUser(person.getId());
			}

			BeanUtils.copyProperties(user, person);
		} catch (Exception e) {
			throw new PersistenceException("");
		}

		return user;
	}

	@Override
	protected Class<Person> getModelClass() {
		return Person.class;
	}

	
	public Person persist(Person person) throws PersistenceException {
		try {		
			User user;

			user = getEntity(person);
			user.persist();
			user.getContact().persist();
			
			ServiceContext sc = new ServiceContext();
			boolean habitant = false;
			long[] newOrganizationIds = new long[0];

			for (Membership m : person.getMemberships()) {
				if (m.getGroup() instanceof Flat) {
					newOrganizationIds = ArrayUtils.add(newOrganizationIds, m.getGroup().getId());
				}

				if (m.getRole().getType() == GroupType.FLAT || m.getRole() == Role.EMPLOYEE) {
					habitant = true;
				}
			}

			UserLocalServiceUtil.updateOrganizations(user.getUserId(), newOrganizationIds, sc);

			/* Visitantes, convidados e terceirizados devem ter suas contas travadas para n�o acessarem o site */
			if (!habitant) {
				UserLocalServiceUtil.updateLockout(user, true);
			} else {
				UserLocalServiceUtil.sendPassword(user.getCompanyId(), user.getEmailAddress(), StringUtils.EMPTY, 
												  StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, sc);
			}

		} catch (Exception e) {
			
		}
		
		return null;
	}
	
	public Person save(Person person) throws PersistenceException {
		try {
			User user;
			Person p;

			if (person.getId() == 0) {
				User creatorUser = helper.getUser();
				Calendar c = Calendar.getInstance();
				c.setTime(person.getBirthday());

				int birthdayDay = c.get(Calendar.DAY_OF_MONTH);
				int birthdayMonth = c.get(Calendar.MONTH);
				int birthdayYear = c.get(Calendar.YEAR);

				boolean isMale = person.getGender().equals(Gender.MALE);

				long[] flatIds = new long[person.getFlats().size()];

				for (int i = 0; person.getFlats().iterator().hasNext(); i++) {
					flatIds[i] = person.getFlats().iterator().next().getId();
				}

				user = UserLocalServiceUtil.addUser(creatorUser.getUserId(), creatorUser.getCompanyId(), true, 
											 		StringUtils.EMPTY, StringUtils.EMPTY, true, StringUtils.EMPTY, 
											 		person.getEmailAddress(), 0, StringUtils.EMPTY, creatorUser.getLocale(), 
											 		person.getFirstName(), StringUtils.EMPTY, person.getLastName(), 0, 0, 
											 		isMale, birthdayMonth, birthdayDay, birthdayYear, 
											 		StringUtils.EMPTY, creatorUser.getGroupIds(), 
											 		flatIds, creatorUser.getRoleIds(), 
											 		new long[] {GUEST_ROLE_ID}, true, new ServiceContext());

				p = getModel(user);
			} else {
				p = person;
				user = getEntity(person);
				user.persist();
				user.getContact().persist();
			}

			//user.getExpandoBridge().setAttribute(HOME, person.getHome().getId());
			
			if (person.getPicture() != null && person.getPicture().getId() == 0) {
				File file = new File(new URL(person.getPicture().getPath()).toURI());
				UserLocalServiceUtil.updatePortrait(person.getId(),	FileUtils.readFileToByteArray(file));
			}

			return p;
		} catch (Exception e) {
			throw new PersistenceException(e, "");
		}
	}

	public void delete(Person person) {
		try {
			if (person.getId() != 0) {
				UserLocalServiceUtil.updateStatus(person.getId(), WorkflowConstants.STATUS_INACTIVE);
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Person> findPeople(Group group) throws PersistenceException {
		try {
			List<User> users;

			if (group instanceof Flat) {
				users = UserLocalServiceUtil.getOrganizationUsers(group.getId());
			} else {
				users = new ArrayList<User>();
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
	public Person getModel(User user) throws PersistenceException {
		Person person = super.getModel(user);

		try {
			if (!CollectionUtils.isEmpty(person.getFlats())) {
				Flat home = person.getFlats().get(0);
				if (person.getFlats().size() > 1) {
					Long id = (Long) user.getExpandoBridge().getAttribute(HOME);
					if (id > 0) {
						home = new Flat();
						home.setId(id);
						int index = person.getFlats().indexOf(home);
						if (index > -1) {
							home = person.getFlats().get(index);
						}
					}
				}

				person.setHome(home);
			}

			person.setStatus(PersonStatus.parseStatus(user.getStatus()));

			person.setPicture(user.getPortraitURL(helper.getThemeDisplay()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return person;
	}
	
	public Person findById(Object id) throws PersistenceException {
		try {
			return getModel(UserLocalServiceUtil.getUser((Long) id));
		} catch (Exception e) {
			throw new PersistenceException("");
		}
	}

	public List<Person> findAll() throws PersistenceException {
		try {
			List<User> users = UserLocalServiceUtil.getUsers(-1, -1);
			
			
			return getModels(users);
		} catch (Exception e) {
			throw new PersistenceException("");
		}
	}

	public boolean hasPermission(Person person, Permission permission) {
		try {
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(helper.getUser());

			if (permission == Permission.UPDATE_PERSON) {
				return UserPermissionUtil.contains(permissionChecker, person.getId(), ActionKeys.UPDATE);
			} else if (permission == Permission.ADD_USER) { 
				return UserPermissionUtil.contains(permissionChecker, person.getId(), ActionKeys.ADD_USER);
			} else {
				throw new br.com.abware.jcondo.exception.SystemException("Permission not supported");
			}
		} catch (Exception e) {
			throw new br.com.abware.jcondo.exception.SystemException("Permission not supported");
		}
	}

}

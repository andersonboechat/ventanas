package br.com.atilo.jcondo.core.persistence.manager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonStatus;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.PersistenceException;
import br.com.atilo.jcondo.commons.BeanUtils;
import br.com.atilo.jcondo.core.persistence.entity.MembershipEntity;
import br.com.atilo.jcondo.core.persistence.entity.PersonEntity;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.comparator.UserFirstNameComparator;

public class PersonManagerImpl extends JCondoManager<PersonEntity, Person> {

	private String getPath(long imageId) {
		return helper.getPortalURL() + helper.getThemeDisplay().getPathImage() + "/user_male_portrait?img_id=" + imageId; 
	}

	@Override
	protected Class<PersonEntity> getEntityClass() {
		return PersonEntity.class;
	}

	@Override
	protected Class<Person> getModelClass() {
		return Person.class;
	}

	@Override
	protected PersonEntity getEntity(Person model) throws Exception {
		try {
			PersonEntity person = super.getEntity(model);

			List<MembershipEntity> memberships = new ArrayList<MembershipEntity>();
			for (Membership membership : model.getMemberships()) {
				MembershipEntity m = new MembershipEntity(person);
				BeanUtils.copyProperties(m, membership);
				m.setUpdateDate(new Date());
				m.setUpdateUser(helper.getUserId());
				memberships.add(m);
			}
			person.setMemberships(memberships);

			return person;
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.get.entity.fail");
		}
	}
	
	@Override
	protected Person getModel(PersonEntity entity) throws PersistenceException {
		try {
			if (entity == null) {
				return null;
			}

			Person person = super.getModel(entity);

			List<Membership> memberships = new ArrayList<Membership>();
			for (MembershipEntity membership : entity.getMemberships()) {
				Membership m = new Membership();
				BeanUtils.copyProperties(m, membership);
				memberships.add(m);
			}
			person.setMemberships(memberships);

			User user = UserLocalServiceUtil.getUser(entity.getUserId());

			person.setFullName(user.getFullName());
			person.setFirstName(user.getFirstName());
			person.setLastName(user.getLastName());
			person.setEmailAddress(user.getEmailAddress().endsWith("no.ventanasresidencial.com.br") ? null : user.getEmailAddress());
			person.setGender(user.isMale() ? Gender.MALE : Gender.FEMALE);
			person.setStatus(user.getStatus() == WorkflowConstants.STATUS_APPROVED ? PersonStatus.ACTIVE : PersonStatus.INACTIVE);
			person.setPicture(new Image(user.getPortraitId(), 0, getPath(user.getPortraitId()), null, null));
			person.setBirthday(user.getBirthday());

			return person;
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.get.model.failed");
		}
	}

	public Person save(Person person) throws PersistenceException {
		try {	
			User user = null;
			
			try {
				user = UserLocalServiceUtil.getUserById(person.getUserId());
			} catch (NoSuchUserException e) {
			}

			boolean isMale = person.getGender().equals(Gender.MALE);
			Calendar calendar = Calendar.getInstance();

			if (user == null) {
				calendar.setTime(person.getBirthday() == null ? new Date() : person.getBirthday());
				user = UserLocalServiceUtil.addUser(helper.getUser().getUserId(), helper.getCompanyId(), true, 
											 		StringUtils.EMPTY, StringUtils.EMPTY, true, StringUtils.EMPTY, 
											 		person.getEmailAddress(), 0, StringUtils.EMPTY, helper.getUser().getLocale(), 
											 		person.getFirstName(), StringUtils.EMPTY, person.getLastName(), 0, 0, 
											 		isMale, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 
											 		calendar.get(Calendar.YEAR), StringUtils.EMPTY, null, null, null, null, 
											 		false, new ServiceContext());
				person.setUserId(user.getUserId());
			} else {
				calendar.setTime(person.getBirthday() == null ? user.getBirthday() : person.getBirthday());
				String emailAddress = StringUtils.isEmpty(person.getEmailAddress()) ? user.getEmailAddress() : person.getEmailAddress();
				user = UserLocalServiceUtil.updateUser(person.getUserId(), user.getPassword(), StringUtils.EMPTY, StringUtils.EMPTY, false, 
													   user.getReminderQueryQuestion(), user.getReminderQueryAnswer(), user.getScreenName(), 
													   emailAddress, user.getFacebookId(), user.getOpenId(), user.getLanguageId(), 
													   user.getTimeZoneId(), user.getGreeting(), user.getComments(), person.getFirstName(), 
													   StringUtils.EMPTY, person.getLastName(), 0, 0, isMale, calendar.get(Calendar.MONTH), 
													   calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR), StringUtils.EMPTY, StringUtils.EMPTY, 
													   StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, 
													   StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, user.getJobTitle(), null, null, null, 
													   null, null, new ServiceContext());
			}

			try {
				if(StringUtils.isEmpty(person.getPicture().getPath())) {
					if (person.getPicture().getId() > 0) {
						UserLocalServiceUtil.deletePortrait(person.getUserId());
					}
				} else if (!person.getPicture().getPath().equals(getPath(person.getPicture().getId()))) {
					user = UserLocalServiceUtil.updatePortrait(person.getUserId(), 
															   IOUtils.toByteArray(new URL(person.getPicture().getPath()).openStream()));
					new File(new URL(person.getPicture().getPath()).getPath()).delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return super.save(person);
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.save.fail");
		}
	}

	public void delete(Person person) throws PersistenceException {
		try {
			UserLocalServiceUtil.updateStatus(person.getUserId(), WorkflowConstants.STATUS_INACTIVE);
			person.setStatus(PersonStatus.INACTIVE);
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.delete.fail");
		}
	}
	
	public void lock(Person person) throws Exception {
		try {
			UserLocalServiceUtil.updateLockoutById(person.getUserId(), true);
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.lock.fail");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Person> findPeople(Domain domain) throws Exception {
		String key = generateKey();
		String queryString = "SELECT p FROM PersonEntity p JOIN p.memberships m WHERE m.domain.id = :id";

		try {
			openManager(key);
			Query query = em.createQuery(queryString);
			query.setParameter("id", domain.getId());
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Person>();
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.find.people.fail");
		} finally {
			closeManager(key);
		}
	}

	public Person findPerson() throws Exception {
		return findPerson(helper.getUser().getUserId());
	}
	
	public Person findPerson(long userId) throws Exception {
		String key = generateKey();
		String queryString = "FROM PersonEntity WHERE userId = :userId";

		try {
			openManager(key);
			Query query = em.createQuery(queryString);
			query.setParameter("userId", userId);
			return getModel((PersonEntity) query.getSingleResult());
		} catch (NoResultException e) {
			return null;
		} finally {
			closeManager(key);
		}
	}

	public Person findPerson(String identity) throws PersistenceException {
		String key = generateKey();
		String queryString = "FROM PersonEntity WHERE identity = :identity AND identity IS NOT NULL";

		try {
			openManager(key);
			Query query = em.createQuery(queryString);
			query.setParameter("identity", identity);
			return getModel((PersonEntity) query.getSingleResult());
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.find.person.fail");
		} finally {
			closeManager(key);
		}
	}

	public List<Person> findPeople(String name) throws Exception {
		List<Person> people = new ArrayList<Person>();
		List<User> users;
		users = UserLocalServiceUtil.search(helper.getCompanyId(), name, StringUtils.EMPTY, 
											name, StringUtils.EMPTY, StringUtils.EMPTY, 
											WorkflowConstants.STATUS_APPROVED, null, false, 
											-1, -1, new UserFirstNameComparator());

		for (User user : users) {
			people.add(findPerson(user.getUserId()));
		}

		return people;
	}
	
	@SuppressWarnings("unchecked")
	public List<Person> findPeopleByType(Domain domain, PersonType type) throws Exception {
		String key = generateKey();
		String queryString = "SELECT p FROM PersonEntity p JOIN p.memberships m WHERE m.domain.id = :id AND m.type = :type";

		try {
			openManager(key);
			Query query = em.createQuery(queryString);
			query.setParameter("id", domain.getId());
			query.setParameter("type", type);
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Person>();
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.find.people.by.type.fail");
		} finally {
			closeManager(key);
		}
	}

	public void removeDomain(Person person, Domain domain) throws Exception {
		try {
			UserLocalServiceUtil.unsetGroupUsers(domain.getRelatedId(), new long[] {person.getUserId()}, null);
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.remove.domain.fail");
		}
	}

	public void addDomain(Person person, Domain domain) throws Exception {
		try {
			UserLocalServiceUtil.addGroupUsers(domain.getRelatedId(), new long[] {person.getUserId()});
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.add.domain.fail");
		}
	}

	public boolean authenticate(Person person, String password) throws Exception {
		try {
			return UserLocalServiceUtil.authenticateByUserId(helper.getCompanyId(), 
															 person.getUserId(), 
															 password, null, null, null) == 1 ? true : false;
		} catch (Exception e) {
			throw new PersistenceException(e, "psn.mgr.authenticate.fail");
		}
	}

}

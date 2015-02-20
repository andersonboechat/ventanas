package br.com.abware.accountmgm.persistence.manager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.accountmgm.persistence.entity.MembershipEntity;
import br.com.abware.accountmgm.persistence.entity.PersonEntity;
import br.com.abware.accountmgm.util.BeanUtils;
import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonStatus;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.PersistenceException;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;

public class NewPersonManagerImpl extends JCondoManager<PersonEntity, Person> {

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

	public Person save(Person person) throws PersistenceException {
		try {	
			User user = null;
			
			try {
				user = UserLocalServiceUtil.getUserById(person.getUserId());
			} catch (NoSuchUserException e) {
			}

			boolean isMale = person.getGender().equals(Gender.MALE);

			if (user == null) {
				user = UserLocalServiceUtil.addUser(helper.getUser().getUserId(), helper.getCompanyId(), true, 
											 		StringUtils.EMPTY, StringUtils.EMPTY, true, StringUtils.EMPTY, 
											 		person.getEmailAddress(), 0, StringUtils.EMPTY, helper.getUser().getLocale(), 
											 		person.getFirstName(), StringUtils.EMPTY, person.getLastName(), 0, 0, 
											 		isMale, 1, 1, 1900, StringUtils.EMPTY, null, null, null, null, false, new ServiceContext());
				person.setUserId(user.getUserId());
			} else {
				user = UserLocalServiceUtil.updateUser(person.getUserId(), user.getPassword(), StringUtils.EMPTY, StringUtils.EMPTY, false, 
													   user.getReminderQueryQuestion(), user.getReminderQueryAnswer(), user.getScreenName(), 
													   person.getEmailAddress(), user.getFacebookId(), user.getOpenId(), user.getLanguageId(), 
													   user.getTimeZoneId(), user.getGreeting(), user.getComments(), person.getFirstName(), 
													   StringUtils.EMPTY, person.getLastName(), 0, 0, isMale, 1, 1, 1900, StringUtils.EMPTY, 
													   StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, 
													   StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, 
													   StringUtils.EMPTY, user.getJobTitle(), null, null, null, null, null, new ServiceContext());
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
			throw new PersistenceException("");
		}
	}

	public void delete(Person person) throws PersistenceException {
		try {
			UserLocalServiceUtil.updateStatus(person.getUserId(), WorkflowConstants.STATUS_INACTIVE);
			person.setStatus(PersonStatus.INACTIVE);
		} catch (NoSuchUserException e) {
			throw new PersistenceException("usuario nao cadastrado");
		} catch (Exception e) {
			throw new PersistenceException("");
		}
	}
	
	public void lock(Person person) throws Exception {
		try {
			UserLocalServiceUtil.updateLockoutById(person.getUserId(), true);
		} catch (NoSuchUserException e) {
			throw new PersistenceException("usuario nao cadastrado");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Person> findPeople(Domain domain) throws Exception {
		long[] userIds = UserLocalServiceUtil.getGroupUserIds(domain.getRelatedId());
		if (userIds.length > 0) {
			String key = generateKey();
			String queryString = "FROM PersonEntity WHERE userId in :userIds";
	
			try {
				openManager(key);
				Query query = em.createQuery(queryString);
				query.setParameter("userIds", Arrays.asList(ArrayUtils.toObject(userIds)));
				return getModels(query.getResultList());
			} finally {
				closeManager(key);
			}
		}
		
		return new ArrayList<Person>();
	}

	public Person getPerson() throws PersistenceException {
		String key = generateKey();
		String queryString = "FROM PersonEntity WHERE userId = :userId";

		try {
			openManager(key);
			Query query = em.createQuery(queryString);
			query.setParameter("userId", helper.getUser().getUserId());
			return getModel((PersonEntity) query.getSingleResult());
		} finally {
			closeManager(key);
		}
	}
	
	@Override
	protected PersonEntity getEntity(Person model) throws Exception {
		PersonEntity person = super.getEntity(model);

		List<MembershipEntity> memberships = new ArrayList<MembershipEntity>();
		for (Membership membership : model.getMemberships()) {
			MembershipEntity m = new MembershipEntity(model.getId());
			BeanUtils.copyProperties(m, membership);
			m.setUpdateDate(new Date());
			m.setUpdateUser(helper.getUserId());
			memberships.add(m);
		}
		person.setMemberships(memberships);

		return person;
	}

	@Override
	protected Person getModel(PersonEntity entity) throws PersistenceException {
		try {
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
			person.setEmailAddress(user.getEmailAddress());
			person.setGender(user.isMale() ? Gender.MALE : Gender.FEMALE);
			person.setStatus(user.getStatus() == WorkflowConstants.STATUS_APPROVED ? PersonStatus.ACTIVE : PersonStatus.INACTIVE);
			person.setPicture(new Image(user.getPortraitId(), getPath(user.getPortraitId()), null, null));
			//person.setBirthday(new Date());

			return person;
		} catch (Exception e) {
			throw new PersistenceException(e, "");
		}
	}

	public void removeDomain(Person person, Domain domain) throws Exception {
		UserLocalServiceUtil.unsetGroupUsers(domain.getRelatedId(), new long[] {person.getUserId()}, null);
	}

	public void addDomain(Person person, Domain domain) throws Exception {
		UserLocalServiceUtil.addGroupUsers(domain.getRelatedId(), new long[] {person.getUserId()});
	}

	@SuppressWarnings("unchecked")
	public List<Person> findPeopleByType(Domain domain, PersonType type) throws Exception {
		String key = generateKey();
		String queryString = "FROM PersonEntity p JOIN p.memberships m WHERE m.domain.id = :id AND m.type = :type";

		try {
			openManager(key);
			Query query = em.createQuery(queryString);
			query.setParameter("id", domain.getId());
			query.setParameter("type", type);
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Person>();
		} finally {
			closeManager(key);
		}
	}

}

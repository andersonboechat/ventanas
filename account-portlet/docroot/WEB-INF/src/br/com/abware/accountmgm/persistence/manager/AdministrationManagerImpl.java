package br.com.abware.accountmgm.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;

import br.com.abware.accountmgm.persistence.entity.AdministrationEntity;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Person;

public class AdministrationManagerImpl extends JCondoManager<AdministrationEntity, Administration> {

	public static final Administration ADMINISTRATION = new Administration();

	static {
	}
	
	
	@Override
	protected Class<Administration> getModelClass() {
		return Administration.class;
	}

	@Override
	protected Class<AdministrationEntity> getEntityClass() {
		return AdministrationEntity.class;
	}

	public Administration save(Administration admin) throws Exception {
		Administration adm = super.save(admin);

		if (admin.getRelatedId() == 0) {
			Group group = GroupLocalServiceUtil.addGroup(helper.getUserId(), Group.class.getName(), 0, admin.getName(), 
														 StringUtils.EMPTY, GroupConstants.TYPE_SITE_PRIVATE, 
														 "/" + admin.getName(), true, true, null);
			ResourceLocalServiceUtil.addResources(helper.getCompanyId(), group.getGroupId(), helper.getUserId(), 
												  Administration.class.getName(), adm.getId(), false, true, false);
			adm.setRelatedId(group.getGroupId());
			adm = super.save(adm);
		}

		return adm;
	}

	public void delete(Administration flat) throws Exception {
		super.delete(flat);
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Administration.class.getName(), ResourceConstants.SCOPE_COMPANY, helper.getCompanyId());
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Administration.class.getName(), ResourceConstants.SCOPE_GROUP, flat.getRelatedId());
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Administration.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL, flat.getId());
		GroupLocalServiceUtil.deleteGroup(flat.getRelatedId());
	}

	public Administration findByName(String name) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM AdministrationEntity a WHERE a.name = :name";
			Query query = em.createQuery(queryString);
			query.setParameter("name", name);
			return getModel((AdministrationEntity) query.getSingleResult());
		} catch (NoResultException e) {
			return null;
		} finally {
			closeManager(key);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Administration> findByPerson(Person person) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "SELECT a FROM AdministrationEntity a JOIN a.people p WHERE p.id = :id";
			Query query = em.createQuery(queryString);
			query.setParameter("id", person.getId());
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Administration>();
		} finally {
			closeManager(key);
		}
	}
}

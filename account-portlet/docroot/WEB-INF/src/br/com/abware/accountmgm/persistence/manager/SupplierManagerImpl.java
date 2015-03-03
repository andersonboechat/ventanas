package br.com.abware.accountmgm.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;

import br.com.abware.accountmgm.persistence.entity.AdministrationEntity;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;

public class SupplierManagerImpl extends JCondoManager<AdministrationEntity, Supplier> {

	@Override
	protected Class<Supplier> getModelClass() {
		return Supplier.class;
	}

	@Override
	protected Class<AdministrationEntity> getEntityClass() {
		return AdministrationEntity.class;
	}

	public Supplier save(Supplier supplier, Domain domain) throws Exception {
		Supplier sup = super.save(supplier);

		if (supplier.getRelatedId() == 0) {
			Organization org = OrganizationLocalServiceUtil.addOrganization(helper.getUserId(), domain.getRelatedId(), supplier.getName(), "supplier", 
																			true, 0, 0, ListTypeConstants.ORGANIZATION_STATUS_DEFAULT, null, false, null);
			sup.setRelatedId(org.getGroupId());
			sup = super.save(sup);
		}

		return sup;
	}

	public void delete(Supplier flat) throws Exception {
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

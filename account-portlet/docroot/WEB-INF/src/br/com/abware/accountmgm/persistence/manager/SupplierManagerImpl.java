package br.com.abware.accountmgm.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

import br.com.abware.accountmgm.persistence.entity.AdministrationEntity;
import br.com.abware.jcondo.core.SupplierStatus;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;

public class SupplierManagerImpl extends JCondoManager<AdministrationEntity, Supplier> {

	private static AdministrationManagerImpl adminManager = new AdministrationManagerImpl();	
	
	@Override
	protected Class<Supplier> getModelClass() {
		return Supplier.class;
	}

	@Override
	protected Class<AdministrationEntity> getEntityClass() {
		return AdministrationEntity.class;
	}
	
	public void assignTo(Supplier supplier, Domain domain) throws Exception {
		Supplier sup = findById(supplier.getId());

		if (sup == null || sup.getRelatedId() > 0) {
			
		}
		
		if (sup == null || sup.getRelatedId() > 0 domain.getRelatedId() <= 0) {
			throw new Exception("dominio invalido");
		}

		Organization org = OrganizationLocalServiceUtil.addOrganization(helper.getUserId(), domain.getRelatedId(), supplier.getName(), "supplier", 
																		true, 0, 0, ListTypeConstants.ORGANIZATION_STATUS_DEFAULT, null, false, null);

		supplier.setRelatedId(org.getOrganizationId());
		super.save(supplier);
	}

	public void removeFrom(Supplier supplier, Domain domain) throws Exception {
		Organization bin = OrganizationLocalServiceUtil.getOrganization(helper.getCompanyId(), "bin");
		Organization org = OrganizationLocalServiceUtil.getOrganization(supplier.getRelatedId());
		org.setParentOrganizationId(bin.getOrganizationId());
		supplier.setStatus(SupplierStatus.DISABLED);
		super.save(supplier);
	}

	public Supplier save(Supplier supplier, Domain domain) throws Exception {
		if (supplier.getRelatedId() <= 0 && domain.getRelatedId() > 0) {
			Organization org = OrganizationLocalServiceUtil.addOrganization(helper.getUserId(), domain.getRelatedId(), supplier.getName(), "supplier", 
																			true, 0, 0, ListTypeConstants.ORGANIZATION_STATUS_DEFAULT, null, false, null);
			supplier.setRelatedId(org.getOrganizationId());
		}

		return super.save(supplier);
	}	

	public Supplier save(Supplier supplier) throws Exception {
		return super.save(supplier);
	}

	public void delete(Supplier supplier) throws Exception {
		supplier.setStatus(SupplierStatus.DISABLED);
		super.save(supplier);
	}

	public Supplier findByName(String name) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM SupplierEntity a WHERE a.name = :name";
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
	public List<Supplier> findByPerson(Person person) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "SELECT a FROM SupplierEntity a JOIN a.people p WHERE p.id = :id";
			Query query = em.createQuery(queryString);
			query.setParameter("id", person.getId());
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Supplier>();
		} finally {
			closeManager(key);
		}
	}
}

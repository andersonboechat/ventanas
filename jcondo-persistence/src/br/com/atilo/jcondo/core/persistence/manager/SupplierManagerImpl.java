package br.com.atilo.jcondo.core.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.model.Organization;
import com.liferay.portal.service.OrganizationLocalServiceUtil;

import br.com.abware.jcondo.core.SupplierStatus;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.atilo.jcondo.core.persistence.entity.SupplierEntity;

public class SupplierManagerImpl extends JCondoManager<SupplierEntity, Supplier> {

	private static AdministrationManagerImpl adminManager = new AdministrationManagerImpl();	
	
	@Override
	protected Class<Supplier> getModelClass() {
		return Supplier.class;
	}

	@Override
	protected Class<SupplierEntity> getEntityClass() {
		return SupplierEntity.class;
	}
	
	public Supplier save(Supplier supplier) throws Exception {
		if (supplier.getParent() == null || supplier.getParent().getRelatedId() <= 0) {
			throw new Exception("dominio invalido");
		}

		Supplier sup = findById(supplier.getId());

		if (sup == null) {
			Organization org = OrganizationLocalServiceUtil.addOrganization(helper.getUserId(), 
																			supplier.getParent().getRelatedId(), supplier.getName() + ":" + supplier.getParent().getRelatedId(), 
																			"supplier", true, 0, 0, ListTypeConstants.ORGANIZATION_STATUS_DEFAULT, null, false, null);
			supplier.setRelatedId(org.getOrganizationId());
		}

		return super.save(supplier);
	}	

	public void delete(Supplier supplier) throws Exception {
		supplier.setStatus(SupplierStatus.DISABLED);
		super.save(supplier);
	}

	@SuppressWarnings("unchecked")
	public List<Supplier> findByDomain(Domain domain) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM SupplierEntity s WHERE s.parent.id = :id";
			Query query = em.createQuery(queryString);
			query.setParameter("id", domain.getId());
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Supplier>();
		} finally {
			closeManager(key);
		}
	}

	public Supplier findByName(String name) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM SupplierEntity s WHERE s.name = :name";
			Query query = em.createQuery(queryString);
			query.setParameter("name", name);
			return getModel((SupplierEntity) query.getSingleResult());
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
			String queryString = "SELECT a FROM SupplierEntity s JOIN s.people s WHERE s.id = :id";
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

package br.com.atilo.jcondo.core.persistence.manager;


import java.util.ArrayList;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.liferay.portal.model.ListTypeConstants;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;

import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.PetType;

import br.com.atilo.jcondo.core.persistence.entity.FlatEntity;

public class FlatManagerImpl extends JCondoManager<FlatEntity, Flat> {

	private AdministrationManagerImpl adminManager = new AdministrationManagerImpl();
	
	private PersonManagerImpl personManager = new PersonManagerImpl();

	@Override
	protected Class<Flat> getModelClass() {
		return Flat.class;
	}

	@Override
	protected Class<FlatEntity> getEntityClass() {
		return FlatEntity.class;
	}
	
	@Override
	protected Flat getModel(FlatEntity entity) throws Exception {
		Flat flat = super.getModel(entity);
		flat.setPetTypes(new ArrayList<PetType>(flat.getPetTypes()));
		if (flat.getPerson() != null) {
			flat.setPerson(personManager.findById(flat.getPerson().getId()));
		}
		return flat;
	}

	public Flat save(Flat flat) throws Exception {
		Flat f = super.save(flat);

		if (flat.getRelatedId() == 0) {
			String name = flat.getBlock() + "/" + StringUtils.leftPad(String.valueOf(flat.getNumber()), 4, "0");
			Administration administration = adminManager.findByName("Security");
			Organization org = OrganizationLocalServiceUtil.addOrganization(helper.getUserId(), administration.getRelatedId(), name, "flat", 
																			true, 0, 0, ListTypeConstants.ORGANIZATION_STATUS_DEFAULT, null, false, null);
			f.setRelatedId(org.getOrganizationId());
			f = super.save(f);
		}

		return f;
	}

	public void delete(Flat flat) throws Exception {
		super.delete(flat);
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Flat.class.getName(), ResourceConstants.SCOPE_COMPANY, helper.getCompanyId());
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Flat.class.getName(), ResourceConstants.SCOPE_GROUP, flat.getRelatedId());
		ResourceLocalServiceUtil.deleteResource(helper.getCompanyId(), Flat.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL, flat.getId());
		OrganizationLocalServiceUtil.deleteOrganization(flat.getRelatedId());
	}

	@SuppressWarnings("unchecked")
	public List<Flat> findByPerson(Person person) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "SELECT f FROM FlatEntity f JOIN f.memberships m WHERE m.person.id = :id ORDER BY f.block, f.number";
			Query query = em.createQuery(queryString);
			query.setParameter("id", person.getId());
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Flat>();
		} finally {
			closeManager(key);
		}
	}

	public Flat findByNumberAndBlock(int number, int block) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM FlatEntity WHERE number = :number and block = :block";
			Query query = em.createQuery(queryString);
			query.setParameter("number", number);
			query.setParameter("block", block);
			return getModel((FlatEntity) query.getSingleResult());
		} catch (NoResultException e) {
			return null;
		} finally {
			closeManager(key);
		}
	}

	public Flat findByFolder(long folderId) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM FlatEntity WHERE folderId = :id";
			Query query = em.createQuery(queryString);
			query.setParameter("id", folderId);
			return getModel((FlatEntity) query.getSingleResult());
		} catch (NoResultException e) {
			return null;
		} finally {
			closeManager(key);
		}
	}
}

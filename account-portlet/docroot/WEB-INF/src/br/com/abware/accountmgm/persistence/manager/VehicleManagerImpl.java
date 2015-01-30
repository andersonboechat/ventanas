package br.com.abware.accountmgm.persistence.manager;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.liferay.portal.service.OrganizationLocalServiceUtil;

import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.persistence.entity.VehicleEntity;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.exception.PersistenceException;

public class VehicleManagerImpl extends JCondoManager<VehicleEntity, Vehicle>{

	@Override	
	protected Class<Vehicle> getModelClass() {
		return Vehicle.class;
	}

	@Override
	protected Class<VehicleEntity> getEntityClass() {
		return VehicleEntity.class;
	}

	@Override
	protected VehicleEntity getEntity(Vehicle model) throws Exception {
		VehicleEntity vehicle = super.getEntity(model);
		vehicle.setDomainId(model.getDomain() != null ? model.getDomain().getId() : 0);
		return vehicle;
	}

	@Override
	protected Vehicle getModel(VehicleEntity entity) throws Exception {
		Vehicle vehicle = super.getModel(entity);
		if (entity.getDomainId() != 0) {
			String name = OrganizationLocalServiceUtil.getOrganization(entity.getDomainId()).getName();
			vehicle.setDomain(new Flat(entity.getDomainId(), Long.parseLong(name.split("/")[0]), Long.parseLong(name.split("/")[1])));
		}
		return vehicle;
	}

	@SuppressWarnings("unchecked")
	public List<Vehicle> findVehicles(Domain domain) throws PersistenceException {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM VehicleEntity WHERE domainId = :domainId";
			Query query = em.createQuery(queryString);
			query.setParameter("domainId", domain.getId());
			return getModels(query.getResultList());
		} finally {
			closeManager(key);
		}
	}

	public Vehicle findByLicense(String license) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM VehicleEntity WHERE license = :license";
			Query query = em.createQuery(queryString);
			query.setParameter("license", license);
			return getModel((VehicleEntity) query.getSingleResult());
		} catch (NoResultException e) {
			return null;
		} finally {
			closeManager(key);
		}
	}

}

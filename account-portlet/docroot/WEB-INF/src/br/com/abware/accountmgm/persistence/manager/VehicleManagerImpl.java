package br.com.abware.accountmgm.persistence.manager;

import java.util.List;

import javax.persistence.Query;

import com.liferay.portal.service.OrganizationLocalServiceUtil;

import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.persistence.entity.VehicleEntity;
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
		vehicle.setFlatId(model.getFlat().getId());
		return vehicle;
	}

	@Override
	protected Vehicle getModel(VehicleEntity entity) throws Exception {
		Vehicle vehicle = super.getModel(entity);
		String name = OrganizationLocalServiceUtil.getOrganization(entity.getFlatId()).getName();
		vehicle.setFlat(new Flat(entity.getFlatId(), Long.parseLong(name.split("/")[0]), Long.parseLong(name.split("/")[1])));
		return vehicle;
	}

	@SuppressWarnings("unchecked")
	public List<Vehicle> findVehicles(Flat flat) throws PersistenceException {
		try {
			String queryString = "FROM VehicleEntity WHERE flatId = :flatId";
			openManager("VehicleManager.findVehicles");
			Query query = em.createQuery(queryString);
			query.setParameter("flatId", flat.getId());
			return getModels(query.getResultList());
		} finally {
			closeManager("VehicleManager.findVehicles");
		}
	}

	public Vehicle findByLicense(String license) throws Exception {
		try {
			String queryString = "FROM VehicleEntity WHERE license = :license";
			openManager("VehicleManager.findByLicense");
			Query query = em.createQuery(queryString);
			query.setParameter("license", license);
			return getModel((VehicleEntity) query.getSingleResult());
		} finally {
			closeManager("VehicleManager.findByLicense");
		}
	}

}

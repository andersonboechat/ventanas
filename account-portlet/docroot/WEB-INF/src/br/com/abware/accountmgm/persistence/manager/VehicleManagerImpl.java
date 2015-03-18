package br.com.abware.accountmgm.persistence.manager;

import java.net.URL;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.service.ImageLocalServiceUtil;

import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.model.VehicleType;
import br.com.abware.accountmgm.persistence.entity.VehicleEntity;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Image;
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
		vehicle.setImageId(model.getImage().getId());
		return vehicle;
	}

	private String getPath(long imageId) {
		return imageId == 0 ? null : helper.getPortalURL() + helper.getThemeDisplay().getPathImage() + "/portrait?img_id=" + imageId;
	}
	
	@Override
	protected Vehicle getModel(VehicleEntity entity) throws Exception {
		Vehicle vehicle = super.getModel(entity);

		String path = getPath(entity.getImageId());
		vehicle.setImage(new Image(entity.getImageId(), path, null, null));

		return vehicle;
	}

	@Override
	public Vehicle save(Vehicle vehicle) throws Exception {
		vehicle.setLicense(vehicle.getLicense().toUpperCase());

		if(StringUtils.isEmpty(vehicle.getImage().getPath())) {
			if (vehicle.getImage().getId() > 0) {
				ImageLocalServiceUtil.deleteImage(vehicle.getImage().getId());
			}
		} else if (!vehicle.getImage().getPath().equals(getPath(vehicle.getImage().getId()))) {
			URL url = new URL(vehicle.getImage().getPath());
			long imageId = vehicle.getImage().getId() == 0 ? CounterLocalServiceUtil.increment() : vehicle.getImage().getId();
			ImageLocalServiceUtil.updateImage(imageId, url.openStream());
			vehicle.getImage().setId(imageId);
		}

		return super.save(vehicle);
	}
	
	@SuppressWarnings("unchecked")
	public List<Vehicle> findVehicles(Domain domain) throws PersistenceException {
		String key = generateKey();
		try {
			openManager(key);
			//String queryString = "FROM VehicleEntity WHERE domainId = :domainId ORDER BY license";
			String queryString = "FROM VehicleEntity WHERE domain IS NOT NULL AND domain.id = :domainId ORDER BY license";
			Query query = em.createQuery(queryString);
			query.setParameter("domainId", domain.getId());
			return getModels(query.getResultList());
		} finally {
			closeManager(key);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Vehicle> findVehicles(Domain domain, VehicleType type) throws PersistenceException {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM VehicleEntity WHERE type := type AND domain IS NOT NULL AND domainId = :domainId ORDER BY license";
			Query query = em.createQuery(queryString);
			query.setParameter("type", type);
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

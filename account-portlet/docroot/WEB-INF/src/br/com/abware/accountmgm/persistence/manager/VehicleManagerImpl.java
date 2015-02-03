package br.com.abware.accountmgm.persistence.manager;

import java.net.URL;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.service.ImageLocalServiceUtil;

import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.persistence.entity.VehicleEntity;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.exception.PersistenceException;

public class VehicleManagerImpl extends JCondoManager<VehicleEntity, Vehicle>{

	private FlatManagerImpl flatManager = new FlatManagerImpl();
	
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
		vehicle.setDomainId(model.getDomain().getId());
		vehicle.setImageId(model.getImage().getId());
		return vehicle;
	}

	private String getPath(long imageId) {
		return imageId == 0 ? null : helper.getPortalURL() + helper.getThemeDisplay().getPathImage() + "/portrait?img_id=" + imageId;
	}
	
	@Override
	protected Vehicle getModel(VehicleEntity entity) throws Exception {
		Vehicle vehicle = super.getModel(entity);

		if (entity.getDomainId() != 0) {
			vehicle.setDomain(flatManager.findById(entity.getDomainId()));
		} else {
			vehicle.setDomain(new Flat());
		}

		String path = getPath(entity.getImageId());
		vehicle.setImage(new Image(entity.getImageId(), path, null, null));

		return vehicle;
	}

	@Override
	public Vehicle save(Vehicle model) throws Exception {
		if(StringUtils.isEmpty(model.getImage().getPath())) {
			if (model.getImage().getId() > 0) {
				ImageLocalServiceUtil.deleteImage(model.getImage().getId());
			}
		} else if (!model.getImage().getPath().equals(getPath(model.getImage().getId()))) {
			URL url = new URL(model.getImage().getPath());
			long imageId = model.getImage().getId() == 0 ? CounterLocalServiceUtil.increment() : model.getImage().getId();
			ImageLocalServiceUtil.updateImage(imageId, url.openStream());
			model.getImage().setId(imageId);
		}

		return super.save(model);
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
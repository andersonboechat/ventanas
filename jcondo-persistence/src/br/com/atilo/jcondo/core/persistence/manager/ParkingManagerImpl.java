package br.com.atilo.jcondo.core.persistence.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Parking;
import br.com.abware.jcondo.core.model.Vehicle;
import br.com.abware.jcondo.exception.PersistenceException;
import br.com.atilo.jcondo.core.persistence.entity.ParkingEntity;

public class ParkingManagerImpl extends JCondoManager<ParkingEntity, Parking> {

	@Override
	protected Class<Parking> getModelClass() {
		return Parking.class;
	}

	@Override
	protected Class<ParkingEntity> getEntityClass() {
		return ParkingEntity.class;
	}

	public Parking findByVehicle(Vehicle vehicle) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM ParkingEntity WHERE vehicle.id = :vehicleId";
			Query query = em.createQuery(queryString);
			query.setParameter("vehicleId", vehicle.getId());
			return getModel((ParkingEntity) query.getSingleResult());
		} catch (NoResultException e) {
			return null;
		} finally {
			closeManager(key);
		}		
	}
	
	@SuppressWarnings("unchecked")
	public List<Parking> findNotOwnedParkings() throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM ParkingEntity WHERE ownerDomain IS NULL";
			Query query = em.createQuery(queryString);
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Parking>();
		} finally {
			closeManager(key);
		}
	}	

	@SuppressWarnings("unchecked")
	public List<Parking> findOwnedParkings(Domain domain) throws Exception {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM ParkingEntity WHERE ownerDomain IS NOT NULL AND ownerDomain.id = :domainId";
			Query query = em.createQuery(queryString);
			query.setParameter("domainId", domain.getId());
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Parking>();
		} finally {
			closeManager(key);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Parking> findGrantedParkings(Domain domain) throws PersistenceException {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM ParkingEntity WHERE ownerDomain IS NOT NULL AND ownerDomain.id = :domainId AND renterDomain IS NOT NULL";
			Query query = em.createQuery(queryString);
			query.setParameter("domainId", domain.getId());
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Parking>();
		} finally {
			closeManager(key);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Parking> findRentedParkings(Domain domain) throws PersistenceException {
		String key = generateKey();
		try {
			openManager(key);
			String queryString = "FROM ParkingEntity WHERE renterDomain IS NOT NULL AND renterDomain.id = :domainId";
			Query query = em.createQuery(queryString);
			query.setParameter("domainId", domain.getId());
			return getModels(query.getResultList());
		} catch (NoResultException e) {
			return new ArrayList<Parking>();
		} finally {
			closeManager(key);
		}
	}

}

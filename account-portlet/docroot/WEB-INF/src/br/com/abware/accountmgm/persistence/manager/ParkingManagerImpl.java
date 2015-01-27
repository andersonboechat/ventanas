package br.com.abware.accountmgm.persistence.manager;

import java.util.List;

import javax.persistence.Query;

import br.com.abware.accountmgm.model.Parking;
import br.com.abware.accountmgm.persistence.entity.ParkingEntity;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.exception.PersistenceException;

public class ParkingManagerImpl extends JCondoManager<ParkingEntity, Parking> {

	@Override
	protected Class<Parking> getModelClass() {
		return Parking.class;
	}

	@Override
	protected Class<ParkingEntity> getEntityClass() {
		return ParkingEntity.class;
	}

	@SuppressWarnings("unchecked")
	public List<Parking> findOwnedParkings(Domain domain) throws PersistenceException {
		try {
			String queryString = "FROM ParkingEntity WHERE domainId = :domainId";
			openManager("ParkingManager.findGrantedParkings");
			Query query = em.createQuery(queryString);
			query.setParameter("domainId", domain.getId());
			return getModels(query.getResultList());
		} finally {
			closeManager("ParkingManager.findGrantedParkings");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Parking> findGrantedParkings(Domain domain) throws PersistenceException {
		try {
			String queryString = "FROM RentedParkingEntity  WHERE domainId = :domainId";
			openManager("ParkingManager.findGrantedParkings");
			Query query = em.createQuery(queryString);
			query.setParameter("domainId", domain.getId());
			return getModels(query.getResultList());
		} finally {
			closeManager("ParkingManager.findGrantedParkings");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Parking> findRentedParkings(Domain domain) throws PersistenceException {
		try {
			String queryString = "FROM RentedParkingEntity WHERE renterDomainId = :domainId";
			openManager("ParkingManager.findRentedParkings");
			Query query = em.createQuery(queryString);
			query.setParameter("domainId", domain.getId());
			return getModels(query.getResultList());
		} finally {
			closeManager("ParkingManager.findRentedParkings");
		}
	}

}

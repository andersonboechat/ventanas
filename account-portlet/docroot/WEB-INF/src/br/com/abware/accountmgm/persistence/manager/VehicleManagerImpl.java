package br.com.abware.accountmgm.persistence.manager;

import java.util.List;

import javax.persistence.Query;

import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.persistence.entity.VehicleVO;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.exception.PersistenceException;

public class VehicleManagerImpl extends JCondoManager<VehicleVO, Vehicle>{

	protected static final long COMPANY = 10153;

	@Override	
	protected Class<Vehicle> getModelClass() {
		return Vehicle.class;
	}

	@Override
	protected Class<VehicleVO> getEntityClass() {
		return VehicleVO.class;
	}

	@SuppressWarnings("unchecked")
	public List<Vehicle> findVehicles(Flat flat) throws PersistenceException {
		String queryString = "FROM VehicleVO v WHERE v.id IN (SELECT v.id FROM v.flats f WHERE f.id = :flatId)";
		Query query = em.createQuery(queryString);
		query.setParameter("flatId", flat.getId());
		return getModels(query.getResultList());
	}
	
}

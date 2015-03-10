package br.com.abware.accountmgm.service.core;

import java.util.List;

import br.com.abware.accountmgm.model.Parking;
import br.com.abware.accountmgm.persistence.manager.ParkingManagerImpl;
import br.com.abware.accountmgm.persistence.manager.VehicleManagerImpl;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.service.BaseService;

public class ParkingServiceImpl implements BaseService<Parking> {

	private ParkingManagerImpl parkingManager = new ParkingManagerImpl();
	
	private VehicleManagerImpl vehicleManager = new VehicleManagerImpl();	

	public int getParkingAmount(Domain domain) {
		try {
			int freeParkingAmount = parkingManager.findOwnedParkings(domain).size();
			int busyParkingAmount = vehicleManager.findVehicles(domain).size();

			if (domain instanceof Flat) {
				int grantedParkingAmount = parkingManager.findGrantedParkings(domain).size();
				int rentedParkingAmount = parkingManager.findRentedParkings(domain).size();
				freeParkingAmount += rentedParkingAmount;
				busyParkingAmount += grantedParkingAmount;
			}

			return freeParkingAmount - busyParkingAmount;
		} catch (Exception e) {
			return 0;
		}
	}

	public List<Parking> getRentedParkings(Domain renterDomain) throws Exception {
		return parkingManager.findRentedParkings(renterDomain);
	}

	public List<Parking> getParkings(Domain domain) throws Exception {
		return parkingManager.findOwnedParkings(domain);
	}

	public List<Parking> getAvailableParkings() throws Exception {
		return parkingManager.findAvailableParkings();
	}
	
	public Parking register(Parking parking) throws Exception {
		return parkingManager.save(parking);
	}

	public Parking update(Parking parking) throws Exception {
		if (parking.getId() <= 0) {
			throw new Exception("vaga inexistente");
		}

		Parking p = parkingManager.findById(parking.getId());

		if (p == null) {
			throw new Exception("vaga nao encontrada");
		}

		if (parking.getOwnerDomain() != null) {
			if (p.getOwnerDomain() != null && !p.getOwnerDomain().equals(parking.getOwnerDomain())) {
				throw new Exception("vaga ja associada a outro apartamento");
			}
		}

		if (parking.getRenterDomain() != null) {
			if (p.getRenterDomain() != null && !p.getRenterDomain().equals(parking.getRenterDomain())) {
				throw new Exception("vaga ja alugada para outro apartamento");
			}
		}

		return parkingManager.save(parking);
	}

}

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

	public List<Parking> getParkings(Domain domain) throws Exception {
		List<Parking> parkings = parkingManager.findOwnedParkings(domain);
		parkings.removeAll(parkingManager.findGrantedParkings(domain));
		parkings.addAll(parkingManager.findRentedParkings(domain));
		return parkings;
	}

	public List<Parking> getAvailableParkings() throws Exception {
		return parkingManager.findAvailableParkings();
	}
	
	public Parking register(Parking parking) throws Exception {
		return parkingManager.save(parking);
	}

	public Parking update(Parking parking) throws Exception {
		if (parking.getOwnerDomain() != null) {
			Parking p = parkingManager.findById(parking.getId());
			if (p.getOwnerDomain() != null) {
				throw new Exception("vaga associada a outro dominio");
			}
		}

		return parkingManager.save(parking);
	}

}

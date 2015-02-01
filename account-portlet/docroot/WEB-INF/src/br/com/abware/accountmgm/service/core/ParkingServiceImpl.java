package br.com.abware.accountmgm.service.core;

import br.com.abware.accountmgm.model.Parking;
import br.com.abware.accountmgm.persistence.manager.ParkingManagerImpl;
import br.com.abware.accountmgm.persistence.manager.VehicleManagerImpl;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.service.BaseService;

public class ParkingServiceImpl implements BaseService<Parking> {

	private ParkingManagerImpl parkingManager = new ParkingManagerImpl();
	
	private VehicleManagerImpl vehicleManager = new VehicleManagerImpl();	

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
}

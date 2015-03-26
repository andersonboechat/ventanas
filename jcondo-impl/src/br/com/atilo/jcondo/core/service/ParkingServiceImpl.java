package br.com.atilo.jcondo.core.service;

import java.util.List;

import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Parking;
import br.com.abware.jcondo.core.service.BaseService;

import br.com.atilo.jcondo.core.persistence.manager.ParkingManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.VehicleManagerImpl;

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

	public List<Parking> getOwnedParkings(Domain domain) throws Exception {
		return parkingManager.findOwnedParkings(domain);
	}

	public List<Parking> getParkings(Domain domain) throws Exception {
		List<Parking> parkings = getOwnedParkings(domain); 
		parkings.addAll(getRentedParkings(domain));
		return parkings;
	}	

	public List<Parking> getAvailableParkings(Domain domain) throws Exception {
		List<Parking> parkings = parkingManager.findOwnedParkings(domain); 
		parkings.addAll(parkingManager.findRentedParkings(domain));
		parkings.removeAll(parkingManager.findGrantedParkings(domain));
		return parkings;
	}

	public List<Parking> getFreeParkings(Domain domain) throws Exception {
		List<Parking> parkings = getAvailableParkings(domain);

		for(int i = parkings.size() - 1; i >= 0; i--){
			Parking parking = parkings.get(i);
			if (parking.getVehicle() != null) {
				parkings.remove(parking);
			}
		}

		return parkings;
	}

	public List<Parking> getBusyParkings(Domain domain) throws Exception {
		List<Parking> parkings = getAvailableParkings(domain);

		for(int i = parkings.size() - 1; i >= 0; i--){
			Parking parking = parkings.get(i);
			if (parking.getVehicle() == null) {
				parkings.remove(parking);
			}
		}

		return parkings;
	}
	
	
	public List<Parking> getNotOwnedParkings() throws Exception {
		return parkingManager.findNotOwnedParkings();
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

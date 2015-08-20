package br.com.atilo.jcondo.core.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Parking;
import br.com.abware.jcondo.core.model.Vehicle;
import br.com.abware.jcondo.core.service.BaseService;
import br.com.abware.jcondo.exception.BusinessException;

import br.com.atilo.jcondo.core.persistence.manager.ParkingManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.VehicleManagerImpl;

public class ParkingServiceImpl implements BaseService<Parking> {

	private ParkingManagerImpl parkingManager = new ParkingManagerImpl();
	
	private VehicleManagerImpl vehicleManager = new VehicleManagerImpl();
	
	private SecurityManagerImpl securityManager = new SecurityManagerImpl();

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

	public Parking getParking(Vehicle vehicle) throws Exception {
		return parkingManager.findByVehicle(vehicle);
	}
	
	public List<Parking> getOwnedParkings(Domain domain) throws Exception {
		return parkingManager.findOwnedParkings(domain);
	}

	public List<Parking> getGrantedParkings(Domain domain) throws Exception {
		return parkingManager.findGrantedParkings(domain);
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
			throw new BusinessException("pkg.not.exists", parking);
		}

		Parking p = parkingManager.findById(parking.getId());

		if (p == null) {
			throw new BusinessException("pkg.not.found", parking);
		}

		if (parking.getOwnerDomain() != null) {
			if (p.getOwnerDomain() != null && !p.getOwnerDomain().equals(parking.getOwnerDomain())) {
				throw new BusinessException("pkg.already.owned");
			}
		}

		if (parking.getRenterDomain() != null) {
			if (p.getRenterDomain() != null && !p.getRenterDomain().equals(parking.getRenterDomain())) {
				throw new BusinessException("pkg.already.rented");
			}
		}

		return parkingManager.save(parking);
	}

	public Parking rent(Domain ownerDomain, Domain renterDomain) throws Exception {
		if (!securityManager.hasPermission(ownerDomain, Permission.RENT_PARKING)) {
			throw new BusinessException("parking.rent.denied");
		}

		List<Parking> ownedParkings = parkingManager.findOwnedParkings(ownerDomain);

		if (CollectionUtils.isEmpty(ownedParkings)) {
			throw new BusinessException("no.parking.for.domain", ownerDomain);
		}

		for(Parking parking : ownedParkings){
			if (parking.getVehicle() == null) {
				parking.setRenterDomain(renterDomain);
				return parkingManager.save(parking);
			}
		}

		throw new BusinessException("no.parking.to.rent", ownerDomain);
	}

	public Parking unrent(long parkingId) throws Exception {
		Parking p = parkingManager.findById(parkingId);

		if (p == null) {
			throw new BusinessException("parking.not.exists", parkingId);
		}

		if (!securityManager.hasPermission(p.getOwnerDomain(), Permission.UNRENT_PARKING)) {
			throw new BusinessException("parking.unrent.denied");
		}

		if (p.getVehicle() != null) {
			Vehicle vehicle = vehicleManager.findById(p.getVehicle().getId());
			vehicle.setDomain(null);
			vehicleManager.save(vehicle);
		}

		p.setRenterDomain(null);
		p.setVehicle(null);
		return parkingManager.save(p);
	}
}

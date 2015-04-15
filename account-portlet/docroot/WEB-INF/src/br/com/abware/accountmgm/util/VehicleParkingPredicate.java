package br.com.abware.accountmgm.util;

import org.apache.commons.collections.Predicate;

import br.com.abware.jcondo.core.model.Parking;
import br.com.abware.jcondo.core.model.Vehicle;

public class VehicleParkingPredicate implements Predicate {

	private Vehicle vehicle;
	
	public VehicleParkingPredicate(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	
	@Override
	public boolean evaluate(Object obj) {
		return obj != null && obj instanceof Parking && ((Parking) obj).getVehicle() != null && vehicle.equals(((Parking) obj).getVehicle());
	}

}

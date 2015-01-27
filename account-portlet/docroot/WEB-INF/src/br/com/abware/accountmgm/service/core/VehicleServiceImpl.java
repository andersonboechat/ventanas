package br.com.abware.accountmgm.service.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.persistence.manager.FlatManagerImpl;
import br.com.abware.accountmgm.persistence.manager.ParkingManagerImpl;
import br.com.abware.accountmgm.persistence.manager.PersonManagerImpl;
import br.com.abware.accountmgm.persistence.manager.VehicleManagerImpl;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.BaseService;

public class VehicleServiceImpl implements BaseService<Vehicle> {

	private VehicleManagerImpl vehicleManager = new VehicleManagerImpl();

	private PersonManagerImpl personManager = new PersonManagerImpl();

	private FlatManagerImpl flatManager = new FlatManagerImpl();
	
	private ParkingManagerImpl parkingManager = new ParkingManagerImpl();

	public Vehicle getVehicle(long vehicleId) throws Exception {
		// TODO verificar permissao
		return vehicleManager.findById(vehicleId);
	}

	public List<Vehicle> getVehicles(Person person) throws Exception {
		List<Vehicle> vehicles = new ArrayList<Vehicle>();

		for (Flat flat : flatManager.findByPerson(person)) {
			CollectionUtils.addAll(vehicles, vehicleManager.findVehicles(flat).iterator());
		}
		
		// TODO verificar permissao de visualizar veiculos de visitantes
		CollectionUtils.addAll(vehicles, vehicleManager.findVehicles(new Flat()).iterator());

		return vehicles;
	}

	public Vehicle register(Vehicle vehicle) throws Exception {
		return register(vehicle, false);
	}

	public Vehicle register(Vehicle vehicle, boolean force) throws Exception {
		if (StringUtils.isEmpty(vehicle.getLicense())) {
			throw new Exception("placa nao especificada");
		}

		// TODO verificar permissao

		int amount = getParkingAmount(vehicle.getFlat());
		if (amount > 0) {
			if (vehicle.getFlat() != null) {
				throw new Exception("nao ha vagas disponíveis");	
			}
		}

		Vehicle v = vehicleManager.findByLicense(vehicle.getLicense());
		if (v != null) {
			return v;
		} else {
			return vehicleManager.save(vehicle, personManager.getLoggedPerson().getId());
		}
	}

	public int getParkingAmount(Flat flat) {
		if (flat != null) {
			int ownedParkingAmount = parkingManager.findOwnedParkings(flat).size();
			int usedParkingAmount = vehicleManager.findVehicles(flat).size();
			int grantedParkingAmount = parkingManager.findGrantedParkings(flat).size();
			int rentedParkingAmount = parkingManager.findRentedParkings(flat).size();
			return (ownedParkingAmount + rentedParkingAmount) - (grantedParkingAmount + usedParkingAmount);
		} else {
			int ownedParkingAmount = parkingManager.findOwnedParkings(condominium).size();
			int usedParkingAmount = vehicleManager.findVisitorVehicles(condominium).size();
			return ownedParkingAmount - usedParkingAmount;
		}
	}

	public void assignTo(Vehicle vehicle, Flat flat) throws Exception {
		Vehicle v = vehicleManager.findById(vehicle.getId());
		if (v.getFlat() != null && !v.getFlat().equals(flat)) {
			throw new Exception("veiculo associado ao apartamento " + v.getFlat());
		}
		// TODO verificar permissao
		v.setFlat(flat);
		vehicleManager.save(v, personManager.getLoggedPerson().getId());
	}

	public void removeFrom(Vehicle vehicle) throws Exception {
		Vehicle v = vehicleManager.findById(vehicle.getId());
		if (v == null) {
			throw new Exception("veiculo nao existente");
		}
		// TODO verificar permissao
		v.setFlat(null);
		vehicleManager.save(v, personManager.getLoggedPerson().getId());
	}

}

package br.com.abware.accountmgm.service.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.persistence.manager.FlatManagerImpl;
import br.com.abware.accountmgm.persistence.manager.ParkingManagerImpl;
import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.accountmgm.persistence.manager.VehicleManagerImpl;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.BaseService;

public class VehicleServiceImpl implements BaseService<Vehicle> {

	private VehicleManagerImpl vehicleManager = new VehicleManagerImpl();

	private FlatManagerImpl flatManager = new FlatManagerImpl();
	
	private ParkingManagerImpl parkingManager = new ParkingManagerImpl();
	
	private SecurityManagerImpl securityManager = new SecurityManagerImpl();
	
	public Vehicle getVehicle(long vehicleId) throws Exception {
		Vehicle vehicle = vehicleManager.findById(vehicleId);
		if (vehicle == null) {
			throw new Exception("veiculo nao cadastrado");
		}

		if (!securityManager.hasPermission(vehicle, Permission.VIEW)) {
			throw new Exception("sem permissao para visualizar o veiculo " + vehicle);
		}

		return vehicle;
	}

	public List<Vehicle> getVehicles(Person person) throws Exception {
		List<Vehicle> vehicles = new ArrayList<Vehicle>();

		for (Flat flat : flatManager.findByPerson(person)) {
			CollectionUtils.addAll(vehicles, vehicleManager.findVehicles(flat).iterator());
		}

		// TODO verificar permissao de visualizar veiculos de visitantes
		if (!securityManager.hasPermission(new Vehicle(), Permission.VIEW)) {
			CollectionUtils.addAll(vehicles, vehicleManager.findVehicles(new Flat()).iterator());
		}

		return vehicles;
	}

	public Vehicle register(Vehicle vehicle) throws Exception {
		if (StringUtils.isEmpty(vehicle.getLicense())) {
			throw new Exception("placa nao especificada");
		}

		Vehicle v = vehicleManager.findByLicense(vehicle.getLicense());

		if (v != null) {
			throw new Exception("veiculo ja registrado");
		}

		if (!securityManager.hasPermission(vehicle, Permission.ADD)) {
			throw new Exception("sem permissao para cadastrar veiculos");
		}

		// Verifica se tem vaga para o apartamento especificado
		// Visitantes podem acessar o condominio apenas para deixar/buscar passageiros
		if (vehicle.getDomain() instanceof Flat && getParkingAmount(vehicle.getDomain()) > 0) {
			throw new Exception("nao ha vagas disponíveis");
		}

		return vehicleManager.save(vehicle);
	}

	public int getParkingAmount(Domain domain) {
		try {
			if (domain instanceof Flat) {
				int ownedParkingAmount = parkingManager.findOwnedParkings(domain).size();
				int usedParkingAmount = vehicleManager.findVehicles(domain).size();
				int grantedParkingAmount = parkingManager.findGrantedParkings(domain).size();
				int rentedParkingAmount = parkingManager.findRentedParkings(domain).size();
				return (ownedParkingAmount + rentedParkingAmount) - (grantedParkingAmount + usedParkingAmount);
			} else {
				int ownedParkingAmount = parkingManager.findOwnedParkings(domain).size();
				int usedParkingAmount = vehicleManager.findVehicles(domain).size();
				return ownedParkingAmount - usedParkingAmount;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	public void assignTo(Vehicle vehicle, Domain domain) throws Exception {
		Vehicle v = getVehicle(vehicle.getId());

		if (v.getDomain() != null && !v.getDomain().equals(domain)) {
			throw new Exception("veiculo ja associado ao dominio " + v.getDomain());
		}

		if (!securityManager.hasPermission(vehicle, Permission.DELETE)) {
			throw new Exception("sem permissao para cadastrar veiculos");
		}

		v.setDomain(domain);
		vehicleManager.save(v);
	}

	public void removeFrom(Vehicle vehicle) throws Exception {
		Vehicle v = getVehicle(vehicle.getId());

		if (!securityManager.hasPermission(vehicle, Permission.DELETE)) {
			throw new Exception("sem permissao para cadastrar veiculos");
		}

		v.setDomain(null);
		vehicleManager.save(v);
	}

}

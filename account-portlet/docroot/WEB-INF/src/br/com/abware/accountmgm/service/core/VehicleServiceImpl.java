package br.com.abware.accountmgm.service.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.accountmgm.exception.ModelExistException;
import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.model.VehicleType;
import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.accountmgm.persistence.manager.VehicleManagerImpl;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.BaseService;

public class VehicleServiceImpl implements BaseService<Vehicle> {

	private VehicleManagerImpl vehicleManager = new VehicleManagerImpl();

	private FlatServiceImpl flatService = new FlatServiceImpl();

	private ParkingServiceImpl parkingService = new ParkingServiceImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();

	public List<VehicleType> getTypes(Domain domain) {
		List<VehicleType> types = new ArrayList<VehicleType>();

		for (VehicleType type : VehicleType.values()) {
//			if (securityManager.hasPermission(type, Permission.VIEW)) {
				types.add(type);
//			}
		}

		return types;
	}

	public Vehicle getVehicle(long vehicleId) throws Exception {
		Vehicle vehicle = vehicleManager.findById(vehicleId);

		if (vehicle == null) {
			throw new Exception("veiculo nao cadastrado");
		}

//		if (!securityManager.hasPermission(vehicle, Permission.VIEW)) {
//			throw new Exception("sem permissao para visualizar o veiculo " + vehicle);
//		}

		return vehicle;
	}

	public Vehicle getVehicle(String license) {
		Vehicle vehicle = null;
		try {
			vehicle = vehicleManager.findByLicense(license);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		if (!securityManager.hasPermission(vehicle, Permission.VIEW)) {
//			throw new Exception("sem permissao para visualizar o veiculo " + vehicle);
//		}

		return vehicle;
	}

	public List<Vehicle> getVehicles(Domain domain) throws Exception {
		return vehicleManager.findVehicles(domain);
	}

	public List<Vehicle> getVehicles(Person person) throws Exception {
		List<Vehicle> vehicles = new ArrayList<Vehicle>();

		for (Flat flat : flatService.getFlats(person)) {
			CollectionUtils.addAll(vehicles, vehicleManager.findVehicles(flat).iterator());
		}

		// TODO verificar permissao de visualizar veiculos de visitantes
//		if (!securityManager.hasPermission(new Vehicle(), Permission.VIEW)) {
			CollectionUtils.addAll(vehicles, vehicleManager.findVehicles(new Flat()).iterator());
//		}

		return vehicles;
	}

	public Vehicle register(Vehicle vehicle) throws Exception {
//		if (!securityManager.hasPermission(vehicle, Permission.ADD)) {
//			throw new Exception("sem permissao para cadastrar veiculos");
//		}

		if (StringUtils.isEmpty(vehicle.getLicense())) {
			throw new Exception("placa nao especificada");
		}

		if(vehicle.getType() != VehicleType.BIKE && !vehicle.getLicense().matches("[A-Za-z]{3,3}[0-9]{4,4}")) {
			throw new Exception("placa invalida");
		}

		Vehicle v = vehicleManager.findByLicense(vehicle.getLicense());

		if (v != null) {
			throw new ModelExistException(null, "vehicle.exists");
		}

		// Verifica se tem vaga para o apartamento especificado
		// Visitantes podem acessar o condominio apenas para deixar/buscar passageiros
		if (vehicle.getDomain() != null && vehicle.getDomain() instanceof Flat) {
			if (vehicle.getDomain().getId() > 0) {
				if (flatService.getFlat(vehicle.getDomain().getId()) == null) {
					throw new Exception("apartamento nao encontrado");
				} else if (parkingService.getParkingAmount(vehicle.getDomain()) <= 0){
					throw new Exception("nao ha vagas disponíveis");
				}
			}
		}

		return vehicleManager.save(vehicle);
	}

	public void assignTo(Vehicle vehicle, Domain domain) throws Exception {
//		if (!securityManager.hasPermission(vehicle, Permission.UPDATE)) {
//			throw new Exception("sem permissao para cadastrar veiculos");
//		}

		Vehicle v = getVehicle(vehicle.getId());

		if (domain instanceof Flat && !domain.equals(v.getDomain())) {
			if (domain.getId() > 0) {
				if (flatService.getFlat(domain.getId()) == null) {
					throw new Exception("apartamento nao encontrado");
				} else if (parkingService.getParkingAmount(domain) <= 0){
					throw new Exception("nao ha vagas disponíveis");
				}
			}

			v.setDomain(domain);
			vehicleManager.save(v);
			vehicle.setDomain(domain);
		}
	}

	public void removeFrom(Vehicle vehicle, Domain domain) throws Exception {
//		if (!securityManager.hasPermission(vehicle, Permission.UPDATE)) {
//			throw new Exception("sem permissao para cadastrar veiculos");
//		}

		Vehicle v = getVehicle(vehicle.getId());

		if (!domain.equals(v.getDomain())) {
			throw new Exception("veiculo associado a outro apartamento");
		}

		v.setDomain(domain);
		vehicleManager.save(v);
	}

	public void updateImage(Vehicle vehicle, Image image) throws Exception {
//		if (!securityManager.hasPermission(vehicle, Permission.UPDATE)) {
//			throw new Exception("sem permissao para cadastrar veiculos");
//		}

		Vehicle v = getVehicle(vehicle.getId());
		v.setImage(image);

		v = vehicleManager.save(v);
		vehicle.setImage(v.getImage());
	}

}

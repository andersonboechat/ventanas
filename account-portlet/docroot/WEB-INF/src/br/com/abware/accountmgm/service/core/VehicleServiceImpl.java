package br.com.abware.accountmgm.service.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.model.VehicleAccess;
import br.com.abware.accountmgm.model.VehicleType;
import br.com.abware.accountmgm.persistence.manager.VehicleManagerImpl;
import br.com.abware.accountmgm.util.FlatTransformer;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.BaseService;

public class VehicleServiceImpl implements BaseService<Vehicle> {

	private VehicleManagerImpl vehicleManager = new VehicleManagerImpl();
	
	private PersonServiceImpl personService = new PersonServiceImpl();

	public List<Vehicle> getVehicles(Flat flat) throws Exception {
		return vehicleManager.findVehicles(flat);
	}

	public void register(Vehicle vehicle) throws Exception {
		vehicleManager.save(vehicle, personService.getPerson().getId());
	}

	@SuppressWarnings("unchecked")
	public void unregister(Vehicle vehicle) throws Exception {
		Person person = personService.getPerson();
		Collection<Flat> flats = CollectionUtils.transformedCollection(person.getMemberships(), new FlatTransformer());
		vehicle.getFlats().removeAll(flats);
		vehicle.setType(VehicleType.VISITOR);
		vehicleManager.save(vehicle, personService.getPerson().getId());
	}

	public void changeType(VehicleType type, Vehicle vehicle, Flat flat) throws Exception {
		Vehicle v = vehicleManager.findById(vehicle.getId());

		if (type == VehicleType.RESIDENT) {
			// Verificar se a quantidade de vagas do apartamento ainda nao excedeu
			
			// Remover o veiculo de outros apartamentos que estiver associado
		}
		
		v.setType(type);
		vehicleManager.save(v, personService.getPerson().getId());
	}

	public void assignTo(Vehicle vehicle, Flat[] flats) throws Exception {
		Vehicle v = vehicleManager.findById(vehicle.getId());
		CollectionUtils.addAll(v.getFlats(), flats);
		vehicleManager.save(v, personService.getPerson().getId());
	}

	public void removeFrom(Vehicle vehicle, Flat[] flats) throws Exception {
		Vehicle v = vehicleManager.findById(vehicle.getId());
		v.getFlats().removeAll(Arrays.asList(flats));
		vehicleManager.save(v, personService.getPerson().getId());		
	}

	public void checkAccess(VehicleAccess access) {
		
	}

}

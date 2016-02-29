package br.com.atilo.jcondo.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.velocity.VelocityContext;
import com.liferay.portal.kernel.velocity.VelocityEngineUtil;
import com.liferay.util.ContentUtil;

import br.com.abware.jcondo.exception.ModelExistException;
import br.com.abware.jcondo.core.model.Parking;
import br.com.abware.jcondo.core.model.Vehicle;
import br.com.abware.jcondo.core.model.VehicleType;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.VehicleManagerImpl;
import br.com.atilo.jcondo.util.MailService;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.BaseService;
import br.com.abware.jcondo.exception.BusinessException;

public class VehicleServiceImpl implements BaseService<Vehicle> {
	
	private static final Logger LOGGER = Logger.getLogger(VehicleServiceImpl.class);
	
	private static final ResourceBundle rb = ResourceBundle.getBundle("i18n");
	
	public static final String LICENSE_PATTERN = "[A-Za-z]{3,3}[0-9]{4,4}";

	private VehicleManagerImpl vehicleManager = new VehicleManagerImpl();

	private FlatServiceImpl flatService = new FlatServiceImpl();

	private ParkingServiceImpl parkingService = new ParkingServiceImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();

	public List<VehicleType> getTypes(Domain domain) throws Exception {
		List<VehicleType> types = new ArrayList<VehicleType>();
		types.add(VehicleType.CAR);
		types.add(VehicleType.MOTO);

//		for (VehicleType type : VehicleType.values()) {
//			if (securityManager.hasPermission(type, Permission.VIEW)) {
//				types.add(type);
//			}
//		}

		return types;
	}

	public Vehicle getVehicle(long vehicleId) throws Exception {
		Vehicle vehicle = vehicleManager.findById(vehicleId);

		if (vehicle == null) {
			throw new BusinessException("vhc.not.found", vehicle);
		}

		if (!securityManager.hasPermission(vehicle, Permission.VIEW)) {
			throw new BusinessException("vhc.view.denied", vehicle);
		}

		return vehicle;
	}

	public Vehicle getVehicle(String license)  {
		Vehicle vehicle = null;
		try {
			vehicle = vehicleManager.findByLicense(license.replaceAll("[^A-Za-z0-9]", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}

//		if (!securityManager.hasPermission(vehicle, Permission.VIEW)) {
//			throw new BusinessException("vhc.view.denied", vehicle);
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

		return vehicles;
	}

	public List<Vehicle> getAllVehicles() throws Exception {
		if (!securityManager.hasPermission(new Vehicle(), Permission.VIEW)) {
			throw new BusinessException("vhc.view.denied");
		}

		return vehicleManager.findAll();
	}

	/**
	 * Veículos com domínio nulo, são considerados visitantes.
	 * 
	 * @param vehicle
	 * @return
	 * @throws Exception
	 */
	public Vehicle register(Vehicle vehicle) throws Exception {
		if (!securityManager.hasPermission(vehicle, Permission.ADD)) {
			throw new BusinessException("vhc.create.denied");
		}

		if (vehicle.getDomain() != null && !securityManager.hasPermission(vehicle.getDomain(), Permission.ASSIGN_VEHICLE)) {
			if (vehicle.getDomain() instanceof Flat) {
				throw new BusinessException("vhc.domain.assign.denied", ((Flat) vehicle.getDomain()).getNumber(), ((Flat) vehicle.getDomain()).getBlock());
			} else {
				throw new BusinessException("vhc.domain.assign.denied", vehicle.getDomain());				
			}
		}

		if (StringUtils.isEmpty(vehicle.getLicense())) {
			throw new BusinessException("vhc.license.empty");
		}

		String license = vehicle.getLicense().replaceAll("[^A-Za-z0-9]", "");
		
		if(vehicle.getType() != VehicleType.BIKE && !license.matches(LICENSE_PATTERN)) {
			throw new BusinessException("vhc.license.invalid", vehicle.getLicense());
		}

		Vehicle v = vehicleManager.findByLicense(license);

		if (v != null) {
			throw new ModelExistException(null, "vhc.exists", v);
		} else {
			v = new Vehicle();
		}

		v.setLicense(license);
		v.setType(vehicle.getType());
		v.setImage(vehicle.getImage());

		v = vehicleManager.save(v);

		assignTo(v, vehicle.getDomain());

		return v;
	}

	public Vehicle update(Vehicle vehicle) throws Exception {
		if (!securityManager.hasPermission(vehicle, Permission.UPDATE)) {
			throw new BusinessException("vhc.update.denied");
		}

		String license = vehicle.getLicense().replaceAll("[^A-Za-z0-9]", "");
		
		if(vehicle.getType() != VehicleType.BIKE && !license.matches(LICENSE_PATTERN)) {
			throw new BusinessException("vhc.license.invalid", vehicle.getLicense());
		}

		Vehicle v = getVehicle(vehicle.getLicense());

		if (v == null) {
			throw new ModelExistException(null, "vehicle.not.exists");
		}

		v.setType(vehicle.getType());
		v.setImage(vehicle.getImage());
		v = vehicleManager.save(v);

		if (!v.getImage().getPath().equalsIgnoreCase(vehicle.getImage().getPath())) {
			try {
				notifyVehicleUpdate(vehicle, "image");
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}

		assignTo(v, vehicle.getDomain());

		return v;
	}

	public void assignTo(Vehicle vehicle, Domain domain) throws Exception {
		String event;
		Vehicle v = getVehicle(vehicle.getId());

		if (v == null) {
			throw new ModelExistException(null, "vehicle.not.exists");
		}

		if (domain == null) {
			if (v.getDomain() == null) {
				return;
			}

			if (!securityManager.hasPermission(v.getDomain(), Permission.REMOVE_VEHICLE)) {
				throw new BusinessException("vhc.domain.assign.denied", domain);
			}

			event = "remove";
		} else {
			if (domain.equals(v.getDomain())) {
				return;
			}

			if (!securityManager.hasPermission(domain, Permission.ASSIGN_VEHICLE)) {
				throw new BusinessException("vhc.domain.assign.denied", domain);
			}

			if (v.getDomain() != null) {
				throw new Exception("vhc.domain.exists");
			} else if (!(domain instanceof Flat)) {
				throw new BusinessException("vhc.domain.not.flat");
			} else if (flatService.getFlat(domain.getId()) == null) {
				throw new BusinessException("vhc.domain.unknown", domain.getId());
			}

			event = "assign";
		}  

		Domain d = v.getDomain();
		v.setDomain(domain);
		vehicleManager.save(v);

		try {
			v.setDomain(domain == null ? d : domain);
			notifyVehicleUpdate(v, event);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		vehicle.setDomain(domain);
	}

	public void updateImage(Vehicle vehicle, Image image) throws Exception {
		if (!securityManager.hasPermission(vehicle, Permission.UPDATE)) {
			throw new BusinessException("vhc.update.denied");
		}

		Vehicle v = getVehicle(vehicle.getId());
		v.setImage(image);

		v = vehicleManager.save(v);
		vehicle.setImage(v.getImage());
		
		try {
			notifyVehicleUpdate(vehicle, "image");
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	private void notifyVehicleUpdate(Vehicle vehicle, String event) throws Exception {
		LOGGER.info("vehicle update notify begin: " + vehicle);

		Vehicle v = getVehicle(vehicle.getId());

		if (v == null) {
			throw new ModelExistException(null, "vehicle.not.exists");
		}

		String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/core/mail/vehicle-updated-notify.vm");
		
		LOGGER.debug(mailBodyTemplate);

		VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
		variables.put("vehicle", vehicle);
		variables.put("event", event);
		variables.put("props", rb);
		UnsyncStringWriter writer = new UnsyncStringWriter();
		VelocityEngineUtil.mergeTemplate("VUN", mailBodyTemplate, variables, writer);

		String mailBody = writer.toString();
		LOGGER.debug(mailBody);

		String mailTo = rb.getString("admin.mail");
		String mailSubject = rb.getString("vehicle.update.subject");

		MailService.send(mailTo, mailSubject, mailBody);

		LOGGER.info("vehicle update notify end: " + vehicle);
	}

	public void claim(Vehicle vehicle) {
//		LOGGER.info("Enviando reivindicacao de veiculo " + occurrence.getId());
//
//		String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/core/mail/vehicle-claim-notify.vm");
//		LOGGER.debug(mailBodyTemplate);
//
//		VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
//		variables.put("occurrence", occurrence);
//		variables.put("occurrenceLabel", rb.getString(occurrence.getType().getLabel()).toLowerCase());
//		variables.put("occurrenceDate", DateFormatUtils.format(occurrence.getDate(), "dd 'de' MMMM 'de' yyyy 'às' HH:mm"));
//		variables.put("answer", occurrence.getAnswer());
//		variables.put("answerDate", DateFormatUtils.format(occurrence.getAnswer().getDate(), "dd 'de' MMMM 'de' yyyy 'às' HH:mm"));
//		UnsyncStringWriter writer = new UnsyncStringWriter();
//		VelocityEngineUtil.mergeTemplate("OAN", mailBodyTemplate, variables, writer);
//
//		String mailBody = writer.toString();
//		LOGGER.debug(mailBody);
//
//		String mailTo = occurrence.getPerson().getEmailAddress();
//		String mailSubject = MessageFormat.format(rb.getString("occurrence.answer.subject"), 
//												  rb.getString(occurrence.getType().getLabel()).toLowerCase());
//
//		MailService.send(mailTo, mailSubject, mailBody);
	}

	public String getLabel(Vehicle vehicle) throws Exception {
		Parking parking = parkingService.getParking(vehicle);
		return (parking == null || parking.getRenterDomain() != null) ? "vehicle.label.rented" : "vehicle.label.owned";
	}
}

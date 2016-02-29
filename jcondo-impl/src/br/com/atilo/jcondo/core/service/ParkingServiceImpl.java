package br.com.atilo.jcondo.core.service;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.velocity.VelocityContext;
import com.liferay.portal.kernel.velocity.VelocityEngineUtil;
import com.liferay.util.ContentUtil;

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
import br.com.atilo.jcondo.util.MailService;

public class ParkingServiceImpl implements BaseService<Parking> {

	private static final Logger LOGGER = Logger.getLogger(ParkingServiceImpl.class);	
	
	private static final ResourceBundle rb = ResourceBundle.getBundle("i18n");

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

	private void notifyParkingRent(Parking parking) throws Exception {
		LOGGER.info("parking rent notify begin: " + parking);

		Parking p = parkingManager.findById(parking.getId());

		if (p == null) {
			throw new BusinessException("parking.not.exists");
		}

		if (p.getOwnerDomain() == null) {
			throw new BusinessException("parking.not.owned", p);
		}		

		if (p.getRenterDomain() == null) {
			throw new BusinessException("parking.not.rented", p);
		}

		String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/core/mail/parking-rented-notify.vm");
		
		LOGGER.debug(mailBodyTemplate);

		VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
		variables.put("parking", p);
		UnsyncStringWriter writer = new UnsyncStringWriter();
		VelocityEngineUtil.mergeTemplate("PRTN", mailBodyTemplate, variables, writer);

		String mailBody = writer.toString();
		LOGGER.debug(mailBody);

		String mailTo = "adm@ventanasresidencial.com.br";
		String mailSubject = rb.getString("parking.rent.notify");

		MailService.send(mailTo, mailSubject, mailBody);

		LOGGER.info("parking rent notify end: " + parking);
	}	
	
	private void notifyParkingUnrent(Parking parking) throws Exception {
		LOGGER.info("parking unrent notify begin: " + parking);

		Parking p = parkingManager.findById(parking.getId());

		if (p == null) {
			throw new BusinessException("parking.not.exists");
		}

		if (p.getOwnerDomain() == null) {
			throw new BusinessException("parking.not.owned", p);
		}		

		if (p.getRenterDomain() != null) {
			throw new BusinessException("parking.is.rented", p);
		}

		p.setRenterDomain(parking.getRenterDomain());

		String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/core/mail/parking-unrented-notify.vm");
		
		LOGGER.debug(mailBodyTemplate);

		VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
		variables.put("parking", p);
		UnsyncStringWriter writer = new UnsyncStringWriter();
		VelocityEngineUtil.mergeTemplate("PUTN", mailBodyTemplate, variables, writer);

		String mailBody = writer.toString();
		LOGGER.debug(mailBody);

		String mailTo = "adm@ventanasresidencial.com.br";
		String mailSubject = rb.getString("parking.unrent.notify");

		MailService.send(mailTo, mailSubject, mailBody);

		LOGGER.info("parking unrent notify end: " + parking);
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
		Parking p = parkingManager.findById(parking.getId());

		if (p == null) {
			throw new BusinessException("parking.not.exists", parking);
		}

		if (parking.getOwnerDomain() != null) {
			if (p.getOwnerDomain() != null && !p.getOwnerDomain().equals(parking.getOwnerDomain())) {
				throw new BusinessException("parking.already.owned");
			}
		}

		if (parking.getRenterDomain() != null) {
			if (p.getRenterDomain() != null && !p.getRenterDomain().equals(parking.getRenterDomain())) {
				throw new BusinessException("parking.already.rented");
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

		if (renterDomain.getId() == 0) {
			throw new BusinessException("parking.domain.not.found", renterDomain);
		}

		for(Parking parking : ownedParkings){
			if (parking.getRenterDomain() == null) {
				parking.setRenterDomain(renterDomain);
				Parking p = parkingManager.save(parking);
				try {
					notifyParkingRent(p);
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
				return p;				
			}
		}

		throw new BusinessException("no.parking.to.rent", ownerDomain);
	}

	public Parking unrent(Parking parking) throws Exception {
		Parking p = parkingManager.findById(parking.getId());

		if (p == null) {
			throw new BusinessException("parking.not.exists", parking);
		}

		if (p.getRenterDomain() == null) {
			throw new BusinessException("parking.not.rented", parking);
		}

		if (!securityManager.hasPermission(parking.getOwnerDomain(), Permission.UNRENT_PARKING)) {
			throw new BusinessException("parking.unrent.denied");
		}

		parking.setOwnerDomain(p.getOwnerDomain());
		parking.setRenterDomain(p.getRenterDomain());
		p.setRenterDomain(null);
		p = parkingManager.save(p);

		try {
			notifyParkingUnrent(parking);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		return p;
	}
}

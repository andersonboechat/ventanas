package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.util.VehicleParkingPredicate;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Parking;
import br.com.abware.jcondo.core.model.Vehicle;
import br.com.abware.jcondo.core.model.VehicleType;

@ManagedBean
@ViewScoped
public class ParkingBean extends BaseBean implements Observer {

	private static Logger LOGGER = Logger.getLogger(ParkingBean.class);

	private ModelDataModel<Parking> model;

	private HashMap<String, Object> filters;
	
	private Parking parking;
	
	private List<Parking> parkings;
	
	private List<Flat> flats;

	public void init(List<Flat> flats) {
		try {
			Set<Parking> parkings;
			this.flats = flats;

			if (!CollectionUtils.isEmpty(flats)) {
				parkings = new HashSet<Parking>();

				for (Flat flat : flats) {
					parkings.addAll(parkingService.getParkings(flat));
				}
				
				this.parkings = new ArrayList<Parking>(parkings);
				model = new ModelDataModel<Parking>(this.parkings);
			}

			filters = new HashMap<String, Object>();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	
	public void onCreate() {
		
	}
	
	public void onSave() {
		
	}
	
	public void onEdit() {
		
	}

	public void onRent() throws Exception {
		parkingService.update(parking);
	}

	public void onRentalCancel() throws Exception {
		parkingService.update(parking);
	}
	
	@SuppressWarnings("unchecked")
	public void onDomainSearch(Domain domain) throws Exception {
		List<Parking> parkings;
		filters.put("renterDomain", domain != null ? domain : null);
		model.filter(filters);
		parkings = (List<Parking>) model.getWrappedData();
		filters.clear();
		filters.put("ownerDomain", domain != null ? domain : null);
		model.filter(filters);
		((List<Parking>) model.getWrappedData()).addAll(parkings);
	}

	@Override
	public void update(Observable observable, Object object) {
		try {
			Vehicle vehicle = (Vehicle) object;
			Parking parking;

			parking = (Parking) CollectionUtils.find(this.parkings, new VehicleParkingPredicate(vehicle));

			if (parking != null) {
				if (vehicle.getType() != VehicleType.CAR || vehicle.getDomain() == null) {
					parking.setVehicle(null);
				}
			} else {
				parking = (Parking) CollectionUtils.find(parkingService.getParkings(vehicle.getDomain()), 
						 															new VehicleParkingPredicate(vehicle));
				if (parking != null) {
					model.update(parking);
				}
				
			}

		} catch (Exception e) {
			LOGGER.warn("Failure on flat parkings update", e);
		}
	}

	public List<Flat> getAllFlats() throws Exception {
		return flatService.getFlats();
	}
	
	public ModelDataModel<Parking> getModel() {
		return model;
	}

	public Parking getParking() {
		return parking;
	}

	public void setParking(Parking parking) {
		this.parking = parking;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}
}

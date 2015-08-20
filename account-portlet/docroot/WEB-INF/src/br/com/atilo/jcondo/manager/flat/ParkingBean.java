package br.com.atilo.jcondo.manager.flat;

import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.context.RequestContext;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Parking;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.core.service.ParkingServiceImpl;

@ViewScoped
@ManagedBean(name="parkingView")
public class ParkingBean {

	private static Logger LOGGER = Logger.getLogger(ParkingBean.class);

	private ParkingServiceImpl parkingService = new ParkingServiceImpl();

	private ModelDataModel<Parking> model;

	private HashMap<String, Object> filters;

	private Parking parking;

	private Flat flat;

	private Flat renterFlat;

	private int ownedParkingAmount;

	private int rentedParkingAmount;

	public void init(Flat flat) {
		try {
			this.flat = flat;
			model = new ModelDataModel<Parking>(parkingService.getGrantedParkings(flat));
			ownedParkingAmount = parkingService.getOwnedParkings(flat).size();
			rentedParkingAmount = parkingService.getRentedParkings(flat).size();
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

	public void onParkingRent() {
		try {
			parking = parkingService.rent(flat, renterFlat);
			model.addModel(parking);
			renterFlat = null;
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "parking.rent.success", null);
		} catch (BusinessException e) {
			LOGGER.warn("Business failure on parking rent: " + e.getMessage());
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on parking rent", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}	
	}

	public void onParkingUnrent(Parking parking) {
		try {
			parkingService.unrent(parking.getId());
			model.removeModel(parking);
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "parking.unrent.success", null);
		} catch (BusinessException e) {
			LOGGER.warn("Business failure on parking unrent: " + e.getMessage());
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on parking unrent", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}	
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

	public Flat getRenterFlat() {
		return renterFlat;
	}

	public void setRenterFlat(Flat renterFlat) {
		this.renterFlat = renterFlat;
	}

	public int getOwnedParkingAmount() {
		return ownedParkingAmount;
	}

	public int getRentedParkingAmount() {
		return rentedParkingAmount;
	}

}

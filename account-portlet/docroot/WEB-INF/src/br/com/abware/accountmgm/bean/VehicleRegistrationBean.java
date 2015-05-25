package br.com.abware.accountmgm.bean;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.context.RequestContext;

import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Vehicle;
import br.com.abware.jcondo.core.model.VehicleType;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.abware.jcondo.exception.ModelExistException;

@ViewScoped
@ManagedBean
public class VehicleRegistrationBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(VehicleRegistrationBean.class);

	private CameraBean cameraBean;

	private Vehicle vehicle;

	private List<VehicleType> types;
	
	@PostConstruct
	public void init() {
		try {
			types = Arrays.asList(VehicleType.CAR, VehicleType.MOTO);
			vehicle = createVehicle();
			cameraBean = new CameraBean(320, 240);
			cameraBean.setImage(vehicle.getImage());
		} catch (Exception e) {
			LOGGER.fatal("Failure on vehicle initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
		}
	}

	public void onSave() {
		try {
			if (vehicle.getId() == 0) {
				vehicle = vehicleService.register(vehicle);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "vehicle.create.success", null);
			} else {
				vehicle = vehicleService.update(vehicle);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "vehicle.update.success", null);
			}

		} catch (ModelExistException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs(), "tabs:vehicle-details-form:alertMsg");
			RequestContext.getCurrentInstance().addCallbackParam("alert", true);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}
	
	public Vehicle createVehicle() {
		Vehicle vehicle = new Vehicle();
		vehicle.setImage(new Image());
		return vehicle;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public CameraBean getCameraBean() {
		return cameraBean;
	}

	public void setCameraBean(CameraBean cameraBean) {
		this.cameraBean = cameraBean;
	}

	public List<VehicleType> getTypes() {
		return types;
	}

	public void setTypes(List<VehicleType> types) {
		this.types = types;
	}
	
}

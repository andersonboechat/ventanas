package br.com.atilo.jcondo.manager.flat;

import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.context.RequestContext;

import com.sun.faces.util.MessageFactory;

import br.com.abware.accountmgm.bean.model.ModelDataModel;

import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.core.model.Vehicle;
import br.com.abware.jcondo.core.model.VehicleType;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.abware.jcondo.exception.ModelExistException;
import br.com.atilo.jcondo.core.service.VehicleServiceImpl;
import br.com.atilo.jcondo.manager.ImageUploadBean;

@ViewScoped
@ManagedBean(name="vehicleView")
public class VehicleBean {

	private static Logger LOGGER = Logger.getLogger(VehicleBean.class);
	
	private VehicleServiceImpl vehicleService = new VehicleServiceImpl();

	private ImageUploadBean imageUploadBean;

	private ModelDataModel<Vehicle> model;

	private HashMap<String, Object> filters;

	private Vehicle vehicle;

	private String license;

	private Flat flat;

	private List<VehicleType> types;

	private Vehicle[] selectedVehicles;

	private boolean visitor;
	
	public void init(Flat flat) {
		try {
			this.flat = flat;
			model = new ModelDataModel<Vehicle>(vehicleService.getVehicles(flat));
			types = vehicleService.getTypes(flat);
			vehicle = new Vehicle();
			vehicle.setDomain(flat);
			vehicle.setImage(new Image());
			filters = new HashMap<String, Object>();
			imageUploadBean = new ImageUploadBean(364, 300);
		} catch (Exception e) {
			LOGGER.fatal("Failure on vehicle initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
		}
	}

	public void onVehicleSave() {
		try {
			String msg;
			Vehicle v;
			vehicle.setLicense(vehicle.getLicense().replaceAll("[^A-Za-z0-9]", ""));

			if (vehicle.getId() == 0) {
				v = vehicleService.register(vehicle);
				msg = "vehicle.create.success";
			} else {
				v = vehicleService.update(vehicle);
				msg = "vehicle.edit.success";
			}

			model.update(v);

			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, msg, null);
		} catch (ModelExistException e) {
			Vehicle v = (Vehicle) e.getArgs()[0];

			// Vehicle is not associated to any domain. So, it can be taken
			if (v.getDomain() == null) {
				vehicle.setId(v.getId());
				if (StringUtils.isEmpty(vehicle.getImage().getPath())) {
					vehicle.setImage(v.getImage());
				}
				onVehicleSave();
			} else {
				MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs(), ":tabs:vehicle-details-form:alertMsg");
				RequestContext.getCurrentInstance().addCallbackParam("alert", true);
				RequestContext.getCurrentInstance().addCallbackParam("exception", true);
			}
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onVehicleClaim() {
		try {
			vehicleService.claim(vehicle);
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "vehicle.claim.success", null);
		} catch (Exception e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}	
	}
	
	public void onVehicleCreate() throws Exception {
		vehicle = new Vehicle();
		vehicle.setDomain(flat);
		vehicle.setImage(new Image());
		visitor = false;
		imageUploadBean.setImage(vehicle.getImage());
	}

	public void onVehiclesDelete() throws Exception {
		try {
			for (Vehicle vehicle : selectedVehicles) {
				vehicleService.assignTo(vehicle, null);
				model.removeModel(vehicle);
			}
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Failure on vehicle delete", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "vehicles.delete.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onVehicleDelete() throws Exception {
		try {
			vehicleService.assignTo(model.getRowData(), null);
			model.removeModel(model.getRowData());
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "vehicle.delete.success", null);
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Failure on vehicle delete", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "vehicle.delete.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}
	
	public void onVehicleEdit() {
		try {
			BeanUtils.copyProperties(vehicle, model.getRowData());
			visitor = vehicle.getDomain() == null ? true : false;
			imageUploadBean.setImage(vehicle.getImage());
		} catch (Exception e) {
			LOGGER.error("Failure on vehicle editing", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "vehicle.edit.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onVehicleSearch(ValueChangeEvent event) throws Exception {
		filters.put("license", (String) event.getNewValue());
		model.filter(filters);
	}

	public void onDomainSearch(Domain domain) throws Exception {
		filters.put("domain.id", domain != null ? domain.getId() : null);
		model.filter(filters);
	}

	public void validateVehicleType(FacesContext context, UIComponent component, Object value) {
		if (! (value instanceof VehicleType)) {
			FacesMessage message = MessageFactory.getMessage(UIInput.REQUIRED_MESSAGE_ID, null);
			throw new ValidatorException(message);  
		}

		VehicleType type = (VehicleType) value;
	}  

	public ImageUploadBean getImageUploadBean() {
		return imageUploadBean;
	}

	public void setImageUploadBean(ImageUploadBean imageUploadBean) {
		this.imageUploadBean = imageUploadBean;
	}
	
	public String displayDomain(Domain domain) {
		if (domain != null && domain.getId() > 0) {
			if (domain instanceof Flat) {
				Flat flat = (Flat) domain;
				return "Apt. " + flat.getNumber() + " - bloco " + flat.getBlock();
			}
			if (domain instanceof Supplier) {
				return ((Supplier) domain).getName();
			}
			if (domain instanceof Condominium) {
				return "Visitante";
			}
		}
		return "Visitante";
	}

	public String displayLabel(Vehicle vehicle) {
		try {
			return vehicleService.getLabel(vehicle);
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on getting parking for vehicle " + vehicle, e);
		}

		return null;
	}	
	
	public ModelDataModel<Vehicle> getModel() {
		return model;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public List<VehicleType> getTypes() {
		return types;
	}

	public void setTypes(List<VehicleType> types) {
		this.types = types;
	}

	public Vehicle[] getSelectedVehicles() {
		return selectedVehicles;
	}

	public void setSelectedVehicles(Vehicle[] selectedVehicles) {
		this.selectedVehicles = selectedVehicles;
	}

	public boolean isVisitor() {
		return visitor;
	}

	public void setVisitor(boolean visitor) {
		this.visitor = visitor;
	}

}

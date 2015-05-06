package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

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

@ViewScoped
@ManagedBean
public class VehicleBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(VehicleBean.class);

	@ManagedProperty(value="#{fileUploadBean}")
	private FileUploadBean fileUploadBean;

	private ImageUploadBean imageUploadBean;

	private ModelDataModel<Vehicle> model;

	private HashMap<String, Object> filters;

	private int block;

	private int number;

	private String license;

	private Vehicle vehicle;

	private Vehicle[] selectedVehicles;

	private List<Flat> flats;

	private Set<Integer> blocks;

	private Set<Integer> numbers;
	
	private boolean visitor;
	
	private Map<Domain, List<VehicleType>> types;
	
	public void init(List<Flat> flats) {
		try {
			ArrayList<Vehicle> vehicles;
			this.flats = flats;
			types = new HashMap<Domain, List<VehicleType>>();

			if (!CollectionUtils.isEmpty(flats)) {
				vehicles = new ArrayList<Vehicle>();
				blocks = new TreeSet<Integer>();
				numbers = new TreeSet<Integer>();

				for (Flat flat : flats) {
					blocks.add(flat.getBlock());
					numbers.add(flat.getNumber());
					vehicles.addAll(vehicleService.getVehicles(flat));
					types.put(flat, vehicleService.getTypes(flat));
				}
				
				model = new ModelDataModel<Vehicle>(vehicles);
			}

			vehicle = new Vehicle();
			vehicle.setDomain(new Flat());
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

			this.setChanged();
			this.notifyObservers(v);

			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, msg, null);
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

	public void onVehicleClaim() {
		try {
			vehicleService.claim(vehicle);
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}	
	}
	
	public void onVehicleCreate() throws Exception {
		vehicle = new Vehicle();
		vehicle.setDomain(new Flat());
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

	public void onVehicleSearch() throws Exception {
		filters.put("license", license);
		model.filter(filters);
	}

	public void onDomainSearch(Domain domain) throws Exception {
		filters.put("domain.id", domain != null ? domain.getId() : null);
		model.filter(filters);
	}

	public void onFlatSelect(ValueChangeEvent event) throws Exception {
		long id = (Long) event.getNewValue();
		if (id == 0) {
			vehicle.setDomain(new Flat());
		} else {
			vehicle.setDomain(flats.get(flats.indexOf(new Flat(id, 0, 0))));
		}
	}

	public void onBlockSelect(AjaxBehaviorEvent event) throws Exception {
		filters.put("domain.block", block);
		model.filter(filters);
	}

	public void onNumberSelect(AjaxBehaviorEvent event) throws Exception {
		filters.put("domain.number", number);
		model.filter(filters);
	}

	public void onImageUpload(FileUploadEvent event) {
		fileUploadBean.onFileUpload(event);
		vehicle.getImage().setPath(fileUploadBean.getImagePath());
	}

	public void onImageCropp() {
		fileUploadBean.onCropp();
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

	public void setFileUploadBean(FileUploadBean fileUploadBean) {
		this.fileUploadBean = fileUploadBean;
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

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

	public Vehicle[] getSelectedVehicles() {
		return selectedVehicles;
	}

	public void setSelectedVehicles(Vehicle[] selectedVehicles) {
		this.selectedVehicles = selectedVehicles;
	}

	public Set<Integer> getBlocks() {
		return blocks;
	}

	public void setBlocks(Set<Integer> blocks) {
		this.blocks = blocks;
	}

	public int getBlock() {
		return block;
	}

	public void setBlock(int block) {
		this.block = block;
	}

	public Set<Integer> getNumbers() {
		return numbers;
	}

	public void setNumbers(Set<Integer> numbers) {
		this.numbers = numbers;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isVisitor() {
		return visitor;
	}

	public void setVisitor(boolean visitor) {
		this.visitor = visitor;
	}

	public Map<Domain, List<VehicleType>> getTypes() {
		return types;
	}

	public void setTypes(Map<Domain, List<VehicleType>> types) {
		this.types = types;
	}

	
}

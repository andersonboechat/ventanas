package br.com.abware.accountmgm.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.VehicleDataModel;
import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.jcondo.core.model.Flat;

@ManagedBean
@ViewScoped
public class VehicleBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(VehicleBean.class);

	private VehicleDataModel model;

	private HashMap<String, Object> filters;

	private Long block;

	private Long number;

	private String license;

	private Vehicle vehicle;

	private Vehicle[] selectedVehicles;

	private List<Flat> flats;

	private Set<Long> blocks;

	private Set<Long> numbers;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getFlats(personService.getPerson());
			model = new VehicleDataModel(vehicleService.getVehicles(personService.getPerson()));
			vehicle = new Vehicle();
			vehicle.setDomain(flats.get(0));
			blocks = new TreeSet<Long>();
			numbers = new TreeSet<Long>();
			for (Flat flat : flats) {
				blocks.add(flat.getBlock());
				numbers.add(flat.getNumber());
			}
			filters = new HashMap<String, Object>();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onVehicleSave() {
		try {
			vehicle = vehicleService.register(vehicle);
			model.addModel(vehicle);
			FacesContext context = FacesContext.getCurrentInstance();
			String component = context.getViewRoot().findComponent("outputMsg").getClientId();
			context.addMessage(component, new FacesMessage(FacesMessage.SEVERITY_INFO, "veiculo registrado com sucesso", ""));
		} catch (Exception e) {
			FacesContext context = FacesContext.getCurrentInstance();
			String component = context.getViewRoot().findComponent("outputMsg").getClientId();
			context.addMessage(component, new FacesMessage(FacesMessage.SEVERITY_INFO, e.getMessage(), ""));
		}
	}

	public void onVehicleCreate() throws Exception {
		vehicle = new Vehicle();
		vehicle.setDomain(new Flat());
	}

	public void onVehiclesDelete() throws Exception {
		for (Vehicle vehicle : selectedVehicles) {
			vehicleService.removeFrom(vehicle);
		}
	}

	public void onVehicleDelete() throws Exception {
		vehicleService.removeFrom(model.getRowData());
		model.removeModel(model.getRowData());
		FacesContext context = FacesContext.getCurrentInstance();
		String component = context.getViewRoot().findComponent("outputMsg").getClientId();
		context.addMessage(component, new FacesMessage(FacesMessage.SEVERITY_INFO, "exclusao realizada com sucesso", ""));
	}

	public void onVehicleSearch() throws Exception {
		filters.put("license", license);
		model.filter(filters);
	}

	public void onFlatSelect(ValueChangeEvent event) throws Exception {
		Flat flat = flats.get(Integer.valueOf((String) event.getNewValue()));
		vehicle.setDomain(flat);
	}

	public void onBlockSelect(AjaxBehaviorEvent event) throws Exception {
		filters.put("flat.block", block);
		model.filter(filters);
	}

	public void onNumberSelect(AjaxBehaviorEvent event) throws Exception {
		filters.put("flat.number", number);
		model.filter(filters);
	}

	public void onPictureUpload() {
		
	}

	public VehicleDataModel getModel() {
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

	public Set<Long> getBlocks() {
		return blocks;
	}

	public void setBlocks(Set<Long> blocks) {
		this.blocks = blocks;
	}

	public Long getBlock() {
		return block;
	}

	public void setBlock(Long block) {
		this.block = block;
	}

	public Set<Long> getNumbers() {
		return numbers;
	}

	public void setNumbers(Set<Long> numbers) {
		this.numbers = numbers;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	
}

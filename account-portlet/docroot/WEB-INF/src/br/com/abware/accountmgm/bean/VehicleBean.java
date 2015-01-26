package br.com.abware.accountmgm.bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.VehicleDataModel;
import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.model.VehicleType;
import br.com.abware.jcondo.core.model.Flat;

@ManagedBean
@ViewScoped
public class VehicleBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(VehicleBean.class);

	private VehicleDataModel model;

	private HashMap<String, Object> filters;

	private Vehicle vehicle;

	private String license;

	private VehicleType type;

	private List<VehicleType> types;

	private Flat flat;

	private Flat selectedFlat;

	private List<Flat> flats;

	private Vehicle[] selectedVehicles;

	private Set<Long> blocks;

	private Long block;

	private Set<Long> numbers;

	private Long number;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getFlats(personService.getPerson());
			model = new VehicleDataModel(vehicleService, flats);

			blocks = new TreeSet<Long>();
			numbers = new TreeSet<Long>();
			for (Flat flat : flats) {
				blocks.add(flat.getBlock());
				numbers.add(flat.getNumber());
			}

			filters = new HashMap<String, Object>();
			types = Arrays.asList(VehicleType.values());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onVehicleSave() {
		vehicleService.register(vehicle);
		Flat[] fs = selectedFlat == null ? (Flat[]) flats.toArray() : new Flat[] {selectedFlat};
		vehicleService.assignTo(vehicle, fs);
	}

	public void onVehicleDelete() {
		vehicleService.removeFrom(vehicle, (Flat[]) flats.toArray());
		vehicleService.unregister(vehicle);
	}

	public void onVehicleSearch() throws Exception {
		filters.put("license", license);
		filters.put("type", type);
		filters.put("block", flat != null ? flat.getBlock() : null);
		filters.put("number", flat != null ? flat.getNumber() : null);
		model.filter(model, filters);
	}

	public void onTypeSelect() {
		vehicleService.changeType(vehicle);
	}

	public void onFlatAdd() {
		//vehicleService.addToDomain(vehicle, flat);
	}

	public void onFlatRemove() {
		//vehicleService.removeFromDomain(vehicle, flat);
	}

	public void onPictureUpload() {
		
	}
	
	public List<Flat> getVehicleFlats(Vehicle vehicle) {
		return null;
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

	public VehicleType getType() {
		return type;
	}

	public void setType(VehicleType type) {
		this.type = type;
	}

	public List<VehicleType> getTypes() {
		return types;
	}

	public void setTypes(List<VehicleType> types) {
		this.types = types;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public Flat getSelectedFlat() {
		return selectedFlat;
	}

	public void setSelectedFlat(Flat selectedFlat) {
		this.selectedFlat = selectedFlat;
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

package br.com.abware.accountmgm.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.VehicleDataModel;
import br.com.abware.accountmgm.model.Flat;

@ManagedBean
@ViewScoped
public class VehicleBean {

	private static Logger LOGGER = Logger.getLogger(VehicleBean.class);
	
	private static final VehicleDataModel model = null;
	
	private String type;
	
	private List<String> types;
	
	private Flat flat;
	
	private List<Flat> selectedFlats;
	
	@PostConstruct
	public void init() {
		
	}
	
	public void onVehicleCreate() {
		
	}
	
	public void onVehicleDelete() {
		
	}
	
	public void onVehicleEdit() {
		
	}
	
	public void onTypeSelect() {
		
	}
	
	public void onFlatSelect() {
		
	}

	public VehicleDataModel getModel() {
		return model;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public List<Flat> getSelectedFlats() {
		return selectedFlats;
	}

	public void setSelectedFlats(List<Flat> selectedFlats) {
		this.selectedFlats = selectedFlats;
	}
	
	
}

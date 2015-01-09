package br.com.abware.accountmgm.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.FlatDataModel;
import br.com.abware.accountmgm.model.Flat;
import br.com.abware.accountmgm.model.FlatUser;

@ManagedBean
@ViewScoped
public class FlatBean {

	private static Logger LOGGER = Logger.getLogger(FlatBean.class);

	private static final FlatDataModel model = null;

	private FlatUser user;
	
	private List<Flat> flats;

	private List<Flat> selectedFlats;

	private List<String> flatBlocks;

	private List<String> flatNumbers;

	@PostConstruct
	public void init() {
		flats = Flat.getFlats(0);
	}

	public void onFlatCreate() {
		
	}
	
	public void onFlatDelete() {
		
	}
	
	public void onFlatEdit() {
		
	}
	
	public void onFlatBlockSelect() {
		
	}
	
	public void onFlatNumberSelect() {
		
	}

	public boolean hasPermission() {
		return false;
	}
	
	public List<FlatUser> getOwners(Flat flat) {
		return null;
	}
	
	public List<FlatUser> getRenters(Flat flat) {
		return null;
	}

	public static FlatDataModel getModel() {
		return model;
	}

	public FlatUser getUser() {
		return user;
	}

	public void setUser(FlatUser user) {
		this.user = user;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

	public List<Flat> getSelectedFlats() {
		return selectedFlats;
	}

	public void setSelectedFlats(List<Flat> selectedFlats) {
		this.selectedFlats = selectedFlats;
	}

	public List<String> getFlatBlocks() {
		return flatBlocks;
	}

	public void setFlatBlocks(List<String> flatBlocks) {
		this.flatBlocks = flatBlocks;
	}

	public List<String> getFlatNumbers() {
		return flatNumbers;
	}

	public void setFlatNumbers(List<String> flatNumbers) {
		this.flatNumbers = flatNumbers;
	}	
}

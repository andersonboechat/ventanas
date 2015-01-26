package br.com.abware.accountmgm.model;

import br.com.abware.jcondo.core.model.BaseModel;

public class Vehicle implements BaseModel {

	private long id;
	
	private String picture;

	private String license;

	private VehicleType type;

	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
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

}

package br.com.abware.accountmgm.model;

import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.core.model.Flat;

public class Vehicle implements BaseModel {

	private long id;

	private String name;

	private String picture;

	private String license;

	private Flat flat;

	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		this.license = license.toUpperCase();
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

}

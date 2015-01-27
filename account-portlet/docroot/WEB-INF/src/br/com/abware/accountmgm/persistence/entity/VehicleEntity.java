package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the jco_vehicle database table.
 * 
 */
@Entity
@Table(name="jco_vehicle")
public class VehicleEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private long flatId;

	private String license;

	private String name;

	private String picture;

	public VehicleEntity() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFlatId() {
		return flatId;
	}

	public void setFlatId(long flatId) {
		this.flatId = flatId;
	}

	public String getLicense() {
		return this.license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicture() {
		return this.picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

}
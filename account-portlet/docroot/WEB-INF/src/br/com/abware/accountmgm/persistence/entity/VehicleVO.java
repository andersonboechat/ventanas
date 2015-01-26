package br.com.abware.accountmgm.persistence.entity;

import java.util.List;

import javax.persistence.*;


/**
 * The persistent class for the jco_vehicle database table.
 * 
 */
@Entity
@Table(name="jco_vehicle")
public class VehicleVO extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private String license;

	private String picture;

	private int type;
	
	@OneToMany
	@JoinTable(name="jco_vehicle_flats", joinColumns={@JoinColumn(name="vehicleId", referencedColumnName="id")}, inverseJoinColumns={@JoinColumn(name="flatId", referencedColumnName="organizationId")})
	private List<FlatVO> flats;
	
	public VehicleVO() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLicense() {
		return this.license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getPicture() {
		return this.picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
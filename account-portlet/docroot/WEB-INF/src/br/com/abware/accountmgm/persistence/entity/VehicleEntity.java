package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
	@GeneratedValue
	private long id;

	private long domainId;

	@Column(updatable=false)
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

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
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
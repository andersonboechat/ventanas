package br.com.abware.accountmgm.model;

import br.com.abware.jcondo.core.model.BaseModel;

public class Parking implements BaseModel {

	private long id;
	
	private String code;

	private long domainId;

	private ParkingType type;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public ParkingType getType() {
		return type;
	}

	public void setType(ParkingType type) {
		this.type = type;
	}

}

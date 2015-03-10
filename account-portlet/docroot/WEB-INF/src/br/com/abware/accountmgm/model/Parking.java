package br.com.abware.accountmgm.model;

import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.core.model.Domain;

public class Parking extends AbstractModel {

	private long id;

	private String code;

	private ParkingType type;

	private Domain ownerDomain;

	private Domain renterDomain;

	private Vehicle vehicle;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{id:").append(id).append(", code:").append(code)
		  .append(", type:").append(type.getLabel())
		  .append(", domain: ").append(ownerDomain).append("}");

		return sb.toString();
	}
	
	public boolean equals(BaseModel obj) {
		return super.equals(obj) || (code != null && code.equalsIgnoreCase(((Parking) obj).getCode()));
	}	

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

	public ParkingType getType() {
		return type;
	}

	public void setType(ParkingType type) {
		this.type = type;
	}

	public Domain getOwnerDomain() {
		return ownerDomain;
	}

	public void setOwnerDomain(Domain domain) {
		this.ownerDomain = domain;
	}

	public Domain getRenterDomain() {
		return renterDomain;
	}

	public void setRenterDomain(Domain renterDomain) {
		this.renterDomain = renterDomain;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

}

package br.com.abware.accountmgm.model;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

public class VehicleAccessLog extends AbstractModel {

	private long id;

	private AccessType type;

	private Date date;

	private Vehicle vehicle;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{id:").append(id).append(", type:")
		  .append(type.getLabel()).append(", date: ")
		  .append(DateFormatUtils.format(date, "dd/MM/yyyy HH:mm:ss"))
		  .append(", vehicle:").append(vehicle).append("}");

		return sb.toString();
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AccessType getType() {
		return type;
	}

	public void setType(AccessType type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

}

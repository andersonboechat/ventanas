package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="jco_vehicle_access_log")
@DiscriminatorValue(value="1")
public class VehicleAccessLogEntity extends AccessLogEntity {

	private static final long serialVersionUID = 1L;

	private long vehicleId;

	public long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

}

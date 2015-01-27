package br.com.abware.accountmgm.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


@Entity
@Table(name="jco_parking_domain")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class RentedParkingEntity extends ParkingEntity {

	private static final long serialVersionUID = 1L;

	private long renterDomainId;

	public long getRenterDomainId() {
		return renterDomainId;
	}

	public void setRenterDomainId(long renterDomainId) {
		this.renterDomainId = renterDomainId;
	}

}

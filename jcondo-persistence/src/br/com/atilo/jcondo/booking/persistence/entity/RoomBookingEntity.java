package br.com.atilo.jcondo.booking.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the rb_booking database table.
 * 
 */
@Entity
@Table(name="jco_booking")
public class RoomBookingEntity extends BookingEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="resourceId", updatable=false)
	private RoomEntity resource;

	public RoomBookingEntity() {
	}

	public RoomEntity getResource() {
		return this.resource;
	}

	public void setResource(RoomEntity resource) {
		this.resource = resource;
	}

}
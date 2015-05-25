package br.com.atilo.jcondo.booking.persistence.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.atilo.jcondo.core.persistence.entity.PersonEntity;

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

	@ManyToMany
	@JoinTable(name="jco_booking_person", 
			   joinColumns={@JoinColumn(name="bookingId", referencedColumnName="id")}, 
			   inverseJoinColumns={@JoinColumn(name="personId", referencedColumnName="id")})
	private List<PersonEntity> guests;

	public RoomBookingEntity() {
	}

	public RoomEntity getResource() {
		return this.resource;
	}

	public void setResource(RoomEntity resource) {
		this.resource = resource;
	}

	public List<PersonEntity> getGuests() {
		return guests;
	}

	public void setGuests(List<PersonEntity> guests) {
		this.guests = guests;
	}

}
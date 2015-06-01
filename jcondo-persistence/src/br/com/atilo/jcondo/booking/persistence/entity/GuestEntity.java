package br.com.atilo.jcondo.booking.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import br.com.atilo.jcondo.core.persistence.entity.BaseEntity;
import br.com.atilo.jcondo.core.persistence.entity.PersonEntity;



/**
 * The persistent class for the jco_booking_guest database table.
 * 
 */
@Entity
@Table(name="jco_booking_guest")
public class GuestEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private String firstName;

	private String lastName;

	@ManyToOne
	@JoinColumn(name="bookingId")
	private RoomBookingEntity booking;

	@OneToOne
	@JoinColumn(name="personId")
	private PersonEntity person;


	public GuestEntity() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public RoomBookingEntity getBooking() {
		return booking;
	}

	public void setBooking(RoomBookingEntity booking) {
		this.booking = booking;
	}

	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(PersonEntity person) {
		this.person = person;
	}

}
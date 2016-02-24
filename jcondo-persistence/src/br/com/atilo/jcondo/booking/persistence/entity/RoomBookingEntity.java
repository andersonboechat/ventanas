package br.com.atilo.jcondo.booking.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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

	@OneToMany(cascade=CascadeType.ALL, mappedBy="booking", orphanRemoval=true)
	private List<GuestEntity> guests;

	@OneToOne
	@JoinColumn(name="noteId")
	private BookingNoteEntity note;

	public RoomBookingEntity() {
		this.guests = new ArrayList<GuestEntity>();
	}

	public RoomEntity getResource() {
		return this.resource;
	}

	public void setResource(RoomEntity resource) {
		this.resource = resource;
	}

	public List<GuestEntity> getGuests() {
		return guests;
	}

	public void setGuests(List<GuestEntity> guests) {
		this.guests = guests;
	}

	public BookingNoteEntity getNote() {
		return note;
	}

	public void setNote(BookingNoteEntity note) {
		this.note = note;
	}

}
package br.com.atilo.jcondo.booking.persistence.entity;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.atilo.jcondo.core.persistence.entity.BaseEntity;
import br.com.atilo.jcondo.core.persistence.entity.PersonEntity;

import java.sql.Time;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * The persistent class for the rb_booking database table.
 * 
 */
@Entity
@Table(name="jco_booking")
public class BookingEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@Enumerated(EnumType.ORDINAL)
	private BookingStatus status;
	
	private double price;
	
	@OneToOne
	@JoinColumn(name="resourceId", updatable=false)
	private PersonEntity person;

	@ManyToOne
	@JoinColumn(name="resourceId", updatable=false)
	private RoomEntity room;

	public BookingEntity() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BookingStatus getStatus() {
		return this.status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public RoomEntity getRoom() {
		return this.room;
	}

	public void setRoom(RoomEntity room) {
		this.room = room;
	}

}
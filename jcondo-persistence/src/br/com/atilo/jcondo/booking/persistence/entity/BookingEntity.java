package br.com.atilo.jcondo.booking.persistence.entity;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.atilo.jcondo.core.persistence.entity.BaseEntity;
import br.com.atilo.jcondo.core.persistence.entity.PersonEntity;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


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

	@Temporal(TemporalType.DATE)
	private Date date;
	
	@Temporal(TemporalType.TIME)
	private Date beginTime;
	
	@Temporal(TemporalType.TIME)
	private Date endTime;

	@Enumerated(EnumType.ORDINAL)
	private BookingStatus status;
	
	private double price;
	
	@OneToOne
	@JoinColumn(name="personId", updatable=false)
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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

	public PersonEntity getPerson() {
		return person;
	}

	public void setPerson(PersonEntity person) {
		this.person = person;
	}

	public RoomEntity getRoom() {
		return this.room;
	}

	public void setRoom(RoomEntity room) {
		this.room = room;
	}

}
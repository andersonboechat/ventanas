package br.com.abware.agenda.persistence.entity;

import br.com.abware.agenda.BookingStatus;

import java.sql.Time;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the rb_booking database table.
 * 
 */
@Entity
@Table(name="rb_booking")
public class Booking extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BookingPK id;

	@Enumerated(EnumType.ORDINAL)
	private BookingStatus status;
	
	private double price;

	@ManyToOne
	@JoinColumn(name="roomId", insertable=false, updatable=false)
	private Room room;

	private long flatId;
	
	private long userId;

	public Booking() {
		this.id = new BookingPK();
	}

	public BookingPK getId() {
		return this.id;
	}

	public void setId(BookingPK id) {
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

	public Room getRoom() {
		return this.room;
	}

	public void setRoom(Room room) {
		this.room = room;
		this.id.setRoomId(room.getId());
	}

	public long getFlatId() {
		return flatId;
	}

	public void setFlatId(long flatId) {
		this.flatId = flatId;
	}

	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return id.getDate();	
	}
	
	public void setDate(Date date) {
		this.id.setDate(date);
	}
	
	public Time getStartTime() {
		return id.getStartTime();
	}

	public void setStartTime(Time startTime) {
		this.id.setStartTime(startTime);
	}

	public Time getEndTime() {
		return id.getEndTime();
	}

	public void setEndTime(Time endTime) {
		this.id.setEndTime(endTime);
	}	
}
package br.com.atilo.jcondo.booking.persistence.entity;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The primary key class for the rb_booking database table.
 * 
 */
@Embeddable
public class BookingPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int roomId;

	@Temporal(TemporalType.DATE)
	private Date date;

	private Time startTime;	

	private Time endTime;

	public BookingPK() {
	}
	
	public BookingPK(int roomId, Date date, Time startTime, Time endTime) {
		this.roomId = roomId;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BookingPK)) {
			return false;
		}
		BookingPK castOther = (BookingPK)other;
		return 
			this.date.equals(castOther.date)
			&& (this.roomId == castOther.roomId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.date.hashCode();
		hash = hash * prime + this.roomId;
		
		return hash;
	}
	

}
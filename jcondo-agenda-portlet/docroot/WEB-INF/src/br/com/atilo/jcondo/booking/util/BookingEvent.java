package br.com.atilo.jcondo.booking.util;

import java.util.EventObject;

import br.com.abware.jcondo.booking.model.RoomBooking;

public class BookingEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	
	private EventType type;

	public BookingEvent(RoomBooking booking, EventType type) {
		super(booking);
		this.type = type;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}
}

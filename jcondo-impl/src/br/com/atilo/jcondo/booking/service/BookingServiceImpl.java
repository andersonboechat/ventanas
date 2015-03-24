package br.com.atilo.jcondo.booking.service;

import java.util.Date;
import java.util.List;

import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;

public class BookingServiceImpl {
	
	public static final int BKG_MIN_HOUR = 10;
	
	public static final int BKG_MAX_HOUR = 22;
	
	public static final int BKG_RANGE_HOUR = 4;
	
	public static final int BKG_CANCEL_DEADLINE = 7;

	public static final int BKG_MAX_DAYS = 90;

	public List<RoomBooking> getBookings(Room room, Date start, Date end) {
		// TODO Auto-generated method stub
		return null;
	}

	public void delete(RoomBooking booking) {
		// TODO Auto-generated method stub
		
	}

	public RoomBooking book(RoomBooking booking) {
		return null;
	}

	public void cancel(RoomBooking booking) {
		// TODO Auto-generated method stub
		
	}

}

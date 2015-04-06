package br.com.atilo.jcondo.occurrence.bean;

import java.util.Observable;
import java.util.PropertyResourceBundle;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.atilo.jcondo.core.service.FlatServiceImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;
import br.com.atilo.jcondo.occurrence.service.OccurrenceServiceImpl;


public abstract class BaseBean extends Observable {

	protected static PersonServiceImpl personService = new PersonServiceImpl();
	
	protected static FlatServiceImpl flatService = new FlatServiceImpl();
	
	protected static OccurrenceServiceImpl occurrenceService = new OccurrenceServiceImpl();

	public String getBookingStyle(RoomBooking booking) {
		String style;

		if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
			style = "cld.bkg.style.room." + booking.getResource().getId();
		} else {
			style = "bkg.style.room." + booking.getResource().getId();
		}

		return PropertyResourceBundle.getBundle("application").getString(style);
	}

	public String getRoomStyle(Room room) {
		return PropertyResourceBundle.getBundle("application").getString("bkg.style.room." + room.getId());
	}

}

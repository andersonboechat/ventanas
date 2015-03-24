package br.com.atilo.jcondo.booking.bean;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;

import br.com.atilo.jcondo.booking.service.BookingServiceImpl;

public class CalendarModel extends LazyScheduleModel {

	private static final long serialVersionUID = 1L;
	
	private BookingServiceImpl bookingService;
	
	private Room room;
	 
	public CalendarModel(BookingServiceImpl bookingService, Room room) {
		super();
		this.bookingService = bookingService;
		this.room = room;
	}
	
	@Override
    public void loadEvents(Date start, Date end) {
    	clear();
		for (RoomBooking b : bookingService.getBookings(room, start, end)) {
			try {
				addEvent(createEvent(b));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Room getRoom() {
		return room;
	}

	public DefaultScheduleEvent createEvent(RoomBooking booking) throws Exception {
		DefaultScheduleEvent event; 
		StringBuffer sb = new StringBuffer();
		sb.append(booking.getFlat().getBlock()).append("/")
		  .append(StringUtils.leftPad(String.valueOf(booking.getFlat().getNumber()), 4, "0"))
		  .append(" ").append(booking.getStatus().getLabel()); 

		event = new DefaultScheduleEvent(sb.toString(), 
										 booking.getDateIn(), 
										 booking.getDateOut(), 
										 getBookingStyleClass(booking));
		event.setData(booking);
		return event;
	}
	
	private String getBookingStyleClass(RoomBooking booking) {
		String style;

		if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
			style = "cld.bkg.style.room.";
		} else {
			style = "bkg.style.room.";
		}

		return style;
	}

}

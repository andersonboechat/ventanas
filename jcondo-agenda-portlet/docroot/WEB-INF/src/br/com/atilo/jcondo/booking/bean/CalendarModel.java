package br.com.atilo.jcondo.booking.bean;

import java.util.Date;
import java.util.PropertyResourceBundle;

import org.apache.commons.lang.time.DateFormatUtils;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Flat;

import br.com.atilo.jcondo.booking.service.RoomBookingServiceImpl;

public class CalendarModel extends LazyScheduleModel {

	private static final long serialVersionUID = 1L;
	
	private RoomBookingServiceImpl bookingService;
	
	private Room room;
	 
	public CalendarModel(RoomBookingServiceImpl bookingService, Room room) {
		super();
		this.bookingService = bookingService;
		this.room = room;
	}
	
	@Override
    public void loadEvents(Date start, Date end) {
    	clear();
    	try {
    		for (RoomBooking b : bookingService.getBookings(room, start, end)) {
    			try {
    				addEvent(createEvent(b));
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Room getRoom() {
		return room;
	}

	public DefaultScheduleEvent createEvent(RoomBooking booking) throws Exception {
		DefaultScheduleEvent event; 
		StringBuffer sb = new StringBuffer();
//		sb.append(((Flat) booking.getDomain()).getBlock()).append("/")
//		  .append(StringUtils.leftPad(String.valueOf(((Flat) booking.getDomain()).getNumber()), 4, "0"))
//		  .append(" ").append(PropertyResourceBundle.getBundle("Language").getString(booking.getStatus().getLabel()));
		if (booking.getDomain() instanceof Flat) {
			sb.append("Apt. ").append(((Flat) booking.getDomain()).getNumber()).append(" - Bl. ")
			  .append(((Flat) booking.getDomain()).getBlock()).append(" ")
			  .append(DateFormatUtils.format(booking.getBeginDate(), "HH:mm'h'"))
			  .append(DateFormatUtils.format(booking.getEndDate(), "'-'HH:mm'h'"));
		} else {
			sb.append("Indisponível");
		}
		

		event = new DefaultScheduleEvent(sb.toString(), booking.getBeginDate(), booking.getEndDate(), getBookingStyleClass(booking));
		event.setData(booking);
		return event;
	}
	
	private String getBookingStyleClass(RoomBooking booking) {
		String style;

		if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
			style = "cld.bkg.style.room." + booking.getResource().getId();
		} else {
			style = "bkg.style.room." + booking.getResource().getId();
		}

		return PropertyResourceBundle.getBundle("application").getString(style);
	}

}

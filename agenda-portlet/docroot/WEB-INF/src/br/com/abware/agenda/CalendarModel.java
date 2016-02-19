package br.com.abware.agenda;

import java.util.Date;

import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;

import br.com.abware.agenda.bean.ScheduleBean;

public class CalendarModel extends LazyScheduleModel {

	private static final long serialVersionUID = 1L;
	
	private RoomModel roomModel;
	 
	public CalendarModel(RoomModel roomModel) {
		super();
		this.roomModel = roomModel;
	}
	
	@Override
    public void loadEvents(Date start, Date end) {
    	clear();
    	BookingModel model = new BookingModel();
		for (BookingModel b : model.getBookings(roomModel, start, end)) {
			try {
				addEvent(createEvent(b));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public RoomModel getRoomModel() {
		return roomModel;
	}
	
	public DefaultScheduleEvent createEvent(BookingModel booking) throws Exception {
		String bookingName;
		
		if (booking.getFlatId() == 0) {
			bookingName = "Indisponível";
		} else {
			bookingName = booking.getFlat().getName() + " " + booking.getStatus().getLabel();
		}

		DefaultScheduleEvent event; 
		event = new DefaultScheduleEvent(bookingName, booking.getDate(), booking.getDate(), 
										 ScheduleBean.getBookingStyleClass(booking));
		event.setData(booking);
		return event;
	}
}

package br.com.atilo.jcondo.booking.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;

import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.model.Person;

@ManagedBean
@ViewScoped
public class NextBookingsBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(NextBookingsBean.class);

	private List<RoomBooking> bookings;

	private Person person;

	@PostConstruct
	public void init() {
		try {
			Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			Date tomorrow = DateUtils.truncate(DateUtils.addDays(today, 1), Calendar.DAY_OF_MONTH);

			bookings = new ArrayList<RoomBooking>();
			for (Room room : roomService.getRooms(true)) {
				bookings.addAll(bookingService.getBookings(room, today, tomorrow));	
			}
			
			if (bookings.isEmpty()) {
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "no.today.bookings", null, "no-bookings");
			}
		} catch (Exception e) {
			LOGGER.fatal("");
		}
	}
	
	public void onCheckIn() {
		
	}

	public void onCheckOut() {
		
	}	

	public List<RoomBooking> getBookings() {
		return bookings;
	}

	public void setBookings(List<RoomBooking> bookings) {
		this.bookings = bookings;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}

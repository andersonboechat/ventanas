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
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

import br.com.abware.jcondo.booking.model.Guest;
import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.model.Person;

@ManagedBean
@ViewScoped
public class NextBookingsBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(NextBookingsBean.class);

	private List<RoomBooking> bookings;

	private RoomBooking booking;
	
	private List<Room> rooms;
	
	private Person person;

	@PostConstruct
	public void init() {
		try {
			Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			Date tomorrow = DateUtils.truncate(DateUtils.addDays(today, 1), Calendar.DAY_OF_MONTH);

			bookings = new ArrayList<RoomBooking>();
			rooms = roomService.getRooms(true);

			for (Room room : rooms) {
				bookings.addAll(bookingService.getBookings(room, today, tomorrow));	
			}

			if (!bookings.isEmpty()) {
				booking = bookings.get(0);
			}
			
//			if (bookings.isEmpty()) {
//				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "no.today.bookings", null, "no-bookings");
//			}
		} catch (Exception e) {
			LOGGER.fatal("");
		}
	}

	public void onGuestAdd(Guest guest) {
		try {
			bookingService.addGuest(guest);
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on guest add", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}	

	public void onGuestDelete(Guest guest) {
		try {
			bookingService.deleteGuest(guest);
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on guest delete", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onTabChange(TabChangeEvent event) {
		try {
			int index = ((TabView) event.getTab().getParent()).getIndex();
			booking = bookings.get(index);	
		} catch (IndexOutOfBoundsException e) {
			booking = null;
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on booking tab change", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onCheckIn(Guest guest) {
		try {
			guest = bookingService.checkInGuest(guest);
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on guest check in", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onCheckOut(Guest guest) {
//		guest.setCheckOut(!guest.isCheckOut());
//		bookingService.updateGuest(guest);		
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

	public RoomBooking getBooking() {
		return booking;
	}

	public void setBooking(RoomBooking booking) {
		this.booking = booking;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

}

package br.com.atilo.jcondo.booking.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.abware.jcondo.booking.model.Guest;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.model.Person;
import br.com.atilo.jcondo.booking.service.RoomBookingServiceImpl;
import br.com.atilo.jcondo.commons.collections.BeanSorter;

@ManagedBean
@ViewScoped
public class HistoryBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(HistoryBean.class);

	private List<RoomBooking> bookings;

	private RoomBooking booking;
	
	private Person person;

	private String firstName;

	private String lastName;	

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			bookings = bookingService.getBookings(person);
			Collections.sort(bookings, new BeanSorter<RoomBooking>("beginDate", BeanSorter.DESCENDING_ORDER));
		} catch (Exception e) {
			LOGGER.fatal("Booking history load failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "history.load.failure", null);
		}
	}

	public void onCancel(RoomBooking booking) {
		try { 
			RoomBooking rb = bookingService.cancel(booking);
			if (rb.getStatus() == BookingStatus.CANCELLED) {
				booking.setStatus(BookingStatus.CANCELLED);
			} else if (rb.getStatus() == BookingStatus.DELETED) {
				bookings.remove(booking);
			}
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "bkg.cancel.success", null);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "bkg.cancel.failure", null);
		}
	}
	
	public void onGuestAdd() {
		try {
			Guest guest = new Guest(new String(firstName), new String(lastName));

			if (CollectionUtils.isEmpty(booking.getGuests())) {
				booking.setGuests(new ArrayList<Guest>());
			}

			booking.getGuests().add(guest);
			firstName = null;
			lastName = null;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "booking.guest.add.failure", null);
		}
	}

	public void onGuestRemove(Guest guest) {
		try {
			booking.getGuests().remove(guest);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "booking.guest.remove.failure", null);
		}
	}
	
	public void onGuestsSave() {
		try {
			bookingService.updateGuests(booking);
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "booking.guests.update.success", null);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "booking.guests.update.failure", null);
		}
	}
	

	public boolean isCancelEnabled(RoomBooking booking) {
		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Date bookingDate = DateUtils.truncate(booking.getBeginDate(), Calendar.DAY_OF_MONTH);
		return !bookingDate.before(today) && booking.getStatus() != BookingStatus.CANCELLED;
	}

	public boolean hasDeadlineExceeded(RoomBooking booking) {
		if (booking.getBeginDate() != null) {
			Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			Date bookingDate = DateUtils.truncate(booking.getBeginDate(), Calendar.DAY_OF_MONTH);
			Date deadline = DateUtils.addDays(bookingDate, -RoomBookingServiceImpl.BKG_CANCEL_DEADLINE);
			return today.after(deadline);
		}
		return false;
	}

	public List<RoomBooking> getBookings() {
		return bookings;
	}

	public void setBookings(List<RoomBooking> bookings) {
		this.bookings = bookings;
	}

	public RoomBooking getBooking() {
		return booking;
	}

	public void setBooking(RoomBooking booking) {
		this.booking = booking;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}

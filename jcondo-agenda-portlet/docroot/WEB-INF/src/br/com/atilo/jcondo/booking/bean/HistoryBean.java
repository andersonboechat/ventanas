package br.com.atilo.jcondo.booking.bean;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;

import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.model.Person;
import br.com.atilo.jcondo.commons.collections.BeanSorter;

@ManagedBean
@ViewScoped
public class HistoryBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(HistoryBean.class);

	private List<RoomBooking> bookings;

	private RoomBooking booking;

	@PostConstruct
	public void init() {
		try {
			Person person = personService.getPerson();
			bookings = bookingService.getBookings(person);
			Collections.sort(bookings, new BeanSorter<RoomBooking>("beginDate", BeanSorter.ASCENDING_ORDER));
		} catch (Exception e) {
			LOGGER.fatal("Booking history load failure", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "history.load.failure", null);
		}
	}

	public void onCancel() {
		try { 
			if (booking != null) {
				bookingService.cancel(booking);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "booking.cancel.success", null);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "booking.cancel.failure", null);
		}
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
}

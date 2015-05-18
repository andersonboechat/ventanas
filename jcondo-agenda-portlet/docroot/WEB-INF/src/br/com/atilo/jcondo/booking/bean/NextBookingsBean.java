package br.com.atilo.jcondo.booking.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;

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
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "no.today.bookings", null, "no-bookings");
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

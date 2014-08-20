package br.com.abware.agenda;

import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;

import junit.framework.Assert;

import org.junit.Test;

public class BookingModelTest {

	@Test
	public void getBookingsTest() {
		List<BookingModel> bookings = new BookingModel().getBookings();
		Assert.assertTrue(!bookings.isEmpty());
	}

	@Test
	public void doBookingTest() {
		BookingModel booking = new BookingModel().doBooking(new Date(), 1, 10195);
		Assert.assertTrue(booking != null);
	}

	@Test
	public void getUserBookingsTest() {
		List<BookingModel> bookings = new BookingModel().getUserBookings(10195);
		Assert.assertTrue(!bookings.isEmpty());
	}

	@Test
	public void myTest() {
		FacesMessage.VALUES_MAP.keySet();
		FacesMessage.VALUES_MAP.values();
		
	}
}

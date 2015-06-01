package br.com.atilo.jcondo.booking.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.Interval;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.abware.jcondo.booking.model.Guest;
import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.booking.persistence.manager.RoomBookingManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;

public class RoomBookingServiceImpl {

	public static final Logger LOGGER = Logger.getLogger(RoomBookingServiceImpl.class);

	public static final int BKG_MIN_HOUR = 10;

	public static final int BKG_MAX_HOUR = 22;

	public static final int BKG_RANGE_HOUR = 4;

	public static final int BKG_CANCEL_DEADLINE = 7;

	public static final int BKG_MAX_DAYS = 90;
	
	private RoomBookingManagerImpl bookingManager = new RoomBookingManagerImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();
	
	private void checkAvailability(RoomBooking booking) throws Exception {
		List<RoomBooking> bookings = bookingManager.findByPeriod(booking.getResource(), booking.getBeginDate(), booking.getEndDate());

		if (RoomServiceImpl.CINEMA == booking.getResource().getId()) {
			List<RoomBooking> overlapCancelledBkgs = new ArrayList<RoomBooking>();
			Interval interval = new Interval(booking.getBeginDate().getTime(), booking.getEndDate().getTime());

			for (RoomBooking b : bookings) {
				Interval i = new Interval(b.getBeginDate().getTime(), b.getEndDate().getTime());

				if (interval.overlaps(i)) {
					if (!BookingStatus.CANCELLED.equals(b.getStatus())) {
						throw new BusinessException("bkg.room.already.booked", booking.getResource().getName(), 
													DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"),
													DateFormatUtils.format(booking.getBeginDate(), "HH:mm'h'"), 
													DateFormatUtils.format(booking.getEndDate(), "HH:mm'h'"));
					} else {
						overlapCancelledBkgs.add(booking);
					}
				}
			}

			for(RoomBooking b: overlapCancelledBkgs) {
				delete(b, false);
			}
		} else {
			RoomBooking b = CollectionUtils.isEmpty(bookings) ? null : bookings.get(0);

			if (b != null && !BookingStatus.CANCELLED.equals(b.getStatus())) {
				throw new BusinessException("bkg.room.already.booked", booking.getResource().getName(), 
											DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"),
											DateFormatUtils.format(booking.getBeginDate(), "HH:mm'h'"), 
											DateFormatUtils.format(booking.getEndDate(), "HH:mm'h'"));
			}
		}
	}

	private RoomBooking delete(RoomBooking booking, boolean checkPermission) throws Exception {
		if (checkPermission && !securityManager.hasPermission(booking, Permission.DELETE)) {
			throw new BusinessException("bkg.delete.denied");
		}

		RoomBooking b = bookingManager.findById(booking.getId());

		if (b == null || !BookingStatus.CANCELLED.equals(b.getStatus())) {
			throw new BusinessException("bkg.not.cancelled");			
		}

		b.setStatus(BookingStatus.DELETED);
		bookingManager.save(b);
		LOGGER.info("Booking set as deleted: " + booking);

		bookingManager.delete(b);
		LOGGER.info("Booking deleted: " + booking);
		
		return b;
	}
	
	public RoomBooking delete(RoomBooking booking) throws Exception {
		return delete(booking, true);
	}

	public RoomBooking cancel(RoomBooking booking) throws Exception {
		if (!securityManager.hasPermission(booking, Permission.UPDATE)) {
			throw new BusinessException("bkg.cancel.denied");
		}

		RoomBooking b = bookingManager.findById(booking.getId());

		if (b == null) {
			throw new BusinessException("bkg.not.found");			
		}

		if (BookingStatus.CANCELLED.equals(b.getStatus())) {
			return booking;
		}

		b.setStatus(BookingStatus.CANCELLED);
		b = bookingManager.save(b);

		LOGGER.info("Booking cancelled: " + booking);

		Date today = new Date();
		if (!today.after(DateUtils.addDays(b.getBeginDate(), -BKG_CANCEL_DEADLINE))) {
			LOGGER.info("Booking cancelled within deadline on " + DateFormatUtils.format(today, "dd/MM/yyyy"));
			b = delete(b, false);
			return null;
		}

		return b;
	}

	public List<RoomBooking> getBookings(Person person) throws Exception {
		return bookingManager.findByPerson(person);
	}

	public List<RoomBooking> getBookings(Room room, Date beginDate, Date endDate) throws Exception {
		return bookingManager.findByPeriod(room, beginDate, endDate);
	}

	public RoomBooking book(RoomBooking booking) throws Exception {
		if (!securityManager.hasPermission(booking, Permission.ADD)) {
			throw new BusinessException("bkg.create.denied");
		}

		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

		if (booking.getBeginDate().before(today)) {
			throw new BusinessException("bkg.past.date", DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"),
										DateFormatUtils.format(today, "dd/MM/yyyy"));
		}
		
		if (booking.getBeginDate().after(DateUtils.addDays(today, BKG_MAX_DAYS))) {
			throw new BusinessException("bkg.over.date", DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"), BKG_MAX_DAYS, 
										DateFormatUtils.format(DateUtils.addDays(booking.getBeginDate(), -BKG_MAX_DAYS), "dd/MM/yyyy"));
		}

		Date minDate = DateUtils.setHours(booking.getBeginDate(), BKG_MIN_HOUR);
		Date maxDate = DateUtils.setHours(booking.getEndDate(), BKG_MAX_HOUR);

		if (booking.getBeginDate().equals(booking.getEndDate()) || booking.getBeginDate().after(booking.getEndDate())) {
			throw new BusinessException("bkg.time.invalid.range", DateFormatUtils.format(booking.getBeginDate(), "HH:mm'h'"), 
										DateFormatUtils.format(booking.getEndDate(), "HH:mm'h'"));
		}

		if (booking.getBeginDate().before(minDate)) {
			throw new BusinessException("bkg.time.bound.low", DateFormatUtils.format(booking.getBeginDate(), "HH:mm'h'"), 
										DateFormatUtils.format(minDate, "HH:mm'h'"));
		}

		if (booking.getEndDate().after(maxDate)) {
			throw new BusinessException("bkg.time.bound.high", DateFormatUtils.format(booking.getBeginDate(), "HH:mm'h'"), 
										DateFormatUtils.format(maxDate, "HH:mm'h'"));
		}

		if (RoomServiceImpl.CINEMA == booking.getResource().getId()) {
			if (booking.getEndDate().after(DateUtils.addHours(booking.getBeginDate(), BKG_RANGE_HOUR))) {
				throw new BusinessException("bkg.time.exceeded.range", booking.getResource().getName(), 
											BKG_RANGE_HOUR + " horas");
			}
		}
		
		checkAvailability(booking);

		booking.setStatus(BookingStatus.BOOKED);
		return bookingManager.save(booking);
	}

	@SuppressWarnings("unchecked")
	public void updateGuests(RoomBooking booking) throws Exception {
//		if (!securityManager.hasPermission(booking, Permission.UPDATE)) {
//			throw new BusinessException("bkg.guests.edit.denied");
//		}

		RoomBooking rb = bookingManager.findById(booking.getId());

		if (rb == null) {
			throw new BusinessException("bkg.not.found");
		}

		if (rb.getGuests() == null) {
			rb.setGuests(booking.getGuests());
		} else {
			List<Guest> removedGuests = (List<Guest>) CollectionUtils.subtract(rb.getGuests(), booking.getGuests());
			List<Guest> addedGuests = (List<Guest>) CollectionUtils.subtract(booking.getGuests(), rb.getGuests());

			rb.getGuests().removeAll(removedGuests);
			rb.getGuests().addAll(addedGuests);
		}

		bookingManager.save(rb);
	}
}

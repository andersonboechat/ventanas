package br.com.atilo.jcondo.booking.service;

import java.sql.Time;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.Interval;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.booking.persistence.manager.BookingManagerImpl;

public class RoomBookingServiceImpl {

	public static final Logger LOGGER = Logger.getLogger(RoomBookingServiceImpl.class);

	public static final int BKG_MIN_HOUR = 10;

	public static final int BKG_MAX_HOUR = 22;

	public static final int BKG_RANGE_HOUR = 4;

	public static final int BKG_CANCEL_DEADLINE = 7;

	public static final int BKG_MAX_DAYS = 90;
	
	private BookingManagerImpl bookingManager = new BookingManagerImpl();

	public List<RoomBooking> getBookings(Room room, Date beginDate, Date endDate) throws Exception {
		return bookingManager.findByPeriod(room, beginDate, endDate);
	}

	public RoomBooking book(RoomBooking booking) throws Exception {
		Date today = new Date();

		if (booking.getDate().before(today)) {
			throw new BusinessException(null, "register.past.date", DateFormatUtils.format(booking.getDate(), "dd/MM/yyyy"),
										DateFormatUtils.format(today, "dd/MM/yyyy"));
		}
		
		if (booking.getDate().after(DateUtils.addDays(today, BKG_MAX_DAYS))) {
			throw new BusinessException(null, "register.over.date", DateFormatUtils.format(booking.getDate(), "dd/MM/yyyy"), BKG_MAX_DAYS, 
										DateFormatUtils.format(DateUtils.addDays(booking.getDate(), -BKG_MAX_DAYS), "dd/MM/yyyy"));
		}

		Time minTime = Time.valueOf(BKG_MIN_HOUR + ":00:00");
		Time maxTime = Time.valueOf(BKG_MAX_HOUR + ":00:00");

		if (booking.getBeginTime().equals(booking.getEndTime()) || booking.getBeginTime().after(booking.getEndTime())) {
			throw new BusinessException(null, "register.time.invalid.range", DateFormatUtils.format(booking.getBeginTime(), "HH:mm'h'"), 
										DateFormatUtils.format(booking.getEndTime(), "HH:mm'h'"));
		}

		if (booking.getBeginTime().before(minTime)) {
			throw new BusinessException(null, "register.time.bound.low", DateFormatUtils.format(booking.getBeginTime(), "HH:mm'h'"), 
										DateFormatUtils.format(minTime, "HH:mm'h'"));
		}

		if (booking.getEndTime().after(maxTime)) {
			throw new BusinessException(null, "register.time.bound.high", DateFormatUtils.format(booking.getBeginTime(), "HH:mm'h'"), 
										DateFormatUtils.format(maxTime, "HH:mm'h'"));
		}

		if (RoomServiceImpl.CINEMA == booking.getResource().getId()) {
			if (booking.getEndTime().after(DateUtils.addHours(booking.getBeginTime(), BKG_RANGE_HOUR))) {
				throw new BusinessException(null, "register.time.exceeded.range", booking.getResource().getName(), 
											BKG_RANGE_HOUR + " horas");
			}
		}

		checkAvailability(booking);

		booking.setStatus(BookingStatus.BOOKED);
		return bookingManager.save(booking);
	}

	private void checkAvailability(RoomBooking booking) throws Exception {
		List<RoomBooking> bookings = bookingManager.findByPeriod(booking.getResource(), booking.getDate(), booking.getDate());

		if (RoomServiceImpl.CINEMA == booking.getResource().getId()) {
			List<RoomBooking> overlapCancelledBkgs = new ArrayList<RoomBooking>();
			Interval interval = new Interval(booking.getBeginTime().getTime(), booking.getEndTime().getTime());

			for (RoomBooking b : bookings) {
				Interval i = new Interval(b.getBeginTime().getTime(), b.getEndTime().getTime());

				if (interval.overlaps(i)) {
					if (!BookingStatus.CANCELLED.equals(b.getStatus())) {
						throw new BusinessException(null, "register.already.done.failure", booking.getResource().getName(), 
													DateFormatUtils.format(booking.getDate(), "dd/MM/yyyy"),
													DateFormatUtils.format(booking.getBeginTime(), "HH:mm'h'"), 
													DateFormatUtils.format(booking.getEndTime(), "HH:mm'h'"));
					} else {
						overlapCancelledBkgs.add(booking);
					}
				}
			}

			for(RoomBooking b: overlapCancelledBkgs) {
				delete(b);
			}
		} else {
			RoomBooking b = CollectionUtils.isEmpty(bookings) ? null : bookings.get(0);

			if (b != null && !BookingStatus.CANCELLED.equals(b.getStatus())) {
				throw new BusinessException(null, "register.already.done.failure", booking.getResource().getName(), 
											DateFormatUtils.format(booking.getDate(), "dd/MM/yyyy"),
											DateFormatUtils.format(booking.getBeginTime(), "HH:mm'h'"), 
											DateFormatUtils.format(booking.getEndTime(), "HH:mm'h'"));
			}
		}
	}

	public RoomBooking cancel(RoomBooking booking) throws Exception {
		RoomBooking b = bookingManager.findById(booking.getId());

		if (b == null) {
			throw new BusinessException(null, "register.booking.not.found");			
		}

		if (BookingStatus.CANCELLED.equals(b.getStatus())) {
			return booking;
		}

		b.setStatus(BookingStatus.CANCELLED);
		b = bookingManager.save(b);

		LOGGER.info("Booking cancelled: " + booking);

		Date today = new Date();
		if (!today.after(DateUtils.addDays(b.getDate(), -BKG_CANCEL_DEADLINE))) {
			LOGGER.info("Booking cancelled within deadline on " + DateFormatUtils.format(today, "dd/MM/yyyy"));
			delete(b);
			return null;
		}

		return b;
	}

	public void delete(RoomBooking booking) throws Exception {
		RoomBooking b = bookingManager.findById(booking.getId());

		if (b == null || !BookingStatus.CANCELLED.equals(b.getStatus())) {
			throw new BusinessException(null, "register.booking.not.cancelled");			
		}

		b.setStatus(BookingStatus.DELETED);
		bookingManager.save(b);
		LOGGER.info("Booking set as deleted: " + booking);

		bookingManager.delete(b);
		LOGGER.info("Booking deleted: " + booking);
	}

}

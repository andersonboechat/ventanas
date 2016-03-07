package br.com.atilo.jcondo.booking.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.Interval;

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.velocity.VelocityContext;
import com.liferay.portal.kernel.velocity.VelocityEngineUtil;
import com.liferay.util.ContentUtil;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.abware.jcondo.booking.model.Guest;
import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.booking.persistence.manager.GuestManagerImpl;
import br.com.atilo.jcondo.booking.persistence.manager.RoomBookingManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.PersonManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;
import br.com.atilo.jcondo.util.MailService;

public class RoomBookingServiceImpl {

	public static final Logger LOGGER = Logger.getLogger(RoomBookingServiceImpl.class);
	
	private static final ResourceBundle rb = ResourceBundle.getBundle("i18n");

	public static final int BKG_MIN_HOUR = 10;

	public static final int BKG_MAX_HOUR = 22;

	public static final int BKG_RANGE_HOUR = 4;

	public static final int BKG_CANCEL_DEADLINE = 7;

	public static final int BKG_MAX_DAYS = 90;
	
	private RoomBookingManagerImpl bookingManager = new RoomBookingManagerImpl();
	
	private GuestManagerImpl guestManager = new GuestManagerImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();
	
	private PersonManagerImpl personManager = new PersonManagerImpl();
	
	private void checkAvailability(RoomBooking booking) throws Exception {
		Date beginDate = DateUtils.truncate(booking.getBeginDate(), Calendar.DAY_OF_MONTH);
		Date endDate = DateUtils.truncate(DateUtils.addDays(booking.getBeginDate(), 1), Calendar.DAY_OF_MONTH);
		List<RoomBooking> bookings = bookingManager.findByPeriod(booking.getResource(), beginDate, endDate);

		if (RoomServiceImpl.CINEMA == booking.getResource().getId()) {
			List<RoomBooking> overlapCancelledBkgs = new ArrayList<RoomBooking>();
			Interval interval = new Interval(booking.getBeginDate().getTime(), booking.getEndDate().getTime());

			for (RoomBooking b : bookings) {
				Interval i = new Interval(b.getBeginDate().getTime(), b.getEndDate().getTime());

				if (interval.overlaps(i)) {
					if (!BookingStatus.CANCELLED.equals(b.getStatus())) {
						throw new BusinessException("bkg.room.already.booked", b.getResource().getName(), 
													DateFormatUtils.format(b.getBeginDate(), "dd/MM/yyyy"),
													DateFormatUtils.format(b.getBeginDate(), "HH:mm'h'"), 
													DateFormatUtils.format(b.getEndDate(), "HH:mm'h'"));
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

			if (b != null) { 
				if (!BookingStatus.CANCELLED.equals(b.getStatus())) {
				throw new BusinessException("bkg.room.already.booked", booking.getResource().getName(), 
											DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"),
											DateFormatUtils.format(booking.getBeginDate(), "HH:mm'h'"), 
											DateFormatUtils.format(booking.getEndDate(), "HH:mm'h'"));
				} else {
					delete(b, false);
				}
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

		if (checkPermission) {
			if (booking.getNote() == null || StringUtils.isEmpty(booking.getNote().getText())) {
				throw new BusinessException("bkg.note.empty");
			} else {
				b.setNote(booking.getNote());
			}
		}

		b.setStatus(BookingStatus.DELETED);
		bookingManager.save(b);
		LOGGER.info("Booking set as deleted: " + booking);

		bookingManager.delete(b);
		LOGGER.info("Booking deleted: " + booking);

		try {
			notifyBookingDelete(b);
		} catch (Exception e) {
			LOGGER.error("booking delete notify failure", e);
		}

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

		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Date bookingDate = DateUtils.truncate(b.getBeginDate(), Calendar.DAY_OF_MONTH);

		if (!today.after(DateUtils.addDays(bookingDate, -BKG_CANCEL_DEADLINE))) {
			LOGGER.info("Booking cancelled within deadline on " + DateFormatUtils.format(today, "dd/MM/yyyy"));
			b = delete(b, false);
		} else {
			try {
				notifyBookingCancel(b);
			} catch (Exception e) {
				LOGGER.error("booking cancel notify failure", e);
			}
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
		booking.setPrice(booking.getResource().getPrice());
		RoomBooking b = bookingManager.save(booking);
		
		try {
			notifyBookingCreate(b);
		} catch (Exception e) {
			LOGGER.error("booking creation notify failure", e);
		}
		
		return b;
	}
	
	private void notifyBookingCreate(RoomBooking booking) throws Exception {
		LOGGER.info("booking creation notify begin: " + booking);

		Person p = personManager.findById(booking.getPerson().getId());

		if (p == null) {
			throw new BusinessException("psn.person.not.found");
		}
		
		if (StringUtils.isEmpty(p.getEmailAddress())) {
			throw new BusinessException("psn.person.email.invalid", p);
		}

		String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/booking/mail/booking-created-notify.vm");
		
		LOGGER.debug(mailBodyTemplate);

		VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
		variables.put("personName", p.getFullName());
		variables.put("roomName", booking.getResource().getName().toUpperCase());
		variables.put("date", DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"));
		variables.put("startTime", DateFormatUtils.format(booking.getBeginDate(), "HH:mm'h'"));
		variables.put("endTime", DateFormatUtils.format(booking.getEndDate(), "HH:mm'h'"));
		variables.put("deadline", DateFormatUtils.format(DateUtils.addDays(booking.getBeginDate(), -BKG_CANCEL_DEADLINE), "dd/MM/yyyy"));
		variables.put("adm-mail", "adm@ventanasresidencial.com.br");
		variables.put("support-mail", "suporte@ventanasresidencial.com.br");
		
		//String url = helper.getPortalURL() + "/group/guest/membership?p_p_id=membershipauth_WAR_accountportlet&data=" + DateUtils.addDays(new Date(), 3).getTime() + "p" + p.getId()  + "p" + flat.getId();
		
		//variables.put("url", url);
		UnsyncStringWriter writer = new UnsyncStringWriter();
		VelocityEngineUtil.mergeTemplate("BCN", mailBodyTemplate, variables, writer);

		String mailBody = writer.toString();
		LOGGER.debug(mailBody);

		String mailTo = p.getEmailAddress();
		String mailSubject = rb.getString("booking.create.notify");

		MailService.send(mailTo, mailSubject, mailBody);

		LOGGER.info("booking creation notify end: " + booking);
	}

	private void notifyBookingCancel(RoomBooking booking) throws Exception {
		LOGGER.info("booking cancel notify begin: " + booking);

		Person p = personManager.findById(booking.getPerson().getId());

		if (p == null) {
			throw new BusinessException("psn.person.not.found");
		}
		
		if (StringUtils.isEmpty(p.getEmailAddress())) {
			throw new BusinessException("psn.person.email.invalid", p);
		}

		String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/booking/mail/booking-cancelled-notify.vm");
		
		LOGGER.debug(mailBodyTemplate);

		VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
		variables.put("personName", p.getFullName());
		variables.put("roomName", booking.getResource().getName().toUpperCase());
		variables.put("date", DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"));
		variables.put("startTime", DateFormatUtils.format(booking.getBeginDate(), "HH:mm'h'"));
		variables.put("endTime", DateFormatUtils.format(booking.getEndDate(), "HH:mm'h'"));
		variables.put("deadline", DateFormatUtils.format(DateUtils.addDays(booking.getBeginDate(), -BKG_CANCEL_DEADLINE), "dd/MM/yyyy"));

		UnsyncStringWriter writer = new UnsyncStringWriter();
		VelocityEngineUtil.mergeTemplate("BCAN", mailBodyTemplate, variables, writer);

		String mailBody = writer.toString();
		LOGGER.debug(mailBody);

		String mailTo = p.getEmailAddress();
		String mailSubject = rb.getString("booking.cancel.notify");

		MailService.send(mailTo, mailSubject, mailBody);

		LOGGER.info("booking cancel notify end: " + booking);
	}
	
	private void notifyBookingDelete(RoomBooking booking) throws Exception {
		LOGGER.info("booking delete notify begin: " + booking);

		Person p = personManager.findById(booking.getPerson().getId());

		if (p == null) {
			throw new BusinessException("psn.person.not.found");
		}
		
		if (StringUtils.isEmpty(p.getEmailAddress())) {
			throw new BusinessException("psn.person.email.invalid", p);
		}

		String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/booking/mail/booking-deleted-notify.vm");
		
		LOGGER.debug(mailBodyTemplate);

		VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
		variables.put("personName", p.getFullName());
		variables.put("roomName", booking.getResource().getName().toUpperCase());
		variables.put("date", DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"));
		variables.put("startTime", DateFormatUtils.format(booking.getBeginDate(), "HH:mm'h'"));
		variables.put("endTime", DateFormatUtils.format(booking.getEndDate(), "HH:mm'h'"));
		
		UnsyncStringWriter writer = new UnsyncStringWriter();
		VelocityEngineUtil.mergeTemplate("BDN", mailBodyTemplate, variables, writer);

		String mailBody = writer.toString();
		LOGGER.debug(mailBody);

		String mailTo = p.getEmailAddress();
		String mailSubject = rb.getString("booking.cancel.notify");

		MailService.send(mailTo, mailSubject, mailBody);

		LOGGER.info("booking delete notify end: " + booking);
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

	public Guest checkInGuest(Guest guest) throws Exception {
		Guest g = guestManager.findById(guest.getId());
		
		if (g == null) {
			throw new BusinessException("bkg.guest.not.found");
		}
		
		g.setCheckIn(g.getCheckIn() != null && g.getCheckIn() ? false : true);

		return guestManager.save(g);
	}
	
	public void addGuest(Guest guest) throws Exception {
		if (!securityManager.hasPermission(guest.getBooking(), Permission.UPDATE)) {
			throw new BusinessException("bkg.edit.denied");
		}
		
		guestManager.save(guest);
	}	
	
	public void deleteGuest(Guest guest) throws Exception {
		if (!securityManager.hasPermission(guest.getBooking(), Permission.UPDATE)) {
			throw new BusinessException("bkg.edit.denied");
		}
		
		guestManager.delete(guest);
	}
}

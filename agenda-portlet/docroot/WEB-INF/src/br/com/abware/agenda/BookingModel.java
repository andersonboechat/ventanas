package br.com.abware.agenda;

import java.lang.reflect.InvocationTargetException;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

import br.com.abware.agenda.persistence.entity.Booking;
import br.com.abware.agenda.persistence.entity.BookingPK;
import br.com.abware.agenda.persistence.entity.Room;
import br.com.abware.agenda.persistence.manager.BookingManager;
import br.com.abware.agenda.util.BeanUtils;

public class BookingModel {

	private static Logger logger = Logger.getLogger(BookingModel.class);

	private final BookingManager bm = new BookingManager();

	private long userId;

	private RoomModel room;

	private Date date;

	private Time endTime;

	private Time startTime;

	private BookingStatus status;
	
	private String comment;
	
	private String receipt;

	public BookingModel() {
	}
	
	public User getUser() throws PortalException, SystemException {
		return UserLocalServiceUtil.getUser(userId);
	}
	
	public synchronized BookingModel doBooking(Date date, int room, long userId) {
		String owner = String.valueOf("BookingModel.doBooking");
		BookingModel booking = null;
		String log = "Sala: " + room + ", Data: " + DateFormatUtils.format(date, "dd/MM/yyyy");

		logger.info("Logged in user: " + userId);

		try {
			bm.openManager(owner);

			if (!bookingExists(date, room)) {
				Booking b = bm.save(new Booking(room, date, userId), UserHelper.getLoggedUser().getUserId());
				booking = new BookingModel();
				BeanUtils.copyProperties(booking, b);
			} else {
				logger.info("Sala ja esta reservada. " + log);
			}
		} catch (Exception e) {
			logger.error("Falha ao realizar reserva de sala: " + log, e);
		} finally {
			bm.closeManager(owner);
		}


		return booking;
	}
	
	public List<BookingModel> getBookings(RoomModel room, Date fromDate, Date toDate) {
		String owner = String.valueOf("BookingModel.getBookings");
		BookingModel booking;
		ArrayList<BookingModel> bookings = new ArrayList<BookingModel>();

		try {
			bm.openManager(owner);
			Room r = new Room();
			BeanUtils.copyProperties(r, room);
			
			for (Booking b : bm.findActiveBookingsByPeriod(r, fromDate, toDate)) {
				booking = new BookingModel();
				BeanUtils.copyProperties(booking, b);
				bookings.add(booking);
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bm.closeManager(owner);
		}

		return bookings;
	}

	public List<BookingModel> getBookings(int month, int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date fromDate = calendar.getTime();
		
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date toDate = calendar.getTime();

		return getBookings(null, fromDate, toDate);
	}

	public List<BookingModel> getBookings() {
		Date today = new Date();
		Date lastDate = DateUtils.addDays(today, 90);
		return getBookings(null, today, lastDate);
	}
	
	public boolean bookingExists(Date date, int room) {
		String owner = String.valueOf("BookingModel.bookingExists");
		bm.openManager(owner);
		Booking b = bm.findById(new BookingPK(room, date));
		bm.closeManager(owner);
		return b != null && !BookingStatus.CANCELLED.equals(b.getStatus()) ? true : false;
	}

	public List<BookingModel> getUserBookings(long userId) {
		String owner = String.valueOf("BookingModel.getUserBookings");
		BookingModel booking;
		ArrayList<BookingModel> bookings = new ArrayList<BookingModel>();

		try {
			bm.openManager(owner);

			for (Booking b : bm.findBookingsByUserId(userId)) {
				booking = new BookingModel();
				BeanUtils.copyProperties(booking, b);
				bookings.add(booking);
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bm.closeManager(owner);
		}


		return bookings;		
	}
	
	public void updateStatus(BookingModel booking, BookingStatus status) throws Exception {
		if (status.equals(booking.getStatus())) {
			throw new Exception("booking already " + status.getLabel());
		}

		String owner = String.valueOf("BookingModel.updateStatus");

		try {
			bm.openManager(owner);
			Booking b = bm.findById(new BookingPK(booking.getRoom().getId(), booking.getDate()));

			if (b == null) {
				throw new Exception();
			}

			b.setStatus(status);
			bm.save(b, UserHelper.getLoggedUserId());
			booking.setStatus(status);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			bm.closeManager(owner);
		}
	}
	
	private void delete(Booking booking) {
		String owner = String.valueOf("BookingModel.delete");
		try {
			bm.openManager(owner);
			bm.delete(booking, UserHelper.getLoggedUserId());
		} finally {
			bm.closeManager(owner);
		}
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public RoomModel getRoom() {
		return room;
	}

	public void setRoom(RoomModel room) {
		this.room = room;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

}

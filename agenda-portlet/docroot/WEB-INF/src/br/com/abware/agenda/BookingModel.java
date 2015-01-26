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
import org.joda.time.Interval;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

import br.com.abware.agenda.exception.BusinessException;
import br.com.abware.agenda.persistence.entity.Booking;
import br.com.abware.agenda.persistence.entity.BookingPK;
import br.com.abware.agenda.persistence.entity.Room;
import br.com.abware.agenda.persistence.manager.BookingManager;
import br.com.abware.agenda.util.BeanUtils;

public class BookingModel {

	private static Logger logger = Logger.getLogger(BookingModel.class);

	public static final int BKG_MIN_HOUR = 9;
	
	public static final int BKG_MAX_HOUR = 22;
	
	public static final int BKG_RANGE_HOUR = 4;
	
	public static final int BKG_CANCEL_DEADLINE = 7;

	public static final int BKG_MAX_DAYS = 90;
	
	private final BookingManager bm = new BookingManager();

	private long flatId;
	
	private long userId;

	private RoomModel room;

	private Date date;

	private Time endTime;

	private Time startTime;

	private BookingStatus status;
	
	private double price;
	
	private String comment;
	
	private String receipt;

	public BookingModel() {
	}
	
	public User getUser() throws PortalException, SystemException {
		return UserLocalServiceUtil.getUser(userId);
	}

	public Flat getFlat() throws Exception {
		return Flat.getFlatById(flatId);
	}
	
	public synchronized void doBooking() throws Exception {
		String owner = String.valueOf("BookingModel.doBooking");
		Date today = new Date();

		logger.info("Logged in user: " + userId);

		if (date == null || date.before(today)) {
			throw new BusinessException("register.past.date", DateFormatUtils.format(date, "dd/MM/yyyy"),
										DateFormatUtils.format(today, "dd/MM/yyyy"));
		}
		
		if (date.after(DateUtils.addDays(today, BKG_MAX_DAYS))) {
			throw new BusinessException("register.over.date", DateFormatUtils.format(date, "dd/MM/yyyy"), BKG_MAX_DAYS, 
										DateFormatUtils.format(DateUtils.addDays(date, -BKG_MAX_DAYS), "dd/MM/yyyy"));
		}

		Time minTime = Time.valueOf(BKG_MIN_HOUR + ":00:00");
		Time maxTime = Time.valueOf(BKG_MAX_HOUR + ":00:00");
		
		if (RoomModel.CINEMA == room.getId()) {
			if (startTime == null || endTime == null) {
				throw new BusinessException("register.time.unknown");
			}
		} else {
			if (startTime == null) {
				startTime = minTime;
			}
			
			if (endTime == null) {
				endTime = maxTime;
			}
		}
		
		if (startTime.equals(endTime) || startTime.after(endTime)) {
			throw new BusinessException("register.time.invalid.range", DateFormatUtils.format(startTime, "HH:mm'h'"), 
										DateFormatUtils.format(endTime, "HH:mm'h'"));
		}

		if (startTime.before(minTime)) {
			throw new BusinessException("register.time.bound.low", DateFormatUtils.format(startTime, "HH:mm'h'"), 
										DateFormatUtils.format(minTime, "HH:mm'h'"));
		}

		if (endTime.after(maxTime)) {
			throw new BusinessException("register.time.bound.high", DateFormatUtils.format(startTime, "HH:mm'h'"), 
										DateFormatUtils.format(maxTime, "HH:mm'h'"));
		}

		if (RoomModel.CINEMA == room.getId()) {
			if (endTime.after(DateUtils.addHours(startTime, BKG_RANGE_HOUR))) {
				throw new BusinessException("register.time.exceeded.range", room.getName(), BKG_RANGE_HOUR + " horas");
			}
		}

		try {
			bm.openManager(owner);
			Booking b = getBooking();
			BeanUtils.copyProperties(b, this);
			bm.save(b, UserHelper.getLoggedUser().getUserId());
			BeanUtils.copyProperties(this, b);
		} finally {
			bm.closeManager(owner);
		}

	}
	
	private Booking getBooking() throws Exception {
		Booking b = null;

		if (RoomModel.CINEMA == room.getId()) {
			List<BookingModel> overlapCancelledBkgs = new ArrayList<BookingModel>();
			List<BookingModel> bookings = getBookings(room, date, date);
			Interval interval = new Interval(startTime.getTime(), endTime.getTime());

			for (BookingModel booking : bookings) {
				Interval i = new Interval(booking.getStartTime().getTime(), booking.getEndTime().getTime());

				if (interval.overlaps(i)) {
					if (!BookingStatus.CANCELLED.equals(booking.getStatus())) {
						throw new BusinessException("register.already.done.failure", room.getName(), 
													DateFormatUtils.format(booking.getDate(), "dd/MM/yyyy"),
													DateFormatUtils.format(booking.getStartTime(), "HH:mm'h'"), 
													DateFormatUtils.format(booking.getEndTime(), "HH:mm'h'"));
					} else {
						overlapCancelledBkgs.add(booking);
					}
				}
			}

			if (overlapCancelledBkgs.size() > 0) {
				for(BookingModel booking: overlapCancelledBkgs) {
					delete(booking, false);
				}
			}

			b = new Booking();
		} else {
			b = bm.findById(new BookingPK(room.getId(), date, startTime, endTime));

			if (b == null) {
				b = new Booking();
			} else if (!BookingStatus.CANCELLED.equals(b.getStatus())) {
				throw new BusinessException("register.already.done.failure", room.getName(), 
											DateFormatUtils.format(date, "dd/MM/yyyy"),
											DateFormatUtils.format(startTime, "HH:mm'h'"), 
											DateFormatUtils.format(endTime, "HH:mm'h'"));
			}
		}

		return b;
	}
	
	public List<BookingModel> getBookings(RoomModel room, Date fromDate, Date toDate) {
		String owner = String.valueOf("BookingModel.getBookings");
		BookingModel booking;
		ArrayList<BookingModel> bookings = new ArrayList<BookingModel>();

		try {
			bm.openManager(owner);
			Room r = new Room();
			BeanUtils.copyProperties(r, room);
			
			for (Booking b : bm.findBookingsByPeriod(r, fromDate, toDate)) {
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
	
	public boolean bookingExists(BookingModel booking) {
		String owner = String.valueOf("BookingModel.bookingExists");
		bm.openManager(owner);
		Booking b = bm.findById(new BookingPK(booking.getRoom().getId(), booking.getDate(),
											  booking.getStartTime(), booking.getEndTime()));
		bm.closeManager(owner);
		return b != null && !BookingStatus.CANCELLED.equals(b.getStatus());
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
			Booking b = bm.findById(new BookingPK(booking.getRoom().getId(), booking.getDate(),
												  booking.getStartTime(), booking.getEndTime()));

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
	
	public void delete(BookingModel booking) throws Exception {
		delete(booking, true);
	}

	private void delete(BookingModel booking, boolean checkPermission) throws Exception {
		String owner = String.valueOf("BookingModel.delete");
		try {
			if (checkPermission) {
				boolean hasPermission = false;
				User user = UserHelper.getLoggedUser();
				Role admin = RoleLocalServiceUtil.getRole(user.getCompanyId(), RoleConstants.ADMINISTRATOR);
				Role powerUser = RoleLocalServiceUtil.getRole(user.getCompanyId(), RoleConstants.POWER_USER);

				hasPermission = RoleLocalServiceUtil.hasUserRole(user.getUserId(), admin.getRoleId()) ||
									RoleLocalServiceUtil.hasUserRole(user.getUserId(), powerUser.getRoleId());

				for (long userGroupId : user.getUserGroupIds()) {
					hasPermission = hasPermission || UserGroupGroupRoleLocalServiceUtil.hasUserGroupGroupRole(userGroupId, user.getGroupId(), powerUser.getRoleId());
				}

				if(!hasPermission) {
					throw new BusinessException("register.delete.denied");
				}
			}
			bm.openManager(owner);
			Booking b = bm.findById(new BookingPK(booking.getRoom().getId(), booking.getDate(), 
												  booking.getStartTime(), booking.getEndTime()));
			BeanUtils.copyProperties(b, booking);
			bm.delete(b, UserHelper.getLoggedUserId());
		} finally {
			bm.closeManager(owner);
		}
	}
	
	
	public long getFlatId() {
		return flatId;
	}

	public void setFlatId(long flatId) {
		this.flatId = flatId;
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
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

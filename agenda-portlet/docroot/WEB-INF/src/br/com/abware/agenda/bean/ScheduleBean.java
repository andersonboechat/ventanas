package br.com.abware.agenda.bean;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.sun.faces.util.MessageFactory;

import br.com.abware.agenda.BookingModel;
import br.com.abware.agenda.BookingStatus;
import br.com.abware.agenda.CalendarModel;
import br.com.abware.agenda.Flat;
import br.com.abware.agenda.RoomModel;
import br.com.abware.agenda.UserHelper;
import br.com.abware.agenda.exception.BusinessException;

@ManagedBean
@ViewScoped
public class ScheduleBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(ScheduleBean.class);

	private static List<CalendarModel> models = initModels();
	
	private Date bookingDate;
	
	private int bkgStartTime;

	private int bkgEndTime;
	
	private Integer roomId;

	private RoomModel room;

	private String password;	

	private boolean deal;

	private List<BookingModel> userBookings;
	
	private List<Flat> flats;

	private Flat flat;

	private User resident;
	
	private BookingModel booking;
	
	@PostConstruct
	public void init() {
		bookingDate = new Date();
		room = rooms.get(0);
		roomId = room.getId();
		resident = UserHelper.getLoggedUser();
		flats = Flat.getFlats();
		Collections.sort(flats);
	}

	private static List<CalendarModel> initModels() {
		List<CalendarModel> models = new ArrayList<CalendarModel>();
		for (RoomModel r : rooms) {
			models.add(new CalendarModel(r));
		}
		return models;
	}

	public void onBookingRequest(int modelIndex) {
		LOGGER.trace("Method in");

		try {
			if (deal) {
				BookingModel bm = new BookingModel();
				bm.setRoom(room);
				bm.setDate(bookingDate);
				if (RoomModel.CINEMA == room.getId()) {
					bm.setStartTime(Time.valueOf(bkgStartTime + ":00:00"));
					bm.setEndTime(Time.valueOf(bkgEndTime + ":00:00"));
				}
				bm.setFlatId(flat.getId());
				bm.setUserId(resident.getUserId());
				bm.setStatus(BookingStatus.OPENED);

				bm.doBooking();

				models.get(modelIndex).addEvent(models.get(modelIndex).createEvent(bm));

				setMessages(FacesMessage.SEVERITY_INFO, getClientId(":tab:tabs:booking-dialog-form-" + modelIndex + ":bookingBtn"), 
							"register.success");

				if (isCancelEnable()) {
					Date deadline = DateUtils.addDays(bookingDate, -BookingModel.BKG_CANCEL_DEADLINE);
					setMessages(FacesMessage.SEVERITY_INFO, getClientId(":tab:tabs:booking-dialog-form-" + modelIndex + ":bookingBtn"), 
								"register.cancel.notify", DateFormatUtils.format(deadline, "dd/MM/yyyy"));
				}

				bookingDate = null;
				deal = false;
			} else {
				setMessages(FacesMessage.SEVERITY_WARN, getClientId(":tab:tabs:booking-dialog-form-" + modelIndex + ":bookingBtn"), "agreement.deal.unchecked");
			}

			setMessages(FacesMessage.SEVERITY_INFO, getClientId("agenda-status"), "request.search");
		} catch (BusinessException e) {
			LOGGER.warn(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_WARN, null, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.fatal(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.runtime.failure");
		}

		LOGGER.trace("Method out");
	}

	public void onBookingCancel(int modelIndex) {
		try { 
			if (booking != null) {
				booking.updateStatus(booking, BookingStatus.CANCELLED);
				setMessages(FacesMessage.SEVERITY_INFO, getClientId(":tab:tabs:event-dialog-form-" + modelIndex + ":cancelBkgBtn"), "register.cancel.success");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.cancel.failure");
		}
	}

	public void onBookingDelete() {
		try {
			booking.delete(booking);
		} catch (BusinessException e) {
			LOGGER.warn(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_WARN, null, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.runtime.failure");
		}
		
	}

	public void onBookingSelect(SelectEvent event) {
		ScheduleEvent e = (ScheduleEvent) event.getObject();
		booking = (BookingModel) e.getData();
		bookingDate = booking.getDate();
	}

	public void onDateSelect(SelectEvent e) {
		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		bookingDate = (Date) e.getObject();

		if (bookingDate.before(today)) {
			setMessages(FacesMessage.SEVERITY_WARN, getClientId("growl2"), "register.past.date", 
						DateFormatUtils.format(bookingDate, "dd/MM/yyyy"), DateFormatUtils.format(today, "dd/MM/yyyy"));
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	@Deprecated
	public void onCalendarDateSelect(SelectEvent e) {
		bookingDate = (Date) e.getObject();
		setMessages(FacesMessage.SEVERITY_INFO, getClientId("agenda-status"), "request.register");
	}	

	@Deprecated	
	public void onLoad() {
		setMessages(FacesMessage.SEVERITY_INFO, getClientId("agenda-status"), "request.search");
	}
	
	@Deprecated
	public void onCancel() {
		LOGGER.trace("Method in");

		try {
			setMessages(FacesMessage.SEVERITY_INFO, getClientId("agenda-status"), "request.search");
		} catch (RuntimeException e) {
			LOGGER.fatal(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.runtime.failure");
		}
	
		LOGGER.trace("Method out");
	}

	@Deprecated
	public void onCancelBooking(Integer index) {
		try {
			BookingModel bm = userBookings.get(index.intValue());
			bm.updateStatus(bm, BookingStatus.CANCELLED);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Deprecated
	public void onSelectRoom(ValueChangeEvent event) {
		Integer roomsIndex = (Integer) event.getNewValue() - 1;
		room = rooms.get(roomsIndex);
	}

	public void onFlatSelect(AjaxBehaviorEvent event) {
		HtmlSelectOneMenu selectOneMenu = (HtmlSelectOneMenu) event.getSource();
		Integer id = Integer.valueOf((String) selectOneMenu.getValue());
		Flat f = Flat.getFlat(id);
		if (id != -1) {
			flat = flats.get(flats.indexOf(f));	
		} else {
			flat = f;
		}
	}
	
	public void onResidentSelect(AjaxBehaviorEvent event) {
		HtmlSelectOneMenu selectOneMenu = (HtmlSelectOneMenu) event.getSource();
		Integer id = Integer.valueOf((String) selectOneMenu.getValue());

		if (id != -1) {
			List<User> users = flat.getUsers();
			resident = users.get(users.indexOf(UserLocalServiceUtil.createUser(id)));
		} else {
			resident = null;
		}
	}	
	
	public void onTabChange(TabChangeEvent event) {
		TabView tv = (TabView) event.getSource();
		int i = tv.getChildren().indexOf(event.getTab());
		room = ((CalendarModel) models.get(i)).getRoomModel();
		roomId = room.getId();
	}
   
	@Deprecated
	public boolean bookingExists(String roomId) {
		boolean exists = false;

		try {
			exists = true; //new BookingModel().bookingExists(bookingDate, Integer.parseInt(roomId));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return exists;
	}
	
	public void validateCheckbox(FacesContext context, UIComponent component, Object value) {  
		if (value instanceof Boolean && ((Boolean) value).equals(Boolean.FALSE)) {
			String clientId = component.getClientId(context);
			FacesMessage message = MessageFactory.getMessage(UIInput.REQUIRED_MESSAGE_ID, clientId);
			throw new ValidatorException(message);  
		}  
	}  

	public void validatePassword(FacesContext context, UIComponent component, Object value) {  
		if (value instanceof String) {
			String pwd = (String) value;
			if (!UserHelper.isUserPasswordMatch(UserHelper.getLoggedUser(), pwd)) {
				String clientId = component.getClientId(context);
				FacesMessage message = MessageFactory.getMessage(UIInput.REQUIRED_MESSAGE_ID, clientId);
				throw new ValidatorException(message);  
			}
		}
	}
	
	public boolean isCancelEnable() {
		if (bookingDate != null) {
			Date today = new Date();
			Date deadline = DateUtils.addDays(bookingDate, -BookingModel.BKG_CANCEL_DEADLINE);
			return deadline.after(today);
		}

		return false;
	}
	
	public boolean isTimeSelectionEnabled() {
		return room.getId() == RoomModel.CINEMA;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public int getBkgStartTime() {
		return bkgStartTime;
	}

	public void setBkgStartTime(int bkgStartTime) {
		this.bkgStartTime = bkgStartTime;
	}

	public int getBkgEndTime() {
		return bkgEndTime;
	}

	public void setBkgEndTime(int bkgEndTime) {
		this.bkgEndTime = bkgEndTime;
	}

	public RoomModel getRoom() {
		return room;
	}

	public void setRoom(RoomModel room) {
		this.room = room;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isDeal() {
		return deal;
	}

	public void setDeal(boolean deal) {
		this.deal = deal;
	}

	public List<BookingModel> getUserBookings() {
		userBookings = new BookingModel().getUserBookings(UserHelper.getLoggedUserId());
		return userBookings;
	}

	public void setUserBookings(List<BookingModel> userBookings) {
		this.userBookings = userBookings;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public User getResident() {
		return resident;
	}

	public void setResident(User resident) {
		this.resident = resident;
	}

	public BookingModel getBooking() {
		return booking;
	}

	public void setBooking(BookingModel booking) {
		this.booking = booking;
	}

	public static List<CalendarModel> getModels() {
		return models;
	}

	public static void setModels(List<CalendarModel> models) {
		ScheduleBean.models = models;
	}
	
	public static ScheduleModel getModel(int index) {
		return models.get(index);
	}

}
package br.com.atilo.jcondo.booking.bean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.sun.faces.util.MessageFactory;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.booking.service.RoomBookingServiceImpl;
import br.com.atilo.jcondo.booking.service.RoomServiceImpl;
import br.com.atilo.jcondo.booking.util.BookingEvent;
import br.com.atilo.jcondo.booking.util.EventType;
import br.com.atilo.jcondo.booking.util.collections.BookingDatePredicate;

@ManagedBean
@ViewScoped
public class CalendarBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(CalendarBean.class);

	private static final List<CalendarModel> MODELS = initModels();

	private CalendarModel model;

	private Person person;

	private List<Flat> flats;
	
	private RoomBooking booking;

	private boolean deal;

	private int beginTime;

	private int endTime;

	@PostConstruct
	public void init() {
		try {
			model = MODELS.get(0);
			person = personService.getPerson();
			flats = flatService.getFlats(person);
			booking = new RoomBooking();
			booking.setStatus(BookingStatus.BOOKED);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	private static List<CalendarModel> initModels() {
		List<CalendarModel> models = new ArrayList<CalendarModel>();

		try {
			for (Room room : RoomBean.ROOMS) {
				models.add(new CalendarModel(bookingService, room));
			}
		} catch (Exception e) {
			LOGGER.fatal("Calendar models load failure", e);
		}

		return models;
	}	

	public void onBook() {
		LOGGER.trace("Method in");

		try {
			if (deal) {
				booking.getBeginDate().setTime(DateUtils.setHours(booking.getBeginDate(), beginTime).getTime());
				booking.getEndDate().setTime(DateUtils.setHours(booking.getEndDate(), endTime).getTime());

				booking = bookingService.book(booking);
				model.addEvent(model.createEvent(booking));

				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "register.success", null);

				if (isCancelEnable()) {
					Date deadline = DateUtils.addDays(booking.getBeginDate(), -RoomBookingServiceImpl.BKG_CANCEL_DEADLINE);
					MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "register.cancel.notify", 
											new String[] {DateFormatUtils.format(deadline, "dd/MM/yyyy")});
				}

				deal = false;
				setChanged();
				notifyObservers(new BookingEvent(booking, EventType.BOOK));
			} else {
				MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "agreement.deal.unchecked", null);
			}
		} catch (BusinessException e) {
			LOGGER.warn(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.fatal(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}

		LOGGER.trace("Method out");
	}

	public void onCancel() {
		try { 
			if (booking != null) {
				booking = bookingService.cancel(booking);
				setChanged();
				if (booking.getStatus() == BookingStatus.CANCELLED) {
					notifyObservers(new BookingEvent(booking, EventType.BOOK_CANCEL));
				}
				if (booking.getStatus() == BookingStatus.DELETED) {
					notifyObservers(new BookingEvent(booking, EventType.BOOK_DELETE));
				}
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "register.cancel.success", null);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.cancel.failure", null);
		}
	}

	public void onDelete() {
		try {
			bookingService.delete(booking);
			setChanged();
			notifyObservers(new BookingEvent(booking, EventType.BOOK_DELETE));
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "register.delete.success", null);
		} catch (BusinessException e) {
			LOGGER.warn(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}

	public void onSelect(SelectEvent event) {
		ScheduleEvent e = (ScheduleEvent) event.getObject();
		RoomBooking booking = (RoomBooking) e.getData();
		if (booking.getResource().getId() == RoomServiceImpl.CINEMA || booking.getStatus() == BookingStatus.CANCELLED) {
			createBooking(e.getStartDate());
		} else {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "booking.already.exists", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onDateSelect(SelectEvent event) {
		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Date bookingDate = (Date) event.getObject();

		if (bookingDate.before(today)) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "register.past.date", new String[] {
									DateFormatUtils.format(bookingDate, "dd/MM/yyyy"), 
									DateFormatUtils.format(today, "dd/MM/yyyy")});
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}

		ScheduleEvent se = (ScheduleEvent) CollectionUtils.find(model.getEvents(), new BookingDatePredicate(bookingDate));
		RoomBooking booking = se != null ? (RoomBooking) se.getData() : null; 

		if (model.getRoom().getId() == RoomServiceImpl.CINEMA || booking == null || booking.getStatus() == BookingStatus.CANCELLED) {
			createBooking(bookingDate);
		} else {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "booking.already.exists", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void createBooking(Date bookingDate) {
		booking = new RoomBooking(person, flats.size() > 1 ? null : flats.get(0), 
								  model.getRoom(), (Date) bookingDate.clone(), (Date) bookingDate.clone());

		if (!isTimeSelectionEnabled()) {
			beginTime = RoomBookingServiceImpl.BKG_MIN_HOUR;
			endTime = RoomBookingServiceImpl.BKG_MAX_HOUR;
		} else {
			beginTime = 0;
			endTime = 0;
		}
	}
	
	public void onTabChange(TabChangeEvent event) {
		TabView tv = (TabView) event.getSource();
		int i = tv.getChildren().indexOf(event.getTab());
		model = (CalendarModel) MODELS.get(i);
	}
	
	public void validateCheckbox(FacesContext context, UIComponent component, Object value) {  
		if (value instanceof Boolean && ((Boolean) value).equals(Boolean.FALSE)) {
			FacesMessage message = MessageUtils.getMessage(UIInput.REQUIRED_MESSAGE_ID, null);
			throw new ValidatorException(message);  
		}  
	}  

	public void validatePassword(FacesContext context, UIComponent component, Object value) {  
		if (value instanceof String) {
			String pwd = (String) value;
			if (!personService.authenticate(person, pwd)) {
				String clientId = component.getClientId(context);
				FacesMessage message = MessageFactory.getMessage(UIInput.REQUIRED_MESSAGE_ID, clientId);
				throw new ValidatorException(message);  
			}
		}
	}

	public boolean isCancelEnable() {
		if (booking != null && booking.getBeginDate() != null) {
			Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			Date bookingDate = DateUtils.truncate(booking.getBeginDate(), Calendar.DAY_OF_MONTH);
			Date deadline = DateUtils.addDays(bookingDate, -RoomBookingServiceImpl.BKG_CANCEL_DEADLINE);
			return deadline.after(today);
		}

		return false;
	}

	public boolean isTimeSelectionEnabled() {
		return model.getRoom().getId() == RoomServiceImpl.CINEMA;
	}

	public ScheduleModel getModel(int index) {
		return MODELS.get(index);
	}	

	public List<Room> getRooms() {
		return RoomBean.ROOMS;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

	public List<CalendarModel> getModels() {
		return MODELS;
	}

	public CalendarModel getModel() {
		return model;
	}

	public void setModel(CalendarModel model) {
		this.model = model;
	}

	public RoomBooking getBooking() {
		return booking;
	}

	public void setBooking(RoomBooking booking) {
		this.booking = booking;
	}

	public boolean isDeal() {
		return deal;
	}

	public void setDeal(boolean deal) {
		this.deal = deal;
	}

	public int getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(int beginTime) {
		this.beginTime = beginTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

}

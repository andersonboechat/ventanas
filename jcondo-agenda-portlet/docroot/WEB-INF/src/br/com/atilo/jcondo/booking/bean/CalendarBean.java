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
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.sun.faces.util.MessageFactory;

import br.com.abware.jcondo.booking.model.BookingStatus;
import br.com.abware.jcondo.booking.model.Guest;
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

	private Person guest;
	
	private String firstName;

	private String lastName;

	private String identity;

	@PostConstruct
	public void init() {
		try {
			model = MODELS.get(0);
			person = personService.getPerson();
			flats = flatService.getPersonFlats(person);
			booking = new RoomBooking();
			booking.setStatus(BookingStatus.BOOKED);
		} catch (Exception e) {
			LOGGER.error("Failure on calendar initialization", e);
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

				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "bkg.success", null);

//				if (isDeadlineOver()) {
//					Date deadline = DateUtils.addDays(booking.getBeginDate(), -RoomBookingServiceImpl.BKG_CANCEL_DEADLINE);
//					MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "bkg.cancel.notify", 
//											new String[] {DateFormatUtils.format(deadline, "dd/MM/yyyy")});
//				}

				deal = false;
				setChanged();
				notifyObservers(new BookingEvent(booking, EventType.BOOK));
			} else {
				MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "agreement.deal.unchecked", null);
			}
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.fatal("Failure on book", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "bkg.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
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
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "bkg.cancel.success", null);
			}
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Failure on booking cancel", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "bkg.cancel.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onDelete() {
		try {
			bookingService.delete(booking);
			setChanged();
			notifyObservers(new BookingEvent(booking, EventType.BOOK_DELETE));
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "bkg.delete.success", null);
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Failure on booking delete", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "bkg.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onSelect(SelectEvent event) {
		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		ScheduleEvent e = (ScheduleEvent) event.getObject();
		RoomBooking booking = (RoomBooking) e.getData();

		if (booking.getResource().getId() == RoomServiceImpl.CINEMA || booking.getStatus() == BookingStatus.CANCELLED) {
			if (e.getStartDate().before(today)) {
				MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "bkg.past.date", 
										new String[] {DateFormatUtils.format(today, "dd/MM/yyyy")});
				RequestContext.getCurrentInstance().addCallbackParam("exception", true);
				return;
			}
			createBooking(e.getStartDate());
		} else {
			if (booking.getDomain() instanceof Flat) {
				MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "bkg.room.already.booked", 
						new String[] {booking.getResource().getName(), 
						DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"), 
						DateFormatUtils.format(booking.getBeginDate(), "HH:mm"),
						DateFormatUtils.format(booking.getEndDate(), "HH:mm")});
			} else {
				Date beginDate = DateUtils.truncate(booking.getBeginDate(), Calendar.DAY_OF_MONTH);
				Date endDate = DateUtils.truncate(booking.getEndDate(), Calendar.DAY_OF_MONTH);
				
				if (beginDate.equals(endDate)) {
					MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "bkg.room.unavailable", 
											new String[] {booking.getResource().getName(), 
											DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy")});
				} else {
					MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "bkg.room.unavailable.range", 
											new String[] {booking.getResource().getName(), 
											DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"),
											DateFormatUtils.format(booking.getEndDate(), "dd/MM/yyyy")});
				}
			}

			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onDateSelect(SelectEvent event) {
		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Date bookingDate = (Date) event.getObject();

		ScheduleEvent se = (ScheduleEvent) CollectionUtils.find(model.getEvents(), new BookingDatePredicate(bookingDate));
		RoomBooking booking = se != null ? (RoomBooking) se.getData() : null; 

		if (model.getRoom().getId() == RoomServiceImpl.CINEMA || booking == null || booking.getStatus() == BookingStatus.CANCELLED) {
			if (bookingDate.before(today)) {
				MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "bkg.past.date", 
										new String[] {DateFormatUtils.format(today, "dd/MM/yyyy")});
				RequestContext.getCurrentInstance().addCallbackParam("exception", true);
				return;
			}

			createBooking(bookingDate);
		} else {
			if (booking.getDomain() instanceof Flat) {
				MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "bkg.room.already.booked", 
						new String[] {booking.getResource().getName(), 
						DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"), 
						DateFormatUtils.format(booking.getBeginDate(), "HH:mm"),
						DateFormatUtils.format(booking.getEndDate(), "HH:mm")});
			} else {
				Date beginDate = DateUtils.truncate(booking.getBeginDate(), Calendar.DAY_OF_MONTH);
				Date endDate = DateUtils.truncate(booking.getEndDate(), Calendar.DAY_OF_MONTH);
				
				if (beginDate.equals(endDate)) {
					MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "bkg.room.unavailable", 
											new String[] {booking.getResource().getName(), 
											DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy")});
				} else {
					MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "bkg.room.unavailable.range", 
											new String[] {booking.getResource().getName(), 
											DateFormatUtils.format(booking.getBeginDate(), "dd/MM/yyyy"),
											DateFormatUtils.format(booking.getEndDate(), "dd/MM/yyyy")});
				}
			}

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

	public void onGuestAdd() {
		try {
			Guest guest = new Guest(new String(firstName), new String(lastName));

			if (CollectionUtils.isEmpty(booking.getGuests())) {
				booking.setGuests(new ArrayList<Guest>());
			}

			booking.getGuests().add(guest);
			firstName = null;
			lastName = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onGuestRemove(Guest guest) {
		try {
			booking.getGuests().remove(guest);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void onGuestSelect(SelectEvent event) {
		guest = (Person) ((AutoComplete) event.getSource()).getValue();
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

	public boolean isDeadlineOver() {
		if (booking != null && booking.getBeginDate() != null) {
			Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			Date bookingDate = DateUtils.truncate(booking.getBeginDate(), Calendar.DAY_OF_MONTH);
			Date deadline = DateUtils.addDays(bookingDate, -RoomBookingServiceImpl.BKG_CANCEL_DEADLINE);
			return today.after(deadline);
		}

		return false;
	}
	
	public List<Person> findGuests(String name) throws Exception {
		return personService.getPeople(name);
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

	public Person getGuest() {
		return guest;
	}

	public void setGuest(Person guest) {
		this.guest = guest;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

}

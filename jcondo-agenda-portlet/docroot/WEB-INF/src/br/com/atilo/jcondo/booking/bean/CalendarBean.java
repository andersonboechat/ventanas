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

import br.com.abware.jcondo.booking.model.Room;
import br.com.abware.jcondo.booking.model.RoomBooking;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.booking.service.BookingServiceImpl;
import br.com.atilo.jcondo.booking.service.RoomServiceImpl;

@ManagedBean
@ViewScoped
public class CalendarBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(CalendarBean.class);

	private static List<CalendarModel> models = initModels();

	private static List<Room> rooms;

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
			model = models.get(0);
			person = personService.getPerson();
			flats = flatService.getFlats(person);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	private static List<CalendarModel> initModels() {
		try {
			rooms = roomService.getRooms(true);
			models = new ArrayList<CalendarModel>();

			for (Room room : rooms) {
				models.add(new CalendarModel(bookingService, room));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return models;
	}	

	public void onBook() {
		LOGGER.trace("Method in");

		try {
			if (deal) {
				DateUtils.setHours(booking.getDateIn(), beginTime);
				DateUtils.setHours(booking.getDateOut(), endTime);
				
				
				
				booking = bookingService.book(booking);
				model.addEvent(model.createEvent(booking));

				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "register.success", null);

				if (isCancelEnable()) {
					Date deadline = DateUtils.addDays(booking.getDateIn(), -BookingServiceImpl.BKG_CANCEL_DEADLINE);
					MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "register.cancel.notify", 
											new String[] {DateFormatUtils.format(deadline, "dd/MM/yyyy")});
				}

				deal = false;
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

	public void onCancel(int modelIndex) {
		try { 
			if (booking != null) {
				bookingService.cancel(booking);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "register.cancel.success", null);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.cancel.failure", null);
		}
	}

	public void onDelete(int modelIndex) {
		try {
			bookingService.delete(booking);
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
		booking = (RoomBooking) e.getData();
	}

	public void onDateSelect(SelectEvent e) {
		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Date bookingDate = (Date) e.getObject();

		if (bookingDate.before(today)) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "register.past.date", new String[] {
									DateFormatUtils.format(bookingDate, "dd/MM/yyyy"), 
									DateFormatUtils.format(today, "dd/MM/yyyy")});
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}

		booking = new RoomBooking(person, model.getRoom(), bookingDate, (Date) bookingDate.clone());
		booking.setFlat(flats.size() > 1 ? null : flats.get(0));
	}

	public void onTabChange(TabChangeEvent event) {
		TabView tv = (TabView) event.getSource();
		int i = tv.getChildren().indexOf(event.getTab());
		model = (CalendarModel) models.get(i);
	}
	
	public void validateCheckbox(FacesContext context, UIComponent component, Object value) {  
		if (value instanceof Boolean && ((Boolean) value).equals(Boolean.FALSE)) {
			FacesMessage message = MessageUtils.getMessage(UIInput.REQUIRED_MESSAGE_ID, null);
			throw new ValidatorException(message);  
		}  
	}  

	public boolean isCancelEnable() {
		if (booking.getDateIn() != null) {
			Date today = new Date();
			Date deadline = DateUtils.addDays(booking.getDateIn(), -BookingServiceImpl.BKG_CANCEL_DEADLINE);
			return deadline.after(today);
		}

		return false;
	}

	public boolean isTimeSelectionEnabled() {
		return model.getRoom().getId() == RoomServiceImpl.CINEMA;
	}

	public ScheduleModel getModel(int index) {
		return models.get(index);
	}	

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
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

	public static List<CalendarModel> getModels() {
		return models;
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

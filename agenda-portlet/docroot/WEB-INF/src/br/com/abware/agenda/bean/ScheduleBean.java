package br.com.abware.agenda.bean;

import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.StreamedContent;

import com.sun.faces.util.MessageFactory;

import br.com.abware.agenda.BookingModel;
import br.com.abware.agenda.BookingStatus;
import br.com.abware.agenda.RoomModel;
import br.com.abware.agenda.UserHelper;

@ManagedBean
@SessionScoped
public class ScheduleBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(ScheduleBean.class);

	private static ScheduleModel model = initModel();

	private Date bookingDate;

	private Integer roomId;

	private RoomModel room;

	private String password;	

	private boolean deal;

	private StreamedContent agreement;

	private List<BookingModel> userBookings;

	public ScheduleBean() {
		bookingDate = new Date();
		room = rooms.get(0);
		roomId = room.getId();
	}

	private static ScheduleModel initModel() {
		ScheduleModel aModel = new DefaultScheduleModel();

		aModel = new LazyScheduleModel() {

			private static final long serialVersionUID = 1L;

			@Override
            public void loadEvents(Date start, Date end) {
            	clear();
            	BookingModel model = new BookingModel();
        		for (BookingModel b : model.getBookings(start, end)) {
        			addEvent(new DefaultScheduleEvent("", 
        											  b.getDate(), 
        											  b.getDate(), 
        											  getRoomStyleClass(b.getRoom())));
        		}
            }   
        };		

		return aModel;
	}

	public void onDoBooking() {
		LOGGER.trace("Method in");

		try {
			if (deal) {
				BookingModel bm = new BookingModel().doBooking(bookingDate, roomId, UserHelper.getLoggedUserId());
	
				if (bm != null) {
					model.addEvent(new DefaultScheduleEvent("", 
															bookingDate, 
															bookingDate, 
															getRoomStyleClass(bm.getRoom())));
				}
	
				roomId = null;
				bookingDate = null;
				deal = false;

				setMessages(FacesMessage.SEVERITY_WARN, getClientId(":booking-dialog-form:bookingBtn"), "register.success");
			} else {
				setMessages(FacesMessage.SEVERITY_WARN, getClientId(":booking-dialog-form:bookingBtn"), "agreement.deal.unchecked");
			}

			setMessages(FacesMessage.SEVERITY_INFO, getClientId("agenda-status"), "request.search");
		} catch (Exception e) {
			LOGGER.fatal(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.runtime.failure");
		}

		LOGGER.trace("Method out");
	}

	public void onDateSelect(DateSelectEvent e) {
		bookingDate = e.getDate();
		setMessages(FacesMessage.SEVERITY_INFO, getClientId("agenda-status"), "request.register");
	}
	
	public void onCalendarDateSelect(SelectEvent e) {
		bookingDate = (Date) e.getObject();
		setMessages(FacesMessage.SEVERITY_INFO, getClientId("agenda-status"), "request.register");
	}	

	public void onLoad() {
		setMessages(FacesMessage.SEVERITY_INFO, getClientId("agenda-status"), "request.search");
	}
	
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

	public void onCancelBooking(Integer index) {
		try {
			BookingModel bm = userBookings.get(index.intValue());
			bm.updateStatus(bm, BookingStatus.CANCELLED);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void onBilletPrint() {
		
	}
	
	public void onSelectRoom(ValueChangeEvent event) {
		Integer roomsIndex = (Integer) event.getNewValue() - 1;
		room = rooms.get(roomsIndex);
	}
	
    public StreamedContent getAgreement() {
        try {
	    	FacesContext context = FacesContext.getCurrentInstance();
	
	        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
	            return new DefaultStreamedContent();
	        } else {
	            String id = context.getExternalContext().getRequestParameterMap().get("id");
	            room = rooms.get(rooms.indexOf(new RoomModel(Integer.valueOf(id))));
	            String agreement = "http://" + context.getExternalContext().getRequestServerName() + room.getAgreement();
				return new DefaultStreamedContent(new URL(agreement).openStream(), "application/pdf");
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return null;
    }	

	public boolean bookingExists(String roomId) {
		boolean exists = false;

		try {
			exists = new BookingModel().bookingExists(bookingDate, Integer.parseInt(roomId));
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
	
	public ScheduleModel getModel() {
		return model;
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

	public void setAgreement(StreamedContent agreement) {
		this.agreement = agreement;
	}

	public List<BookingModel> getUserBookings() {
		userBookings = new BookingModel().getUserBookings(UserHelper.getLoggedUserId());
		return userBookings;
	}

	public void setUserBookings(List<BookingModel> userBookings) {
		this.userBookings = userBookings;
	}

}
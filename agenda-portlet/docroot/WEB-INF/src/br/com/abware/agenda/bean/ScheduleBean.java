package br.com.abware.agenda.bean;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.StreamedContent;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.sun.faces.util.MessageFactory;

import br.com.abware.agenda.BookingModel;
import br.com.abware.agenda.BookingStatus;
import br.com.abware.agenda.Flat;
import br.com.abware.agenda.RoomModel;
import br.com.abware.agenda.UserHelper;

@ManagedBean
@SessionScoped
public class ScheduleBean extends BaseBean {

	private static final Logger LOGGER = Logger.getLogger(ScheduleBean.class);

	private static ScheduleModel model;

	private static List<ScheduleModel> models;
	
	private Date bookingDate;

	private Integer roomId;

	private RoomModel room;

	private String password;	

	private boolean deal;

	private StreamedContent agreement;

	private List<BookingModel> userBookings;
	
	private List<Flat> flats;

	private Flat flat;

	private User resident;
	
	private BookingModel booking;
	
	public ScheduleBean() {
		bookingDate = new Date();
		room = rooms.get(0);
		roomId = room.getId();
		resident = UserHelper.getLoggedUser();
		flats = Flat.getFlats();
		Collections.sort(flats);

		models = new ArrayList<ScheduleModel>();
		for (RoomModel r : rooms) {
			models.add(new CalendarModel(r));
		}
		model = models.get(0);
	}

	public void onDoBooking(int modelIndex) {
		LOGGER.trace("Method in");

		try {
			if (deal) {
				BookingModel bm = new BookingModel().doBooking(bookingDate, roomId, resident.getUserId());

				if (bm != null) {
					models.get(modelIndex).addEvent(new DefaultScheduleEvent(resident.getFirstName(), 
															bookingDate, 
															bookingDate, 
															getRoomStyleClass(bm.getRoom())));
				}

				Date deadline = DateUtils.addDays(bookingDate, -7);
				setMessages(FacesMessage.SEVERITY_WARN, getClientId(":tab:tabs:booking-dialog-form-" + modelIndex + ":bookingBtn"), 
							"register.success", DateFormatUtils.format(deadline, "dd/MM/yyyy"));

				roomId = null;
				bookingDate = null;
				deal = false;
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

	public void onDoCancel() {
		try { 
			if (booking != null) {
				booking.updateStatus(booking, BookingStatus.CANCELLED);
				setMessages(FacesMessage.SEVERITY_WARN, getClientId(":event-dialog-form:cancelBookingBtn"), "register.cancel.success");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.cancel.failure");
		}
	}
	
	public void onDateSelect(SelectEvent e) {
		bookingDate = (Date) e.getObject();
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
	
	public void onSelectRoom(ValueChangeEvent event) {
		Integer roomsIndex = (Integer) event.getNewValue() - 1;
		room = rooms.get(roomsIndex);
	}

	public void onSelectFlat(AjaxBehaviorEvent event) {
		HtmlSelectOneMenu selectOneMenu = (HtmlSelectOneMenu) event.getSource();
		Integer id = Integer.valueOf((String) selectOneMenu.getValue());
		Flat f = Flat.getFlat(id);
		if (id != -1) {
			flat = flats.get(flats.indexOf(f));	
		} else {
			flat = f;
		}
	}

	public void onSelectBooking(SelectEvent event) {
		ScheduleEvent e = (ScheduleEvent) event.getObject();
		booking = (BookingModel) e.getData();
		bookingDate = booking.getDate();
	}
	
	
//	public void onSelectBooking(ScheduleEntrySelectEvent event) {
//		ScheduleEvent e = (ScheduleEvent) event.getScheduleEvent();
//		booking = (BookingModel) e.getData();
//	}
	
	public void onSelectResident(AjaxBehaviorEvent event) {
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
		model = models.get(i);
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

	public void validateFlat(FacesContext context, UIComponent component, Object value) {  
		if (value instanceof String) {
			String flatId = (String) value;
			if (Integer.valueOf(flatId) == -1) {
				String clientId = component.getClientId(context);
				FacesMessage message = MessageFactory.getMessage(UIInput.REQUIRED_MESSAGE_ID, clientId);
				throw new ValidatorException(message);  
			}
		}
	}

	public void validateResident(FacesContext context, UIComponent component, Object value) {  
		if (value instanceof String) {
			String residentId = (String) value;
			if (Integer.valueOf(residentId) == -1) {
				String clientId = component.getClientId(context);
				FacesMessage message = MessageFactory.getMessage(UIInput.REQUIRED_MESSAGE_ID, clientId);
				throw new ValidatorException(message);  
			}
		}
	}	

	public boolean test() {
		return false;
	}
	
	public boolean isCancelEnable() {
		if (bookingDate != null) {
			Date today = new Date();
			Date deadline = DateUtils.addDays(bookingDate, -7);
			return deadline.after(today);
		}
		return false;
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

	public static List<ScheduleModel> getModels() {
		return models;
	}

	public static void setModels(List<ScheduleModel> models) {
		ScheduleBean.models = models;
	}
	
	public static ScheduleModel getModel(int index) {
		return models.get(index);
	}

	public class CalendarModel extends LazyScheduleModel {

		private static final long serialVersionUID = 1L;
		
		private RoomModel roomModel;
		 
		public CalendarModel(RoomModel roomModel) {
			super();
			this.roomModel = roomModel;
		}
		
		@Override
        public void loadEvents(Date start, Date end) {
        	clear();
        	BookingModel model = new BookingModel();
    		for (BookingModel b : model.getBookings(roomModel, start, end)) {
    			try {
    				DefaultScheduleEvent event; 
    				event = new DefaultScheduleEvent(ScheduleBean.getUserFlatName(b.getUser()), 
													 b.getDate(), 
													 b.getDate(), 
													 ScheduleBean.getRoomStyleClass(b.getRoom()));
    				event.setData(b);
					addEvent(event);
				} catch (PortalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SystemException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
		}
		
		public RoomModel getRoomModel() {
			return roomModel;
		}
		
		@Override
		public boolean equals(Object arg0) {
			// TODO Auto-generated method stub
			return super.equals(arg0);
		}
	}	

}
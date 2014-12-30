package br.com.abware.agenda.bean;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.ResourceBundleUtil;

import br.com.abware.agenda.BookingModel;
import br.com.abware.agenda.BookingStatus;
import br.com.abware.agenda.RoomModel;

public abstract class BaseBean {

	protected static final Logger LOGGER = Logger.getLogger(BaseBean.class);

	public static final List<RoomModel> rooms = new RoomModel().getRooms(false);

	protected static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));

	public BaseBean() {
	}

	public static String getBookingStyleClass(BookingModel booking) {
		String style;

		if (BookingStatus.CANCELLED.equals(booking.getStatus())) {
			style = "cld.bkg.style.room.";
		} else {
			style = "bkg.style.room.";
		}

		return ResourceBundleUtil.getString(rb, style + booking.getRoom().getId());
	}

	protected void setMessages(Severity severity, String clientId, String messageKey, Object ... args) {
		LOGGER.trace("Method in");

		FacesContext context = FacesContext.getCurrentInstance();

		LOGGER.debug("Parameters received: " + severity + "; " + clientId + "; " + messageKey + "; " + args);

		String summary = ResourceBundleUtil.getString(rb, new Locale("pt", "BR"), messageKey, args);

		LOGGER.debug("Message found: " + summary);

		context.addMessage(clientId, new FacesMessage(severity, summary, ""));

		LOGGER.trace("Method out");
	}

	protected String getClientId(String key) {
		LOGGER.trace("Method in");

		FacesContext context = FacesContext.getCurrentInstance();

		LOGGER.trace("Method out");

		return context.getViewRoot().findComponent(key).getClientId();
	}
	
	public List<RoomModel> getRooms() {
		return rooms;
	}
	
}

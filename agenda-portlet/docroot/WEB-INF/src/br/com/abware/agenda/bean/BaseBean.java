package br.com.abware.agenda.bean;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.model.User;

import br.com.abware.agenda.RoomModel;

public abstract class BaseBean {

	protected static final Logger LOGGER = Logger.getLogger(BaseBean.class);

	public static final List<RoomModel> rooms = new RoomModel().getRooms(false);

	protected static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));

	public static String getRoomStyleClass(RoomModel room) {
		return ResourceBundleUtil.getString(rb, "room.style.class." + room.getId());
	}

	public BaseBean() {
	}
	
	public String getRoomStyleClass(String roomId) {
		return getRoomStyleClass(new RoomModel(Integer.parseInt(roomId)));
	}
	
	public static String getUserFlatName(User user) throws PortalException, SystemException {
		return !user.getOrganizations().isEmpty() ?	user.getOrganizations().get(0).getName() : ""; 
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

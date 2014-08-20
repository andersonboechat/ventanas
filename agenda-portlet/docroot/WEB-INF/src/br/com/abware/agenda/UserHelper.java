package br.com.abware.agenda;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

public class UserHelper {

	public static long getLoggedUserId() {
		long id = -1;
		User user = getLoggedUser();
		
		id = (user != null ? user.getUserId() : id); 
		
		return id;
	}

	public static User getLoggedUser() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext externalContext = fc.getExternalContext();
		return (User) externalContext.getRequestMap().get(WebKeys.USER);
	}

	public static boolean isUserPasswordMatch(User user, String password) {
		try {
			long userId = UserLocalServiceUtil.authenticateForBasic(user.getCompanyId(), 
																	"emailAddress", 
																	user.getLogin(), password);
			return userId > 0L ? true : false;
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}

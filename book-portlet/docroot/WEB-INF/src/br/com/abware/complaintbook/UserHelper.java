package br.com.abware.complaintbook;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

public class UserHelper {

	public static User getLoggedUser() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext externalContext = fc.getExternalContext();
		return (User) externalContext.getRequestMap().get(WebKeys.USER);
	}
	
	public static User getUserById(long userId) {
		try {
			return UserLocalServiceUtil.getUser(userId);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}

package br.com.abware.accountmgm.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.portlet.PortletContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.com.abware.accountmgm.UserHelper;
import br.com.abware.accountmgm.bean.model.FlatUserDataModel;
import br.com.abware.accountmgm.model.Flat;
import br.com.abware.accountmgm.model.FlatUser;

import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.model.Role;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.service.permission.UserPermissionUtil;

@ManagedBean
@ViewScoped
public class AccountMgmBean {

	private static Logger logger = Logger.getLogger(AccountMgmBean.class);

	private static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));

	private FlatUserDataModel model;
	
	private PermissionChecker permissionChecker;

	private List<Flat> flats;
	
	private List<Role> roles;
	
	private FlatUser user;
	
	private FlatUser[] selectedUsers;
	
	private int flatIndex;
	
	public AccountMgmBean() {
		try {
			flats = Flat.getFlats(UserHelper.getLoggedUserId());

			if (!CollectionUtils.isEmpty(flats)) {
				List<FlatUser> flatUsers = FlatUser.getUsers(flats.get(0).getOrganization());
				user = new FlatUser(UserHelper.getLoggedUser(), flats.get(0).getOrganization());
				flatUsers.remove(user);
				model = new FlatUserDataModel(flatUsers);
				roles = Flat.getRoles(flats.get(0).getOrganization());
			} else {
				model = new FlatUserDataModel(new ArrayList<FlatUser>());
				user = new FlatUser(UserHelper.getLoggedUser(), null);
			}
			

			if (UserHelper.getLoggedUser() != null) {
				permissionChecker = PermissionCheckerFactoryUtil.create(UserHelper.getLoggedUser());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@SuppressWarnings("unused")
	private void setMessages(Severity severity, String clientId, String messageKey, String ... args) {
		logger.trace("Method in");

		FacesContext context = FacesContext.getCurrentInstance();

		logger.debug("Parameters received: " + severity + "; " + clientId + "; " + messageKey + "; " + args);

		String summary = ResourceBundleUtil.getString(rb, new Locale("pt", "BR"), messageKey, args);

		logger.debug("Message found: " + summary);

		context.addMessage(clientId, new FacesMessage(severity, summary, ""));

		logger.trace("Method out");
	}

	@SuppressWarnings("unused")
	private String getClientId(String key) {
		logger.trace("Method in");

		FacesContext context = FacesContext.getCurrentInstance();

		logger.trace("Method out");

		return context.getViewRoot().findComponent(key).getClientId();
	}

	public boolean hasPermission(FlatUser user) {
		try {
			if (user != null) {
				return UserPermissionUtil.contains(permissionChecker, user.getUserId(), ActionKeys.UPDATE);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false; 
	}
	
	public void onNewUser() {
		try {
			user = new FlatUser(flats.get(flatIndex).getOrganization());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onDeleteUser() {
		try {
			List<FlatUser> users = (List<FlatUser>) model.getWrappedData();
	
			for (FlatUser user : selectedUsers) {
				if (UserPermissionUtil.contains(permissionChecker, user.getUserId(), ActionKeys.UPDATE)) {
					user.delete();
					users.remove(user);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public void onEditUser(String userId) {
		try {
			user = model.getRowData(userId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onSaveUser() {
		try {
			user.save();

			if (user.isNew()) {
				model.add(user);
				setMessages(FacesMessage.SEVERITY_INFO, null, "flats.user.add.success");
			} else {
				setMessages(FacesMessage.SEVERITY_INFO, null, "global.sucess");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessages(FacesMessage.SEVERITY_ERROR, null, "global.failure");
		}
	}

	public void onCancelUser() {
		if (user.getUserId() == 0) {
			FileUtils.deleteQuietly(user.getPortraitFile());
		}
	}	

	public void onUpload(FileUploadEvent event) {
		try {
			UploadedFile uploadedFile = event.getFile();
			PortletContext portletContext = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

			user.setPortraitPath("/temp/" + Calendar.getInstance().getTimeInMillis() + 
								 "." + FilenameUtils.getExtension(uploadedFile.getFileName()));
		
			user.setPortraitFile(new File(portletContext.getRealPath("") + user.getPortraitPath()));

			FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), user.getPortraitFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onSelectFlat(AjaxBehaviorEvent event) {
		model.setWrappedData(flats.get(flatIndex).getUsers());
	}

	public FlatUserDataModel getModel() {
		return model;
	}	
	
	public int getFlatIndex() {
		return flatIndex;
	}

	public void setFlatIndex(int flatIndex) {
		this.flatIndex = flatIndex;
	}

	public List<Flat> getFlats() {
		return flats;
	}
	
	public List<Role> getRoles() {
		return roles;
	}

	public FlatUser[] getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(FlatUser[] selectedUsers) {
		this.selectedUsers = selectedUsers;
	}

	public FlatUser getUser() {
		return user;
	}

}

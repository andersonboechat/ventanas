package br.com.abware.accountmgm.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import br.com.abware.accountmgm.UserHelper;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroupRole;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.comparator.UserFirstNameComparator;

public class FlatUser extends UserAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int GUEST_ROLE_ID = 10167;
	
	private static final String PORTRAIT_PATH = "/../image/user_portrait?img_id=";
	
	private Organization organization;
	
	private Contact contact;
	
	private String portraitPath;

	private File portraitFile;
	
	public FlatUser(Organization organization) {
		this(UserLocalServiceUtil.createUser(0), organization);
	}
	
	public FlatUser(User user, Organization organization) {
		super(user);
		this.organization = organization;
		this.portraitPath = PORTRAIT_PATH + getPortraitId();

		try {
			this.contact = super.getContact();
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj) {
		return user.equals(obj);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<FlatUser> getUsers(Organization organization) {
		List<FlatUser> users = new ArrayList<FlatUser>();

		try {
			LinkedHashMap params = new LinkedHashMap();
			params.put("usersOrgs", new Long(organization.getOrganizationId()));

			for (User user : searchUsers(organization, WorkflowConstants.STATUS_APPROVED, params)) {
				users.add(new FlatUser(user, organization));
			}

			for (User user : searchUsers(organization, WorkflowConstants.STATUS_DENIED, params)) {
				users.add(new FlatUser(user, organization));
			}

		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return users;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<User> searchUsers(Organization organization, int status, LinkedHashMap params) throws SystemException {
		return 	UserLocalServiceUtil.search(organization.getCompanyId(), 
											null, status, params, -1, -1, new UserFirstNameComparator());
	}
	
	public Role getUserGroupRole() {
		Role orgUserRole = null;
		try {
			if (organization != null && organization.getGroup() != null) {
				orgUserRole = RoleLocalServiceUtil.getRole(getCompanyId(), RoleConstants.ORGANIZATION_USER);
				List<UserGroupRole> userGroupRoles = UserGroupRoleLocalServiceUtil
															.getUserGroupRoles(getUserId(), 
																			   organization.getGroup().getGroupId());

				return !userGroupRoles.isEmpty() ? userGroupRoles.get(0).getRole() : orgUserRole;		
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return orgUserRole;
	}

	public void updateRole(Long oldRoleId, Long newRoleId) {
		try {
			if (organization != null && organization.getGroup() != null) {
				long groupId = organization.getGroup().getGroupId();
	
				UserGroupRoleServiceUtil.deleteUserGroupRoles(getUserId(), groupId, new long[] {oldRoleId});
				UserGroupRoleServiceUtil.addUserGroupRoles(getUserId(), groupId, new long[] {newRoleId});
			}
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void updateStatus(int status) {
		try {
			if (status == WorkflowConstants.STATUS_APPROVED && getUserGroupRole().getRoleId() == GUEST_ROLE_ID) {
				status = WorkflowConstants.STATUS_DENIED;
			}

			UserLocalServiceUtil.updateStatus(getUserId(), status);
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void save() {
		try {
			if (getUserId() == 0) {
				User creatorUser = UserHelper.getLoggedUser();

				Calendar c = Calendar.getInstance();
				c.setTime(contact.getBirthday());

				int birthdayDay = c.get(Calendar.DAY_OF_MONTH);
				int birthdayMonth = c.get(Calendar.MONTH);
				int birthdayYear = c.get(Calendar.YEAR);

				UserLocalServiceUtil.addUser(creatorUser.getUserId(), creatorUser.getCompanyId(), true, 
									 		StringUtils.EMPTY, StringUtils.EMPTY, true, StringUtils.EMPTY, 
									 		getEmailAddress(), 0, StringUtils.EMPTY, creatorUser.getLocale(), 
									 		getFirstName(), StringUtils.EMPTY, getLastName(), 0, 0, 
									 		contact.getMale(), birthdayMonth, birthdayDay, birthdayYear, 
									 		StringUtils.EMPTY, creatorUser.getGroupIds(), 
									 		creatorUser.getOrganizationIds(), creatorUser.getRoleIds(), 
									 		new long[] {GUEST_ROLE_ID}, true, new ServiceContext());
			} else {
				persist();
				contact.persist();
			}
			
			if (portraitFile != null) {
				UserLocalServiceUtil.updatePortrait(getUserId(), 
						 							FileUtils.readFileToByteArray(portraitFile));
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void delete() {
		updateStatus(WorkflowConstants.STATUS_INACTIVE);
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public String getPortraitPath() {
		return portraitPath;
	}

	public void setPortraitPath(String portrait) {
		this.portraitPath = portrait;
	}

	public File getPortraitFile() {
		return portraitFile;
	}

	public void setPortraitFile(File portraitFile) {
		if (this.portraitFile != null) {
			this.portraitFile.delete();
		}

		this.portraitFile = portraitFile;
	}

}

package br.com.abware.agenda;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.liferay.portal.NoSuchContactException;
import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.CacheModel;
import com.liferay.portal.model.Contact;
import com.liferay.portal.model.EmailAddress;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.PasswordPolicy;
import com.liferay.portal.model.Phone;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.Team;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserGroup;
import com.liferay.portal.model.Website;
import com.liferay.portal.service.ContactLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.expando.model.ExpandoBridge;


public class UserAdapter implements User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected User user;

	public UserAdapter(User user) {
		this.user = user;
	}
	
	@Override
	public Object clone() {
		return user.clone();
	}
	
	@Override
	public long getPrimaryKey() {
		return getUserId();
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setUserId(primaryKey);
	}

	@Override
	@AutoEscape
	public String getUuid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUuid(String uuid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getUserId() {
		return user.getUserId();
	}

	@Override
	public void setUserId(long userId) {
		user.setUserId(userId);
	}

	@Override
	public String getUserUuid() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUserUuid(String userUuid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getCompanyId() {
		return user.getCompanyId();
	}

	@Override
	public void setCompanyId(long companyId) {
		user.setCompanyId(companyId);
	}

	@Override
	public Date getCreateDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCreateDate(Date createDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getModifiedDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDefaultUser() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDefaultUser() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDefaultUser(boolean defaultUser) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getContactId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setContactId(long contactId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@AutoEscape
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public void setPassword(String password) {
		user.setPassword(password);
	}

	@Override
	public boolean getPasswordEncrypted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPasswordEncrypted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPasswordEncrypted(boolean passwordEncrypted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getPasswordReset() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPasswordReset() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPasswordReset(boolean passwordReset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getPasswordModifiedDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPasswordModifiedDate(Date passwordModifiedDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDigest(String digest) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@AutoEscape
	public String getReminderQueryQuestion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReminderQueryQuestion(String reminderQueryQuestion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@AutoEscape
	public String getReminderQueryAnswer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReminderQueryAnswer(String reminderQueryAnswer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getGraceLoginCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setGraceLoginCount(int graceLoginCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@AutoEscape
	public String getScreenName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setScreenName(String screenName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@AutoEscape
	public String getEmailAddress() {
		return user.getEmailAddress();
	}

	@Override
	public void setEmailAddress(String emailAddress) {
		user.setEmailAddress(emailAddress);
	}

	@Override
	public long getFacebookId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFacebookId(long facebookId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@AutoEscape
	public String getOpenId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOpenId(String openId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getPortraitId() {
		return user.getPortraitId();
	}

	@Override
	public void setPortraitId(long portraitId) {
		user.setPortraitId(portraitId);
	}

	@Override
	@AutoEscape
	public String getLanguageId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@AutoEscape
	public String getTimeZoneId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@AutoEscape
	public String getGreeting() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGreeting(String greeting) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@AutoEscape
	public String getComments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setComments(String comments) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@AutoEscape
	public String getFirstName() {
		return user.getFirstName();
	}

	@Override
	public void setFirstName(String firstName) {
		user.setFirstName(firstName);
	}

	@Override
	@AutoEscape
	public String getMiddleName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMiddleName(String middleName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@AutoEscape
	public String getLastName() {
		return user.getLastName();
	}

	@Override
	public void setLastName(String lastName) {
		user.setLastName(lastName);
	}

	@Override
	@AutoEscape
	public String getJobTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setJobTitle(String jobTitle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getLoginDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLoginDate(Date loginDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@AutoEscape
	public String getLoginIP() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLoginIP(String loginIP) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getLastLoginDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastLoginDate(Date lastLoginDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@AutoEscape
	public String getLastLoginIP() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastLoginIP(String lastLoginIP) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getLastFailedLoginDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastFailedLoginDate(Date lastFailedLoginDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFailedLoginAttempts() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFailedLoginAttempts(int failedLoginAttempts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getLockout() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLockout() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLockout(boolean lockout) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Date getLockoutDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLockoutDate(Date lockoutDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getAgreedToTermsOfUse() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAgreedToTermsOfUse() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAgreedToTermsOfUse(boolean agreedToTermsOfUse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getEmailAddressVerified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmailAddressVerified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setEmailAddressVerified(boolean emailAddressVerified) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getStatus() {
		return user.getStatus();
	}

	@Override
	public void setStatus(int status) {
		user.setStatus(status);
	}

	@Override
	public boolean isNew() {
		return user.isNew();
	}

	@Override
	public void setNew(boolean n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCachedModel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEscapedModel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CacheModel<User> toCacheModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User toEscapedModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toXmlString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetOriginalValues() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<?> getModelClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModelClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persist() throws SystemException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Address> getAddresses() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getBirthday() throws PortalException, SystemException {
		return user.getBirthday();
	}

	@Override
	public String getCompanyMx() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact getContact() throws PortalException, SystemException {
		try {
			return user.getContact();			
		} catch (NoSuchContactException e) {
			return ContactLocalServiceUtil.createContact(0);
		}

	}

	@Override
	public String getDigest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDigest(String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayEmailAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayURL(String portalURL, String mainPath)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayURL(ThemeDisplay themeDisplay)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EmailAddress> getEmailAddresses() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getFemale() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@AutoEscape
	public String getFullName() {
		return user.getFullName();
	}

	@Override
	public Group getGroup() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getGroupId() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long[] getGroupIds() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> getGroups() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLogin() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getMale() throws PortalException, SystemException {
		return user.getMale();
	}

	@Override
	public List<Group> getMySites() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> getMySites(boolean includeControlPanel, int max)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> getMySites(int max) throws PortalException,
			SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> getMySites(String[] classNames,
			boolean includeControlPanel, int max) throws PortalException,
			SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> getMySites(String[] classNames, int max)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] getOrganizationIds() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] getOrganizationIds(boolean includeAdministrative)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Organization> getOrganizations() throws PortalException,
			SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Organization> getOrganizations(boolean includeAdministrative)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getPasswordModified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public PasswordPolicy getPasswordPolicy() throws PortalException,
			SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPasswordUnencrypted() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Phone> getPhones() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPortraitURL(ThemeDisplay themeDisplay)
			throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPrivateLayoutsPageCount() throws PortalException,
			SystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPublicLayoutsPageCount() throws PortalException,
			SystemException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> getReminderQueryQuestions() throws PortalException,
			SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] getRoleIds() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Role> getRoles() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] getTeamIds() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Team> getTeams() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeZone getTimeZone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] getUserGroupIds() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserGroup> getUserGroups() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Website> getWebsites() throws SystemException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCompanyMx() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasCompanyMx(String emailAddress) throws PortalException,
			SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasMySites() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasOrganization() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPrivateLayouts() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPublicLayouts() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasReminderQuery() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFemale() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMale() throws PortalException, SystemException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPasswordModified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLanguageId(String languageId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPasswordModified(boolean passwordModified) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPasswordUnencrypted(String passwordUnencrypted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTimeZoneId(String timeZoneId) {
		// TODO Auto-generated method stub
		
	}
}

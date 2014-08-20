package com.liferay.portal.model;

import java.util.List;

public interface User {

	public void setUserId(long userId);
	
	public long getUserId();
	
	public String getFirstName();

	public String getLastName();

	public List<Organization> getOrganizations();
	
	public String getJobTitle();

}

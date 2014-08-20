package com.liferay.portal.model;

import java.util.List;


public class UserFake implements User {

	private long userId;
	
	public UserFake() {
		// TODO Auto-generated constructor stub
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public long getUserId() {
		return userId;
	}

	@Override
	public String getFirstName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLastName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Organization> getOrganizations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJobTitle() {
		// TODO Auto-generated method stub
		return null;
	}
}

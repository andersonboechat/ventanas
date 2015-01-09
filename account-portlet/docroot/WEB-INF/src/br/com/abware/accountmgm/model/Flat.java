package br.com.abware.accountmgm.model;

import java.util.ArrayList;
import java.util.List;


import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.util.comparator.RoleNameComparator;

public class Flat {
	
	private Organization organization;
	
	private List<FlatUser> users;
	
	public Flat(Organization organization) {
		this.organization = organization;
		this.users = FlatUser.getUsers(organization);
	}

	public static List<Flat> getFlats(long userId) {
		List<Flat> flats = new ArrayList<Flat>();

		try {
			for (Organization organization : OrganizationLocalServiceUtil.getUserOrganizations(userId)) {
				flats.add(new Flat(organization));
			}
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return flats;
	}

	public static List<Role> getRoles(Organization organization) {
		List<Role> roles;

		try {
			roles = RoleLocalServiceUtil.search(organization.getCompanyId(), 
											    null, new Integer[] {RoleConstants.TYPE_ORGANIZATION}, 
											    -1, -1, new RoleNameComparator());
		} catch (Exception e) {
			e.printStackTrace();
			roles = new ArrayList<Role>();
		}

		return roles;
	}	
	
	public Organization getOrganization() {
		return organization;
	}

	public List<FlatUser> getUsers() {
		return users;
	}

	public long getId() {
		return organization.getOrganizationId();
	}

	public String getName() {
		return organization.getName();
	}

}

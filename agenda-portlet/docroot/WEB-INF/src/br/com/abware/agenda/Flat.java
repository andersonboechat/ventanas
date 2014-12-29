package br.com.abware.agenda;

import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.util.comparator.RoleNameComparator;

public class Flat implements Comparable<Flat>{
	
	private Organization organization;
	
	public Flat(Organization organization) {
		this.organization = organization;
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
	
	public static Flat getFlat(long id) {
		return new Flat(OrganizationLocalServiceUtil.createOrganization(id));
	}

	public static Flat getFlatById(long id) throws Exception {
		return new Flat(OrganizationLocalServiceUtil.getOrganization(id));
	}	

	public static List<Flat> getFlats() {
		List<Flat> flats = new ArrayList<Flat>();

		try {
			for (Organization organization : OrganizationLocalServiceUtil.getOrganizations(-1, -1)) {
				flats.add(new Flat(organization));
			}
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

	public long getId() {
		return organization.getOrganizationId();
	}

	public String getName() {
		return organization.getName();
	}

	@Override
	public int compareTo(Flat flat) {
		if (flat == null || flat.getName() == null || getName() == null) {
			return 0;
		} else {
			return getName().compareTo(flat.getName());
		}
	}

	public List<User> getUsers() {
		return FlatUser.getResidents(organization);
	}

	@Override
	public boolean equals(Object obj) {
		try {
			if (super.equals(obj)) {
				return true;
			}
			Flat flat = (Flat) obj;
			return this.getId() == flat.getId();
		} catch (Exception e) {
			return false;
		}
	}
}

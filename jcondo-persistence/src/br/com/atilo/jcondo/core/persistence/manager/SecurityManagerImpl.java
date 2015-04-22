package br.com.atilo.jcondo.core.persistence.manager;

import java.util.List;

import com.liferay.faces.portal.context.LiferayPortletHelper;
import com.liferay.faces.portal.context.LiferayPortletHelperImpl;
import com.liferay.portal.model.Organization;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.service.permission.PortalPermissionUtil;
import com.liferay.portal.service.permission.UserPermissionUtil;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.RoleName;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.core.model.Vehicle;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.SystemException;

public class SecurityManagerImpl {
	
	private static PersonManagerImpl personManager = new PersonManagerImpl();
	
	private LiferayPortletHelper helper = new LiferayPortletHelperImpl();
	
	private Condominium portal;
	
	public SecurityManagerImpl() {
		try {
			portal = new Condominium();
			portal.setRelatedId(10179);
		} catch (Exception e) {
			
		}
	}
	
	public void addPermission(Person person, BaseModel resource, Permission permission) throws Exception {
		ResourceLocalServiceUtil.addResources(helper.getCompanyId(), 0, person.getUserId(), 
											  resource.getClass().getName(), resource.getId(), false, false, false);
	}

	public void updatePassword(Person person, String password, String newPassword) throws Exception {
		int result = UserLocalServiceUtil.authenticateByEmailAddress(helper.getCompanyId(), person.getEmailAddress(), password, null, null, null);

		if (result == -1) {
			throw new Exception("user authetication failure.");
		}

		UserLocalServiceUtil.updatePassword(person.getUserId(), newPassword, newPassword, false);
	}

	public void addMembership(Person person, Membership membership) throws Exception {
		if (membership.getDomain() instanceof Flat) {
			if (membership.getType() == PersonType.OWNER) {
				List<Person> renters = personManager.findPeopleByType(membership.getDomain(), PersonType.RENTER);

				// Se o apartamento estiver alugado, o proprietario não tem pepel no apartamento
				if (renters.isEmpty()) {
					addRole(person, RoleName.SENIOR_USER);
					addRole(person, membership.getDomain(), RoleName.FLAT_MANAGER);
					addOrganization(person, membership.getDomain());
				}
			}

			if (membership.getType() == PersonType.RENTER) {
				List<Person> owners = personManager.findPeopleByType(membership.getDomain(), PersonType.OWNER);
				
				for (Person owner : owners) {
					if (owner.getMemberships() == null || owner.getMemberships().size() <= 1) {
						removeRole(person, RoleName.SENIOR_USER);
					}
					removeRole(owner, membership.getDomain(), RoleName.FLAT_MANAGER);
					removeOrganization(owner, membership.getDomain());
				}

				addRole(person, RoleName.SENIOR_USER);
				addRole(person, membership.getDomain(), RoleName.FLAT_MANAGER);
				addOrganization(person, membership.getDomain());
			}

			if (membership.getType() == PersonType.RESIDENT) {
				addRole(person, RoleName.SENIOR_USER);
				addRole(person, membership.getDomain(), RoleName.FLAT_ASSISTANT);
				addOrganization(person, membership.getDomain());
			}

			if (membership.getType() == PersonType.DEPENDENT) {
				addRole(person, RoleName.SENIOR_USER);
				addRole(person, membership.getDomain(), RoleName.FLAT_MEMBER);
				addOrganization(person, membership.getDomain());
			}
		}

		if (membership.getDomain() instanceof Supplier) {
			if (membership.getType() == PersonType.MANAGER) {
				addRole(person, membership.getDomain(), RoleName.SUPPLIER_MANAGER);
			}

			if (((Supplier) membership.getDomain()).getName() == "SECURITY") {
				if (membership.getType() == PersonType.GATEKEEPER) {
					//addRole(person, portal, RoleName.SECURITY_USER));
				}
			}

			addOrganization(person, membership.getDomain());
		}

		if (membership.getDomain() instanceof Administration) {
			if (membership.getType() == PersonType.SYNCDIC || membership.getType() == PersonType.SUB_SYNCDIC) {
				addRole(person, membership.getDomain(), RoleName.ADMIN_MANAGER);
				//addSiteRole(person, RoleName.SITE_ADMIN);
			}

			if (membership.getType() == PersonType.ADMIN_ASSISTANT) {
				addRole(person, membership.getDomain(), RoleName.ADMIN_ASSISTANT);
				addRole(person, RoleName.SENIOR_USER);
				//addSiteRole(person, RoleName.SITE_ADMIN);
				addToSite(person);
			}

			if (membership.getType() == PersonType.ADMIN_ADVISOR || membership.getType() == PersonType.TAX_ADVISOR) {
				addRole(person, membership.getDomain(), RoleName.ADMIN_MEMBER);
			}

			if (membership.getType() == PersonType.GATEKEEPER) {
				//addRole(person, membership.getDomain(), RoleName.SECURITY_STAFF));
			}

			addOrganization(person, membership.getDomain());
		}

	}

	public void removeMembership(Person person, Membership membership) throws Exception {
		Person p = personManager.findById(person.getId());

		if (membership.getDomain() instanceof Flat) {
			if (membership.getType() == PersonType.OWNER || membership.getType() == PersonType.RENTER) {
				removeRole(person, membership.getDomain(), RoleName.FLAT_MANAGER);
			}

			if (membership.getType() == PersonType.RESIDENT || membership.getType() == PersonType.DEPENDENT) {
				removeRole(person, membership.getDomain(), RoleName.FLAT_ASSISTANT);
				removeRole(person, membership.getDomain(), RoleName.FLAT_MEMBER);
			}

			if (membership.getType() == PersonType.RENTER) {
				List<Person> renters = personManager.findPeopleByType(membership.getDomain(), PersonType.RENTER);

				// Se não houver outros locatários, devolver papeis para os proprietarios
				if (renters.isEmpty() || renters.size() == 1) {
					List<Person> owners = personManager.findPeopleByType(membership.getDomain(), PersonType.OWNER);

					for (Person owner : owners) {
						addRole(owner, RoleName.SENIOR_USER);
						addRole(owner, membership.getDomain(), RoleName.FLAT_MANAGER);
						addOrganization(owner, membership.getDomain());
					}
				}
			}
		}

		if (membership.getDomain() instanceof Supplier) {
			//removeRole(person, portal, RoleName.SECURITY_USER));
			removeRole(person, membership.getDomain(), RoleName.SUPPLIER_MANAGER);
		}

		if (membership.getDomain() instanceof Administration) {
			removeRole(person, membership.getDomain(), RoleName.ADMIN_MANAGER);
			removeRole(person, membership.getDomain(), RoleName.ADMIN_ASSISTANT);
			removeRole(person, membership.getDomain(), RoleName.ADMIN_MEMBER);
		}

		if (p.getMemberships() == null || p.getMemberships().size() <= 1) {
			removeRole(person, RoleName.SENIOR_USER);
		}

		removeOrganization(person, membership.getDomain());
	}

	private void addRole(Person person, RoleName role) throws ApplicationException {
		addRole(person, null, role);
	}

	private void addRole(Person person, Domain domain, RoleName role) throws ApplicationException {
		try {
			long roleId = RoleLocalServiceUtil.getRole(helper.getCompanyId(), role.getLabel()).getRoleId();
			if (domain == null) {
				RoleLocalServiceUtil.addUserRoles(person.getUserId(), new long[] {roleId});
			} else {
				long id = GroupLocalServiceUtil.getOrganizationGroup(helper.getCompanyId(), domain.getRelatedId()).getGroupId();
				UserGroupRoleLocalServiceUtil.addUserGroupRoles(person.getUserId(), id, new long[] {roleId});	
			}
		} catch (Exception e) {
			throw new ApplicationException(e, "");
		}
	}

	private void removeRole(Person person, RoleName role) throws ApplicationException {
		removeRole(person, null, role);
	}
	
	private void removeRole(Person person, Domain domain, RoleName role) throws ApplicationException {
		try {
			long roleId = RoleLocalServiceUtil.getRole(helper.getCompanyId(), role.getLabel()).getRoleId();
			if (domain == null) {
				RoleLocalServiceUtil.unsetUserRoles(person.getUserId(), new long[] {roleId});
			} else {
				long id = GroupLocalServiceUtil.getOrganizationGroup(helper.getCompanyId(), domain.getRelatedId()).getGroupId();
				UserGroupRoleLocalServiceUtil.deleteUserGroupRoles(person.getUserId(), id, new long[] {roleId});	
			}
		} catch (Exception e) {
			throw new ApplicationException(e, "");
		}
	}

	private void addSiteRole(Person person) throws Exception {
		UserLocalServiceUtil.addGroupUsers(helper.getScopeGroupId(), new long[] {person.getUserId()});
	}
	
	
	private void removeOrganization(Person person, Domain domain) throws Exception {
		UserLocalServiceUtil.unsetOrganizationUsers(domain.getRelatedId(), new long[] {person.getUserId()});
	}

	private void addOrganization(Person person, Domain domain) throws Exception {
		UserLocalServiceUtil.addOrganizationUsers(domain.getRelatedId(), new long[] {person.getUserId()});
	}	

	private void removeFromSite(Person person) throws Exception {
		UserLocalServiceUtil.unsetGroupUsers(helper.getScopeGroupId(), new long[] {person.getUserId()}, null);
	}

	private void addToSite(Person person) throws Exception {
		UserLocalServiceUtil.addGroupUsers(helper.getScopeGroupId(), new long[] {person.getUserId()});
	}

	public boolean hasPermission(BaseModel model, Permission permission) throws ApplicationException {
		try {
			PermissionChecker permissionChecker = helper.getThemeDisplay().getPermissionChecker();

			if (model instanceof Person) {
				return checkUserPermission(permissionChecker, ((Person) model).getUserId(), permission);
			} else if (model instanceof Flat || model instanceof Supplier || model instanceof Administration) {
				// return checkDomainPermission(permissionChecker, (Domain) model, permission); 
				return checkPermission(permissionChecker, (Domain) model, (Domain) model, permission);
			} else if (model instanceof Membership) {
				return checkPermission(permissionChecker, ((Membership) model).getType(), ((Membership) model).getDomain(), permission);
			} else if (model instanceof Vehicle) {
				return checkPermission(permissionChecker, model, ((Vehicle) model).getDomain(), permission);
			} else {
				return checkPermission(permissionChecker, model, permission);
			}
		} catch (Exception e) {
			throw new ApplicationException(e, "fail.check.permission");
		}
	}

	private boolean checkPermission(PermissionChecker permissionChecker, BaseModel model, Domain domain, Permission permission) throws Exception {
		try {
			if (domain == null) {
				return permissionChecker.hasPermission(helper.getScopeGroupId(), model.getClass().getName(), model.getId(), permission.name());
			}

			Domain d = domain.getRelatedId() == 0 ? domain.getParent() : domain;
			Organization organization = OrganizationLocalServiceUtil.getOrganization(d.getRelatedId());

	        for(; !organization.isRoot(); organization = organization.getParentOrganization()) {
	            if(permissionChecker.hasPermission(organization.getGroup().getGroupId(), model.getClass().getName(), model.getId(), permission.name())) {
	            	return true;
	            }
	        }

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}	

	private boolean checkPermission(PermissionChecker permissionChecker, BaseModel model, Permission permission) throws Exception {
		return checkPermission(permissionChecker, model, null, permission);
	}
	
	private boolean checkDomainPermission(PermissionChecker permissionChecker, Domain domain, Permission permission) throws Exception {
		String actionkey;

		if (permission == Permission.VIEW) {
			actionkey = ActionKeys.VIEW;
		} else {
			throw new SystemException("permission.not.supported");
		}

		return OrganizationPermissionUtil.contains(permissionChecker, domain.getRelatedId(), actionkey);
	}

	private boolean checkUserPermission(PermissionChecker permissionChecker, long userId, Permission permission) throws Exception {
		String actionkey;

		if (permission == Permission.UPDATE) {
			actionkey = ActionKeys.UPDATE;
		} else if (permission == Permission.ADD) { 
			return PortalPermissionUtil.contains(permissionChecker, ActionKeys.ADD_USER);
		} else {
			throw new SystemException("permission.not.supported");
		}

		return UserPermissionUtil.contains(permissionChecker, userId, actionkey);
	}

}

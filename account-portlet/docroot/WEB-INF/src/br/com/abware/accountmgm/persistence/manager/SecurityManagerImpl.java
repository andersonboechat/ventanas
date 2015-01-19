package br.com.abware.accountmgm.persistence.manager;

import java.util.List;

import com.liferay.faces.portal.context.LiferayPortletHelper;
import com.liferay.faces.portal.context.LiferayPortletHelperImpl;
import com.liferay.portal.model.Group;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.service.permission.UserPermissionUtil;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Role;
import br.com.abware.jcondo.core.model.RoleName;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.SystemException;

public class SecurityManagerImpl {
	
	private LiferayPortletHelper helper = new LiferayPortletHelperImpl();

	/**
	 * 
	 * @param person
	 * @param domain
	 * @param role
	 * @throws ApplicationException
	 */
	public void addRole(Person person, Domain domain, Role role) throws ApplicationException {
		try {
			if (domain instanceof Flat) {
				if (role.getName().getType() != 0) {
					throw new Exception();
				}
			}

			if (domain instanceof Supplier) {
				if (role.getName().getType() != 1) {
					throw new Exception();
				}
			}

			if (domain instanceof Condominium) {
				if (role.getName().getType() != 2) {
					throw new Exception();
				}
			}

			UserGroupRoleLocalServiceUtil.addUserGroupRoles(person.getId(), domain.getId(), new long[] {role.getId()});
		} catch (Exception e) {
			throw new ApplicationException(e, "");
		}
	}

	public void removeRole(Person person, Domain domain, Role role) throws ApplicationException {
		try {
			if (domain instanceof Flat) {
				if (role.getName().getType() != 0) {
					throw new Exception();
				}
			}

			if (domain instanceof Supplier) {
				if (role.getName().getType() != 1) {
					throw new Exception();
				}
			}

			if (domain instanceof Condominium) {
				if (role.getName().getType() != 2) {
					throw new Exception();
				}
			}

			UserGroupRoleLocalServiceUtil.deleteUserGroupRoles(person.getId(), domain.getId(), new long[] {role.getId()});	
		} catch (Exception e) {
			throw new ApplicationException(e, "");
		}
	}
	
	public Role getRole(Domain domain, RoleName roleName) throws ApplicationException {
		try {
			Group group = GroupLocalServiceUtil.getGroup(domain.getId());
			com.liferay.portal.model.Role role = RoleLocalServiceUtil.getRole(group.getCompanyId(), roleName.getLabel());
			return new Role(role.getRoleId(), RoleName.parse(role.getName()), role.getTitle());
		} catch(Exception e) {
			throw new ApplicationException(e, "role could not be found"); 
		}
	}

	public boolean hasRole(Person person, Domain domain, Role role) {
		return hasRole(person, new Domain[] {domain}, role);
	}	

	public boolean hasRole(Person person, Domain[] domain, Role role) {
		return false;
	}
	
	public void updateRoles(Person person, List<Membership> memberships) {
		
	}
	
	public boolean hasPermission(BaseModel model, Permission permission) throws ApplicationException {
		try {
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(helper.getUser());

			if (model instanceof Person) {
				return checkUserPermission(permissionChecker, model.getId(), permission);
			} else if (model instanceof Flat || model instanceof Supplier) {
				return checkOrganizationPermission(permissionChecker, model.getId(), permission);
			} else if (model instanceof Condominium) {
				return checkGroupPermission(permissionChecker, model.getId(), permission);
			} else if (model instanceof Role) {
				return checkRolePermission(permissionChecker, model.getId(), permission);
			} else {
				throw new SystemException("model.not.supported");
			}
		} catch (Exception e) {
			throw new ApplicationException(e, "fail.check.permission");
		}
	}

	private boolean checkUserPermission(PermissionChecker permissionChecker, long userId, Permission permission) throws Exception {
		String actionkey;

		if (permission == Permission.UPDATE_PERSON) {
			actionkey = ActionKeys.UPDATE;
		} else if (permission == Permission.ADD_USER) { 
			actionkey = ActionKeys.ADD_USER;
		} else {
			throw new SystemException("permission.not.supported");
		}

		return UserPermissionUtil.contains(permissionChecker, userId, actionkey);
	}
	
	private boolean checkOrganizationPermission(PermissionChecker permissionChecker, long organizationId, Permission permission) throws Exception {
		String actionkey;

		if (permission == Permission.UPDATE_PERSON) {
			actionkey = ActionKeys.MANAGE_USERS;
		} else if (permission == Permission.ADD_USER) {
			actionkey = ActionKeys.ADD_USER;
		} else if (permission == Permission.DELETE_PERSON) {
			actionkey = ActionKeys.MANAGE_USERS;
		} else if (permission == Permission.UPDATE) {
			actionkey = ActionKeys.UPDATE;
		} else if (permission == Permission.ADD) {
			actionkey = ActionKeys.ADD_ORGANIZATION;
		} else if (permission == Permission.DELETE) {
			actionkey = ActionKeys.DELETE;
		} else {
			throw new SystemException("Permission not supported");
		}

		return OrganizationPermissionUtil.contains(permissionChecker, organizationId, actionkey);
	}

	private boolean checkGroupPermission(PermissionChecker permissionChecker, long groupId, Permission permission) throws Exception {
		String actionkey;

		if (permission == Permission.UPDATE_PERSON) {
			actionkey = ActionKeys.MANAGE_USERS;
		} else if (permission == Permission.ADD_USER) {
			actionkey = ActionKeys.ADD_USER;
		} else if (permission == Permission.DELETE_PERSON) {
			actionkey = ActionKeys.MANAGE_USERS;
		} else if (permission == Permission.UPDATE) {
			actionkey = ActionKeys.UPDATE;
		} else if (permission == Permission.ADD) {
			actionkey = ActionKeys.MANAGE;
		} else if (permission == Permission.DELETE) {
			actionkey = ActionKeys.DELETE;
		} else {
			throw new SystemException("Permission not supported");
		}

		return OrganizationPermissionUtil.contains(permissionChecker, groupId, actionkey);
	}

	private boolean checkRolePermission(PermissionChecker permissionChecker, long roleId, Permission permission) throws Exception {
		String actionkey;

		if (permission == Permission.ADD_USER) { 
			actionkey = ActionKeys.ASSIGN_USER_ROLES;
		} else if (permission == Permission.DELETE_PERSON) { 
			actionkey = ActionKeys.ASSIGN_USER_ROLES;
		} else {
			throw new SystemException("permission.not.supported");
		}

		return UserPermissionUtil.contains(permissionChecker, roleId, actionkey);
	}

}

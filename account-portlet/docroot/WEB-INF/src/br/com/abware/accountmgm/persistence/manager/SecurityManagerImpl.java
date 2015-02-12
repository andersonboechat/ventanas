package br.com.abware.accountmgm.persistence.manager;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.liferay.faces.portal.context.LiferayPortletHelper;
import com.liferay.faces.portal.context.LiferayPortletHelperImpl;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.permission.GroupPermissionUtil;
import com.liferay.portal.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.service.permission.UserPermissionUtil;

import br.com.abware.accountmgm.util.PersonTypePredicate;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.RoleName;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.SystemException;

public class SecurityManagerImpl {
	
	private static NewPersonManagerImpl personManager = new NewPersonManagerImpl();	
	
	private LiferayPortletHelper helper = new LiferayPortletHelperImpl();
	
	private Condominium portal;
	
	public SecurityManagerImpl() {
		try {
			portal = new Condominium();
			portal.setDomainId(10179);
		} catch (Exception e) {
			
		}
	}

	public void addMembership(Person person, Membership membership) throws Exception {
		if (membership.getDomain() instanceof Flat) {
			if (membership.getType() == PersonType.OWNER) {
				List<Person> renters = personManager.findPeopleByType(membership.getDomain(), PersonType.RENTER);

				if (renters.isEmpty()) {
					addRole(person, portal, RoleName.LESSEE);
					addRole(person, portal, RoleName.DEBATER);
					addRole(person, portal, RoleName.HABITANT);
					addRole(person, portal, RoleName.USER_ENROLLER);
					addRole(person, membership.getDomain(), RoleName.FLAT_ADMIN);
				}

				addRole(person, portal, RoleName.SITE_MEMBER);
			}

			if (membership.getType() == PersonType.RENTER) {
				List<Person> owners = personManager.findPeopleByType(membership.getDomain(), PersonType.OWNER);
				PersonTypePredicate predicate = new PersonTypePredicate(PersonType.OWNER);
				
				for (Person owner : owners) {
					// Remover papeis somente se não for proprietário de outros apartamentos
					if (CollectionUtils.countMatches(owner.getMemberships(), predicate) == 1) {
						removeRole(owner, portal, RoleName.LESSEE);
						removeRole(owner, portal, RoleName.DEBATER);
						removeRole(owner, portal, RoleName.HABITANT);
						removeRole(owner, portal, RoleName.USER_ENROLLER);
					}

					removeRole(owner, membership.getDomain(), RoleName.FLAT_ADMIN);
				}

				addRole(person, membership.getDomain(), RoleName.FLAT_ADMIN);
			}

			if (membership.getType() == PersonType.RESIDENT) {
				addRole(person, membership.getDomain(), RoleName.VEHIACLE_ENROLLER);
			}

			if (membership.getType() == PersonType.RENTER || membership.getType() == PersonType.RESIDENT) {
				addRole(person, portal, RoleName.LESSEE);
				addRole(person, portal, RoleName.DEBATER);
				addRole(person, portal, RoleName.USER_ENROLLER);
			}

			if (membership.getType() == PersonType.RENTER || 
					membership.getType() == PersonType.RESIDENT || membership.getType() == PersonType.DEPENDENT) {
				addRole(person, portal, RoleName.HABITANT);
				addRole(person, portal, RoleName.SITE_MEMBER);
			}

		}

		if (membership.getDomain() instanceof Supplier) {
			if (membership.getType() == PersonType.MANAGER) {
				addRole(person, membership.getDomain(), RoleName.SUPPLIER_ADMIN);
			}

			if (membership.getType() == PersonType.EMPLOYEE) {
				addRole(person, membership.getDomain(), RoleName.EMPLOYEE);
			}

			if (membership.getType() == PersonType.GATEKEEPER) {
				addRole(person, portal, RoleName.ACCESS_LOGGER);
				addRole(person, portal, RoleName.USER_ENROLLER);
				addRole(person, portal, RoleName.VEHIACLE_ENROLLER);
			}

		}

		if (membership.getDomain() instanceof Condominium) {
			if (membership.getType() == PersonType.SYNCDIC || membership.getType() == PersonType.SUB_SYNCDIC) {
				//addRole(person, portal, RoleName.ADMIN));
			}

			if (membership.getType() == PersonType.ADMIN_ASSISTANT) {
				addRole(person, membership.getDomain(), RoleName.FLAT_ADMIN);
				addRole(person, membership.getDomain(), RoleName.SUPPLIER_ADMIN);
				addRole(person, portal, RoleName.USER_ENROLLER);
				addRole(person, portal, RoleName.VEHIACLE_ENROLLER);
			}

			if (membership.getType() == PersonType.EMPLOYEE) {
				addRole(person, membership.getDomain(), RoleName.EMPLOYEE);
			}
		}
	}

	public void removeMembership(Person person, Membership membership) throws Exception {
		if (membership.getDomain() instanceof Flat) {
			removeRole(person, portal, RoleName.LESSEE);
			removeRole(person, portal, RoleName.DEBATER);
			removeRole(person, portal, RoleName.HABITANT);
			removeRole(person, portal, RoleName.GUEST);
			removeRole(person, portal, RoleName.VISITOR);
			removeRole(person, portal, RoleName.USER_ENROLLER);
			removeRole(person, portal, RoleName.VEHIACLE_ENROLLER);			
			removeRole(person, membership.getDomain(), RoleName.FLAT_ADMIN);
		}

		if (membership.getDomain() instanceof Supplier) {
			removeRole(person, portal, RoleName.EMPLOYEE);
			removeRole(person, portal, RoleName.ACCESS_LOGGER);
			removeRole(person, portal, RoleName.USER_ENROLLER);
			removeRole(person, portal, RoleName.VEHIACLE_ENROLLER);
		}

		if (membership.getDomain() instanceof Condominium) {
			removeRole(person, portal, RoleName.EMPLOYEE);
			removeRole(person, portal, RoleName.VISITOR);
			removeRole(person, membership.getDomain(), RoleName.FLAT_ADMIN);
			removeRole(person, membership.getDomain(), RoleName.SUPPLIER_ADMIN);
			removeRole(person, portal, RoleName.USER_ENROLLER);
			removeRole(person, portal, RoleName.VEHIACLE_ENROLLER);
		}
	}

	private void addRole(Person person, Domain domain, RoleName role) throws ApplicationException {
		try {
			long roleId = RoleLocalServiceUtil.getRole(helper.getCompanyId(), role.getLabel()).getRoleId();
			UserGroupRoleLocalServiceUtil.addUserGroupRoles(person.getUserId(), domain.getDomainId(), new long[] {roleId});
		} catch (Exception e) {
			throw new ApplicationException(e, "");
		}
	}
	
	private void removeRole(Person person, Domain domain, RoleName role) throws ApplicationException {
		try {
			long roleId = RoleLocalServiceUtil.getRole(helper.getCompanyId(), role.getLabel()).getRoleId();
			UserGroupRoleLocalServiceUtil.deleteUserGroupRoles(person.getUserId(), domain.getDomainId(), new long[] {roleId});	
		} catch (Exception e) {
			throw new ApplicationException(e, "");
		}
	}

	public boolean hasPermission(BaseModel model, Permission permission) throws ApplicationException {
		try {
			//PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(helper.getUser());
			PermissionChecker permissionChecker = helper.getThemeDisplay().getPermissionChecker();

			if (model instanceof Person) {
				return checkUserPermission(permissionChecker, ((Person) model).getUserId(), permission);
			} else if (model instanceof Flat || model instanceof Supplier || model instanceof Condominium) {
				return checkDomainPermission(permissionChecker, ((Membership) model).getDomain(), permission);
			} else if (model instanceof Membership) {
				return checkDomainPermission(permissionChecker, ((Membership) model).getDomain(), permission) && 
						checkPersonTypePermission(permissionChecker, ((Membership) model).getType(), permission);
			} else {
				throw new SystemException("model.not.supported");
			}
		} catch (Exception e) {
			throw new ApplicationException(e, "fail.check.permission");
		}
	}

	private boolean checkPersonTypePermission(PermissionChecker permissionChecker, PersonType type, Permission permission) throws Exception {
		return permissionChecker.hasPermission(portal.getDomainId(), PersonType.class.getName(), type.ordinal(), permission.name());
	}	

	private boolean checkDomainPermission(PermissionChecker permissionChecker, Domain domain, Permission permission) throws Exception {
		return permissionChecker.hasPermission(domain.getDomainId(), domain.getClass().getName(), domain.getId(), permission.name());
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

		return GroupPermissionUtil.contains(permissionChecker, groupId, actionkey);
	}

}

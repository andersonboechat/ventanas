package br.com.abware.accountmgm.persistence.manager;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.liferay.faces.portal.context.LiferayPortletHelper;
import com.liferay.faces.portal.context.LiferayPortletHelperImpl;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.permission.GroupPermissionUtil;
import com.liferay.portal.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.service.permission.PortalPermissionUtil;
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
			portal.setRelatedId(10179);
			
			helper.getScopeGroup();
		} catch (Exception e) {
			
		}
	}

	public void addMembership(Person person, Membership membership) throws Exception {
		if (membership.getDomain() instanceof Flat) {
			if (membership.getType() == PersonType.OWNER) {
				List<Person> renters = personManager.findPeopleByType(membership.getDomain(), PersonType.RENTER);

				// Se o apartamento estiver alugado, o proprietario não tem pepel no apartamento
				if (renters.isEmpty()) {
					addRole(person, portal, RoleName.SENIOR_USER);
					addRole(person, membership.getDomain(), RoleName.FLAT_MANAGER);
					addGroup(person, membership.getDomain());
				}
			}

			if (membership.getType() == PersonType.RENTER) {
				List<Person> owners = personManager.findPeopleByType(membership.getDomain(), PersonType.OWNER);
				PersonTypePredicate ownerPredicate = new PersonTypePredicate(PersonType.OWNER);
				PersonTypePredicate renterPredicate = new PersonTypePredicate(PersonType.RENTER);
				
				for (Person owner : owners) {
					// Remover papeis do portal somente se não for proprietário/locatário de outros apartamentos
					int counter = CollectionUtils.countMatches(owner.getMemberships(), ownerPredicate) + 
								  CollectionUtils.countMatches(owner.getMemberships(), renterPredicate);

					if (counter == 1) {
						removeRole(owner, portal, RoleName.SENIOR_USER);
					}

					removeRole(owner, membership.getDomain(), RoleName.FLAT_MANAGER);
					removeGroup(owner, membership.getDomain());
				}

				addRole(person, portal, RoleName.SENIOR_USER);
				addRole(person, membership.getDomain(), RoleName.FLAT_MANAGER);
				addGroup(person, membership.getDomain());
			}

			if (membership.getType() == PersonType.RESIDENT) {
				addRole(person, portal, RoleName.REGULAR_USER);
				addRole(person, membership.getDomain(), RoleName.FLAT_ASSISTANT);
				addGroup(person, membership.getDomain());
			}

			if (membership.getType() == PersonType.DEPENDENT) {
				addRole(person, portal, RoleName.JUNIOR_USER);
				addRole(person, membership.getDomain(), RoleName.FLAT_MEMBER);
				addGroup(person, membership.getDomain());
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

			addGroup(person, membership.getDomain());
		}

		if (membership.getDomain() instanceof Condominium) {
			if (membership.getType() == PersonType.SYNCDIC || membership.getType() == PersonType.SUB_SYNCDIC) {
				addRole(person, portal, RoleName.SITE_ADMIN);
				addRole(person, membership.getDomain(), RoleName.ADMIN_MANAGER);
			}

			if (membership.getType() == PersonType.ADMIN_ASSISTANT) {
				addRole(person, portal, RoleName.SITE_ADMIN);
				addRole(person, membership.getDomain(), RoleName.ADMIN_ASSISTANT);
			}

			addGroup(person, membership.getDomain());		
		}
	}

	public void removeMembership(Person person, Membership membership) throws Exception {
		if (membership.getDomain() instanceof Flat) {
			if (membership.getType() == PersonType.OWNER || membership.getType() == PersonType.RENTER) {
				Person p = personManager.findById(person.getId());
				PersonTypePredicate ownerPredicate = new PersonTypePredicate(PersonType.OWNER);
				PersonTypePredicate renterPredicate = new PersonTypePredicate(PersonType.RENTER);
				
				// Remover papeis do portal somente se não for proprietário/locatário de outros apartamentos
				int counter = CollectionUtils.countMatches(p.getMemberships(), ownerPredicate) + 
							  CollectionUtils.countMatches(p.getMemberships(), renterPredicate);				
				if (counter == 1) {
					removeRole(person, portal, RoleName.SENIOR_USER);
				}				

				removeRole(person, membership.getDomain(), RoleName.FLAT_MANAGER);
			}

			if (membership.getType() == PersonType.RENTER) {
				List<Person> renters = personManager.findPeopleByType(membership.getDomain(), PersonType.RENTER);

				// Se não houver outros locatários, devolver papeis para os proprietarios
				if (renters.isEmpty() || renters.size() == 1) {
					List<Person> owners = personManager.findPeopleByType(membership.getDomain(), PersonType.OWNER);

					for (Person owner : owners) {
						addRole(owner, portal, RoleName.SENIOR_USER);
						addRole(owner, membership.getDomain(), RoleName.FLAT_MANAGER);
						addGroup(owner, membership.getDomain());
					}
				}
			}

			removeRole(person, portal, RoleName.REGULAR_USER);
			removeRole(person, membership.getDomain(), RoleName.FLAT_ASSISTANT);
			removeRole(person, portal, RoleName.JUNIOR_USER);
			removeRole(person, membership.getDomain(), RoleName.FLAT_MEMBER);
		}

		if (membership.getDomain() instanceof Supplier) {
			//removeRole(person, portal, RoleName.SECURITY_USER));
			removeRole(person, membership.getDomain(), RoleName.SUPPLIER_MANAGER);
		}

		if (membership.getDomain() instanceof Condominium) {
			removeRole(person, portal, RoleName.SITE_ADMIN);
			removeRole(person, membership.getDomain(), RoleName.ADMIN_MANAGER);
			removeRole(person, membership.getDomain(), RoleName.ADMIN_ASSISTANT);
			removeRole(person, membership.getDomain(), RoleName.ADMIN_MEMBER);
		}

		removeGroup(person, membership.getDomain());
	}

	private void addRole(Person person, Domain domain, RoleName role) throws ApplicationException {
		try {
			long roleId = RoleLocalServiceUtil.getRole(helper.getCompanyId(), role.getLabel()).getRoleId();
			UserGroupRoleLocalServiceUtil.addUserGroupRoles(person.getUserId(), domain.getRelatedId(), new long[] {roleId});
		} catch (Exception e) {
			throw new ApplicationException(e, "");
		}
	}

	private void removeRole(Person person, Domain domain, RoleName role) throws ApplicationException {
		try {
			long roleId = RoleLocalServiceUtil.getRole(helper.getCompanyId(), role.getLabel()).getRoleId();
			UserGroupRoleLocalServiceUtil.deleteUserGroupRoles(person.getUserId(), domain.getRelatedId(), new long[] {roleId});	
		} catch (Exception e) {
			throw new ApplicationException(e, "");
		}
	}

	private void removeGroup(Person person, Domain domain) throws Exception {
		UserLocalServiceUtil.unsetGroupUsers(domain.getRelatedId(), new long[] {person.getUserId()}, null);
	}

	private void addGroup(Person person, Domain domain) throws Exception {
		UserLocalServiceUtil.addGroupUsers(domain.getRelatedId(), new long[] {person.getUserId()});
	}	

	public boolean hasPermission(BaseModel model, Permission permission) throws ApplicationException {
		try {
			//PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(helper.getUser());
			PermissionChecker permissionChecker = helper.getThemeDisplay().getPermissionChecker();

			if (model instanceof Person) {
				return checkUserPermission(permissionChecker, ((Person) model).getUserId(), permission);
			} else if (model instanceof Flat || model instanceof Supplier || model instanceof Condominium) {
				return checkDomainPermission(permissionChecker, (Domain) model, permission);
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
		return permissionChecker.hasPermission(portal.getRelatedId(), PersonType.class.getName(), type.ordinal(), permission.name());
	}	

	private boolean checkDomainPermission(PermissionChecker permissionChecker, Domain domain, Permission permission) throws Exception {
		return permissionChecker.hasPermission(domain.getRelatedId(), domain.getClass().getName(), domain.getId(), permission.name());
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
	
	private boolean checkOrganizationPermission(PermissionChecker permissionChecker, long organizationId, Permission permission) throws Exception {
		String actionkey;

		if (permission == Permission.UPDATE_PERSON) {
			actionkey = ActionKeys.MANAGE_USERS;
		} else if (permission == Permission.ADD_PERSON) {
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
		} else if (permission == Permission.ADD) {
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

package br.com.abware.accountmgm.persistence.manager;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.liferay.faces.portal.context.LiferayPortletHelper;
import com.liferay.faces.portal.context.LiferayPortletHelperImpl;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.permission.PortalPermissionUtil;
import com.liferay.portal.service.permission.UserPermissionUtil;

import br.com.abware.accountmgm.util.PersonTypePredicate;
import br.com.abware.accountmgm.util.PersonTypeTransformer;
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
		} catch (Exception e) {
			
		}
	}

	public void addMembership(Person person, Membership membership) throws Exception {
		if (membership.getDomain() instanceof Flat) {
			if (membership.getType() == PersonType.OWNER) {
				List<Person> renters = personManager.findPeopleByType(membership.getDomain(), PersonType.RENTER);

				// Se o apartamento estiver alugado, o proprietario não tem pepel no apartamento
				if (renters.isEmpty()) {
					addRole(person, RoleName.SENIOR_USER);
					addRole(person, membership.getDomain(), RoleName.FLAT_MANAGER);
					addGroup(person, membership.getDomain());
					addGroup(person, portal);
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
						removeRole(owner, RoleName.SENIOR_USER);
						removeGroup(person, portal);
					}

					removeRole(owner, membership.getDomain(), RoleName.FLAT_MANAGER);
					removeGroup(owner, membership.getDomain());
				}

				addRole(person, RoleName.SENIOR_USER);
				addRole(person, membership.getDomain(), RoleName.FLAT_MANAGER);
				addGroup(person, membership.getDomain());
				addGroup(person, portal);
			}

			if (membership.getType() == PersonType.RESIDENT) {
				addRole(person, RoleName.REGULAR_USER);
				addRole(person, membership.getDomain(), RoleName.FLAT_ASSISTANT);
				addGroup(person, membership.getDomain());
				addGroup(person, portal);
			}

			if (membership.getType() == PersonType.DEPENDENT) {
				addRole(person, RoleName.JUNIOR_USER);
				addRole(person, membership.getDomain(), RoleName.FLAT_MEMBER);
				addGroup(person, membership.getDomain());
				addGroup(person, portal);
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

		if (membership.getDomain() instanceof Administration) {
			if (membership.getType() == PersonType.SYNCDIC || membership.getType() == PersonType.SUB_SYNCDIC) {
				addRole(person, portal, RoleName.SITE_ADMIN);
				addRole(person, membership.getDomain(), RoleName.ADMIN_MANAGER);
			}

			if (membership.getType() == PersonType.ADMIN_ASSISTANT) {
				addRole(person, portal, RoleName.SITE_ADMIN);
				addRole(person, membership.getDomain(), RoleName.ADMIN_ASSISTANT);
			}

			if (membership.getType() == PersonType.ADMIN_ADVISOR || membership.getType() == PersonType.TAX_ADVISOR) {
				addRole(person, membership.getDomain(), RoleName.ADMIN_MEMBER);
			}

			addGroup(person, membership.getDomain());		
			addGroup(person, portal);
		}
	}

	@SuppressWarnings("unchecked")
	public void removeMembership(Person person, Membership membership) throws Exception {
		Person p = personManager.findById(person.getId());

		if (membership.getDomain() instanceof Flat) {
			if (membership.getType() == PersonType.OWNER || membership.getType() == PersonType.RENTER) {
				PersonTypePredicate ownerPredicate = new PersonTypePredicate(PersonType.OWNER);
				PersonTypePredicate renterPredicate = new PersonTypePredicate(PersonType.RENTER);
				
				// Remover papeis do portal somente se não for proprietário/locatário de outros apartamentos
				int counter = CollectionUtils.countMatches(p.getMemberships(), ownerPredicate) + 
							  CollectionUtils.countMatches(p.getMemberships(), renterPredicate);				
				if (counter == 1) {
					removeRole(person, RoleName.SENIOR_USER);
				}

				removeRole(person, membership.getDomain(), RoleName.FLAT_MANAGER);
			}

			if (membership.getType() == PersonType.RENTER) {
				List<Person> renters = personManager.findPeopleByType(membership.getDomain(), PersonType.RENTER);

				// Se não houver outros locatários, devolver papeis para os proprietarios
				if (renters.isEmpty() || renters.size() == 1) {
					List<Person> owners = personManager.findPeopleByType(membership.getDomain(), PersonType.OWNER);

					for (Person owner : owners) {
						addRole(owner, RoleName.SENIOR_USER);
						addRole(owner, membership.getDomain(), RoleName.FLAT_MANAGER);
						addGroup(owner, membership.getDomain());
						addGroup(owner, portal);
					}
				}
			}

			removeRole(person, RoleName.REGULAR_USER);
			removeRole(person, membership.getDomain(), RoleName.FLAT_ASSISTANT);
			removeRole(person, RoleName.JUNIOR_USER);
			removeRole(person, membership.getDomain(), RoleName.FLAT_MEMBER);
		}

		if (membership.getDomain() instanceof Supplier) {
			//removeRole(person, portal, RoleName.SECURITY_USER));
			removeRole(person, membership.getDomain(), RoleName.SUPPLIER_MANAGER);
		}

		if (membership.getDomain() instanceof Administration) {
			removeRole(person, portal, RoleName.SITE_ADMIN);
			removeRole(person, membership.getDomain(), RoleName.ADMIN_MANAGER);
			removeRole(person, membership.getDomain(), RoleName.ADMIN_ASSISTANT);
			removeRole(person, membership.getDomain(), RoleName.ADMIN_MEMBER);
		}

		removeGroup(person, membership.getDomain());

		if (p.getMemberships() == null || p.getMemberships().size() <= 1) {
			removeGroup(person, portal);
		} else {
			List<PersonType> types = (List<PersonType>) CollectionUtils.union(Arrays.asList(PersonType.ADMIN_TYPES), Arrays.asList(PersonType.FLAT_TYPES));
			types.remove(PersonType.GUEST);
			types.remove(PersonType.VISITOR);
			p.getMemberships().remove(membership);
			if (!CollectionUtils.containsAny(CollectionUtils.collect(p.getMemberships(), new PersonTypeTransformer()), types)) {
				removeGroup(person, portal);
			}
		}
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
				UserGroupRoleLocalServiceUtil.addUserGroupRoles(person.getUserId(), domain.getRelatedId(), new long[] {roleId});	
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
				UserGroupRoleLocalServiceUtil.deleteUserGroupRoles(person.getUserId(), domain.getRelatedId(), new long[] {roleId});	
			}
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
			} else if (model instanceof Flat || model instanceof Supplier || model instanceof Administration) {
				return checkDomainPermission(permissionChecker, (Domain) model, permission);
			} else if (model instanceof Membership) {
				return checkPersonTypePermission(permissionChecker, ((Membership) model).getType(), ((Membership) model).getDomain(), permission);
			} else {
				throw new SystemException("model.not.supported");
			}
		} catch (Exception e) {
			throw new ApplicationException(e, "fail.check.permission");
		}
	}

	private boolean checkPersonTypePermission(PermissionChecker permissionChecker, PersonType type, Domain domain, Permission permission) throws Exception {
		 return permissionChecker.hasPermission(domain.getRelatedId(), PersonType.class.getName(), type.ordinal(), permission.name());
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

}

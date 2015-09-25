package br.com.atilo.jcondo.core.service;

import java.util.List;

import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.RoleName;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.core.persistence.manager.MembershipManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.PersonManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;

public class MembershipServiceImpl {
	
	private MembershipManagerImpl membershipManager = new MembershipManagerImpl();

	private PersonManagerImpl personManager = new PersonManagerImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();

	public void requestAuthorization(Domain domain, Person person, PersonType type) {
		
	}
	
	public Membership getMembership(Person person, Domain domain) {
		return membershipManager.findByPersonAndDomain(person, domain);
	}

	public List<Membership> getMemberships(Person person) {
		return membershipManager.findByPerson(person);
	}

	public Membership add(Domain domain, Person person, PersonType type) {
		Membership membership = new Membership(type, domain);

		if (membershipManager.findByPersonAndDomain(person, domain) != null) {
			throw new BusinessException("psn.membership.already.exists");
		}

		if (!securityManager.hasPermission(membership, Permission.ASSIGN_MEMBER)) {
			throw new BusinessException("psn.membership.assign.member.denied");
		}

		if (domain instanceof Flat) {
			if (type == PersonType.OWNER) {
				List<Person> renters = personManager.findPeopleByType(domain, PersonType.RENTER);

				// Se o apartamento estiver alugado, o proprietario não tem pepel no apartamento
				if (renters.isEmpty()) {
					addRole(person, RoleName.SENIOR_USER);
					addRole(person, domain, RoleName.FLAT_MANAGER);
					addOrganization(person, domain);
				}
			}

			if (type == PersonType.RENTER) {
				List<Person> owners = personManager.findPeopleByType(domain, PersonType.OWNER);
				
				for (Person owner : owners) {
					if (owner.getMemberships() == null || owner.getMemberships().size() <= 1) {
						removeRole(person, RoleName.SENIOR_USER);
					}
					removeRole(owner, domain, RoleName.FLAT_MANAGER);
					removeOrganization(owner, domain);
				}

				addRole(person, RoleName.SENIOR_USER);
				addRole(person, domain, RoleName.FLAT_MANAGER);
				addOrganization(person, domain);
			}

			if (type == PersonType.RESIDENT) {
				addRole(person, RoleName.SENIOR_USER);
				addRole(person, domain, RoleName.FLAT_ASSISTANT);
				addOrganization(person, domain);
			}

			if (type == PersonType.DEPENDENT) {
				addRole(person, RoleName.SENIOR_USER);
				addRole(person, domain, RoleName.FLAT_MEMBER);
				addOrganization(person, domain);
			}
		}

		if (domain instanceof Supplier) {
			if (type == PersonType.MANAGER) {
				addRole(person, domain, RoleName.SUPPLIER_MANAGER);
			}

			if (((Supplier) domain).getName() == "SECURITY") {
				if (type == PersonType.GATEKEEPER) {
					//addRole(person, portal, RoleName.SECURITY_USER));
				}
			}

			addOrganization(person, domain);
		}

		if (domain instanceof Administration) {
			if (type == PersonType.SYNCDIC || type == PersonType.SUB_SYNCDIC) {
				addRole(person, domain, RoleName.ADMIN_MANAGER);
				//addSiteRole(person, RoleName.SITE_ADMIN);
			}

			if (type == PersonType.ADMIN_ASSISTANT) {
				addRole(person, domain, RoleName.ADMIN_ASSISTANT);
				addRole(person, RoleName.SENIOR_USER);
				//addSiteRole(person, RoleName.SITE_ADMIN);
				addToSite(person);
			}

			if (type == PersonType.ADMIN_ADVISOR || type == PersonType.TAX_ADVISOR) {
				addRole(person, domain, RoleName.ADMIN_MEMBER);
			}

			if (type == PersonType.GATEKEEPER) {
				//addRole(person, domain, RoleName.SECURITY_STAFF));
			}

			addOrganization(person, domain);
		}

		return membershipManager.save(membership);
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
			throw new ApplicationException(e, "sct.add.role.failed");
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
			throw new ApplicationException(e, "sct.remove.role.failed");
		}
	}
	
	private void removeOrganization(Person person, Domain domain) throws Exception {
		UserLocalServiceUtil.unsetOrganizationUsers(domain.getRelatedId(), new long[] {person.getUserId()});
	}

	private void addOrganization(Person person, Domain domain) throws Exception {
		UserLocalServiceUtil.addOrganizationUsers(domain.getRelatedId(), new long[] {person.getUserId()});
	}

}

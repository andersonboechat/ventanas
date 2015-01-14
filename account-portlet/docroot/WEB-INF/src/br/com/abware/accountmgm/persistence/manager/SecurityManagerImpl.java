package br.com.abware.accountmgm.persistence.manager;

import com.liferay.faces.portal.context.LiferayPortletHelper;
import com.liferay.faces.portal.context.LiferayPortletHelperImpl;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.service.permission.UserPermissionUtil;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.ApplicationException;
import br.com.abware.jcondo.exception.SystemException;

public class SecurityManagerImpl {

	private LiferayPortletHelper helper = new LiferayPortletHelperImpl();	

	public boolean hasPermission(BaseModel model, Permission permission) throws ApplicationException {
		try {
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(helper.getUser());

			if (model instanceof Person) {
				Person person = (Person) model;
				return checkPersonPermission(permissionChecker, person.getId(), permission);
			} else if (model instanceof Flat) {
				Flat flat = (Flat) model;
				return checkFlatPermission(permissionChecker, flat.getId(), permission);
			} else {
				throw new SystemException("model.not.supported");
			}
		} catch (Exception e) {
			throw new ApplicationException(e, "fail.check.permission");
		}
	}
	
	private boolean checkPersonPermission(PermissionChecker permissionChecker, long personId, Permission permission) throws Exception {
		String actionkey;

		if (permission == Permission.UPDATE_PERSON) {
			actionkey = ActionKeys.UPDATE;
		} else if (permission == Permission.ADD_USER) { 
			actionkey = ActionKeys.ADD_USER;
		} else {
			throw new SystemException("permission.not.supported");
		}

		return UserPermissionUtil.contains(permissionChecker, personId, actionkey);
	}
	
	private boolean checkFlatPermission(PermissionChecker permissionChecker, long flatId, Permission permission) throws Exception {
		String actionkey;
		
		if (permission == Permission.UPDATE_PERSON) {
			actionkey = ActionKeys.UPDATE;
		} else if (permission == Permission.ADD_USER) {
			actionkey = ActionKeys.ADD_ORGANIZATION;
		} else {
			throw new SystemException("Permission not supported");
		}
		
		return OrganizationPermissionUtil.contains(permissionChecker, flatId, actionkey);
	}
}

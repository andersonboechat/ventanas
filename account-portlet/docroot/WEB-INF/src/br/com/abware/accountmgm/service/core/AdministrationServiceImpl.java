package br.com.abware.accountmgm.service.core;

import br.com.abware.accountmgm.persistence.manager.AdministrationManagerImpl;
import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Administration;

public class AdministrationServiceImpl {

	private static AdministrationManagerImpl adminManager = new AdministrationManagerImpl();

	private static SecurityManagerImpl securityManager = new SecurityManagerImpl();

	public Administration getAdministration(long adminId) throws Exception {
		return adminManager.findById(adminId);
	}

	public Administration getAdministration(String name) throws Exception {
		return adminManager.findByName(name);
	}

	public Administration register(Administration admin) throws Exception {
		if (!securityManager.hasPermission(admin, Permission.ADD)) {
			throw new Exception("sem permissao para cadastrar administra��o");
		}

		if (getAdministration(admin.getName()) != null) {
			throw new Exception("J� existe uma administra��o com este nome");
		}

		return adminManager.save(admin);
	}

	public void delete(Administration admin) {
	}
}

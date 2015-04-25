package br.com.atilo.jcondo.core.service;

import java.util.ArrayList;
import java.util.List;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.core.persistence.manager.AdministrationManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;

public class AdministrationServiceImpl {

	private AdministrationManagerImpl adminManager = new AdministrationManagerImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();

	public Administration getAdministration(long adminId) throws Exception {
		return adminManager.findById(adminId);
	}

	public Administration getAdministration(String name) throws Exception {
		Administration admin = adminManager.findByName(name);
		if (!securityManager.hasPermission(admin, Permission.VIEW)) {
			throw new BusinessException("adm.view.denied");
		}
		return admin;
	}

	public List<Administration> getAdministrations(Person person) throws Exception {
		List<Administration> administrations = new ArrayList<Administration>();

		for (Administration administration : adminManager.findAll()) {
			if (securityManager.hasPermission(administration, Permission.VIEW)) {
				administrations.add(administration);
			}
		}

		return administrations;
	}

	public Administration register(Administration admin) throws Exception {
		if (!securityManager.hasPermission(admin, Permission.ADD)) {
			throw new BusinessException("adm.create.denied");
		}

		if (adminManager.findByName(admin.getName()) != null) {
			throw new BusinessException("adm.exists");
		}

		return adminManager.save(admin);
	}

	public void delete(Administration admin) {
	}
}

package br.com.atilo.jcondo.core.service;

import java.util.ArrayList;
import java.util.List;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Person;
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
			return null;
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
			throw new Exception("sem permissao para cadastrar administração");
		}

		if (getAdministration(admin.getName()) != null) {
			throw new Exception("Já existe uma administração com este nome");
		}

		return adminManager.save(admin);
	}

	public void delete(Administration admin) {
	}
}

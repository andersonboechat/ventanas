package br.com.abware.accountmgm.service.core;

import java.util.ArrayList;
import java.util.List;

import br.com.abware.accountmgm.persistence.manager.AdministrationManagerImpl;
import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;

public class SupplierServiceImpl {

	private static AdministrationManagerImpl adminManager = new AdministrationManagerImpl();

	private static SecurityManagerImpl securityManager = new SecurityManagerImpl();

	public List<Supplier> getSuppliers(Domain domain) throws Exception {
		List<Supplier> suppliers = new ArrayList<Supplier>();

		for (Supplier supplier : adminManager.findAll()) {
			if (securityManager.hasPermission(supplier, Permission.VIEW)) {
				suppliers.add(supplier);
			}
		}

		return suppliers;
	}

	public Supplier register(Supplier supplier) throws Exception {
		if (!securityManager.hasPermission(supplier, Permission.ADD)) {
			throw new Exception("sem permissao para cadastrar fornecedor");
		}

//		if (getAdministration(admin.getName()) != null) {
//			throw new Exception("Já existe uma administração com este nome");
//		}

		return adminManager.save(admin);
	}

	public void delete(Administration admin) {
	}
}

package br.com.atilo.jcondo.core.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.SupplierStatus;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.BusinessException;

import br.com.atilo.jcondo.core.persistence.manager.AdministrationManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SupplierManagerImpl;

public class SupplierServiceImpl {

	private AdministrationManagerImpl adminManager = new AdministrationManagerImpl();
	
	private SupplierManagerImpl supplierManager = new SupplierManagerImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();

	public List<Supplier> getSuppliers(Domain domain) throws Exception {
		List<Supplier> suppliers = new ArrayList<Supplier>();

		for (Supplier supplier : supplierManager.findByDomain(domain)) {
//			if (securityManager.hasPermission(supplier, Permission.VIEW)) {
				suppliers.add(supplier);
//			}
		}

		return suppliers;
	}

	public List<SupplierStatus> getStatuses(Domain domain) {
		return Arrays.asList(SupplierStatus.values());
	}
	
	public Supplier register(Supplier supplier) throws Exception {
		if (!securityManager.hasPermission(supplier, Permission.ADD)) {
			throw new BusinessException("supplier.create.denied");
		}

//		if (getAdministration(admin.getName()) != null) {
//			throw new BusinessException("supplier.already.exists");
//		}

		return supplierManager.save(supplier);
	}

	public Supplier update(Supplier supplier) throws Exception {
		if (!securityManager.hasPermission(supplier, Permission.UPDATE)) {
			throw new BusinessException("supplier.update.denied");
		}

		return supplierManager.save(supplier);
	}

	public void delete(Supplier supplier) throws Exception {
		if (!securityManager.hasPermission(supplier, Permission.DELETE)) {
			throw new BusinessException("supplier.delete.denied");
		}

		supplierManager.delete(supplier);
	}

}

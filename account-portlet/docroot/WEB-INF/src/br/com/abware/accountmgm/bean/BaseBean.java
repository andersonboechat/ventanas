package br.com.abware.accountmgm.bean;

import java.util.Locale;
import java.util.ResourceBundle;

import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.accountmgm.service.core.AdministrationServiceImpl;
import br.com.abware.accountmgm.service.core.FlatServiceImpl;
import br.com.abware.accountmgm.service.core.ParkingServiceImpl;
import br.com.abware.accountmgm.service.core.PersonServiceImpl;
import br.com.abware.accountmgm.service.core.SupplierServiceImpl;
import br.com.abware.accountmgm.service.core.VehicleServiceImpl;
import br.com.abware.jcondo.core.model.Condominium;

public abstract class BaseBean {

	protected static final AdministrationServiceImpl adminService = new AdministrationServiceImpl();
	
	protected static final FlatServiceImpl flatService = new FlatServiceImpl();

	protected static final PersonServiceImpl personService = new PersonServiceImpl();	

	protected static final VehicleServiceImpl vehicleService = new VehicleServiceImpl();
	
	protected static final SupplierServiceImpl supplierService = new SupplierServiceImpl();
	
	protected static final ParkingServiceImpl parkingService = new ParkingServiceImpl();
	
	protected static final SecurityManagerImpl securityManager = new SecurityManagerImpl();

	protected static final Condominium CONDOMINIUM = new Condominium();
	
	protected static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));
	
}

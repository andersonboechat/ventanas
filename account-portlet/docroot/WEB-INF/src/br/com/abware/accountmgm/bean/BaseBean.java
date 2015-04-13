package br.com.abware.accountmgm.bean;

import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;

import br.com.abware.jcondo.core.model.Condominium;

import br.com.atilo.jcondo.core.service.AdministrationServiceImpl;
import br.com.atilo.jcondo.core.service.DocumentServiceImpl;
import br.com.atilo.jcondo.core.service.FlatServiceImpl;
import br.com.atilo.jcondo.core.service.ParkingServiceImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;
import br.com.atilo.jcondo.core.service.SupplierServiceImpl;
import br.com.atilo.jcondo.core.service.VehicleServiceImpl;

public abstract class BaseBean extends Observable {

	protected static final AdministrationServiceImpl adminService = new AdministrationServiceImpl();
	
	protected static final FlatServiceImpl flatService = new FlatServiceImpl();

	protected static final PersonServiceImpl personService = new PersonServiceImpl();	

	protected static final VehicleServiceImpl vehicleService = new VehicleServiceImpl();
	
	protected static final SupplierServiceImpl supplierService = new SupplierServiceImpl();
	
	protected static final ParkingServiceImpl parkingService = new ParkingServiceImpl();
	
	protected static final DocumentServiceImpl documentService = new DocumentServiceImpl();

	protected static final Condominium CONDOMINIUM = new Condominium();
	
	protected static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));
	
	public BaseBean() {
		super();
	}
	
}

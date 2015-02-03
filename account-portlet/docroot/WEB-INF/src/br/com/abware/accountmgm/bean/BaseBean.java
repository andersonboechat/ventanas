package br.com.abware.accountmgm.bean;

import br.com.abware.accountmgm.service.core.FlatServiceImpl;
import br.com.abware.accountmgm.service.core.PersonServiceImpl;
import br.com.abware.accountmgm.service.core.VehicleServiceImpl;
import br.com.abware.jcondo.core.service.FlatService;

public abstract class BaseBean {

	protected static final FlatService flatService = new FlatServiceImpl();
	
	protected static final PersonServiceImpl personService = new PersonServiceImpl();	

	protected static final VehicleServiceImpl vehicleService = new VehicleServiceImpl();	
	
}

package br.com.abware.accountmgm.bean;

import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.accountmgm.service.core.FlatServiceImpl;
import br.com.abware.accountmgm.service.core.PersonServiceImpl;
import br.com.abware.accountmgm.service.core.VehicleServiceImpl;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.service.FlatService;

public abstract class BaseBean {

	protected static final FlatServiceImpl flatService = new FlatServiceImpl();

	protected static final PersonServiceImpl personService = new PersonServiceImpl();	

	protected static final VehicleServiceImpl vehicleService = new VehicleServiceImpl();
	
	protected static final SecurityManagerImpl securityManager = new SecurityManagerImpl();

	protected static final Condominium CONDOMINIUM = new Condominium();

}

package br.com.abware.accountmgm.bean;

import br.com.abware.accountmgm.service.core.FlatServiceImpl;
import br.com.abware.accountmgm.service.core.PersonServiceImpl;
import br.com.abware.jcondo.core.service.FlatService;
import br.com.abware.jcondo.core.service.PersonService;

public class BaseBean {

	protected static final FlatService flatService = new FlatServiceImpl();
	
	protected static final PersonService personService = new PersonServiceImpl();	
	
	public BaseBean() {
		// TODO Auto-generated constructor stub
	}

}

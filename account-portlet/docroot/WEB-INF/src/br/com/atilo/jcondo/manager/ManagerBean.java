package br.com.atilo.jcondo.manager;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.atilo.jcondo.core.service.FlatServiceImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;

@SessionScoped
@ManagedBean
public class ManagerBean {

	private static Logger LOGGER = Logger.getLogger(ManagerBean.class);

	private PersonServiceImpl personService = new PersonServiceImpl();
	
	private FlatServiceImpl flatService = new FlatServiceImpl();

	private Person person;

	private List<Flat> flats; 

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			flats = flatService.getFlats(person);
		} catch (Exception e) {
			LOGGER.fatal("Failure on jcondo initialization", e);
		}
	}
	
	public void name() {
		for (Flat flat : flats) {
			
		}
		
		if (!flats.isEmpty()) {
			
		}
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	
}

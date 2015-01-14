package br.com.abware.accountmgm.bean;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.service.core.PersonServiceImpl;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.PersonService;
import br.com.abware.jcondo.exception.ApplicationException;

@ManagedBean
@ViewScoped
public class PersonBean {

	private static Logger LOGGER = Logger.getLogger(PersonBean.class);

	private static final PersonService personService = new PersonServiceImpl();

	@ManagedProperty(name="flatBean", value="#{flatBean}")
	private FlatBean flatBean;

	private ModelDataModel<Person> model;
	
	private Flat flat;

	private Person person;

	private Person[] selectedPeople;

	private List<PersonType> types;

	@PostConstruct
	public void init() {
		try {
			flat = flatBean.getFlat();
			model = new ModelDataModel<Person>(personService.getPeople(flat));
			person = model.getRowData();
			types = Arrays.asList(PersonType.values());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onPersonSave() {
		try {
			personService.register(person);
		} catch (ApplicationException e) {
			LOGGER.error("", e);
		}
	}
	
	public void onPersonDelete() {
		for (Person person : selectedPeople) {
			
		}
	}
	
	public void onPersonEdit() {
		
	}
	
	public List<Flat> getPersonFlats(Person person) {
		return null;
	}

	public List<String> getPersonSuppliers(Person person) {
		return null;
	}

	public List<String> getUserTypes() {
		return null;
	}
	
	public ModelDataModel<Person> getModel() {
		return model;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Person[] getSelectedPeople() {
		return selectedPeople;
	}

	public void setSelectedPeople(Person[] selectedPeople) {
		this.selectedPeople = selectedPeople;
	}

	public List<PersonType> getTypes() {
		return types;
	}

	public void setTypes(List<PersonType> types) {
		this.types = types;
	}

}

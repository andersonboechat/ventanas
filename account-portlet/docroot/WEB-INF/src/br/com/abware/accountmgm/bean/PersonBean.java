package br.com.abware.accountmgm.bean;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.RoleName;
import br.com.abware.jcondo.exception.ApplicationException;

@ManagedBean
@ViewScoped
public class PersonBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(PersonBean.class);

	@ManagedProperty(name="flatBean", value="#{flatBean}")
	private FlatBean flatBean;

	private ModelDataModel<Person> model;
	
	private Flat flat;

	private Person person;

	private Person[] selectedPeople;

	private List<RoleName> roles;

	@PostConstruct
	public void init() {
		try {
			flat = flatBean.getFlat();
			model = new ModelDataModel<Person>(personService.getPeople(flat));
			person = model.getRowData();
			roles = Arrays.asList(RoleName.values());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onPersonSave() {
		try {
			person = personService.register(person);
		} catch (Exception e) {
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

	public List<RoleName> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleName> roles) {
		this.roles = roles;
	}

}

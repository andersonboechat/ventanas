package br.com.abware.accountmgm.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.service.core.PersonServiceImpl;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.PersonService;
import br.com.abware.jcondo.exception.ApplicationException;

@ManagedBean
@ViewScoped
public class PersonBean {

	private static Logger LOGGER = Logger.getLogger(PersonBean.class);
	
	private static final PersonService personService = new PersonServiceImpl();
	
	private static final ModelDataModel<Person> model = initModel();
	
	/** Tipos: Apartamento, Fornecedor, Condominio */
	private List<String> groups;
	
	private String group;
	
	private Object supplier;
	
	private Flat flat;
	
	private Person person;
	
	private Person[] selectedPeople;

	private static ModelDataModel<Person> initModel() {
		try {
			//return new ModelDataModel<Flat>(personService.);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	

	@PostConstruct
	public void init() {
		
	}
	
	public void onPersonSave() {
		try {
			personService.register(person);
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onPersonDelete() {
		for (Person person : selectedPeople) {
			
		}
	}
	
	public void onPersonEdit() {
		
	}
	
	public void onGroupSelect() {
		
	}
	
	public void onSupplierSelect() {
		
	}
	
	public void onFlatSelect() {
		
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

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Object getSupplier() {
		return supplier;
	}

	public void setSupplier(Object supplier) {
		this.supplier = supplier;
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

}

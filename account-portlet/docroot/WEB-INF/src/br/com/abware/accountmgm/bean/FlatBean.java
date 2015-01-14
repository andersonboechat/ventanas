package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.service.core.FlatServiceImpl;
import br.com.abware.accountmgm.service.core.PersonServiceImpl;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.FlatService;
import br.com.abware.jcondo.core.service.PersonService;
import br.com.abware.jcondo.exception.ApplicationException;

@ManagedBean
@ViewScoped
public class FlatBean {

	private static Logger LOGGER = Logger.getLogger(FlatBean.class);

	private static final FlatService flatService = new FlatServiceImpl();
	
	private static final PersonService personService = new PersonServiceImpl();

	private ModelDataModel<Flat> model;

	private Person person;

	private Flat flat;

	private List<Integer> flatBlocks;

	private List<Integer> flatNumbers;

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			model = new ModelDataModel<Flat>(flatService.getFlats(person));
			flat = model.getRowData();
			flatBlocks = flatService.getBlocks();
			flatNumbers = flatService.getNumbers();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onFlatSave() {
		
	}
	
	public void onFlatDelete() {
		
	}
	
	public void onFlatEdit() {
		
	}

	public boolean hasPermission() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<Person> getOwners(Flat flat) {
		List<Person> people;
		try {
			people = personService.getPeople(flat);
			people = (List<Person>) CollectionUtils.select(people, new Predicate() {
																	@Override
																	public boolean evaluate(Object obj) {
																		Person person = (Person) obj;
																		return PersonType.OWNER.equals(person.getType());
																	}
															});
		} catch (ApplicationException e) {
			e.printStackTrace();
			people = new ArrayList<Person>();
		}

		return people;
	}

	@SuppressWarnings("unchecked")
	public List<Person> getRenters(Flat flat) {
		List<Person> people;
		try {
			people = personService.getPeople(flat);
			people = (List<Person>) CollectionUtils.select(people, new Predicate() {
																	@Override
																	public boolean evaluate(Object obj) {
																		Person person = (Person) obj;
																		return PersonType.RENTER.equals(person.getType());
																	}
															});
		} catch (ApplicationException e) {
			e.printStackTrace();
			people = new ArrayList<Person>();
		}

		return people;
	}

	public ModelDataModel<Flat> getModel() {
		return model;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public List<Integer> getFlatBlocks() {
		return flatBlocks;
	}

	public void setFlatBlocks(List<Integer> flatBlocks) {
		this.flatBlocks = flatBlocks;
	}

	public List<Integer> getFlatNumbers() {
		return flatNumbers;
	}

	public void setFlatNumbers(List<Integer> flatNumbers) {
		this.flatNumbers = flatNumbers;
	}	
}

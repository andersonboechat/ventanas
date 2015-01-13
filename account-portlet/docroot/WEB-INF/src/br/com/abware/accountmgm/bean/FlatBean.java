package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.primefaces.model.SortOrder;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.model.FlatUser;
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

	private static final ModelDataModel<Flat> model = initModel();

	private FlatUser user;
	
	private Flat flat;
	
	private List<Flat> flats;

	private List<Flat> selectedFlats;

	private List<Integer> flatBlocks;

	private List<Integer> flatNumbers;

	private static ModelDataModel<Flat> initModel() {
		try {
			return new ModelDataModel<Flat>(flatService.getFlats());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@PostConstruct
	public void init() {
		try {
			flatBlocks = flatService.getBlocks();
			flatNumbers = flatService.getNumbers();
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onFlatCreate() {
		
	}
	
	public void onFlatDelete() {
		
	}
	
	public void onFlatEdit() {
		
	}

	public void onFlatBlockSelect(AjaxBehaviorEvent event) {
		HashMap<String, String> filters = new HashMap<String, String>();
		filters.put("block", (String) event.getSource()); 
		model.load(0, 0, "number", SortOrder.ASCENDING, filters);
	}

	public void onFlatNumberSelect() {
		
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

	public FlatUser getUser() {
		return user;
	}

	public void setUser(FlatUser user) {
		this.user = user;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

	public List<Flat> getSelectedFlats() {
		return selectedFlats;
	}

	public void setSelectedFlats(List<Flat> selectedFlats) {
		this.selectedFlats = selectedFlats;
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

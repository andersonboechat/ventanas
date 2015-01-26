package br.com.abware.accountmgm.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.component.selectonemenu.SelectOneMenu;

import br.com.abware.accountmgm.bean.model.PersonDataModel;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;

@ManagedBean
@ViewScoped
public class TestBean extends BaseBean {

	private PersonDataModel model;

	private HashMap<String, Object> filters;

	private String personName;

	private Set<Long> blocks;
	
	private Long block;
	
	private Set<Long> numbers;

	private Long number;

	private List<Flat> flats;

	private Person person;
	
	private Person[] selectedPeople;

	@PostConstruct
	public void init() {
		try {
			List<Flat> flats = flatService.getFlats(personService.getPerson());
			model = new PersonDataModel(personService, flats);

			blocks = new TreeSet<Long>();
			numbers = new TreeSet<Long>();
			for (Flat flat : flats) {
				blocks.add(flat.getBlock());
				numbers.add(flat.getNumber());
			}

			filters = new HashMap<String, Object>();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onPersonSearch(AjaxBehaviorEvent event) throws Exception {
		filters.put("fullName", personName);
		model.filter(model, filters);
	}

	public void onBlockSelect(AjaxBehaviorEvent event) throws Exception {
		filters.put("block", block);
		model.filter(model, filters);
	}

	public void onNumberSelect(AjaxBehaviorEvent event) throws Exception {
		filters.put("number", number);
		model.filter(model, filters);
	}

	public void onPersonSave() {
		
	}

	public void onPersonDelete() {
		//personService.delete(person);
	}

	public void onPeopleDelete() {
		for (Person person : selectedPeople) {
			//personService.delete(person);
		}
	}
	
	public String displayMembership(Membership membership) {
		if (membership != null) {
			if (membership.getDomain() instanceof Flat) {
				Flat flat = (Flat) membership.getDomain(); 
				return "Apt. " + flat.getNumber() + " - Bloco " + flat.getBlock() + " --- " + membership.getRole().getTitle();
			} else if (membership.getDomain() instanceof Supplier) {
				return ((Supplier) membership.getDomain()).getName();
			} else if (membership.getDomain() instanceof Condominium) {
				return ((Condominium) membership.getDomain()).getName();
			}
		}

		return null;
	}

	public PersonDataModel getModel() {
		return model;
	}

	public void setModel(PersonDataModel model) {
		this.model = model;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public Long getBlock() {
		return block;
	}

	public void setBlock(Long block) {
		this.block = block;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Set<Long> getBlocks() {
		return blocks;
	}

	public void setBlocks(Set<Long> blocks) {
		this.blocks = blocks;
	}

	public Set<Long> getNumbers() {
		return numbers;
	}

	public void setNumbers(Set<Long> numbers) {
		this.numbers = numbers;
	}

	public Person[] getSelectedPeople() {
		return selectedPeople;
	}

	public void setSelectedPeople(Person[] selectedPeople) {
		this.selectedPeople = selectedPeople;
	}

}

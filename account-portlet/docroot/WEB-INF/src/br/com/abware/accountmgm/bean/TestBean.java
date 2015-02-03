package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.ApplicationException;

@ManagedBean
@ViewScoped
public class TestBean extends BaseBean {
	
	@ManagedProperty(value="#{imageUploadBean}")
	private ImageUploadBean imageUploadBean;

	private ModelDataModel<Person> model;

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
			List<Person> people = new ArrayList<Person>();
			flats = flatService.getFlats(personService.getPerson());

			blocks = new TreeSet<Long>();
			numbers = new TreeSet<Long>();
			for (Flat flat : flats) {
				blocks.add(flat.getBlock());
				numbers.add(flat.getNumber());
				people.addAll(personService.getPeople(flat));
			}

			model = new ModelDataModel<Person>(people);
			filters = new HashMap<String, Object>();
			imageUploadBean.setWidth(100);
			imageUploadBean.setHeight(100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onPersonSearch(AjaxBehaviorEvent event) throws Exception {
		filters.put("fullName", personName);
		model.filter(filters);
	}

	public void onBlockSelect(AjaxBehaviorEvent event) throws Exception {
		filters.put("memberships.domain.block", block);
		model.filter(filters);
	}

	public void onNumberSelect(AjaxBehaviorEvent event) throws Exception {
		filters.put("memberships.domain.number", number);
		model.filter(filters);
	}

	public void onPersonSave() {
		imageUploadBean.getImage();
	}

	public void onPersonCreate() throws Exception {
		
	}
	
	public void onPersonDelete() throws ApplicationException {
		personService.delete(person);
	}

	public void onPeopleDelete() throws ApplicationException {
		for (Person person : selectedPeople) {
			personService.delete(person);
		}
	}

	public void onPersonEdit() throws Exception {
		
	}

	public String displayMembership(Membership membership) {
		if (membership != null) {
			if (membership.getDomain() instanceof Flat) {
				Flat flat = (Flat) membership.getDomain(); 
				return "Apartamento " + flat.getNumber() + " - Bloco " + flat.getBlock() + " --- " + membership.getRole().getTitle();
			} else if (membership.getDomain() instanceof Supplier) {
				return ((Supplier) membership.getDomain()).getName() + " --- " + membership.getRole().getTitle();
			} else if (membership.getDomain() instanceof Condominium) {
				return membership.getRole().getTitle();
			}
		}

		return null;
	}

	public ImageUploadBean getImageUploadBean() {
		return imageUploadBean;
	}

	public void setImageUploadBean(ImageUploadBean imageUploadBean) {
		this.imageUploadBean = imageUploadBean;
	}

	public ModelDataModel<Person> getModel() {
		return model;
	}

	public void setModel(ModelDataModel<Person> model) {
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

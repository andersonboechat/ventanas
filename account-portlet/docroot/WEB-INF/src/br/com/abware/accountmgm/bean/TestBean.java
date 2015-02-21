package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.collections.CollectionUtils;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.util.BeanUtils;
import br.com.abware.accountmgm.util.DomainPredicate;
import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;

@ManagedBean
@ViewScoped
public class TestBean extends BaseBean {
	
	@ManagedProperty(value="#{imageUploadBean}")
	private ImageUploadBean imageUploadBean;

	private ModelDataModel<Person> model;

	private HashMap<String, Object> filters;

	private String personName;

	private Set<Integer> blocks;
	
	private Long block;
	
	private Set<Integer> numbers;

	private Long number;

	private List<Flat> flats;
	
	private Person person;
	
	private Person[] selectedPeople;

	private List<PersonType> types;
	
	private long selectedFlatId;
	
	private List<Gender> genders;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getFlats(personService.getPerson());
			blocks = new TreeSet<Integer>();
			numbers = new TreeSet<Integer>();
			for (Flat flat : flats) {
				blocks.add(flat.getBlock());
				numbers.add(flat.getNumber());
			}

			model = new ModelDataModel<Person>(personService.getPeople(personService.getPerson()));
			person = new Person();
			person.setPicture(new Image());
			filters = new HashMap<String, Object>();
			imageUploadBean.setWidth(198);
			imageUploadBean.setHeight(300);
			types = Arrays.asList(PersonType.FLAT_TYPES);
			genders = Arrays.asList(Gender.values());
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
		try {
			person.setPicture(imageUploadBean.getImage());

			if (person.getId() == 0) {
				person = personService.register(person);
				model.addModel(person);
			} else {
				person = personService.update(person);
				model.setModel(person);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onPersonCreate() throws Exception {
		person = new Person();
		person.setPicture(new Image());
		imageUploadBean.setImage(person.getPicture());
	}

	public void onPersonDelete() throws Exception {
		removeMemberships(person);
		person = personService.update(person);
	}

	public void onPeopleDelete() throws Exception {
		for (Person person : selectedPeople) {
			removeMemberships(person);
			person = personService.update(person);
		}
	}

	private void removeMemberships(Person person) {
		for (Flat flat : flats) {
			person.getMemberships().removeAll(CollectionUtils.select(person.getMemberships(), 
																	 new DomainPredicate(flat)));
		}
	}

	public void onPersonEdit() throws Exception {
		try {
			BeanUtils.copyProperties(person, model.getRowData());
			imageUploadBean.setImage(person.getPicture());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onFlatAdd() {
		Flat flat = flats.get(flats.indexOf(new Flat(selectedFlatId, 0, 0)));
		if (person.getMemberships() == null) {
			person.setMemberships(new ArrayList<Membership>());
		}
		person.getMemberships().add(new Membership(PersonType.VISITOR, flat));
		selectedFlatId = 0;
	}

	public String displayMembership(Membership membership) {
		if (membership != null) {
			if (membership.getDomain() instanceof Flat) {
				Flat flat = (Flat) membership.getDomain(); 
				return "Apartamento " + flat.getNumber() + " - Bloco " + flat.getBlock() + " --- " + rb.getString(membership.getType().getLabel());
			} else if (membership.getDomain() instanceof Supplier) {
				return ((Supplier) membership.getDomain()).getName() + " --- " + rb.getString(membership.getType().getLabel());
			} else if (membership.getDomain() instanceof Condominium) {
				return rb.getString(membership.getType().getLabel());
			}
		}

		return null;
	}
	
	public boolean canChangeType(Membership membership) throws Exception {
		return types.contains(membership.getType());
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

	public Set<Integer> getBlocks() {
		return blocks;
	}

	public void setBlocks(Set<Integer> blocks) {
		this.blocks = blocks;
	}

	public Set<Integer> getNumbers() {
		return numbers;
	}

	public void setNumbers(Set<Integer> numbers) {
		this.numbers = numbers;
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

	public long getSelectedFlatId() {
		return selectedFlatId;
	}

	public void setSelectedFlatId(long selectedFlatId) {
		this.selectedFlatId = selectedFlatId;
	}

	public List<Gender> getGenders() {
		return genders;
	}

	public void setGenders(List<Gender> genders) {
		this.genders = genders;
	}

}

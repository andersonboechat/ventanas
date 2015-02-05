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
import br.com.abware.accountmgm.bean.model.PersonModel;
import br.com.abware.accountmgm.util.BeanUtils;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;

@ManagedBean
@ViewScoped
public class TestBean extends BaseBean {
	
	@ManagedProperty(value="#{imageUploadBean}")
	private ImageUploadBean imageUploadBean;

	private ModelDataModel<PersonModel> model;

	private HashMap<String, Object> filters;

	private String personName;

	private Set<Long> blocks;
	
	private Long block;
	
	private Set<Long> numbers;

	private Long number;

	private List<Flat> flats;

	private Person person;
	
	private PersonModel personModel;
	
	private Person[] selectedPeople;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getFlats(personService.getPerson());
			blocks = new TreeSet<Long>();
			numbers = new TreeSet<Long>();
			for (Flat flat : flats) {
				blocks.add(flat.getBlock());
				numbers.add(flat.getNumber());
			}

			List<PersonModel> people = new ArrayList<PersonModel>();
			for (Person person : personService.getPeople(personService.getPerson())) {
				people.add(new PersonModel(person, personService.getMemberships(person)));
			}

			model = new ModelDataModel<PersonModel>(people);
			filters = new HashMap<String, Object>();
			imageUploadBean.setWidth(100);
			imageUploadBean.setHeight(100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onPersonSearch(AjaxBehaviorEvent event) throws Exception {
		filters.put("person.fullName", personName);
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
			personModel.getPerson().setPicture(imageUploadBean.getImage());
			Person person = personService.register(personModel.getPerson());
			personModel.setPerson(person);
			personService.updateMemberships(personModel.getPerson(), personModel.getMemberships());
			model.addModel(personModel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onPersonCreate() throws Exception {
		personModel = new PersonModel(new Person(), new ArrayList<Membership>());
	}
	
	public void onPersonDelete() throws Exception {
		personService.delete(person);
	}

	public void onPeopleDelete() throws Exception {
		for (Person person : selectedPeople) {
			personService.delete(person);
		}
	}

	public void onPersonEdit() throws Exception {
		try {
			BeanUtils.copyProperties(personModel, model.getRowData());
		} catch (Exception e) {
			//LOGGER.error("Falha ao editar veiculo", e);
		}
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

	public ModelDataModel<PersonModel> getModel() {
		return model;
	}

	public void setModel(ModelDataModel<PersonModel> model) {
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

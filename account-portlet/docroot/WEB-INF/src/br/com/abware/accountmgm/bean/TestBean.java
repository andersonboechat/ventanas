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

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.bean.model.PersonModel;
import br.com.abware.accountmgm.util.BeanUtils;
import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.model.Condominium;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Role;
import br.com.abware.jcondo.core.model.RoleName;
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
	
	private PersonModel personModel;
	
	private Person[] selectedPeople;

	private List<Role> roles;
	
	private long selectedFlatId;
	
	private List<Gender> genders;

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
			Person person = new Person();
			person.setPicture(new Image());
			personModel = new PersonModel(person, new ArrayList<Membership>());
			filters = new HashMap<String, Object>();
			imageUploadBean.setWidth(198);
			imageUploadBean.setHeight(300);
			roles = new ArrayList<Role>();
			for (RoleName roleName : RoleName.values()) {
				if (roleName.getType() == 0) {
					try {
					roles.add(securityManager.getRole(null, roleName));
					} catch (Exception e) {}
				}
			}
			genders = Arrays.asList(Gender.values());
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
	
	public void onRoleSelect(Membership membership) {
		membership.setRole(roles.get(roles.indexOf(membership.getRole())));
	}

	public void onPersonSave() {
		try {
			boolean isNew = personModel.getPerson().getId() == 0;

			personModel.getPerson().setPicture(imageUploadBean.getImage());
			Person person = personService.register(personModel.getPerson());
			personModel.setPerson(person);
			personService.updateMemberships(personModel.getPerson(), personModel.getMemberships());

			if (isNew) {
				model.addModel(personModel);
			} else {
				model.setModel(personModel);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onPersonCreate() throws Exception {
		personModel = new PersonModel(new Person(), new ArrayList<Membership>());
	}
	
	public void onPersonDelete() throws Exception {
		personService.delete(personModel.getPerson());
	}

	public void onPeopleDelete() throws Exception {
		for (Person person : selectedPeople) {
			personService.delete(person);
		}
	}

	public void onPersonEdit() throws Exception {
		try {
			personModel = new PersonModel(new Person(), new ArrayList<Membership>());
			BeanUtils.copyProperties(personModel, model.getRowData());
			imageUploadBean.setImage(personModel.getPerson().getPicture());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onFlatAdd() {
		Flat flat = flats.get(flats.indexOf(new Flat(selectedFlatId, 0, 0)));
		personModel.getMemberships().add(new Membership(new Role(), flat));
		selectedFlatId = 0;
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

	public PersonModel getPersonModel() {
		return personModel;
	}

	public void setPersonModel(PersonModel personModel) {
		this.personModel = personModel;
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

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
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

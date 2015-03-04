package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.util.BeanUtils;
import br.com.abware.accountmgm.util.DomainPredicate;
import br.com.abware.accountmgm.util.DomainTransformer;
import br.com.abware.accountmgm.util.IdPredicate;
import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;

@ManagedBean
@ViewScoped
public class PersonBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(PersonBean.class);

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

	private Map<Domain, List<PersonType>> types;

	private long selectedDomainId;

	private List<Gender> genders;
	
	public void init(List<? extends Domain> domains) {
		try {
			Set<Person> people = new HashSet<Person>();
			types = new HashMap<Domain, List<PersonType>>();

			for (Domain domain: domains) {
				people.addAll(personService.getPeople(domain));
				types.put(domain, personService.getTypes(domain));
			}

			model = new ModelDataModel<Person>(new ArrayList<Person>(people));
			person = model.getRowData();
			filters = new HashMap<String, Object>();
			imageUploadBean.setWidth(198);
			imageUploadBean.setHeight(300);
			genders = Arrays.asList(Gender.values());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
	}

	public void onPersonSearch(AjaxBehaviorEvent event) throws Exception {
		filters.put("fullName", personName);
		model.filter(filters);
	}

	public void onDomainSearch(Domain domain) throws Exception {
		filters.put("memberships.domain.id", domain != null ? domain.getId() : null);
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
	
	public void onDomainAdd() {
		List<Domain> domains = new ArrayList<Domain>(types.keySet());
		Domain domain = (Domain) CollectionUtils.find(domains, new IdPredicate(selectedDomainId));
		
		if (domain != null) {
			if (person.getMemberships() == null) {
				person.setMemberships(new ArrayList<Membership>());
			}

			if (domain instanceof Flat) {
				person.getMemberships().add(new Membership(PersonType.VISITOR, domain));			
			} else if (domain instanceof Administration) {
				person.getMemberships().add(new Membership(PersonType.EMPLOYEE, domain));
			}
		}

		selectedDomainId = 0;
	}

	public String displayMembership(Membership membership) {
		if (membership != null) {
			return displayDomain(membership.getDomain()) + " --- " + rb.getString(membership.getType().getLabel());
		}

		return null;
	}

	public String displayDomain(Domain domain) {
		if (domain instanceof Flat) {
			Flat flat = (Flat) domain; 
			return "Apartamento " + flat.getNumber() + " - Bloco " + flat.getBlock();
		} else if (domain instanceof Supplier) {
			return ((Supplier) domain).getName();
		} else if (domain instanceof Administration) {
			return ((Administration) domain).getName();
		}

		return null;
	}

	public boolean canChangeType(Membership membership) throws Exception {
		List<PersonType> ts = types.get(membership.getDomain());
		return ts != null ? ts.contains(membership.getType()) : false;
	}

	public boolean canEditPerson(Person person) throws Exception {
		boolean canEdit = false;
		if (person.equals(personService.getPerson())) {
			canEdit = true;
		} else {
			for (Membership membership : person.getMemberships()) {
				canEdit = canChangeType(membership);
				if (canEdit) {
					return true;
				}
			}
		}
		return canEdit;
	}

	public boolean canEditInfo() throws Exception {
		return person.getId() == 0 || person.equals(personService.getPerson());
	}

	@SuppressWarnings("unchecked")
	public List<Domain> getUnassignedDomains() throws Exception {
		List<Domain> domains = new ArrayList<Domain>(types.keySet());

		if (person != null && !CollectionUtils.isEmpty(person.getMemberships())) {
			List<Domain> ds = new ArrayList<Domain>(CollectionUtils.collect(person.getMemberships(), new DomainTransformer()));
			return (List<Domain>) CollectionUtils.subtract(domains, ds);
		} else {
			return domains;
		}
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

	public long getSelectedDomainId() {
		return selectedDomainId;
	}

	public void setSelectedDomainId(long selectedDomainId) {
		this.selectedDomainId = selectedDomainId;
	}

	public List<Gender> getGenders() {
		return genders;
	}

	public void setGenders(List<Gender> genders) {
		this.genders = genders;
	}

	public Map<Domain, List<PersonType>> getTypes() {
		return types;
	}

	public void setTypes(Map<Domain, List<PersonType>> types) {
		this.types = types;
	}

}

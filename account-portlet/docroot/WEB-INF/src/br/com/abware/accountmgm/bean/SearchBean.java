package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import br.com.abware.accountmgm.util.DomainPredicate;
import br.com.abware.accountmgm.util.IdentityPredicate;
import br.com.abware.jcondo.core.model.AccessType;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Vehicle;

@ManagedBean
@ViewScoped
public class SearchBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(SearchBean.class);

	private DualListModel<Person> personList;

	private CameraBean cameraBean;

	private Person person;

	private String personName;

	private String identity;
	
	private Vehicle vehicle;
	
	private String license;
	
	private Flat personFlat;
	
	private Flat vehicleFlat;
	
	private List<Flat> flats;
	
	private List<Person> people;
	
	private Person[] selectedPeople;
	
	private List<AccessType> accessTypes;
	
	private AccessType accessType;

	private List<Person> authorizers;
	
	private Person authorizer;
	
	@PostConstruct
	public void init() {
		try {
			flats = flatService.getFlats();
			cameraBean = new CameraBean(158, 240);
			accessTypes = Arrays.asList(AccessType.values());
			personList = new DualListModel<Person>(new ArrayList<Person>(), new ArrayList<Person>());
			people = new ArrayList<Person>();
		} catch (Exception e) {
			LOGGER.fatal("");
		}
	}

	public void onPersonSearch2() throws Exception {
		List<Person> people = personList.getSource(); 
		people.clear();

		if (!StringUtils.isEmpty(personName)) {
			people.addAll(personService.getPeople(personName));
			
			if (CollectionUtils.isEmpty(people)) {
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "person.search.not.found", null);
				RequestContext.getCurrentInstance().addCallbackParam("exception", true);
				return;
			}
		}

		if (!StringUtils.isEmpty(identity)) {
			Person person;
			if (CollectionUtils.isEmpty(people)) {
				person = personService.getPerson(identity);
			} else {
				person = (Person) CollectionUtils.find(people, new IdentityPredicate(identity));
			}

			if (person != null) {
				people.clear();
				people.add(person);
			} else {
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "person.search.not.found", null);
				RequestContext.getCurrentInstance().addCallbackParam("exception", true);
				return;
			}
		}

		if (personFlat != null) {
			if (CollectionUtils.isEmpty(people)) {
				people.addAll(personService.getPeople(personFlat));	
			} else {
				for (int i = people.size() - 1; i >= 0; i--) {
					if(!CollectionUtils.exists(people.get(i).getMemberships(), new DomainPredicate(personFlat))) {
						people.remove(i);
					}
				}
			}
		}

		if (CollectionUtils.isEmpty(people)) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "person.search.not.found", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	
	}
	
	public void onPersonSearch() throws Exception {
		people.clear();

		if (!StringUtils.isEmpty(personName)) {
			people.addAll(personService.getPeople(personName));
			
			if (CollectionUtils.isEmpty(people)) {
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "person.search.not.found", null);
				RequestContext.getCurrentInstance().addCallbackParam("exception", true);
				return;
			}
		}

		if (!StringUtils.isEmpty(identity)) {
			Person person;
			if (CollectionUtils.isEmpty(people)) {
				person = personService.getPerson(identity);
			} else {
				person = (Person) CollectionUtils.find(people, new IdentityPredicate(identity));
			}

			if (person != null) {
				people.clear();
				people.add(person);
			} else {
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "person.search.not.found", null);
				RequestContext.getCurrentInstance().addCallbackParam("exception", true);
				return;
			}
		}

		if (personFlat != null) {
			if (CollectionUtils.isEmpty(people)) {
				people.addAll(personService.getPeople(personFlat));	
			} else {
				for (int i = people.size() - 1; i >= 0; i--) {
					if(!CollectionUtils.exists(people.get(i).getMemberships(), new DomainPredicate(personFlat))) {
						people.remove(i);
					}
				}
			}
		}

		if (CollectionUtils.isEmpty(people)) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "person.search.not.found", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onPersonSave() {
		
	}
	
	public void onFlatSelect() throws Exception {
		authorizers = personService.getPeople(personFlat);
	}

	public void onPersonSelect(Person person) {
		ArrayUtils.add(selectedPeople, person);
	}

	public void onPeopleSelect() {
		LOGGER.info(selectedPeople);
	}	
	
	public void onVehicleSearch() {
		
	}
	
	public String displayAccessInstructions(Person person) throws Exception {
		if (personService.isAccessAuthorized(person)) {
			return rb.getString("person.access.authorized");
		} else {
			return rb.getString("person.access.not.authorized");
		}
	}
	
	public boolean isAccessAuthorized(Person person) throws Exception {
		return personService.isAccessAuthorized(person);
	}
	
	public DualListModel<Person> getPersonList() {
		return personList;
	}

	public void setPersonList(DualListModel<Person> personList) {
		this.personList = personList;
	}

	public CameraBean getCameraBean() {
		return cameraBean;
	}

	public void setCameraBean(CameraBean cameraBean) {
		this.cameraBean = cameraBean;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public Flat getPersonFlat() {
		return personFlat;
	}

	public void setPersonFlat(Flat flat) {
		this.personFlat = flat;
	}

	public Flat getVehicleFlat() {
		return vehicleFlat;
	}

	public void setVehicleFlat(Flat vehicleFlat) {
		this.vehicleFlat = vehicleFlat;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}

	public List<Person> getPeople() {
		return people;
	}

	public void setPeople(List<Person> people) {
		this.people = people;
	}

	public Person[] getSelectedPeople() {
		return selectedPeople;
	}

	public void setSelectedPeople(Person[] selectedPeople) {
		this.selectedPeople = selectedPeople;
	}

	public List<AccessType> getAccessTypes() {
		return accessTypes;
	}

	public void setAccessTypes(List<AccessType> accessTypes) {
		this.accessTypes = accessTypes;
	}

	public AccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	public List<Person> getAuthorizers() {
		return authorizers;
	}

	public void setAuthorizers(List<Person> authorizers) {
		this.authorizers = authorizers;
	}

	public Person getAuthorizer() {
		return authorizer;
	}

	public void setAuthorizer(Person authorizer) {
		this.authorizer = authorizer;
	}
}

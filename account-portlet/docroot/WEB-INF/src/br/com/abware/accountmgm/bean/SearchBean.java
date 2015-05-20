package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.context.RequestContext;

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
	
	private List<Person> selectedPeople;
	
	private List<AccessType> accessTypes;
	
	private AccessType accessType;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getFlats();
			cameraBean = new CameraBean(158, 240);
			accessTypes = Arrays.asList(AccessType.values());
		} catch (Exception e) {
			LOGGER.fatal("");
		}
	}

	public void onPersonSearch() throws Exception {
		people = null;

		if (!StringUtils.isEmpty(personName)) {
			people = personService.getPeople(personName);
			
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
				people = new ArrayList<Person>();
				people.add(person);
			} else {
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "person.search.not.found", null);
				RequestContext.getCurrentInstance().addCallbackParam("exception", true);
				return;
			}
		}

		if (personFlat != null) {
			if (CollectionUtils.isEmpty(people)) {
				people = personService.getPeople(personFlat);	
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

	public List<Person> getSelectedPeople() {
		return selectedPeople;
	}

	public void setSelectedPeople(List<Person> selectedPeople) {
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
}

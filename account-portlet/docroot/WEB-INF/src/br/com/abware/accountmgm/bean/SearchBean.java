package br.com.abware.accountmgm.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.context.RequestContext;

import br.com.abware.accountmgm.util.DomainPredicate;
import br.com.abware.accountmgm.util.IdentityPredicate;
import br.com.abware.jcondo.access.model.PassageEvent;
import br.com.abware.jcondo.access.model.PassageType;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Vehicle;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.event.service.PassageEventServiceImpl;

@ManagedBean
@ViewScoped
public class SearchBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(SearchBean.class);

	private final PassageEventServiceImpl passageEventService = new PassageEventServiceImpl();

	@ManagedProperty(value="#{vehicleRegistrationBean}")
	private VehicleRegistrationBean vehicleBean;

	@ManagedProperty(value="#{personRegistrationBean}")
	private PersonRegistrationBean personBean;

	private String name;

	private String identity;

	private Flat flat;

	private List<Flat> flats;

	private List<Person> foundPeople;

	private Set<Person> selectedPeople;

	private Person[] tablePeople;

	private List<PassageType> passageTypes;

	private PassageType passageType;

	private List<Person> authorizers;

	private Person authorizer;
	
	private Flat destiny;

	private String comment;
	
	private boolean skipVehicleStep;

	@PostConstruct
	public void init() {
		try {
			flats = flatService.getFlats();
			passageTypes = Arrays.asList(PassageType.values());
			passageType = PassageType.INBOUND;
			foundPeople = new ArrayList<Person>();
			selectedPeople = new HashSet<Person>();
		} catch (Exception e) {
			LOGGER.fatal("");
		}
	}

	public void onPersonSearch() throws Exception {
		foundPeople.clear();

		if (!StringUtils.isEmpty(name)) {
			foundPeople.addAll(personService.getPeople(name));
			
			if (CollectionUtils.isEmpty(foundPeople)) {
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "person.search.not.found", null);
				RequestContext.getCurrentInstance().addCallbackParam("exception", true);
				return;
			}
		}

		if (!StringUtils.isEmpty(identity)) {
			Person person;
			if (CollectionUtils.isEmpty(foundPeople)) {
				person = personService.getPerson(identity);
			} else {
				person = (Person) CollectionUtils.find(foundPeople, new IdentityPredicate(identity));
			}

			if (person != null) {
				foundPeople.clear();
				foundPeople.add(person);
			} else {
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "person.search.not.found", null);
				RequestContext.getCurrentInstance().addCallbackParam("exception", true);
				return;
			}
		}

		if (flat != null) {
			if (CollectionUtils.isEmpty(foundPeople)) {
				foundPeople.addAll(personService.getPeople(flat));	
			} else {
				for (int i = foundPeople.size() - 1; i >= 0; i--) {
					if(!CollectionUtils.exists(foundPeople.get(i).getMemberships(), new DomainPredicate(flat))) {
						foundPeople.remove(i);
					}
				}
			}
		}

		if (CollectionUtils.isEmpty(foundPeople)) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "person.search.not.found", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}
	
	public void onFlatSelect() throws Exception {
		authorizers = personService.getPeople(flat);
	}

	public void onPersonSelect(Person person) {
		selectedPeople.add(person);
	}

	public void onPeopleSelect() {
		CollectionUtils.addAll(selectedPeople, tablePeople);
	}	
	
	public void onPersonUnselect(Person person) {
		selectedPeople.remove(person);
	}

	public void onLicenseChange() {
		Vehicle v = vehicleService.getVehicle(vehicleBean.getVehicle().getLicense());
		
		if (v == null) {
			String license = vehicleBean.getVehicle().getLicense();
			vehicleBean.setVehicle(vehicleBean.createVehicle());
			vehicleBean.getVehicle().setLicense(license);
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "vhc.not.found", null);
			return;
		} else {
			vehicleBean.setVehicle(v);
		}
	}

	public void onEventCreate() {
		try {
			PassageEvent event;
			Date now = new Date();

			if (authorizer == null) {
				for (Person person : selectedPeople) {
					if (!personService.isAccessAuthorized(person)) {
						MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "authorizer.empty", null);
					}
				}
			}

			for (Person person : selectedPeople) {
				event = new PassageEvent(now, comment, passageType, 
										 person, vehicleBean.getVehicle(), 
										 flat, authorizer);
				passageEventService.register(event);
			}

			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "passage.event.save.success", null);
		} catch (BusinessException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.fatal("", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "passage.event.save.fail", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}
	
	public boolean doAuthorization() {
		try {
			if (passageType == PassageType.INBOUND) {
				for (Person person : selectedPeople) {
					if (!personService.isAccessAuthorized(person)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.warn("fail to check if person access is authorized", e);
		}

		return false;
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

	public VehicleRegistrationBean getVehicleBean() {
		return vehicleBean;
	}

	public void setVehicleBean(VehicleRegistrationBean vehicleBean) {
		this.vehicleBean = vehicleBean;
	}

	public PersonRegistrationBean getPersonBean() {
		return personBean;
	}

	public void setPersonBean(PersonRegistrationBean personBean) {
		this.personBean = personBean;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
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

	public List<Person> getFoundPeople() {
		return foundPeople;
	}

	public void setFoundPeople(List<Person> foundPeople) {
		this.foundPeople = foundPeople;
	}

	public Set<Person> getSelectedPeople() {
		return selectedPeople;
	}

	public void setSelectedPeople(Set<Person> selectedPeople) {
		this.selectedPeople = selectedPeople;
	}

	public Person[] getTablePeople() {
		return tablePeople;
	}

	public void setTablePeople(Person[] tablePeople) {
		this.tablePeople = tablePeople;
	}

	public List<PassageType> getPassageTypes() {
		return passageTypes;
	}

	public void setPassageTypes(List<PassageType> passageTypes) {
		this.passageTypes = passageTypes;
	}

	public PassageType getPassageType() {
		return passageType;
	}

	public void setPassageType(PassageType passageType) {
		this.passageType = passageType;
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

	public Flat getDestiny() {
		return destiny;
	}

	public void setDestiny(Flat destiny) {
		this.destiny = destiny;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isSkipVehicleStep() {
		return skipVehicleStep;
	}

	public void setSkipVehicleStep(boolean skipVehicleStep) {
		this.skipVehicleStep = skipVehicleStep;
	}


}

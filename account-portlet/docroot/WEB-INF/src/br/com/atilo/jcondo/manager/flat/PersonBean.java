package br.com.atilo.jcondo.manager.flat;

import java.util.Arrays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FlowEvent;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.util.DomainPredicate;
import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.KinType;
import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Phone;
import br.com.abware.jcondo.core.model.PhoneType;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.abware.jcondo.exception.ModelExistException;
import br.com.atilo.jcondo.core.service.PersonDetailServiceImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;
import br.com.atilo.jcondo.manager.CameraBean;
import br.com.atilo.jcondo.manager.ImageUploadBean;


@ViewScoped
@ManagedBean(name="personView")
public class PersonBean {

	private static Logger LOGGER = Logger.getLogger(PersonBean.class);
	
	private static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));

	private PersonServiceImpl personService = new PersonServiceImpl();
	
	private PersonDetailServiceImpl personDetailService = new PersonDetailServiceImpl();

	private ImageUploadBean imageUploadBean;
	
	private CameraBean cameraBean;

	private ModelDataModel<Person> model;

	private HashMap<String, Object> filters;

	private Flat flat;

	private Person person;

	private Person logPerson;	
	
	private List<PersonType> types;

	private long selectedDomainId;

	private List<Gender> genders;

	private Membership membership;

	private Phone phone;

	private String phoneNumber;

	private Kinship kinship;

	private List<KinType> kinTypes;

	private List<PhoneType> phoneTypes;

	private String identity;

	public PersonBean() {
		filters = new HashMap<String, Object>();
		imageUploadBean = new ImageUploadBean(158, 240);
		cameraBean = new CameraBean(158, 240);
		genders = Arrays.asList(Gender.values());
		phoneTypes = Arrays.asList(PhoneType.values());
		phone = new Phone();
		kinship = new Kinship(logPerson, person, null);
		kinTypes = new ArrayList<KinType>();
		kinTypes.add(KinType.SPOUSE);
		kinTypes.add(KinType.PARENT);
		kinTypes.add(KinType.CHILD);
		kinTypes.add(KinType.OTHER);
	}
	
	public void init(Flat flat) {
		try {
			logPerson = personService.getPerson();
			model = new ModelDataModel<Person>(personService.getPeople(flat));
			types = personService.getTypes(flat);
			this.flat = flat;
			onPersonCreate();
		} catch (Exception e) {
			LOGGER.fatal("Failure on person initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onPersonSearch(ValueChangeEvent event) throws Exception {
		filters.put("fullName", (String) event.getNewValue());
		model.filter(filters);
	}
	
	public void onPersonSave() {
		try {
			Person p;

			if (person.getId() == 0) {
				p = personService.register(person);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.add.success", null);
			} else {
				p = personService.update(person);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.update.success", null);
			}

			if (kinship.getType() != null) {
				personDetailService.saveKinship(kinship);
			}

			if (StringUtils.isEmpty(phoneNumber)) {
				if (phone.getId() > 0) {
					personDetailService.removePhone(phone);
				}
			} else {
				String pn = phoneNumber.replaceAll("[^0-9]+", "");
				if (!phoneNumber.equals(phone.getExtension() + phone.getNumber())) {
					phone.setExtension(pn.substring(0, 2));
					phone.setNumber(pn.substring(2));
					personDetailService.savePhone(p, phone);
				}
			}

			model.update(p);
			model.filter(filters);
		} catch (ModelExistException e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs(), ":tabs:person-details-form:alertMsg");
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
			RequestContext.getCurrentInstance().addCallbackParam("alert", true);
		} catch (BusinessException e) {
			LOGGER.warn("Business failure on person saving: " + e.getMessage());
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on person saving", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}	
	}

	public void onPersonCreate() throws Exception {
		imageUploadBean.setImage(new Image());
		person = new Person();
		person.setPicture(imageUploadBean.getImage());
		membership = new Membership(PersonType.RESIDENT, flat);
		person.setMemberships(new ArrayList<Membership>());
		person.getMemberships().add(membership);
	}

	public void onPersonDelete() throws Exception {
		try {
			person = model.getRowData();
			person.getMemberships().removeAll(CollectionUtils.select(person.getMemberships(), new DomainPredicate(flat)));
			personService.update(person);
			model.removeModel(person);
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.remove.success", null);
		} catch (BusinessException e) {
			LOGGER.warn("Business failure on person delete: " + e.getMessage());
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on person delete", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}	
	}

	public void onPersonEdit() throws Exception {
		try {
			BeanUtils.copyProperties(person, model.getRowData());
			imageUploadBean.setImage(person.getPicture());
			membership = (Membership) CollectionUtils.find(person.getMemberships(), new DomainPredicate(flat));

			phone = personDetailService.getPhone(person);

			if (phone != null) {
				phoneNumber = phone.getExtension() + phone.getNumber();
			} else {
				phone = new Phone();
				phone.setPrimary(true);
			}

			kinship = personDetailService.getKinship(logPerson, person);

			if (kinship == null) {
				kinship = new Kinship(logPerson, person, null);
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on person editing", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	@SuppressWarnings("unchecked")
	public void onPersonTypeSelect(AjaxBehaviorEvent event) throws Exception {
		String type = (String) ((SelectOneMenu) event.getSource()).getValue();

		if (type.equalsIgnoreCase("resident")) {
			List<Person> people;
			filters.put("memberships.type", PersonType.OWNER);
			model.filter(filters);
			people = (List<Person>) model.getWrappedData();
			filters.put("memberships.type", PersonType.RENTER);
			model.filter(filters);
			people.addAll((List<Person>) model.getWrappedData());
			filters.put("memberships.type", PersonType.RESIDENT);
			model.filter(filters);
			people.addAll((List<Person>) model.getWrappedData());
			filters.put("memberships.type", PersonType.DEPENDENT);
			model.filter(filters);
			((List<Person>) model.getWrappedData()).addAll(people);
		} else if (type.equalsIgnoreCase("guest")) {
			filters.put("memberships.type", PersonType.GUEST);
			model.filter(filters);
		} else if (type.equalsIgnoreCase("visitor")) {
			filters.put("memberships.type", PersonType.VISITOR);
			model.filter(filters);
		} else if (type.equalsIgnoreCase("employee")) {
			filters.put("memberships.type", PersonType.EMPLOYEE);
			model.filter(filters);
		} else {
			filters.put("memberships.type", null);
			model.filter(filters);
		}
	}
	
	public String displayMembership(Person person) {
		if (person != null && !CollectionUtils.isEmpty(person.getMemberships())) {
			Membership membership = (Membership) CollectionUtils.find(person.getMemberships(), new DomainPredicate(flat));
			return membership != null ? rb.getString(membership.getType().getLabel()) : null;				
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

	@SuppressWarnings("unchecked")
	public List<Person> displayResidents() throws Exception {
		HashMap<String, Object> newFilters = new HashMap<String, Object>();
		List<Person> people;

		newFilters.put("memberships.type", PersonType.OWNER);
		model.filter(newFilters);
		people = (List<Person>) model.getWrappedData();

		newFilters.put("memberships.type", PersonType.RENTER);
		model.filter(newFilters);
		people.addAll((List<Person>) model.getWrappedData());

		newFilters.put("memberships.type", PersonType.RESIDENT);
		model.filter(newFilters);
		people.addAll((List<Person>) model.getWrappedData());

		model.filter(filters);

		return people;
	}
	
	public boolean canChangeType() throws Exception {
		if (person.getId() == 0) {
			return true;
		}

		Membership membership = (Membership) CollectionUtils.find(person.getMemberships(), new DomainPredicate(flat));

		return membership != null && types != null ? types.contains(membership.getType()) : false;
	}

	public boolean canEditPerson(Person person) throws Exception {
		boolean canEdit = false;

		if (person.getId() == 0 || person.equals(personService.getPerson())) {
			canEdit = true;
		} else {
			Membership membership = (Membership) CollectionUtils.find(person.getMemberships(), new DomainPredicate(flat));
			canEdit = membership != null && types != null ? types.contains(membership.getType()) : false;
		}

		return canEdit;
	}

	public boolean canEditInfo() throws Exception {
		return person != null && (person.getId() == 0 || person.equals(personService.getPerson()));
	}

	public void validatePhone(FacesContext context, UIComponent component, Object value) {
		if (!StringUtils.isEmpty((String) value) && phone.getType() == null) {
			FacesMessage message = MessageUtils.getMessage(UIInput.REQUIRED_MESSAGE_ID, null);
			throw new ValidatorException(message);
		}
	}
	
	public String handleFlow(FlowEvent event) throws Exception {
		String PERSON_TYPE = "person-type";
		String PERSON_FOUND = "person-found";
		String PERSON_DETAILS = "person-details";
		String newStep = event.getNewStep();

		if (PERSON_FOUND.equals(newStep)) {
			if (StringUtils.isEmpty(identity)) {
				return PERSON_DETAILS;
			}

			person = personService.getPerson(identity);
			
			return person != null ? PERSON_FOUND : PERSON_DETAILS;
		}
		
		if (PERSON_DETAILS.equals(newStep)) {
			phone = personDetailService.getPhone(person);

			if (phone != null) {
				phoneNumber = phone.getExtension() + phone.getNumber();
			} else {
				phone = new Phone();
				phone.setPrimary(true);
			}

			kinship = personDetailService.getKinship(logPerson, person);
		}
		
		
		return null;
	}
	
	public ImageUploadBean getImageUploadBean() {
		return imageUploadBean;
	}

	public void setImageUploadBean(ImageUploadBean imageUploadBean) {
		this.imageUploadBean = imageUploadBean;
	}

	public CameraBean getCameraBean() {
		return cameraBean;
	}

	public void setCameraBean(CameraBean cameraBean) {
		this.cameraBean = cameraBean;
	}

	public ModelDataModel<Person> getModel() {
		return model;
	}

	public void setModel(ModelDataModel<Person> model) {
		this.model = model;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
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

	public List<PersonType> getTypes() {
		return types;
	}

	public void setTypes(List<PersonType> types) {
		this.types = types;
	}

	public Flat getFlat() {
		return flat;
	}

	public void setFlat(Flat flat) {
		this.flat = flat;
	}

	public Membership getMembership() {
		return membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
	}

	public Phone getPhone() {
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Kinship getKinship() {
		return kinship;
	}

	public void setKinship(Kinship kinship) {
		this.kinship = kinship;
	}

	public List<KinType> getKinTypes() {
		return kinTypes;
	}

	public void setKinTypes(List<KinType> kinTypes) {
		this.kinTypes = kinTypes;
	}

	public List<PhoneType> getPhoneTypes() {
		return phoneTypes;
	}

	public void setPhoneTypes(List<PhoneType> phoneTypes) {
		this.phoneTypes = phoneTypes;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

}

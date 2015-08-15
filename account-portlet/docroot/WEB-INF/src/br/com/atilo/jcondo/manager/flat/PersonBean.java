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
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.context.RequestContext;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.util.DomainPredicate;
import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;
import br.com.atilo.jcondo.manager.CameraBean;
import br.com.atilo.jcondo.manager.ImageUploadBean;

@ViewScoped
@ManagedBean(name="personView")
public class PersonBean {

	private static Logger LOGGER = Logger.getLogger(PersonBean.class);
	
	private static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));

	private PersonServiceImpl personService = new PersonServiceImpl();

	private ImageUploadBean imageUploadBean;
	
	private CameraBean cameraBean;

	private ModelDataModel<Person> model;

	private HashMap<String, Object> filters;

	private String personName;

	private Flat flat;

	private Person person;

	private List<PersonType> types;

	private long selectedDomainId;

	private List<Gender> genders;

	private Membership membership;
	
	public PersonBean() {
		filters = new HashMap<String, Object>();
		imageUploadBean = new ImageUploadBean(158, 240);
		cameraBean = new CameraBean(158, 240);
		genders = Arrays.asList(Gender.values());
	}
	
	public void init(Flat flat) {
		try {
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

	public void onPersonSearch(AjaxBehaviorEvent event) throws Exception {
		filters.put("fullName", personName);
		model.filter(filters);
	}
	
	public void onPersonSave() {
		try {
			Person p;

			if (person.getId() == 0) {
				p = personService.register(person);
				model.addModel(p);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.add.success", null);
			} else {
				p = personService.update(person);
				model.setModel(p);
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.update.success", null);
			}
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
		person = new Person();
		membership = new Membership(null, flat);
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
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on person editing", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	@SuppressWarnings("unchecked")
	public void onPersonTypeSelect(AjaxBehaviorEvent event) throws Exception {
		filters.clear();

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

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
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

}

package br.com.atilo.jcondo.manager.flat;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.context.RequestContext;

import br.com.abware.accountmgm.bean.model.ModelDataModel;
import br.com.abware.accountmgm.util.DomainPredicate;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;


@ViewScoped
@ManagedBean(name="personListView")
public class PersonListBean {

	private static Logger LOGGER = Logger.getLogger(PersonListBean.class);
	
	private static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));

	private PersonServiceImpl personService = new PersonServiceImpl();
	
	private NewPersonBean personBean;

	private ModelDataModel<Person> model;

	private HashMap<String, Object> filters;

	private List<PersonType> types;

	private Flat flat;

	public void init(Flat flat) {
		try {
			this.flat = flat;
			model = new ModelDataModel<Person>(personService.getPeople(flat));
			types = personService.getTypes(flat);
			filters = new HashMap<String, Object>();
			personBean = new NewPersonBean();
			onCreate();
		} catch (Exception e) {
			LOGGER.fatal("Failure on person initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onCreate() throws Exception {
		personBean.init(new Person(), flat);
	}	
	
	public void onEdit() throws Exception {
		personBean.init(model.getRowData(), flat);
	}	

	public void onDelete() throws Exception {
		try {
			Person person = model.getRowData();
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
	
	public void onSearch(ValueChangeEvent event) throws Exception {
		filters.put("fullName", (String) event.getNewValue());
		model.filter(filters);
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

	public NewPersonBean getPersonBean() {
		return personBean;
	}

	public void setPersonBean(NewPersonBean personBean) {
		this.personBean = personBean;
	}

	public ModelDataModel<Person> getModel() {
		return model;
	}

	public void setModel(ModelDataModel<Person> model) {
		this.model = model;
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

}

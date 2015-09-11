package br.com.atilo.jcondo.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.context.RequestContext;

import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.PetType;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.core.service.FlatServiceImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;

@SessionScoped
@ManagedBean
public class ManagerBean {

	private static Logger LOGGER = Logger.getLogger(ManagerBean.class);

	private PersonServiceImpl personService = new PersonServiceImpl();

	private final FlatServiceImpl flatService = new FlatServiceImpl();

	private Person person;

	private Flat flat;
	
	private List<Flat> flats;
	
	private List<Gender> genders;
	
	private List<PetType> petTypes;
	
	private int flatListIndex;

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			if (!person.getRegisterComplete()) {
				genders = Arrays.asList(Gender.values());

				flats = flatService.getPersonFlats(person);

				for (int i = flats.size() - 1; i >= 0; i--) {
					if (!flatService.hasPermission(flats.get(i), Permission.UPDATE)) {
						flats.remove(i);
					}
				}

				if (!flats.isEmpty()) {
					flat = flats.get(0);	
					petTypes = new ArrayList<PetType>();
					petTypes.add(PetType.DOG);
					petTypes.add(PetType.OTHER);
				}

			}
		} catch (Exception e) {
			LOGGER.fatal("Failure on ManagerBean initialization", e);
		}
	}

	public void onSavePerson() {
		try {
			person = personService.update(person);
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

	public void onNext() {
		try {
			flatService.update(flat);

			if (hasNextFlat()) {
				flat = flats.get(++flatListIndex);
				RequestContext.getCurrentInstance().addCallbackParam("value", flat.getPets());
			}
		} catch (Exception e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
			LOGGER.error("", e);
		}
	}

	public void onPrevious() {
		try {
			if (hasPreviousFlat()) {
				flat = flats.get(--flatListIndex);
				RequestContext.getCurrentInstance().addCallbackParam("value", flat.getPets());
			}
		} catch (Exception e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "general.failure", null);
			LOGGER.error("", e);
		}
	}

	public void onFinish() {
		try {
			if (flat != null) {
				flatService.update(flat);
			}
			person.setRegisterComplete(true);
			person = personService.update(person);
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "register.complete.success", null);
		} catch (BusinessException e) {
			LOGGER.warn("Business failure on finish register: " + e.getMessage());
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "register.complete.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
			LOGGER.error("", e);
		}
 	}

	public void onPetSelect(AjaxBehaviorEvent event) {
		Boolean value = (Boolean) ((SelectOneRadio) event.getSource()).getValue();
		RequestContext.getCurrentInstance().addCallbackParam("value", value);
	}	
	
	public boolean hasNextFlat() {
		return flatListIndex + 1 < flats.size();
	}

	public boolean hasPreviousFlat() {
		return flatListIndex - 1 > -1;
	}

	public Person getPerson() {
		return person;
	}

	public Flat getFlat() {
		return flat;
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public List<Gender> getGenders() {
		return genders;
	}

	public List<PetType> getPetTypes() {
		return petTypes;
	}
	
}

package br.com.abware.accountmgm.bean;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.context.RequestContext;

import br.com.abware.jcondo.core.Gender;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;

@ManagedBean
@ViewScoped
public class PersonRegistrationBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(PersonRegistrationBean.class);

	private CameraBean personCameraBean;

	private Person person;

	private List<Gender> genders;

	@PostConstruct
	public void init() {
		try {
			personCameraBean = new CameraBean(158, 240);
			createPerson();
			genders = Arrays.asList(Gender.values());
		} catch (Exception e) {
			LOGGER.fatal("Failure on person initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onSave() {
		try {
			personService.register(person);
			createPerson();
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.add.success", null);
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

	public void createPerson() throws Exception {
		person = new Person();
		person.setPicture(new Image());
		personCameraBean.setImage(person.getPicture());
	}

	public CameraBean getPersonCameraBean() {
		return personCameraBean;
	}

	public void setPersonCameraBean(CameraBean personCameraBean) {
		this.personCameraBean = personCameraBean;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public List<Gender> getGenders() {
		return genders;
	}

	public void setGenders(List<Gender> genders) {
		this.genders = genders;
	}

}

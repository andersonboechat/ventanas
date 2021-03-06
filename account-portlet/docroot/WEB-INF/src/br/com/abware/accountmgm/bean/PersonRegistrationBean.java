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

	private CameraBean cameraBean;

	private Person person;

	private List<Gender> genders;

	@PostConstruct
	public void init() {
		try {
			cameraBean = new CameraBean(158, 240);
			genders = Arrays.asList(Gender.values());
		} catch (Exception e) {
			LOGGER.fatal("Failure on person initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		}
	}

	public void onSave() {
		try {
			if (person.getId() == 0) {
				personService.register(person);	
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.user.add.success", null);
			} else {
				personService.register(person);	
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

	public void createPerson() throws Exception {
		person = new Person();
		person.setPicture(new Image());
		cameraBean.setImage(person.getPicture());
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

	public List<Gender> getGenders() {
		return genders;
	}

	public void setGenders(List<Gender> genders) {
		this.genders = genders;
	}

}

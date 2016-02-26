package br.com.atilo.jcondo.manager;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.context.RequestContext;

import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Administration;
import br.com.atilo.jcondo.core.service.AdministrationServiceImpl;

@ViewScoped
@ManagedBean
public class AdmBean {

	private static Logger LOGGER = Logger.getLogger(AdmBean.class);
	
	private final AdministrationServiceImpl adminService = new AdministrationServiceImpl();

	@ManagedProperty(value="#{personBean2}")
	private PersonBean personBean;
	
	private Administration admin;

	private List<PersonType> personTypes;
	
	@PostConstruct
	public void init() {
		try {
			admin = adminService.getAdministration(1329);
			personTypes = Arrays.asList(PersonType.ADMIN_TYPES);
			personBean.init(admin);
		} catch (Exception e) {
			LOGGER.fatal("Failure on flat initialization", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
		}
	}
	
	public void onSave() {
		try {

			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "flats.success", null);
//		} catch (BusinessException e) { 
//			LOGGER.warn("Business failure on flat update: " + e.getMessage());
//			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
//			RequestContext.getCurrentInstance().addCallbackParam("exception", true);
		} catch (Exception e) {
			LOGGER.error("Failure on flat update", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "general.failure", null);
		}
	}

	public void onCreate() throws Exception {

	}

	public void onOptionSelect(AjaxBehaviorEvent event) {
		Boolean value = (Boolean) ((SelectOneRadio) event.getSource()).getValue();
		RequestContext.getCurrentInstance().addCallbackParam("value", value);
	}

	public PersonBean getPersonBean() {
		return personBean;
	}

	public void setPersonBean(PersonBean personBean) {
		this.personBean = personBean;
	}

	public List<PersonType> getPersonTypes() {
		return personTypes;
	}

	public void setPersonTypes(List<PersonType> personTypes) {
		this.personTypes = personTypes;
	}

}

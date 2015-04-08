package br.com.atilo.jcondo.occurrence.bean;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;

import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.crm.model.Occurrence;
import br.com.abware.jcondo.crm.model.OccurrenceType;

@ManagedBean
@ViewScoped
public class OccurrenceBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(OccurrenceBean.class);

	private OccurrenceDataModel model;

	private Occurrence occurrence;

	private List<OccurrenceType> types;
	
	private Person person;

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			model = new OccurrenceDataModel(occurrenceService.getOccurrences(person));
			occurrence = new Occurrence(null, person);
			types = Arrays.asList(OccurrenceType.values());
		} catch (Exception e) {
			LOGGER.fatal("failure on occurrence loading", e);
		}
	}

	public void onSave() {
		try {
			Occurrence o = occurrenceService.register(occurrence);
			model.add(o);
			occurrence = new Occurrence(null, person);

			String label = ResourceBundle.getBundle("Language").getString(o.getType().getLabel());
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "register.success", 
									new String[] {label.toLowerCase()});
		} catch (Exception e) {
			LOGGER.error("failure on occurrence saving", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "register.unexpected.failure", 
									new String[] {occurrence.getType().getLabel()});
		}
	}
	
	public Occurrence getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(Occurrence occurrence) {
		this.occurrence = occurrence;
	}

	public List<OccurrenceType> getTypes() {
		return types;
	}

	public void setTypes(List<OccurrenceType> types) {
		this.types = types;
	}

	public OccurrenceDataModel getModel() {
		return model;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}

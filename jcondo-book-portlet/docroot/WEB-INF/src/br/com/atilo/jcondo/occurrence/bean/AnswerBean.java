package br.com.atilo.jcondo.occurrence.bean;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.event.SelectEvent;

import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.crm.model.Answer;
import br.com.abware.jcondo.crm.model.Occurrence;
import br.com.abware.jcondo.crm.model.OccurrenceType;

@ManagedBean
@ViewScoped
public class AnswerBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(AnswerBean.class);

	private OccurrenceDataModel model;

	private Occurrence occurrence;

	private Answer answer;

	private List<OccurrenceType> types;

	private Person person;

	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			model = new OccurrenceDataModel(occurrenceService.getOccurrences(null));
			occurrence = model.getRowData();
			answer = occurrence.getAnswer();
			types = Arrays.asList(OccurrenceType.values());
		} catch (Exception e) {
			LOGGER.fatal("failure on occurrence loading", e);
		}
	}
	
	public void onRowSelect(SelectEvent event) {
		occurrence = (Occurrence) event.getObject();
		if (occurrence.getAnswer() == null) {
			occurrence.setAnswer(new Answer(person));
		}
		answer = occurrence.getAnswer();
	}

	public void onAnswer() {
		String label = rb.getString("answer").toLowerCase();

		try {
			occurrence = occurrenceService.answer(occurrence);
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "register.success", 
									new String[] {label.toLowerCase()});
		} catch (Exception e) {
			LOGGER.error("failure on occurrence register", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "register.unexpected.failure", 
									new String[] {label.toLowerCase()});
		}
	}
	
	public void onSave() {
		String label = rb.getString("answer").toLowerCase();

		try {
			occurrence = occurrenceService.saveAsDraft(occurrence);
			MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "save.success", 
									new String[] {label.toLowerCase()});
		} catch (Exception e) {
			LOGGER.error("failure on occurrence saving", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "save.failure", 
									new String[] {label.toLowerCase()});
		}
	}

	public String displayFlat(Person person) throws Exception {
		Flat flat = flatService.getHome(person);
		if (flat != null) {
			return MessageFormat.format(rb.getString("person.flat"), flat.getNumber(), flat.getBlock());
		} else {
			return "";
		}
	}

	public Occurrence getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(Occurrence occurrence) {
		this.occurrence = occurrence;
	}

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
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

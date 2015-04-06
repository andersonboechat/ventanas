package br.com.atilo.jcondo.occurrence.bean;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;

import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.crm.model.Answer;
import br.com.abware.jcondo.crm.model.Occurrence;
import br.com.abware.jcondo.crm.model.OccurrenceType;

@ManagedBean
@ViewScoped
public class OccurrenceBean extends BaseBean {

	private static Logger LOGGER = Logger.getLogger(OccurrenceBean.class);

	private OccurrenceDataModel model;

	private Occurrence occurrence;

	private Answer answer;

	private OccurrenceType type;
	
	private List<OccurrenceType> types;
	
	private Person person;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		try {
			person = personService.getPerson();
			model = new OccurrenceDataModel(occurrenceService.getOccurrences(person));
			occurrence = CollectionUtils.isEmpty((List<Occurrence>) model.getWrappedData()) ? null : ((List<Occurrence>) model.getWrappedData()).get(0);
			types = Arrays.asList(OccurrenceType.values());
		} catch (Exception e) {
			LOGGER.fatal("failure on occurrence loading", e);
		}
	}

	public void onCreate() {
		occurrence = new Occurrence(type, person);
	}

	public void onSave() {
		try {
			Occurrence o = occurrenceService.register(occurrence);
			model.add(o);
		} catch (Exception e) {
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "register.unexpected.failure", 
									new String[] {occurrence.getType().getLabel()});
		}
	}
	
	public void onCancel() {
		occurrence = null;
	}

	public void onOccurrenceSelect() {
		occurrence = model.getRowData();
	}

	public String displayPersonFlat(Person person) {
		return null;
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

	public OccurrenceType getType() {
		return type;
	}

	public void setType(OccurrenceType type) {
		this.type = type;
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

package br.com.abware.complaintbook.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.data.PageEvent;

import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.model.User;

import br.com.abware.complaintbook.AnswerModel;
import br.com.abware.complaintbook.OccurrenceModel;
import br.com.abware.complaintbook.OccurenceType;
import br.com.abware.complaintbook.UserHelper;
import br.com.abware.complaintbook.exception.BusinessException;
import br.com.abware.complaintbook.exception.EmptyPropertyException;
import br.com.abware.complaintbook.exception.OccurrenceAlreadyDoneException;

@ManagedBean
@ViewScoped
public class BookBean {
	
	private static Logger logger = Logger.getLogger(BookBean.class);
	
	private static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));
	
	private String text;
	
	private OccurrenceModel occurrence;
	
	private List<OccurrenceModel> occurrences;
	
	public BookBean() {
		logger.trace("Method in");

		try {
			this.occurrence = new OccurrenceModel();
			this.occurrences = OccurrenceModel.getUserOccurrences(UserHelper.getLoggedUser().getUserId());
			logger.debug("Amount of occurrence found: " + occurrences.size());
			if (CollectionUtils.isEmpty(occurrences)) {
				OccurrenceModel occurrence = new OccurrenceModel();
				occurrence.setId(-1);
				occurrences.add(0, occurrence);
			}
		} catch (Exception e) {
			logger.fatal(e.getMessage(), e);
			this.occurrences = new ArrayList<OccurrenceModel>();
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.runtime.failure");
		}		

		logger.trace("Method out");
	}

	private String getClientId(String key) {
		logger.trace("Method in");

		FacesContext context = FacesContext.getCurrentInstance();

		logger.trace("Method out");

		return context.getViewRoot().findComponent(key).getClientId();
	}
	
	private void setMessages(Severity severity, String clientId, String messageKey, String ... args) {
		logger.trace("Method in");

		FacesContext context = FacesContext.getCurrentInstance();

		logger.debug("Parameters received: " + severity + "; " + clientId + "; " + messageKey + "; " + args);

		String summary = ResourceBundleUtil.getString(rb, new Locale("pt", "BR"), messageKey, args);

		logger.debug("Message found: " + summary);

		context.addMessage(clientId, new FacesMessage(severity, summary, ""));

		logger.trace("Method out");
	}

	public void onNewComplain(ActionEvent event) {
		logger.trace("Method in");

		try {
			OccurrenceModel occurrence = new OccurrenceModel(OccurenceType.COMPLAINT, UserHelper.getLoggedUser());

			if (occurrences.get(0).getId() == -1) {
				occurrences.set(0, occurrence); 
			} else {
				occurrences.add(0, occurrence);
				
			}

			setMessages(FacesMessage.SEVERITY_INFO, getClientId("form:status"), 
						"request.register", OccurenceType.COMPLAINT.getLabel().toLowerCase());
		} catch (RuntimeException e) {
			logger.fatal(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.runtime.failure");
		}

		logger.trace("Method out");
	}

	public void onNewSuggestion(ActionEvent event) {
		logger.trace("Method in");

		try {
			OccurrenceModel occurrence = new OccurrenceModel(OccurenceType.SUGGESTION, UserHelper.getLoggedUser());

			if (occurrences.get(0).getId() == -1) {
				occurrences.set(0, occurrence); 
			} else {
				occurrences.add(0, occurrence);
				
			}

			setMessages(FacesMessage.SEVERITY_INFO, getClientId("form:status"), 
						"request.register", OccurenceType.SUGGESTION.getLabel().toLowerCase());
		} catch (RuntimeException e) {
			logger.fatal(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.runtime.failure");
		}

		logger.trace("Method out");
	}
	
	public void onPaging(PageEvent event) {
		logger.trace("Method in");

		try {
			setMessages(FacesMessage.SEVERITY_INFO, getClientId("form:status"), "request.search");
		} catch (RuntimeException e) {
			logger.fatal(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.runtime.failure");
		}

		logger.trace("Method out");	
	}

	public void onSave(ActionEvent event) {
		logger.trace("Method in");

		try {
			OccurrenceModel occurrence = occurrences.get(0);

			try {
				occurrence.setText(text);
	
				OccurrenceModel.doOccurrence(occurrence);
				setMessages(FacesMessage.SEVERITY_WARN, event.getComponent().getClientId(), 
							"register.success", occurrence.getType().getLabel().toLowerCase());

				setMessages(FacesMessage.SEVERITY_INFO, getClientId("form:status"), "request.search");

				text = null;
			} catch (EmptyPropertyException e) {
				setMessages(FacesMessage.SEVERITY_ERROR, event.getComponent().getClientId(), 
							"register.empty.field.failure", occurrence.getType().getLabel().toLowerCase());
			} catch (OccurrenceAlreadyDoneException e) {
				setMessages(FacesMessage.SEVERITY_ERROR, event.getComponent().getClientId(), 
							"register.already.done.failure", occurrence.getType().getLabel().toLowerCase());
			} catch (BusinessException e) {
				setMessages(FacesMessage.SEVERITY_ERROR, null, 
							"register.unexpected.failure", occurrence.getType().getLabel().toLowerCase());
			}
		} catch (RuntimeException e) {
			logger.fatal(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.runtime.failure");
		}

		logger.trace("Method out");
	}

	public void onCancel() {
		logger.trace("Method in");

		try {
			occurrences.remove(0);

			if (CollectionUtils.isEmpty(occurrences)) {
				OccurrenceModel occurrence = new OccurrenceModel();
				occurrence.setId(-1);
				occurrences.add(0, occurrence);
			}

			setMessages(FacesMessage.SEVERITY_INFO, getClientId("form:status"), "request.search");
		} catch (RuntimeException e) {
			logger.fatal(e.getMessage(), e);
			setMessages(FacesMessage.SEVERITY_FATAL, null, "register.runtime.failure");
		}

		logger.trace("Method out");
	}

	public void onLoad() {
		setMessages(FacesMessage.SEVERITY_INFO, getClientId("form:status"), "request.search");
	}

	public String getOccurrenceFooter(String occurrenceId) {
		logger.trace("Method in");

		String userName = "Usuário desconhecido";
		String flat = null;
		String footer = null;
		
		try {
			logger.debug("occurrenceId: " + occurrenceId);

			int id = Integer.parseInt(occurrenceId);
			OccurrenceModel occurrence;

			if (id == 0) {
				occurrence = occurrences.get(0);
			} else {
				occurrence = OccurrenceModel.getOccurrence(id);
			}

			logger.debug("occurrenceId: " + occurrenceId + "; Occurrence: " + occurrence.toString());			
			
			if (occurrence != null && occurrence.getUser() != null) {
				User user = occurrence.getUser();

				logger.debug("occurrenceId: " + occurrenceId + "; User: " + user.toString());				

				userName = user.getFirstName() + " " + user.getLastName();
				flat = !user.getOrganizations().isEmpty() ?	user.getOrganizations().get(0).getName() : ""; 
				footer = userName + " - " +  flat;
			}

		} catch (RuntimeException e) {
			logger.fatal(e.getMessage(), e);
		} catch (Exception e) {
			// Nothing to do
		}

		logger.info("occurrenceId: " + occurrenceId + "; Footer: " + footer);

		logger.trace("Method out");

		return footer;
	}

	public String getAnswerFooter(String answerId) {
		logger.trace("Method in");
		
		String footer = null;
		
		try {
			AnswerModel answer = AnswerModel.getAnswer(Integer.parseInt(answerId));
			
			logger.trace("AnswerId: " + answerId + "; Answer: " + answer);

			if (answer != null) {
				User user = answer.getUser();

				logger.debug("AnswerId: " + answerId +"; User: " + user);

				footer = user.getFirstName() + " " + user.getLastName() + " - " + user.getJobTitle();
			}

		} catch (NumberFormatException e) { 
			//Do nothing
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			footer = "Usuário desconhecido";
		}

		logger.info("AnswerId: " + answerId + "; Footer: " + footer);

		logger.trace("Method out");
		
		return footer;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public OccurrenceModel getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(OccurrenceModel occurrence) {
		this.occurrence = occurrence;
	}

	public List<OccurrenceModel> getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(List<OccurrenceModel> occurrences) {
		this.occurrences = occurrences;
	}

}

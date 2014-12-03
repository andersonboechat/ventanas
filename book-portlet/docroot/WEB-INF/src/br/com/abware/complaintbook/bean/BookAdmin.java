package br.com.abware.complaintbook.bean;


import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;
import org.primefaces.event.SelectEvent;

import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.velocity.VelocityContext;
import com.liferay.portal.kernel.velocity.VelocityEngineUtil;
import com.liferay.portal.model.User;
import com.liferay.util.ContentUtil;

import br.com.abware.complaintbook.AnswerModel;
import br.com.abware.complaintbook.OccurrenceDataModel;
import br.com.abware.complaintbook.OccurrenceModel;
import br.com.abware.complaintbook.UserHelper;

@ManagedBean
@ViewScoped
public class BookAdmin {

	private static Logger logger = Logger.getLogger(BookBean.class);
	
	private static ResourceBundle rb = ResourceBundle.getBundle("Language", new Locale("pt", "BR"));

	private OccurrenceDataModel model;
	
	private OccurrenceModel occurrence;
	
	public BookAdmin() {
		logger.trace("Method in");

		try {
			this.occurrence = new OccurrenceModel();
			List<OccurrenceModel> occurrences = occurrence.getOccurrences();

			logger.debug("Amount of occurrences found: " + occurrences.size());

			if (CollectionUtils.isEmpty(occurrences)) {
				OccurrenceModel occurrence = new OccurrenceModel();
				occurrence.setId(-1);
				occurrences.add(0, occurrence);
			}
			
			setModel(new OccurrenceDataModel(occurrences));
		} catch (Exception e) {
			logger.fatal(e.getMessage(), e);
		}		

		logger.trace("Method out");
	}

	private String getClientId(String key) {
		logger.trace("Method in");

		FacesContext context = FacesContext.getCurrentInstance();

		logger.trace("Method out");

		return context.getViewRoot().findComponent(key).getClientId();
	}	

	private void sendNotification(OccurrenceModel occurrence) {
		try {
			logger.info("Enviando notificacao de resposta da ocorrencia " + occurrence.getId());

			String mailBodyTemplate = ContentUtil.get("occurrence-answered-notify.vm");
			logger.debug(mailBodyTemplate);

			VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
			variables.put("occurrence", occurrence);
			variables.put("occurrenceDate", DateFormatUtils.format(occurrence.getDate(), "dd 'de' MMMM 'de' yyyy 'às' HH:mm"));
			variables.put("answer", occurrence.getAnswer());
			variables.put("answerDate", DateFormatUtils.format(occurrence.getAnswer().getDate(), "dd 'de' MMMM 'de' yyyy 'às' HH:mm"));
			UnsyncStringWriter writer = new UnsyncStringWriter();
			VelocityEngineUtil.mergeTemplate("OAN", mailBodyTemplate, variables, writer);

			String mailBody = writer.toString();
			logger.debug(mailBody);

			String mailTo = occurrence.getUser().getEmailAddress(); 

			MailMessage mailMessage = new MailMessage();
			mailMessage.setFrom(new InternetAddress("administracao@ventanasresidencial.com.br", "Portal Ventanas"));
			mailMessage.setTo(new InternetAddress(mailTo));
			mailMessage.setSubject("[Ventanas Portal] Sua " + occurrence.getType().getLabel() + " foi respondida");		
			mailMessage.setBody(mailBody);
			mailMessage.setHTMLFormat(true);
			MailServiceUtil.sendEmail(mailMessage);

			logger.info("Sucesso no envio da notificacao de reposta da ocorrencia " + occurrence.getId());
		} catch (Exception e) {
			logger.error("Falha no envio da notificacao de reposta da ocorrencia " + occurrence.getId(), e);
		} 
	}
	
	public void onRowSelect(SelectEvent event) {
		OccurrenceModel occurrence = (OccurrenceModel) event.getObject();
		if (occurrence.getAnswer() == null) {
			AnswerModel answer = new AnswerModel();
			answer.setUser(UserHelper.getLoggedUser());
			occurrence.setAnswer(answer);
		}		
	}
	
	public void onAnswer(boolean sendMail) {
		try {
			if (StringUtils.isNotEmpty(occurrence.getAnswer().getText())) {
				AnswerModel answerModel = occurrence.getAnswer();
				answerModel.setDate(new Date());
				answerModel.setUser(UserHelper.getLoggedUser());
				answerModel.setDraft(!sendMail);
				answerModel.doAnswer(answerModel);

				occurrence.doAnswer(occurrence);
				occurrence.setAnswer(answerModel);
				String answer = rb.getString("answer").toLowerCase();
				
				if (sendMail) {
					MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "register.success", 
							new String[]{answer}, getClientId("adm-book-form:status"));
					sendNotification(occurrence);
				} else {
					MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "save.success", 
							new String[]{answer}, getClientId("adm-book-form:status"));
				}
			} else {
				throw new ValidatorException(MessageUtils.getMessage(FacesMessage.SEVERITY_WARN, "empty.answer", null));
			}
		} catch (ValidatorException e) {
			logger.warn(e.getMessage(), e);
			MessageUtils.addMessage(e.getFacesMessage().getSeverity(), e.getFacesMessage().getSummary(), null);
		} catch (Exception e) {
			logger.fatal(e.getMessage(), e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_FATAL, "register.runtime.failure", null);
		}
	}

	public String getUserLabel(User user) {
		logger.trace("Method in");
		String userName = null;
		String flat = null;
		String label = null;
		
		try {
			if (user != null) {
				userName = user.getFirstName() + " " + user.getLastName();
				flat = !user.getOrganizations().isEmpty() ?	user.getOrganizations().get(0).getName() : ""; 
				label = userName + "<br>" +  flat;
			}
		} catch (RuntimeException e) {
			logger.fatal(e.getMessage(), e);
		} catch (Exception e) {
			// Nothing to do
		}

		logger.trace("Method out");

		return label;
	}

	public OccurrenceDataModel getModel() {
		return model;
	}

	public void setModel(OccurrenceDataModel model) {
		this.model = model;
	}

	public OccurrenceModel getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(OccurrenceModel occurrence) {
		this.occurrence = occurrence;
	}

}

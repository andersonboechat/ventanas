package br.com.abware.complaintbook.bean;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.validator.ValidatorException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
	
	private String text;
	
	private OccurrenceDataModel model;
	
	private OccurrenceModel occurrence;
	
	private List<OccurrenceModel> occurrences;

	public BookAdmin() {
		logger.trace("Method in");

		try {
			this.occurrence = new OccurrenceModel();
			this.occurrences = occurrence.getOccurrences();

			logger.debug("Amount of occurrences found: " + occurrences.size());

			if (CollectionUtils.isEmpty(occurrences)) {
				OccurrenceModel occurrence = new OccurrenceModel();
				occurrence.setId(-1);
				occurrences.add(0, occurrence);
			}
//			occurrence = occurrences.get(0);
//			if (occurrence.getAnswer() == null) {
//				occurrence.setAnswer(new AnswerModel());
//			}
			
			setModel(new OccurrenceDataModel(occurrences));
		} catch (Exception e) {
			logger.fatal(e.getMessage(), e);
			this.occurrences = new ArrayList<OccurrenceModel>();
		}		

		logger.trace("Method out");
	}

	private void sendNotification(OccurrenceModel occurrence) {
		try {
			logger.info("Enviando notificacao de resposta da ocorrencia " + occurrence.getId());

			String mailBodyTemplate = ContentUtil.get("occurrence-created-notify.vm");
			logger.debug(mailBodyTemplate);

			VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
			variables.put("occurrence", occurrence.getText());
			variables.put("answer", occurrence.getText());
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
			occurrence.setAnswer(new AnswerModel());
		}		
	}
	
	public void onAnswer(boolean sendMail) {
		try {
			if (StringUtils.isNotEmpty(occurrence.getAnswer().getText())) {
				AnswerModel answerModel = occurrence.getAnswer();
				answerModel.setDate(new Date());
				answerModel.setUser(UserHelper.getLoggedUser());
				answerModel.doAnswer(answerModel);

				occurrence.doAnswer(occurrence);

				String answer = rb.getString("answer");
				MessageUtils.addMessage(FacesMessage.SEVERITY_INFO, "register.success", new String[]{answer});
				
				if (sendMail) {
					sendNotification(occurrence);
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
				occurrence = new OccurrenceModel().getOccurrence(id);
			}

			logger.debug("occurrenceId: " + occurrenceId + "; Occurrence: " + occurrence.toString());			
			
			if (occurrence != null && occurrence.getUser() != null) {
				User user = occurrence.getUser();

				logger.debug("occurrenceId: " + occurrenceId + "; User: " + user.toString());				

				userName = user.getFirstName() + " " + user.getLastName();
				flat = !user.getOrganizations().isEmpty() ?	user.getOrganizations().get(0).getName() : ""; 
				footer = userName + "<br>" +  flat;
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

	public OccurrenceDataModel getModel() {
		return model;
	}

	public void setModel(OccurrenceDataModel model) {
		this.model = model;
	}

}

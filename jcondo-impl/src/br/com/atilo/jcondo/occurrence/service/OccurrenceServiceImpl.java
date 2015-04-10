package br.com.atilo.jcondo.occurrence.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.velocity.VelocityContext;
import com.liferay.portal.kernel.velocity.VelocityEngineUtil;
import com.liferay.util.ContentUtil;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.crm.model.Answer;
import br.com.abware.jcondo.crm.model.Occurrence;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;
import br.com.atilo.jcondo.core.service.FlatServiceImpl;
import br.com.atilo.jcondo.occurrence.persistence.manager.AnswerManagerImpl;
import br.com.atilo.jcondo.occurrence.persistence.manager.OccurrenceManagerImpl;
import br.com.atilo.jcondo.util.MailService;

public class OccurrenceServiceImpl {
	
	private static final Logger LOGGER = Logger.getLogger(OccurrenceManagerImpl.class);
	
	private static final ResourceBundle rb = ResourceBundle.getBundle("i18n");

	private OccurrenceManagerImpl occurrenceManager = new OccurrenceManagerImpl();
	
	private AnswerManagerImpl answerManager = new AnswerManagerImpl();
	
	private SecurityManagerImpl securityManager = new SecurityManagerImpl();
	
	private FlatServiceImpl flatService = new FlatServiceImpl();

	private String generateCode(Occurrence occurrence) {
		return String.valueOf(occurrence.getId() + occurrence.getDate().getTime());
	}

	private void notifyOccurrenceConfirm(Occurrence occurrence) throws Exception {
		LOGGER.info("Enviando confirmacao de registro da ocorrencia " + occurrence.getId());

		String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/occurrence/mail/occurrence-created-confirm.vm");
		LOGGER.debug(mailBodyTemplate);

		VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
		variables = VelocityEngineUtil.getStandardToolsContext();
		variables.put("occurrence", occurrence);
		variables.put("occurrenceLabel", rb.getString(occurrence.getType().getLabel()).toLowerCase());
		UnsyncStringWriter writer = new UnsyncStringWriter();
		VelocityEngineUtil.mergeTemplate("OCC", mailBodyTemplate, variables, writer);

		String mailBody = writer.toString();
		LOGGER.debug(mailBody);

		String mailTo = occurrence.getPerson().getEmailAddress();
		String mailSubject = MessageFormat.format(rb.getString("occurrence.confirm.subject"), 
												  rb.getString(occurrence.getType().getLabel()));
		
		MailService.send(mailTo, mailSubject, mailBody);

		LOGGER.info("Sucesso no envio da confirmacao de registro da ocorrencia " + occurrence.getId());
	}
	
	private void notifyOccurrenceCreate(Occurrence occurrence) throws Exception {
		LOGGER.info("Enviando notificacao da ocorrencia " + occurrence.getId());

		String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/occurrence/mail/occurrence-created-notify.vm");
		LOGGER.debug(mailBodyTemplate);
		
		Flat flat = flatService.getHome(occurrence.getPerson());

		VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
		variables.put("occurrence", occurrence.getText());
		variables.put("resident", occurrence.getPerson().getFullName());
		variables.put("flatNumber", flat.getNumber());
		variables.put("flatBlock", flat.getBlock());
		UnsyncStringWriter writer = new UnsyncStringWriter();
		VelocityEngineUtil.mergeTemplate("OCN", mailBodyTemplate, variables, writer);

		String mailBody = writer.toString();
		LOGGER.debug(mailBody);

		String mailTo = "sindico@ventanasresidencial.com.br";
		String mailSubject = MessageFormat.format(rb.getString("occurrence.create.subject"), 
												  rb.getString(occurrence.getType().getLabel()).toLowerCase());

		MailService.send(mailTo, mailSubject, mailBody);

		LOGGER.info("Sucesso no envio da notificacao da ocorrencia " + occurrence.getId());
	}

	private void notifyOccurrenceAnswered(Occurrence occurrence) throws Exception {
		LOGGER.info("Enviando notificacao de resposta da ocorrencia " + occurrence.getId());

		String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/occurrence/mail/occurrence-answered-notify.vm");
		LOGGER.debug(mailBodyTemplate);

		VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
		variables.put("occurrence", occurrence);
		variables.put("occurrenceLabel", rb.getString(occurrence.getType().getLabel()).toLowerCase());
		variables.put("occurrenceDate", DateFormatUtils.format(occurrence.getDate(), "dd 'de' MMMM 'de' yyyy 'às' HH:mm"));
		variables.put("answer", occurrence.getAnswer());
		variables.put("answerDate", DateFormatUtils.format(occurrence.getAnswer().getDate(), "dd 'de' MMMM 'de' yyyy 'às' HH:mm"));
		UnsyncStringWriter writer = new UnsyncStringWriter();
		VelocityEngineUtil.mergeTemplate("OAN", mailBodyTemplate, variables, writer);

		String mailBody = writer.toString();
		LOGGER.debug(mailBody);

		String mailTo = occurrence.getPerson().getEmailAddress();
		String mailSubject = MessageFormat.format(rb.getString("occurrence.answer.subject"), 
												  rb.getString(occurrence.getType().getLabel()).toLowerCase());

		MailService.send(mailTo, mailSubject, mailBody);
	}

	protected Occurrence answer(Occurrence occurrence, boolean draft) throws Exception {
		if (!securityManager.hasPermission(occurrence.getAnswer(), Permission.ADD)) {
			throw new Exception("occurrence answering denied");
		}

		if (occurrence.getAnswer() == null) {
			throw new Exception("occurrence answer not specified");
		}

		Occurrence o = occurrenceManager.findById(occurrence.getId());

		if (o == null) {
			throw new Exception("occurrence not exist");
		}

		boolean create = false;
		Answer answer = o.getAnswer();

		if (answer != null) {
			if (answer.getDate() != null) {	
				throw new Exception("occurrence already answered: " + occurrence);
			}
		} else {
			answer = new Answer();
			create = true;
		}

		answer.setDate(draft ? null : new Date());
		answer.setText(occurrence.getAnswer().getText());
		answer.setPerson(occurrence.getAnswer().getPerson());

		answer = answerManager.save(answer);
		o.setAnswer(answer);

		return create ? occurrenceManager.save(o) : o;
	}

	public Occurrence answer(Occurrence occurrence) throws Exception {
		Occurrence o = answer(occurrence, false);

		securityManager.addPermission(o.getPerson(), o.getAnswer(), Permission.VIEW);

		try {
			notifyOccurrenceAnswered(o);
		} catch (Exception e) {
			LOGGER.error("failure on sending the occurrence answered notification", e);
		}

		return o;
	}

	public Occurrence saveAsDraft(Occurrence occurrence) throws Exception {
		return answer(occurrence, true);
	}

	public List<Occurrence> getOccurrences(Person person) throws Exception {
		return occurrenceManager.findOccurrences(person);
	}

	public List<Occurrence> getAllOccurrences(Person person) throws Exception {
		List<Occurrence> occurrences = new ArrayList<Occurrence>();

		for (Occurrence occurrence : occurrenceManager.findAll()) {
			if (securityManager.hasPermission(occurrence, Permission.VIEW)) {
				occurrences.add(occurrence);
			}
		}

		return occurrences;
	}
	
	public Occurrence register(Occurrence occurrence) throws Exception {
		if (!securityManager.hasPermission(occurrence, Permission.ADD)) {
			throw new Exception("occurrence creation denied");
		}

		if (occurrenceManager.findById(occurrence.getId()) != null || 
				occurrenceManager.findOccurrence(occurrence.getCode()) != null) {
			throw new Exception("occurrence already exists");
		}

		occurrence.setId(0);
		occurrence.setDate(new Date());
		occurrence.setCode(generateCode(occurrence));
		occurrence.setAnswer(null);

		Occurrence o = occurrenceManager.save(occurrence);
		securityManager.addPermission(o.getPerson(), o, Permission.VIEW);

		try {
			notifyOccurrenceCreate(o);
			notifyOccurrenceConfirm(o);
		} catch (Exception e) {
			LOGGER.error("failure on sending the occurrence answered notification", e);
		}

		return o;
	}

}

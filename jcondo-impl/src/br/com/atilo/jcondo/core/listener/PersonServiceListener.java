package br.com.atilo.jcondo.core.listener;

import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.velocity.VelocityContext;
import com.liferay.portal.kernel.velocity.VelocityEngineUtil;
import com.liferay.util.ContentUtil;

import br.com.abware.jcondo.core.model.Person;
import br.com.atilo.jcondo.util.MailService;

public class PersonServiceListener implements Observer {

	private static final Logger LOGGER = Logger.getLogger(PersonServiceListener.class);

	private static final ResourceBundle rb = ResourceBundle.getBundle("i18n");

	public PersonServiceListener() {
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof Person && arg != null) {
			notifyPersonChanged((Person) arg);
		}
	}
	
	private void notifyPersonChanged(Person person) {
		try {
			LOGGER.info("Enviando notificacao de alteracao da pessoa " + person.getId());

			String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/core/mail/person-changed-notify.vm");
			LOGGER.debug(mailBodyTemplate);

			VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
			variables = VelocityEngineUtil.getStandardToolsContext();
			variables.put("person", person);
			variables.put("props", rb);
			UnsyncStringWriter writer = new UnsyncStringWriter();
			VelocityEngineUtil.mergeTemplate("PCN", mailBodyTemplate, variables, writer);

			String mailBody = writer.toString();
			LOGGER.debug(mailBody);

			String mailTo = "adm@ventanasresidencial.com.br";
			String mailSubject = rb.getString("person.changed.subject");

			MailService.send(mailTo, mailSubject, mailBody);

			LOGGER.info("Sucesso no envio da notificacao de alteracao da pessoa " + person.getId());
		} catch (Exception e) {
			LOGGER.info("Falha no envio da notificacao de alteracao da pessoa " + person.getId());
		}
	}
}

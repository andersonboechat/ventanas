package br.com.atilo.jcondo.util;

import javax.mail.internet.InternetAddress;

import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.mail.MailMessage;

public class MailService {

	private static final String PORTAL_MAIL = "administracao@ventanasresidencial.com.br";

	private static final String PORTAL_NAME = "Portal Ventanas";

	public static void send(String to, String subject, String body) throws Exception {
		send(null, to, subject, body);
	}
	
	public static void send(String from, String to, String subject, String body) throws Exception {
		InternetAddress mailFrom;

		if (from == null) {
			mailFrom = new InternetAddress(PORTAL_MAIL, PORTAL_NAME);
		} else {
			mailFrom = new InternetAddress(from);
		}

		MailMessage mailMessage = new MailMessage();
		mailMessage.setFrom(mailFrom);
		mailMessage.setTo(new InternetAddress(to));
		mailMessage.setSubject(subject);		
		mailMessage.setBody(body);
		mailMessage.setHTMLFormat(true);
		MailServiceUtil.sendEmail(mailMessage);
	}
}

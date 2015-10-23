package br.com.atilo.jcondo.core.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.velocity.VelocityContext;
import com.liferay.portal.kernel.velocity.VelocityEngineUtil;
import com.liferay.util.ContentUtil;

import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.core.persistence.manager.MembershipManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.PersonManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.SecurityManagerImpl;
import br.com.atilo.jcondo.util.MailService;

public class MembershipServiceImpl {
	
	private static final Logger LOGGER = Logger.getLogger(MembershipServiceImpl.class);
	
	private static final ResourceBundle rb = ResourceBundle.getBundle("i18n");
	
	private MembershipManagerImpl membershipManager = new MembershipManagerImpl();

	private PersonManagerImpl personManager = new PersonManagerImpl();

	private SecurityManagerImpl securityManager = new SecurityManagerImpl();
	
	private static final List<PersonType> INTERNAL_TYPES = Arrays.asList(PersonType.OWNER, PersonType.RENTER, PersonType.RESIDENT, PersonType.DEPENDENT);
	
	public Membership getMembership(Person person, Domain domain) {
		return membershipManager.findByPersonAndDomain(person, domain);
	}

	public List<Membership> getMemberships(Person person) throws Exception {
		return membershipManager.findByPerson(person);
	}

	public void requestAdd(Flat flat, Person person, PersonType type) throws Exception {
		LOGGER.info("membership add request init: " + flat.getId() + ", pessoa: " + person.getId() + ", tipo: " + type);

		if (StringUtils.isEmpty(person.getEmailAddress())) {
			throw new BusinessException("psn.membership.person.email.invalid", person);
		}

		String mailBodyTemplate = ContentUtil.get("br/com/atilo/jcondo/core/mail/membership-request-add.vm");
		
		LOGGER.debug(mailBodyTemplate);

		VelocityContext variables = VelocityEngineUtil.getStandardToolsContext();
		variables.put("personName", person.getFirstName());
		variables.put("residentName", personManager.findPerson().getFirstName());
		variables.put("flatNumber", flat.getNumber());
		variables.put("flatBlock", flat.getBlock());
		variables.put("deadline", DateFormatUtils.format(DateUtils.addDays(new Date(), 3), "dd/MM/yyyy"));
		variables.put("data", "");
		UnsyncStringWriter writer = new UnsyncStringWriter();
		VelocityEngineUtil.mergeTemplate("MRA", mailBodyTemplate, variables, writer);

		String mailBody = writer.toString();
		LOGGER.debug(mailBody);

		String mailTo = person.getEmailAddress();
		String mailSubject = rb.getString("membership.request.add");

		MailService.send(mailTo, mailSubject, mailBody);

		LOGGER.info("membership add request end: " + flat.getId() + ", pessoa: " + person.getId() + ", tipo: " + type);
	}

	public Membership add(Domain domain, Person person, PersonType type) throws Exception {
		Membership membership = new Membership(type, domain);

		if (membershipManager.findByPersonAndDomain(person, domain) != null) {
			throw new BusinessException("psn.membership.already.exists", membership);
		}

		List<Membership> memberships = membershipManager.findByPerson(person);
		memberships.add(membership);

		for (Membership m : memberships) {
			if (INTERNAL_TYPES.contains(m.getType())) {
				throw new BusinessException("psn.membership.assign.member.denied", m);
			}
		}

		securityManager.addMembership(person, membership);

		try {
			membership.setPerson(person);
			return membershipManager.save(membership);
		} catch (Exception e) {
			securityManager.removeMembership(person, membership);
			throw e;
		}
	}

}

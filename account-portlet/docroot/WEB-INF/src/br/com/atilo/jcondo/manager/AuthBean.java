package br.com.atilo.jcondo.manager;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.myfaces.commons.util.MessageUtils;

import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.core.service.FlatServiceImpl;
import br.com.atilo.jcondo.core.service.MembershipServiceImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;

@ManagedBean
@ViewScoped
public class AuthBean {

	private static Logger LOGGER = Logger.getLogger(AuthBean.class);	
	
	private PersonServiceImpl personService = new PersonServiceImpl();

	private FlatServiceImpl flatService = new FlatServiceImpl();

	private MembershipServiceImpl membershipService = new MembershipServiceImpl();

	private boolean expired = true;
	
	private boolean added = true;
	
	private Person person;
	
	private Flat flat;

	@PostConstruct
	public void init() {
		try {
			String data = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("data");
			String datas[] = data.split("p");
			this.person = personService.getPerson(Long.parseLong(datas[1]));
			Person p = personService.getPerson();

			if (!p.equals(person)) {
				throw new BusinessException("membership.add.request.deny", person.getFullName().toUpperCase());
			}

			this.flat = flatService.getFlat(Long.parseLong(datas[2]));
			this.added = membershipService.getMembership(person, flat) != null;

			if (added) {
				throw new BusinessException("membership.add.request.exists", 
											person.getFullName().toUpperCase(),
											flat.getNumber(), flat.getBlock());
			}

			Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			Date deadline = DateUtils.truncate(new Date(Long.parseLong(datas[0])), Calendar.DAY_OF_MONTH);
			this.expired = today.after(deadline);

			if (expired) {
				throw new BusinessException("membership.add.request.expired", 
											person.getFullName().toUpperCase(),
											flat.getNumber(), flat.getBlock(),
											DateFormatUtils.format(deadline, "dd/MM/yyyy"));
			}
			
			
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "membership.add.request.term",
									new Object[] {person.getFullName().toUpperCase(), flat.getNumber(), flat.getBlock()});
		} catch (BusinessException e) {
			LOGGER.warn("Business failure on membership add request: " + e.getMessage());
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getArgs());
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on membership add request", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "membership.add.request.failure", null);
		}
	}
	
	public void onAuthorize() {
		try {
			membershipService.add(flat, person, PersonType.VISITOR);
			added = true;
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, "membership.add.request.success", null);
		} catch (Exception e) {
			LOGGER.error("Unexpected failure on membership add request", e);
			MessageUtils.addMessage(FacesMessage.SEVERITY_ERROR, "membership.add.request.failure", null);
		}
	}

	public boolean getExpired() {
		return expired;
	}

	public boolean getAdded() {
		return added;
	}

	public Person getPerson() {
		return person;
	}

	public Flat getFlat() {
		return flat;
	}
}

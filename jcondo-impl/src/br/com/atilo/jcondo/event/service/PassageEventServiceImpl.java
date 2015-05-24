package br.com.atilo.jcondo.event.service;

import br.com.abware.jcondo.access.model.PassageEvent;
import br.com.abware.jcondo.access.model.PassageType;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.core.persistence.manager.AdministrationManagerImpl;
import br.com.atilo.jcondo.core.persistence.manager.FlatManagerImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;
import br.com.atilo.jcondo.event.persistence.manager.PassageEventManager;

public class PassageEventServiceImpl {

	private PassageEventManager eventManager = new PassageEventManager();
	
	private PersonServiceImpl personService = new PersonServiceImpl();
	
	private FlatManagerImpl flatManager = new FlatManagerImpl();
	
	private AdministrationManagerImpl adminManager = new AdministrationManagerImpl();

	public void register(PassageEvent event) throws Exception {
		if (event.getType() == PassageType.INBOUND) {
			if (event.getDestiny() == null) {
				throw new BusinessException("domain.not.defined");
			}

			Domain domain = null;

			if (event.getDestiny() instanceof Flat) {
				domain = flatManager.findById(event.getDestiny().getId());
			}

			if (event.getDestiny() instanceof Administration) {
				domain = adminManager.findById(event.getDestiny().getId());
			}

			if (domain == null) {
				throw new BusinessException("domain.not.found");
			}

			Person person = personService.getPerson(event.getPerson().getIdentity());

			if (person == null) {
				throw new BusinessException("person.not.defined");
			}
			
			if (!personService.isAccessAuthorized(person)) {
				if (event.getAuthorizer() == null) {
					throw new BusinessException("authorizer.not.defined");
				}

				person = personService.getPerson(event.getAuthorizer().getIdentity());

				if (person == null) {
					throw new BusinessException("authorizer.not.found");
				}
			} else {
				event.setAuthorizer(null);
			}
		}

		eventManager.save(event);
	}
}

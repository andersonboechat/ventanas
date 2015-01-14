package br.com.abware.accountmgm.service.core;

import java.io.File;
import java.util.List;

import br.com.abware.accountmgm.persistence.manager.PersonManagerImpl;
import br.com.abware.accountmgm.persistence.manager.SecurityManagerImpl;
import br.com.abware.jcondo.core.Permission;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Group;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.PersonService;
import br.com.abware.jcondo.exception.ApplicationException;

public class PersonServiceImpl implements PersonService {
	
	private PersonManagerImpl personManager = new PersonManagerImpl();
	
	private SecurityManagerImpl securityManager = new SecurityManagerImpl();

	@Override
	public List<Person> getPeople(Group group) throws ApplicationException {
		if (securityManager.hasPermission(group, Permission.ADD_USER)) {
			return personManager.findPeople(group);
		}
		
		return null;
	}

	@Override
	public Person getPerson() throws ApplicationException {
		return personManager.getLoggedPerson();
	}

	@Override
	public File getPortrait() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPermission(Person person, Permission permission) throws ApplicationException {
		return securityManager.hasPermission(person, permission);
	}

	@Override
	public Person register(Person person) throws ApplicationException {
		Person p = null;

		if (securityManager.hasPermission(person, Permission.ADD_USER)) {
			p = personManager.save(person);

			// Configurando papeis de acordo com o tipo de usuário
			for (Group group : person.getGroups()) {
				if (group instanceof Flat) {
					
				}
			}
		}

		return p;
	}

	@Override
	public void setPortrait(File arg0) {
		// TODO Auto-generated method stub

	}

}

package br.com.atilo.jcondo.core.service;

import java.util.List;

import br.com.abware.jcondo.core.model.KinType;
import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.exception.BusinessException;

import br.com.atilo.jcondo.core.persistence.manager.KinshipManagerImpl;

public class KinshipServiceImpl {
	
	private KinshipManagerImpl kinshipManager = new KinshipManagerImpl();

	public List<Kinship> getKinships(Person person, KinType type) throws Exception {
		return kinshipManager.findByPerson(person);
	}

	public Kinship getKinship(Person person, Person relative) throws Exception {
		return kinshipManager.findByPersonAndRelative(person, relative);
	}	

	public Kinship add(Person person, Person relative, KinType type) throws Exception {
		if (person == null || relative == null || type == null) {
			throw new BusinessException("pdt.kinship.invalid");
		}

		return kinshipManager.save(new Kinship(person, relative, type));
	}

	public void remove(Person person, Person relative, KinType type) throws Exception {
		if (person == null || relative == null || type == null) {
			throw new BusinessException("pdt.kinship.invalid");
		}

		kinshipManager.delete(new Kinship(person, relative, type));
	}

	public void update(Person person, Person relative, KinType type) throws Exception {
		if (person == null || relative == null) {
			throw new BusinessException("pdt.kinship.invalid");
		}

		Kinship kinship = getKinship(person, relative);

		if (kinship == null || (type != null && type != kinship.getType())) {
			kinshipManager.save(new Kinship(person, relative, type));
		}

		if (kinship != null && type == null) {
			kinshipManager.delete(kinship);
		}
	}

}

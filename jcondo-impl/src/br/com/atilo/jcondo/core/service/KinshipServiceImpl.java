package br.com.atilo.jcondo.core.service;

import java.util.List;

import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Person;

import br.com.atilo.jcondo.core.persistence.manager.KinshipManagerImpl;

public class KinshipServiceImpl {
	
	private KinshipManagerImpl kinshipManager = new KinshipManagerImpl();

	public List<Kinship> getKinships(Person person) throws Exception {
		return kinshipManager.findByPerson(person);
	}
	
	public Kinship register(Kinship kinship) throws Exception {
		return kinshipManager.save(kinship);
	}

	public void delete(Kinship kinship) throws Exception {
		kinshipManager.delete(kinship);
	}

}

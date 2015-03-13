package br.com.abware.accountmgm.service.core;

import java.util.List;

import br.com.abware.accountmgm.persistence.manager.KinshipManagerImpl;
import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Person;

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

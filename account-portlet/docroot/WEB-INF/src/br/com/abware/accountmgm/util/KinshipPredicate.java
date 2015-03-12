package br.com.abware.accountmgm.util;

import org.apache.commons.collections.Predicate;

import br.com.abware.jcondo.core.model.KinType;
import br.com.abware.jcondo.core.model.Kinship;
import br.com.abware.jcondo.core.model.Person;

public class KinshipPredicate implements Predicate {

	private Kinship kinship;
	
	public KinshipPredicate(Kinship kinship) {
		this.kinship = kinship;
	}

	public KinshipPredicate(Person person, Person relative, KinType type) {
		this.kinship = new Kinship(person, relative, type);
	}

	@Override
	public boolean evaluate(Object obj) {
		return obj != null && obj instanceof Kinship && ((Kinship) obj).equals(kinship);
	}

}

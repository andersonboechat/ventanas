package br.com.atilo.jcondo.commons.collections;

import org.apache.commons.collections.Predicate;

import br.com.abware.jcondo.core.model.KinType;
import br.com.abware.jcondo.core.model.Kinship;

public class KinTypePredicate implements Predicate {

	private KinType kintype;
	
	public KinTypePredicate(KinType kintype) {
		this.kintype = kintype;
	}
	
	@Override
	public boolean evaluate(Object obj) {
		return obj != null && obj instanceof Kinship && ((Kinship) obj).getType().equals(kintype);
	}

}

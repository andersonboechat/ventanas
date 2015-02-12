package br.com.abware.accountmgm.util;

import org.apache.commons.collections.Predicate;

import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Membership;

public class PersonTypePredicate implements Predicate {

	private PersonType type;
	
	public PersonTypePredicate(PersonType type) {
		this.type = type;
	}
	
	@Override
	public boolean evaluate(Object obj) {
		return ((Membership) obj).getType().equals(type);
	}

}

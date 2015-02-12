package br.com.abware.accountmgm.util;

import org.apache.commons.collections.Predicate;

import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Membership;

public class MembershipPredicate implements Predicate {

	private Domain domain;
	
	private PersonType type;
	
	public MembershipPredicate(Domain domain, PersonType type) {
		this.domain = domain;
		this.type = type;
	}

	@Override
	public boolean evaluate(Object obj) {
		Membership m = (Membership) obj;

		if (m.getDomain().equals(domain) && m.getType().equals(type)) {
			return true;
		}

		return false;
	}
}

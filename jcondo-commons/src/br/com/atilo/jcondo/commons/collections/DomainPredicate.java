package br.com.atilo.jcondo.commons.collections;

import org.apache.commons.collections.Predicate;

import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Membership;

public class DomainPredicate implements Predicate {

	private Domain domain;
	
	public DomainPredicate(Domain domain) {
		this.domain = domain;
	}

	@Override
	public boolean evaluate(Object obj) {
		Membership m = (Membership) obj;

		if (m.getDomain().equals(domain)) {
			return true;
		}

		return false;
	}

}

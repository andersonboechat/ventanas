package br.com.abware.accountmgm.util;

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

		if (m.getDomain().getClass().equals(this.domain.getClass()) && m.getDomain().equals(domain)) {
			return true;
		}

		return false;
	}

}

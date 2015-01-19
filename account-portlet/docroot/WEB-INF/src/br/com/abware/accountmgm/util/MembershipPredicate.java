package br.com.abware.accountmgm.util;

import org.apache.commons.collections.Predicate;

import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Role;

public class MembershipPredicate implements Predicate {

	private Domain domain;
	
	private Role role;
	
	public MembershipPredicate(Domain domain, Role role) {
		this.domain = domain;
		this.role = role;
	}

	@Override
	public boolean evaluate(Object obj) {
		Membership m = (Membership) obj;

		if (m.getDomain().equals(domain) && m.getRole().equals(role)) {
			return true;
		}

		return false;
	}
}

package br.com.abware.accountmgm.util;

import org.apache.commons.collections.Predicate;

import br.com.abware.jcondo.core.model.Group;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Role;

public class MembershipPredicate implements Predicate {

	private Group group;
	
	private Role role;
	
	public MembershipPredicate(Group group, Role role) {
		this.group = group;
		this.role = role;
	}

	@Override
	public boolean evaluate(Object obj) {
		Membership m = (Membership) obj;

		if (m.getGroup().equals(group) && m.getRole() == role) {
			return true;
		}

		return false;
	}
}

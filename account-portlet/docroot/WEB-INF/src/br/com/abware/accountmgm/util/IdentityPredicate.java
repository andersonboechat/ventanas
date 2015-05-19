package br.com.abware.accountmgm.util;

import org.apache.commons.collections.Predicate;

import br.com.abware.jcondo.core.model.Person;

public class IdentityPredicate implements Predicate {

	private String identity;
	
	public IdentityPredicate(String identity) {
		this.identity = identity != null ? identity : "";
	}

	@Override
	public boolean evaluate(Object obj) {
		return obj != null && obj instanceof Person && identity.equals(((Person) obj).getIdentity());
	}

}

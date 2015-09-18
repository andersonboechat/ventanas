package br.com.atilo.jcondo.commons.collections;

import org.apache.commons.collections.Predicate;

import br.com.abware.jcondo.core.model.Phone;

public class PhonePredicate implements Predicate {

	private Phone phone;
	
	public PhonePredicate(Phone phone) {
		this.phone = phone;
	}
	
	@Override
	public boolean evaluate(Object obj) {
		return obj != null && obj instanceof Phone && 
				((Phone) obj).getExtension().equals(phone.getExtension()) &&
				((Phone) obj).getNumber().equals(phone.getNumber());
	}

}

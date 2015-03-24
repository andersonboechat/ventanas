package br.com.atilo.jcondo.commons.collections;

import org.apache.commons.collections.Predicate;

import br.com.abware.jcondo.core.model.BaseModel;

public class IdPredicate implements Predicate {

	private long id;
	
	public IdPredicate(long id) {
		this.id = id;
	}

	@Override
	public boolean evaluate(Object obj) {
		return obj != null && obj instanceof BaseModel && id == ((BaseModel) obj).getId();
	}

}

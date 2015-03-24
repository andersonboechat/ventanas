package br.com.atilo.jcondo.commons.collections;

import org.apache.commons.collections.Transformer;

import br.com.abware.jcondo.core.model.Membership;

public class DomainTransformer implements Transformer {

	@Override
	public Object transform(Object obj) {
		if (obj != null && obj instanceof Membership) {
			return ((Membership) obj).getDomain();
		}
		return null;
	}

}

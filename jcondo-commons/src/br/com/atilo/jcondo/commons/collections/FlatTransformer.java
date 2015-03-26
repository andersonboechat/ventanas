package br.com.atilo.jcondo.commons.collections;

import org.apache.commons.collections.Transformer;

import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Membership;

public class FlatTransformer implements Transformer {

	@Override
	public Object transform(Object obj) {
		if (obj != null) {
			Membership m = (Membership) obj;
			if (m.getDomain() instanceof Flat) {
				return m.getDomain();
			}
		}
		return null;
	}

}

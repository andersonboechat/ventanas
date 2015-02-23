package br.com.abware.accountmgm.util;

import org.apache.commons.collections.Transformer;

import br.com.abware.jcondo.core.model.Membership;

public class PersonTypeTransformer implements Transformer {

	@Override
	public Object transform(Object obj) {
		if (obj != null) {
			return ((Membership) obj).getType();
		}
		return null;
	}

}

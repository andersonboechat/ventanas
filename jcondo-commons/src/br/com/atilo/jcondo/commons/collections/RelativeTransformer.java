package br.com.atilo.jcondo.commons.collections;

import org.apache.commons.collections.Transformer;

import br.com.abware.jcondo.core.model.Kinship;

public class RelativeTransformer implements Transformer {

	@Override
	public Object transform(Object obj) {
		return obj != null && obj instanceof Kinship ? ((Kinship) obj).getRelative() : null;
	}

}

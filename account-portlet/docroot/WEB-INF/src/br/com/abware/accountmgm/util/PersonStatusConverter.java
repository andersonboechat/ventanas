package br.com.abware.accountmgm.util;

import org.apache.commons.beanutils.Converter;

import br.com.abware.jcondo.core.PersonStatus;

public class PersonStatusConverter implements Converter {

	@Override
	@SuppressWarnings("rawtypes")
	public Object convert(Class arg0, Object orig) {
		if ((Integer) orig == 0) {
			return PersonStatus.ACTIVE;
		} else {
			return PersonStatus.INACTIVE;
		}
	}

}

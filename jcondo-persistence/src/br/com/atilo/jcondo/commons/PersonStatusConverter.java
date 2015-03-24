package br.com.atilo.jcondo.commons;

import org.apache.commons.beanutils.Converter;

import br.com.abware.jcondo.core.PersonStatus;

public class PersonStatusConverter implements Converter {

	@Override
	@SuppressWarnings("rawtypes")
	public Object convert(Class dest, Object orig) {
		if (orig instanceof Integer) {
			if ((Integer) orig == 0) {
				return PersonStatus.ACTIVE;
			} else {
				return PersonStatus.INACTIVE;
			}
		} else if (orig instanceof PersonStatus) {
			return orig;
		}

		return null;
	}

}

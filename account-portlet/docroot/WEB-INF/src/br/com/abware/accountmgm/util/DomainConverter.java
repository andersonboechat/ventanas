package br.com.abware.accountmgm.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.Converter;

import br.com.abware.accountmgm.persistence.entity.AdministrationEntity;
import br.com.abware.accountmgm.persistence.entity.FlatEntity;
import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Flat;

public class DomainConverter implements Converter {

	@Override
	@SuppressWarnings("rawtypes")
	public Object convert(Class destClass, Object orig) {
		Object dest = null;

		try {
			if (orig instanceof Flat) {
				dest = new FlatEntity();
			} else if (orig instanceof FlatEntity) {
				dest = new Flat();
			} else if (orig instanceof Administration) {
				dest = new AdministrationEntity();
			} else if (orig instanceof AdministrationEntity) {
				dest = new Administration();
			}

			BeanUtils.copyProperties(dest, orig);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return dest;
	}

}

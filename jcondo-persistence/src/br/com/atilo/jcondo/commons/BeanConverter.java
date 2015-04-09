package br.com.atilo.jcondo.commons;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.Converter;

public class BeanConverter implements Converter {

	@SuppressWarnings("rawtypes")
	@Override
	public Object convert(Class arg0, Object orig) {
		if (orig == null) {
			return null;
		}

		Object dest = null;

		try {
			dest = arg0.newInstance();
			BeanUtils.copyProperties(dest, orig);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dest;
	}

}

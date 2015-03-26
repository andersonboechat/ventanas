package br.com.atilo.jcondo.commons;

import java.lang.reflect.InvocationTargetException;

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
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dest;
	}

}

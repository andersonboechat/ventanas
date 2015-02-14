package br.com.abware.accountmgm.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.Converter;

public class ListConverter implements Converter {

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object convert(Class destClass, Object orig) {
		if (!(orig instanceof List)) {
			return null;
		}

		List dests = new ArrayList();

		for (Object obj : (List) orig) {
			try {
				Object dest = new Object(); 
				BeanUtils.copyProperties(dest, obj);
				dests.add(dest);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return dests;
	}

}

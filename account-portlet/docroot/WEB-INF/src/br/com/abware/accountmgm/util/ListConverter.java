package br.com.abware.accountmgm.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.Converter;

import br.com.abware.accountmgm.persistence.entity.MembershipEntity;
import br.com.abware.jcondo.core.model.Membership;

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
				Object dest = getDestObject(obj); 
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

	private Object getDestObject(Object orig) {
		if (orig instanceof Membership) {
			return new MembershipEntity();
		}
		if (orig instanceof MembershipEntity) {
			return new Membership();
		}		
		return null;
	}
}

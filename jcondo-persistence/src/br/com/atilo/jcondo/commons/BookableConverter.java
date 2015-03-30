package br.com.atilo.jcondo.commons;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.Converter;

import br.com.abware.jcondo.booking.model.Room;
import br.com.atilo.jcondo.booking.persistence.entity.RoomEntity;

public class BookableConverter implements Converter {

	@Override
	@SuppressWarnings("rawtypes")
	public Object convert(Class destClass, Object orig) {
		if (orig == null) {
			return null;
		}

		Object dest = null;

		if (orig instanceof RoomEntity) {
			dest = new Room();
		} else {
			return null;
		}

		try {
			BeanUtils.copyProperties(dest, orig);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return dest;
	}

}

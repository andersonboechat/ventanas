package br.com.atilo.jcondo.commons;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.Converter;

import br.com.abware.jcondo.core.model.Administration;
import br.com.abware.jcondo.core.model.Domain;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Supplier;
import br.com.atilo.jcondo.core.persistence.entity.AdministrationEntity;
import br.com.atilo.jcondo.core.persistence.entity.DomainEntity;
import br.com.atilo.jcondo.core.persistence.entity.FlatEntity;
import br.com.atilo.jcondo.core.persistence.entity.SupplierEntity;

public class DomainConverter implements Converter {

	@Override
	@SuppressWarnings("rawtypes")
	public Object convert(Class destClass, Object orig) {
		if (orig == null) {
			return null;
		}

		Object dest = null;

		try {
			if (destClass.equals(Domain.class)) {
				if (orig instanceof FlatEntity) {
					dest = new Flat();
				} else if (orig instanceof AdministrationEntity) {
					dest = new Administration();
				} else if (orig instanceof SupplierEntity) {
					dest = new Supplier();
				} else {
					dest = orig.getClass().newInstance();
				}
			} else if (destClass.equals(DomainEntity.class)) {
				if (orig instanceof Flat) {
					dest = new FlatEntity();
				} else if (orig instanceof Administration) {
					dest = new AdministrationEntity();
				} else if (orig instanceof Supplier) {
					dest = new SupplierEntity();
				} else {
					dest = orig.getClass().newInstance();
				}
			}

			BeanUtils.copyProperties(dest, orig);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dest;
	}

}

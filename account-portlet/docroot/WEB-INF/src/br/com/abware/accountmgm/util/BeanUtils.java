package br.com.abware.accountmgm.util;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;

import br.com.abware.accountmgm.persistence.entity.MembershipEntity;
import br.com.abware.accountmgm.persistence.entity.PersonEntity;
import br.com.abware.jcondo.core.PersonStatus;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;
import br.com.abware.jcondo.core.model.Membership;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.model.Role;

public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

	static {
		 ConvertUtilsBean cub = BeanUtilsBean.getInstance().getConvertUtils();
		 BeanConverter bc = new BeanConverter();
		 cub.register(bc, Role.class);
		 cub.register(bc, Membership.class);
		 cub.register(bc, MembershipEntity.class);
		 cub.register(bc, PersonEntity.class);
		 cub.register(bc, Person.class);
		 cub.register(bc, Flat.class);
		 cub.register(bc, Image.class);

		 cub.register(new PersonStatusConverter(), PersonStatus.class);

		 ListConverter lc = new ListConverter();
		 cub.register(lc, ArrayList.class);
	}
	
	public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
		BeanUtilsBean.getInstance().copyProperties(dest, orig);
	}
}

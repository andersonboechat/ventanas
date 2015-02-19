package br.com.abware.accountmgm.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;

import br.com.abware.accountmgm.persistence.entity.DomainEntity;
import br.com.abware.accountmgm.persistence.entity.FlatEntity;
import br.com.abware.accountmgm.persistence.entity.MembershipEntity;
import br.com.abware.accountmgm.persistence.entity.PersonEntity;
import br.com.abware.jcondo.core.PersonStatus;
import br.com.abware.jcondo.core.model.Domain;
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
		 cub.register(bc, FlatEntity.class);
		 cub.register(bc, Image.class);

		 DomainConverter dc = new DomainConverter();
		 cub.register(dc, DomainEntity.class);
		 cub.register(dc, Domain.class);

		 cub.register(new PersonStatusConverter(), PersonStatus.class);
	}
	
	public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
		BeanUtilsBean.getInstance().copyProperties(dest, orig);
	}
}

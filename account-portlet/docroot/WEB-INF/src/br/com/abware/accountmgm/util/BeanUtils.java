package br.com.abware.accountmgm.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;

import br.com.abware.jcondo.core.PersonStatus;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Image;

public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

	static {
		 ConvertUtilsBean cub = BeanUtilsBean.getInstance().getConvertUtils();
		 BeanConverter converter = new BeanConverter();
		 cub.register(converter, Flat.class);
		 cub.register(converter, Image.class);
		 cub.register(new PersonStatusConverter(), PersonStatus.class);
	}
	
	public static void copyProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
		BeanUtilsBean.getInstance().copyProperties(dest, orig);
	}
}
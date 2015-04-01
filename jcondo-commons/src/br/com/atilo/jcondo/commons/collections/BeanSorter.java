package br.com.atilo.jcondo.commons.collections;

import java.util.Comparator;

import org.apache.commons.beanutils.PropertyUtils;

public class BeanSorter<Bean> implements Comparator<Bean> {

	public static final Integer ASCENDING_ORDER = 0;
	
	public static final Integer DESCENDING_ORDER = -1;	

	private String field;

    private Integer order;
    
    public BeanSorter(String field, Integer order) {
        this.field = field;
        this.order = order;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public int compare(Bean bean1, Bean bean2) {
        try {
        	Object value1 = PropertyUtils.getProperty(bean1, field);
            Object value2 = PropertyUtils.getProperty(bean2, field);

            int value = ((Comparable) value1).compareTo(value2);

            return ASCENDING_ORDER.equals(order) ? value : -1 * value;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}

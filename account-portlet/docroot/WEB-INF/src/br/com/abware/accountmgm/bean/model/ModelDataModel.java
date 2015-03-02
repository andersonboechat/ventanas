package br.com.abware.accountmgm.bean.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.ListDataModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;
import org.primefaces.model.SelectableDataModel;

import br.com.abware.jcondo.core.model.BaseModel;

public class ModelDataModel<Model extends BaseModel> extends ListDataModel<Model> implements SelectableDataModel<Model> {

	public static final Integer ASCENDING_ORDER = 0;

	public static final Integer DESCENDING_ORDER = 1;

	private List<Model> models;
	
	public ModelDataModel(List<Model> models) {
		super(new ArrayList<Model>(models));
		this.models = models;
	}

    @SuppressWarnings("unchecked")
	public void addModel(Model model) {
    	models.add(0, model);
		((List<Model>) getWrappedData()).add(0, model);
	}
   
    @SuppressWarnings("unchecked")
	public void removeModel(Model model) {
    	models.remove(model);
		((List<Model>) getWrappedData()).remove(model);		
	}

    @SuppressWarnings("unchecked")
	public void setModel(Model model) {
    	int index = ((List<Model>) getWrappedData()).indexOf(model);
    	if (index >= 0) {
    		((List<Model>) getWrappedData()).set(index, model);
    	}

    	index = models.indexOf(model);
    	if (index >= 0) {
    		models.set(index, model);
    	}
	}
	
    @Override
    public Model getRowData(String rowKey) {
        for(Model model : models) {
            if (Long.valueOf(rowKey) == model.getId()) {
            	return model;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(Model model) {
        return model.getId();
    }

	public void filter(Map<String, Object> filters) throws Exception {
    	List<Model> filteredModels = new ArrayList<Model>();

		if (!MapUtils.isEmpty(filters)) {
			for (Model model : models) {
				boolean match = true;
				for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
					String filterProperty = it.next();
					Object filterValue = filters.get(filterProperty);
    				if (!match(model, filterProperty, filterValue)) {
    					match = false;
    					break;
    				}
				}

				if (match) {
					filteredModels.add(model);
	    		}
			}
    	} else {
    		filteredModels = models;
    	}

		setWrappedData(filteredModels);
	}
    
	@SuppressWarnings("unchecked")
	public void sort(String field, int order) {
		Collections.sort((List<Model>) getWrappedData(), new ModelSorter<Model>(field, order));
	}

	@SuppressWarnings("rawtypes")
	private boolean match(Object obj, String filterProperty, Object filterValue) throws Exception {
		// memberships.domain.number
		String[] properties = filterProperty.split("\\.");

		if (properties.length > 1) {
			if (PropertyUtils.getPropertyType(obj, properties[0]).equals(List.class)) {
				List list = (List) PropertyUtils.getProperty(obj, properties[0]);
				for (Object object : list) {
					if (match(object, filterProperty.substring(properties[0].length() + 1), filterValue)) {
						return true;
					}
				}
			} else {
				Object o = PropertyUtils.getProperty(obj, properties[0]);
				return match(o, filterProperty.substring(properties[0].length() + 1), filterValue);
			}
		} else {
			try {
				String fieldValue = BeanUtils.getProperty(obj, filterProperty);
				if (filterValue == null || fieldValue.toLowerCase().matches(".*" + filterValue.toString().toLowerCase() + ".*")) {
					return true;
				}
			} catch (Exception e) {
				return true;
			}
		}

		return false;
	}
	
}

class ModelSorter<Model> implements Comparator<Model> {

    private String field;

    private Integer order;

    public ModelSorter(String field, Integer order) {
        this.field = field;
        this.order = order;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public int compare(Model model1, Model model2) {
        try {
            Object value1 = BeanUtils.getProperty(model1, field);
            Object value2 = BeanUtils.getProperty(model2, field);

            int value = ((Comparable) value1).compareTo(value2);

            return ModelDataModel.ASCENDING_ORDER.equals(order) ? value : -1 * value;
        } catch(Exception e) {
            throw new RuntimeException();
        }
    }
}
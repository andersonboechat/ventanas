package br.com.abware.accountmgm.bean.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.abware.jcondo.core.model.BaseModel;

public class ModelDataModel<Model extends BaseModel> extends LazyDataModel<Model> {

	private static final long serialVersionUID = 1L;
	
	private List<Model> models;
	
	private Class<? extends BaseModel> modelClass;
	
	public ModelDataModel(List<Model> models) {
		this.models = models;
		this.modelClass = models.get(0).getClass();
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

	@Override
	public List<Model> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		List<Model> data = new ArrayList<Model>();

		//filter
		for(Model model : models) {
			boolean match = true;

			if (filters != null) {
				for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
					try {
						String filterProperty = it.next();
						Object filterValue = filters.get(filterProperty);
						String fieldValue = String.valueOf(model.getClass().getField(filterProperty).get(model));

						if(filterValue == null || fieldValue.startsWith(filterValue.toString())) {
							match = true;
						} else {
							match = false;
							break;
						}
					} catch(Exception e) {
						match = false;
					}
				}
			}
 
            if(match) {
                data.add(model);
            }
        }

		//sort
		if(sortField != null) {
			Collections.sort(data, new ModelSorter<Model>(modelClass, sortField, sortOrder));
		}

		//rowCount
		int dataSize = data.size();
		this.setRowCount(dataSize);

		//paginate
		if(dataSize > pageSize) {
			try {
				return data.subList(first, first + pageSize);
			}
			catch(IndexOutOfBoundsException e) {
				return data.subList(first, first + (dataSize % pageSize));
			}
		} else {
			return data;
		}
	}
	
}

class ModelSorter<Model> implements Comparator<Model> {

    private String sortField;

    private SortOrder sortOrder;
    
    private Class<? extends BaseModel> modelClass;

    public ModelSorter(Class<? extends BaseModel> modelClass, String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
        this.modelClass = modelClass;  
    }
 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public int compare(Model car1, Model car2) {
        try {
            Object value1 = modelClass.getField(this.sortField).get(car1);
            Object value2 = modelClass.getField(this.sortField).get(car2);

            int value = ((Comparable) value1).compareTo(value2);

            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        } catch(Exception e) {
            throw new RuntimeException();
        }
    }
}
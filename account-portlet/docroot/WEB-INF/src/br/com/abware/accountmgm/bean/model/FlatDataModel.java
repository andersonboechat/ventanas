package br.com.abware.accountmgm.bean.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.abware.jcondo.core.model.Flat;

public class FlatDataModel extends LazyDataModel<Flat> {

	private static final long serialVersionUID = 1L;
	
	private List<Flat> flats;
	
	public FlatDataModel(List<Flat> flats) {
		this.flats = flats;
	}

    @Override
    public Flat getRowData(String rowKey) {
        for(Flat flat : flats) {
            if (Long.valueOf(rowKey) == flat.getId()) {
            	return flat;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(Flat flat) {
        return flat.getId();
    }

	@Override
	public List<Flat> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		List<Flat> data = new ArrayList<Flat>();

		//filter
		for(Flat flat : flats) {
			boolean match = true;

			if (filters != null) {
				for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
					try {
						String filterProperty = it.next();
						Object filterValue = filters.get(filterProperty);
						String fieldValue = String.valueOf(flat.getClass().getField(filterProperty).get(flat));

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
                data.add(flat);
            }
        }

		//sort
		if(sortField != null) {
			Collections.sort(data, new FlatSorter(sortField, sortOrder));
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

class FlatSorter implements Comparator<Flat> {

    private String sortField;

    private SortOrder sortOrder;

    public FlatSorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public int compare(Flat car1, Flat car2) {
        try {
            Object value1 = Flat.class.getField(this.sortField).get(car1);
            Object value2 = Flat.class.getField(this.sortField).get(car2);
 
            int value = ((Comparable) value1).compareTo(value2);
             
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        } catch(Exception e) {
            throw new RuntimeException();
        }
    }
}
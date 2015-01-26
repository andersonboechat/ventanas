package br.com.abware.accountmgm.bean.model;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.ListDataModel;

import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortOrder;

import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.core.model.Flat;
import br.com.abware.jcondo.core.model.Person;
import br.com.abware.jcondo.core.service.PersonService;

public class PersonDataModel extends ListDataModel<Person> implements SelectableDataModel<Person>  {

	private PersonService personService;

	private List<Flat> flats;

    public PersonDataModel(PersonService personService, List<Flat> flats) throws Exception {
    	this.personService = personService;
		this.flats = flats;
		this.filter(this, null);
	}

	@SuppressWarnings("unchecked")
	@Override
    public Person getRowData(String rowKey) {
        for(Person model : (List<Person>) getWrappedData()) {
            if (Long.valueOf(rowKey) == model.getId()) {
            	return model;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(Person model) {
        return model.getId();
    }

	public void filter(PersonDataModel model, Map<String, Object> filters) throws Exception {
    	List<Person> people = new ArrayList<Person>();

    	for (Flat flat : model.getFlats()) {
			if (filters != null) {
	    		if (doFilter(filters, flat)) {
	    			for (Person person : personService.getPeople(flat)) {
	    				if (doFilter(filters, person)) {
	    					people.add(person);
	    				}
	    			}
	    		}
			} else {
				people.addAll(personService.getPeople(flat));
			}
    	}

		model.setWrappedData(people);
	}

	private boolean doFilter(Map<String, Object> filters, BaseModel model) {
		boolean match = true;

		for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
			String filterProperty = it.next();
			Object filterValue = filters.get(filterProperty);
			String fieldValue = null;

			try {
				fieldValue = BeanUtils.getProperty(model, filterProperty);
    			if (filterValue == null  || fieldValue.toLowerCase().matches(".*" + filterValue.toString().toLowerCase() + ".*")) {
    				match = true;
    			} else {
    				match = false;
                    break;
    			}
			} catch (Exception e) {
				match = true;
			}
		}

		return match;
	}
	
	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}
	
}

class PersonSorter<Model> implements Comparator<Model> {

    private String sortField;

    private SortOrder sortOrder;
    
    private Class<? extends BaseModel> modelClass;

    public PersonSorter(Class<? extends BaseModel> modelClass, String sortField, SortOrder sortOrder) {
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
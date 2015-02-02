package br.com.abware.accountmgm.bean.model;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.ListDataModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.primefaces.model.SelectableDataModel;

import br.com.abware.accountmgm.model.Vehicle;

public class VehicleDataModel extends ListDataModel<Vehicle> implements SelectableDataModel<Vehicle>  {

	private List<Vehicle> vehicles;

    public VehicleDataModel(List<Vehicle> vehicles) throws Exception {
    	super(vehicles);
    	List<Vehicle> vs = new ArrayList<Vehicle>();
    	vs.addAll(vehicles);
		this.vehicles = vs;
	}

    @SuppressWarnings("unchecked")
	public void addModel(Vehicle vehicle) {
		vehicles.add(vehicle);
		((List<Vehicle>) getWrappedData()).add(vehicle);
	}
   
    @SuppressWarnings("unchecked")
	public void removeModel(Vehicle vehicle) {
		vehicles.remove(vehicle);
		((List<Vehicle>) getWrappedData()).remove(vehicle);		
	}

    @SuppressWarnings("unchecked")
	@Override
    public Vehicle getRowData(String rowKey) {
        for(Vehicle vehicle : (List<Vehicle>) getWrappedData()) {
            if (Long.valueOf(rowKey) == vehicle.getId()) {
            	return vehicle;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Vehicle vehicle) {
        return vehicle.getId();
    }

	public void filter(Map<String, Object> filters) throws Exception {
    	List<Vehicle> filteredVehicles = new ArrayList<Vehicle>();

		if (!MapUtils.isEmpty(filters)) {
			for (Vehicle vehicle : vehicles) {
				boolean match = true;
				for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
					String filterProperty = it.next();
					Object filterValue = filters.get(filterProperty);
					String fieldValue = null;

					try {
						fieldValue = BeanUtils.getProperty(vehicle, filterProperty);
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

				if (match) {
					filteredVehicles.add(vehicle);
	    		}
			}
    	} else {
    		filteredVehicles = vehicles;
    	}

		setWrappedData(filteredVehicles);
	}

}
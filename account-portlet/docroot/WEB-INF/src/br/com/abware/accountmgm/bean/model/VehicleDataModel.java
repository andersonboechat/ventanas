package br.com.abware.accountmgm.bean.model;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.ListDataModel;

import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.model.SelectableDataModel;

import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.service.core.VehicleServiceImpl;
import br.com.abware.jcondo.core.model.BaseModel;
import br.com.abware.jcondo.core.model.Flat;

public class VehicleDataModel extends ListDataModel<Vehicle> implements SelectableDataModel<Vehicle>  {

	private VehicleServiceImpl vehicleService;

	private List<Flat> flats;

    public VehicleDataModel(VehicleServiceImpl vehicleService, List<Flat> flats) throws Exception {
    	this.vehicleService = vehicleService;
		this.flats = flats;
		this.filter(this, null);
	}

	@SuppressWarnings("unchecked")
	@Override
    public Vehicle getRowData(String rowKey) {
        for(Vehicle model : (List<Vehicle>) getWrappedData()) {
            if (Long.valueOf(rowKey) == model.getId()) {
            	return model;
            }
        }

        return null;
    }

    @Override
    public Object getRowKey(Vehicle model) {
        return model.getId();
    }

	public void filter(VehicleDataModel model, Map<String, Object> filters) throws Exception {
    	List<Vehicle> vehicles = new ArrayList<Vehicle>();

    	for (Flat flat : model.getFlats()) {
			if (filters != null) {
	    		if (doFilter(filters, flat)) {
	    			for (Vehicle vehicle : vehicleService.getVehicles(flat)) {
	    				if (doFilter(filters, vehicle)) {
	    					vehicles.add(vehicle);
	    				}
	    			}
	    		}
			} else {
				vehicles.addAll(vehicleService.getVehicles(flat));
			}
    	}

		model.setWrappedData(vehicles);
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
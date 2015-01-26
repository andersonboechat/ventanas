package br.com.abware.accountmgm.bean.model;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import br.com.abware.accountmgm.model.Vehicle;
import br.com.abware.accountmgm.service.core.VehicleServiceImpl;
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
	    		for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
	    			String filterProperty = it.next();
	    			Object filterValue = filters.get(filterProperty);
	    			String fieldValue = null;

	    			try {
	    				fieldValue = String.valueOf(flat.getClass().getField(filterProperty).get(flat));
	    			} catch (Exception e) {
	    				// It is not a flat property
	    			}

		    		if (filterValue == null || fieldValue == null || fieldValue.equals(filterValue)) {
		    			for (Vehicle vehicle : vehicleService.getVehicles(flat)) {
			    			try {
			    				fieldValue = String.valueOf(vehicle.getClass().getField(filterProperty).get(vehicle));
			    			} catch (Exception e) {
			    				// It is not a vehicle property
			    			}

				    		if (filterValue == null || fieldValue == null || fieldValue.matches(".*" + filterValue + ".*")) {
		    					vehicles.add(vehicle);
		    				}
		    			}
		    		}
	    		}
			} else {
				vehicles.addAll(vehicleService.getVehicles(flat));
			}
    	}

		model.setWrappedData(vehicles);
	}

	public List<Flat> getFlats() {
		return flats;
	}

	public void setFlats(List<Flat> flats) {
		this.flats = flats;
	}
	
}
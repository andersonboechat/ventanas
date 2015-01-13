package br.com.abware.accountmgm.bean.model;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.abware.accountmgm.model.Vehicle;

public class VehicleDataModel extends LazyDataModel<Vehicle> {

	private static final long serialVersionUID = 1L;

	private List<Vehicle> vehicles;

	public VehicleDataModel(List<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}

	@Override
	public List<Vehicle> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		return vehicles.subList(first, first + pageSize);
	}

}

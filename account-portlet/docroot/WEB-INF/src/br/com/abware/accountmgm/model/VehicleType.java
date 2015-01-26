package br.com.abware.accountmgm.model;

public enum VehicleType {

	RESIDENT("vehicle.type.resident"),
	VISITOR("vehicle.type.visitor");

	private String label;

	private VehicleType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return label;
	}

}

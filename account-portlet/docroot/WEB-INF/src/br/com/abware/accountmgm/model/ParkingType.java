package br.com.abware.accountmgm.model;

public enum ParkingType {

	INDOOR("parking.type.indoor"),
	OUTDOOR("parking.type.outdoor"),
	VISITOR("parking.type.visitor");

	private String label;

	private ParkingType(String label) {
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

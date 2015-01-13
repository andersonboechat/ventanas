package br.com.abware.accountmgm.model;

public enum VehicleAccessType {

	INBOUND("access.inbound"),
	OUTBOUND("access.outbound");
	
	private String label;
	
	private VehicleAccessType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}

package br.com.abware.accountmgm.model;

public enum AccessType {

	INBOUND("access.inbound"),
	OUTBOUND("access.outbound");

	private String label;

	private AccessType(String label) {
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

package br.com.abware.agenda;

public enum BookingStatus {

	OPENED("Reservado"),
	PAID("Pago"),
	CANCELLED("Cancelado");

	private String label;
	
	private BookingStatus(String label) {
		this.setLabel(label);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}

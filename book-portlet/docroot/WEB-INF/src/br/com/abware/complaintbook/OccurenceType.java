package br.com.abware.complaintbook;

public enum OccurenceType {

	COMPLAINT("Reclama��o"),
	SUGGESTION("Sugest�o");
	
	private String label;
	
	private OccurenceType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}

package br.com.abware.complaintbook;

public enum OccurenceType {

	COMPLAINT("Reclamação"),
	SUGGESTION("Sugestão");
	
	private String label;
	
	private OccurenceType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}

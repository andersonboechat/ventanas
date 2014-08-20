package br.com.abware.complaintbook.exception;

public class OccurrenceAlreadyDoneException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public OccurrenceAlreadyDoneException(String message) {
		super(message);
	}

}

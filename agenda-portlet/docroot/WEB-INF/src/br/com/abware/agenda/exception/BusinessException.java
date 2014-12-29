package br.com.abware.agenda.exception;

public class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private Object[] args;

	public BusinessException(String message, Object ... args) {
		super(message);
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

}

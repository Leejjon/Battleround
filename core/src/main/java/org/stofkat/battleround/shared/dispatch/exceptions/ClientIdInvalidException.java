package org.stofkat.battleround.shared.dispatch.exceptions;

public class ClientIdInvalidException extends ActionException {
	private static final long serialVersionUID = 1L;
	private static final String message = "Invalid client id";
	
	public ClientIdInvalidException() {
		super(message);
	}
}

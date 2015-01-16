package org.stofkat.battleround.shared.dispatch.exceptions;

public class AuthenticationException extends ActionException {
	private static final long serialVersionUID = 1L;
	private static final String message = "Please log in.";
	
	public AuthenticationException() {
		super(message);
	}
}

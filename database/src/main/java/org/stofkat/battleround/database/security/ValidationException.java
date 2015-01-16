package org.stofkat.battleround.database.security;

import org.stofkat.battleround.common.resources.Resources;

public class ValidationException extends Exception {
	/**
	 * If this exception ever is going to be stored in the database, this version number should be increased every update.
	 */
	private static final long serialVersionUID = 1L;
	
	private Resources userMessage;
	
	public ValidationException(Resources userMessage) {
		super(userMessage.toString());
		this.userMessage = userMessage;
	}
	
	public ValidationException(Resources userMessage, Exception ex) {
		super(userMessage.toString());
		this.userMessage = userMessage;
	}
	
	public ValidationException(String errorMessage) {
		super(errorMessage);
	}
	
	public ValidationException(String errorMessage, Exception ex) {
		super(errorMessage, ex);
	}
	
	public Resources getUserMessage() {
		return userMessage;
	}
}

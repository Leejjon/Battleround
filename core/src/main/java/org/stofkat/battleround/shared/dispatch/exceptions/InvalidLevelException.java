package org.stofkat.battleround.shared.dispatch.exceptions;

public class InvalidLevelException extends ActionException {
	private static final long serialVersionUID = 1L;
	
	private static final String message = "Could not load selected level.";
	
	public InvalidLevelException() {
		super(message);
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}

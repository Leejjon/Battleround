package org.stofkat.battleround.shared.dispatch.exceptions;

public class GameFullException extends ActionException {
	private static final long serialVersionUID = 1L;
	private static final String message = "Game is full.";
	
	public GameFullException() {
		super(message);
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}

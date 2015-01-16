package org.stofkat.battleround.shared.dispatch.exceptions;

public class CharacterAlreadyChosenException extends ActionException {
	private static final long serialVersionUID = 1L;
	private static final String message = "Game is full.";
	
	public CharacterAlreadyChosenException() {
		super(message);
	}
}

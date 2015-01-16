package org.stofkat.battleround.shared.dispatch.exceptions;

public class GameNotAvailableOnServerException extends ActionException {
	private static final long serialVersionUID = 1L;
	
	private static final String message = "Game not available on server. (Session time-out)";
	
	public GameNotAvailableOnServerException() {
		super(message);
	}
}

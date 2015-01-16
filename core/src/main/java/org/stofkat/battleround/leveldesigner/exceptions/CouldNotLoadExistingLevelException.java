package org.stofkat.battleround.leveldesigner.exceptions;

import org.stofkat.battleround.shared.dispatch.exceptions.ActionException;

public class CouldNotLoadExistingLevelException extends ActionException {
	private static final long serialVersionUID = 1L;
	public static final String message = "Could not load existing level.";

	/**
	 * Do not provide a stacktrace of the original server exception for security
	 * reasons. The user won't need that on the client.
	 * 
	 * @param message
	 */
	public CouldNotLoadExistingLevelException(String message) {
		super(message);
	}
}

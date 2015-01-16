package org.stofkat.battleround.leveldesigner.exceptions;

import org.stofkat.battleround.shared.dispatch.exceptions.ActionException;

public class AuthorizationException extends ActionException {
	private static final long serialVersionUID = 1L;

	public AuthorizationException(String message) {
		super(message);
	}
}

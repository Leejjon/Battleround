package org.stofkat.battleround.core;

import java.io.Serializable;


public class EngineException extends Exception implements Serializable {
	/**
	 * In case this exception will be serialized.
	 */
	private static final long serialVersionUID = 1L;
	
	public enum EngineExceptions {
		FAILED_LOADING_IMAGES("Failed loading the images required to start the game."),
		FILE_NOT_FOUND("Could not find a required file on the filesystem."),
		LEVEL_INVALID("The engine could not be started because the level is invalid."),
		PATH_IS_NOT_A_DIRECTORY("The path to find the levels textures is not a directory."),
		SPRITESHEET_DATA_CONTAINS_INVALID_SPRITE_WIDTH("Incorrect width of sprite or spritesheet."),
		SPRITESHEET_DATA_CONTAINS_INVALID_SPRITE_HEIGHT("Incorrect height of sprite or spritesheet."),
		SPRITESHEET_ORDER_CONTAINS_INVALID_CHARACTER("Order contains invalid characters.");
		
		private final String message;

		private EngineExceptions(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}
	
	public EngineException(String message) {
		super(message);
	}
	
	public EngineException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public EngineException(EngineExceptions knownErrorMessage) {
		super(knownErrorMessage.getMessage());
	}

	public EngineException(EngineExceptions knownErrorMessage, Throwable throwable) {
		super(knownErrorMessage.getMessage(), throwable);
	}
}

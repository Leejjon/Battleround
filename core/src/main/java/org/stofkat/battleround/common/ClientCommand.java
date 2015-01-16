package org.stofkat.battleround.common;

import java.io.Serializable;

public enum ClientCommand implements Serializable {
	/**
	 * Client needs to wait.
	 */
	WAIT,
	
	/**
	 * Client needs to make it's move.
	 */
	SUBMIT_TURN,
	
	/**
	 * Download the round object. 
	 */
	DOWNLOAD_ROUND,
	
	/**
	 * Client has to close the game.
	 */
	FINISHED;
}

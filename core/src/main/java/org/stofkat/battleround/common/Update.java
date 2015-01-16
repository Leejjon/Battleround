package org.stofkat.battleround.common;

import org.stofkat.battleround.shared.dispatch.results.Result;

public interface Update extends Result {
	/**
	 * This method can be used by the clients to see if they already have the
	 * update, and thus not execute it twice, or need to get older updates
	 * through first.
	 * 
	 * @return The number of the update.
	 */
	long getUpdateNumber();

	/**
	 * The server needs to give every update an update number.
	 * 
	 * @param updateNumber
	 */
	void setUpdateNumber(long updateNumber);
	
	/**
	 * This method can be used to sort updates by the round they belong to.
	 * 
	 * @return The number of the round.
	 */
	long getRoundNumber();
	
	/**
	 * This method can be used to tie an update to a round.
	 * 
	 * @param roundNumber
	 */
	void setRoundNumber(long roundNumber);
}

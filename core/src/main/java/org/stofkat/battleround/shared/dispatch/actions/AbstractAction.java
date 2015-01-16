package org.stofkat.battleround.shared.dispatch.actions;

import org.stofkat.battleround.shared.dispatch.results.Result;

public abstract class AbstractAction<R extends Result> implements Action<R> {
	private static final long serialVersionUID = 1L;
	
	private long lastUpdateNumber;
	
	// TODO: Add client id here.
	
	public AbstractAction() {}
	
	public AbstractAction(long lastUpdateNumber) {
		this.lastUpdateNumber = lastUpdateNumber;
	}

	public long getLastUpdateNumber() {
		return lastUpdateNumber;
	}

	public void setLastUpdateNumber(long lastUpdateNumber) {
		this.lastUpdateNumber = lastUpdateNumber;
	}
}

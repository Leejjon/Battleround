package org.stofkat.battleround.shared.dispatch.actions;

import org.stofkat.battleround.shared.dispatch.results.CheckForUpdatesResult;


public class CheckForUpdatesAction extends AbstractAction<CheckForUpdatesResult>  {
	private static final long serialVersionUID = 100L;
	
	private long clientId;
	
	public CheckForUpdatesAction() {}
	
	public CheckForUpdatesAction(long clientId, long lastUpdateNumber) {
		super(lastUpdateNumber);
		this.clientId = clientId;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}
}

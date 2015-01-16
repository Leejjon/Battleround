package org.stofkat.battleround.core;

import org.stofkat.battleround.shared.dispatch.actions.CheckForUpdatesAction;

import com.badlogic.gdx.utils.Timer.Task;

public class UpdateTask extends Task {
	private ServerInterface server;
	
	public UpdateTask(ServerInterface server) {
		this.server = server;
	}
	
	@Override
	public void run() {
		server.executeServerAction(new CheckForUpdatesAction(server.getClientId(), server.getLastUpdateNumber()));
	}
}

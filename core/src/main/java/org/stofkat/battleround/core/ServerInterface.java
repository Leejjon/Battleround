package org.stofkat.battleround.core;

import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.results.Result;

public interface ServerInterface {
	
	/**
	 * Until I've found a way to unify the server interface, every subclass of
	 * this engine needs to provide the correct DispatchServiceAsync for the
	 * client.
	 * 
	 * @param action
	 */
	<R extends Result> void executeServerAction(Action<R> action);
	
	long getClientId();
	
	long getLastUpdateNumber();
	
	/**
	 * A lot of server actions return a CheckForUpdatesResult. If we're doing a
	 * server call, why not getting the newest updates while we're there?
	 * 
	 * So if we're going to do a server call that gets the newest updates, we
	 * should also pause the auto update timer. Otherwise we might get two
	 * server calls around the same time. 
	 */
	void pauseTimerBecauseWereGonnaUpdate();
}

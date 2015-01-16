package org.stofkat.battleround.client.http.dispatch;

import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.results.Result;

public interface HttpDispatchServiceAsync {
	/**
	 * Executes the specified action.
	 * 
	 * @param action
	 *            The action to execute.
	 * @param callback
	 *            The callback to execute once the action completes.
	 * 
	 * @see net.customware.http.dispatch.server.Dispatch
	 */
	<R extends Result> void execute(Action<R> action, AsyncCallback<R> callback);
}
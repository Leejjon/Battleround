package org.stofkat.battleround.html.gwt.dispatch;

import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.results.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GwtDispatchServiceAsync {
	<R extends Result> void execute(Action<R> action, AsyncCallback<R> callback);
}

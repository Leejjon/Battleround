package org.stofkat.battleround.html.gwt.dispatch;

import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.exceptions.DispatchException;
import org.stofkat.battleround.shared.dispatch.results.Result;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("BattleRound-gwt")
public interface GwtDispatchService extends RemoteService {
	<R extends Result> R execute( Action<R> action ) throws DispatchException;
}

package org.stofkat.battleround.html.gwt.dispatch;

import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineInterface;
import org.stofkat.battleround.shared.dispatch.exceptions.ActionException;
import org.stofkat.battleround.shared.dispatch.results.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class basically catches all results from actions.
 * 
 * @author Leejjon
 *
 * @param <R> The return object should always extend from the Result object.
 */
public class GwtAsyncCallbackHandler<R extends Result> implements AsyncCallback<R> {
	private EngineInterface engine;
	
	public GwtAsyncCallbackHandler(EngineInterface engine) {
		this.engine = engine;
	}
	
	@Override
	public void onFailure(Throwable caught) {
		caught.printStackTrace();
		
		if (caught instanceof ActionException) {
			engine.handleError(caught.getMessage());
		} else {
		    engine.close();
		}
	}

	@Override
	public void onSuccess(R result) {
		try {
			result.processResult(engine);
		} catch (EngineException e) {
			e.printStackTrace();
		}
	}
}

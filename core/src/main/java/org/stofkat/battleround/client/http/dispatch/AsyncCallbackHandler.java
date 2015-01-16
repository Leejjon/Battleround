package org.stofkat.battleround.client.http.dispatch;

import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineInterface;
import org.stofkat.battleround.shared.dispatch.exceptions.ActionException;
import org.stofkat.battleround.shared.dispatch.results.Result;

public class AsyncCallbackHandler<R extends Result> implements AsyncCallback<R> {
	private EngineInterface engine;
	
	public AsyncCallbackHandler(EngineInterface engine) {
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

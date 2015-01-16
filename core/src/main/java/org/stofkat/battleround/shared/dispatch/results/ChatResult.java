package org.stofkat.battleround.shared.dispatch.results;

import java.util.List;

import org.stofkat.battleround.common.Update;
import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineInterface;

public class ChatResult implements Result {
	private static final long serialVersionUID = 100L;
	
	private List<Update> updates;
	
	public ChatResult() {} 
	
	public ChatResult(List<Update> updates) {
		this.updates = updates;
	}
	
	@Override
	public void processResult(EngineInterface engineInterface) throws EngineException {
		for (Update update : updates) {
			update.processResult(engineInterface);
		}
	}

	public List<Update> getUpdates() {
		return updates;
	}

	public void setUpdates(List<Update> updates) {
		this.updates = updates;
	}
}

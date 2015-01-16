package org.stofkat.battleround.shared.dispatch.results;

import java.util.List;

import org.stofkat.battleround.common.Update;
import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineInterface;

public class SelectCharacterResult implements Result {
	private static final long serialVersionUID = 100L;
	
	private ResultValue result;
	
	private List<Update> updates;
	
	public SelectCharacterResult() {}
	
	public SelectCharacterResult(ResultValue result, List<Update> updates) {
		this.updates = updates;
		this.result = result;
	}
	
	@Override
	public void processResult(EngineInterface engineInterface)
			throws EngineException {
		engineInterface.setCharacterSelectedResult(result, updates);
	}
	
	public ResultValue getResult() {
		return result;
	}

	public void setResult(ResultValue result) {
		this.result = result;
	}
	
	public List<Update> getUpdates() {
		return updates;
	}

	public void setUpdates(List<Update> updates) {
		this.updates = updates;
	}

	public enum ResultValue {
		SUCCESS("Ok"), CHARACTER_ALREADY_IN_USE("Please choose another character.");
		
		private String message;
		private ResultValue(String message) {
			this.message = message;
		}
		
		@Override
		public String toString() {
			return message;
		}
	}
}

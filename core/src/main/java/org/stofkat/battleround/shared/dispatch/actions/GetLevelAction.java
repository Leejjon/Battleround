package org.stofkat.battleround.shared.dispatch.actions;

import org.stofkat.battleround.shared.dispatch.results.GetLevelResult;

public class GetLevelAction implements Action<GetLevelResult> {
	private static final long serialVersionUID = 1L;
	
	private String levelName;
	
	private String sessionId;
	
	public GetLevelAction() {}
	
	public GetLevelAction(String levelName, String sessionId) {
		this.levelName = levelName;
		this.sessionId = sessionId;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}

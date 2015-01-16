package org.stofkat.battleround.shared.dispatch.results;

import java.util.HashMap;

import org.stofkat.battleround.common.level.structure.Level;
import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineInterface;

public class GetLevelResult implements Result {
	private static final long serialVersionUID = 1L;
	
	private Level level;
	
	private long clientId;
	
	private String userName;
	
	private HashMap<Long, String> alreadySelectedCharacters;
	
	public GetLevelResult() {}
	
	public GetLevelResult(long clientId, String userName, Level level, HashMap<Long, String> alreadySelectedCharacters) {
		this.clientId = clientId;
		this.userName = userName;
		this.level = level;
		this.alreadySelectedCharacters = alreadySelectedCharacters;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public HashMap<Long, String> getAlreadySelectedCharacters() {
		return alreadySelectedCharacters;
	}

	public void setAlreadySelectedCharacters(HashMap<Long, String> alreadySelectedCharacters) {
		this.alreadySelectedCharacters = alreadySelectedCharacters;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public void processResult(EngineInterface engineInterface) throws EngineException {
		engineInterface.setUserName(userName);
		engineInterface.initLevel(level, alreadySelectedCharacters);
		engineInterface.setClientId(clientId);
	}
}

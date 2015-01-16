package org.stofkat.battleround.core;

import java.util.HashMap;
import java.util.List;

import org.stofkat.battleround.common.Update;
import org.stofkat.battleround.common.level.structure.Level;
import org.stofkat.battleround.shared.dispatch.results.SelectCharacterResult.ResultValue;

public interface EngineInterface {
	void initLevel(Level level, HashMap<Long, String> updates) throws EngineException;
	
	Level getLevel();
	
	void updateLastUpdateNumber(long updateNumber);
	
	void setClientId(long clientId);
	
	void setCharacterSelectedResult(ResultValue result, List<Update> updates);
	
	void addPlayer(long characterId, String playerName);
	
	void addChatMessage(long characterId, String chatMessage);
	
	void close();
	
	void handleError(String errorMessage);
	
	void setUserName(String userName);
}

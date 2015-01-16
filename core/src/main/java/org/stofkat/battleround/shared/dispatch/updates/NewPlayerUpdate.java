package org.stofkat.battleround.shared.dispatch.updates;

import org.stofkat.battleround.common.UpdateResult;
import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineInterface;

public class NewPlayerUpdate extends UpdateResult {
	private static final long serialVersionUID = 100L;
	
	private long characterId;
	
	private String playerName;
	
	public NewPlayerUpdate() {}
	
	public NewPlayerUpdate(long updateNumber, long characterId, String playerName) {
		this.characterId = characterId;
		this.playerName = playerName;
		this.updateNumber = updateNumber;
	}
	
	@Override
	public void processResult(EngineInterface engineInterface) throws EngineException {
		engineInterface.addPlayer(characterId, playerName);
		engineInterface.updateLastUpdateNumber(getUpdateNumber());
	}

	public long getCharacterId() {
		return characterId;
	}

	public void setCharacterId(long characterId) {
		this.characterId = characterId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}

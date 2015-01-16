package org.stofkat.battleround.shared.dispatch.actions;

import org.stofkat.battleround.shared.dispatch.results.SelectCharacterResult;

public class SelectCharacterAction extends AbstractAction<SelectCharacterResult> {
	private static final long serialVersionUID = 100L;
	
	private long clientId;
	
	private long characterId;
	
	public SelectCharacterAction() {}
	
	public SelectCharacterAction(long clientId, long characterId, long lastUpdateNumber) {
		super(lastUpdateNumber);
		this.clientId = clientId;
		this.characterId = characterId;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public long getCharacterId() {
		return characterId;
	}

	public void setCharacterId(long characterId) {
		this.characterId = characterId;
	}
}

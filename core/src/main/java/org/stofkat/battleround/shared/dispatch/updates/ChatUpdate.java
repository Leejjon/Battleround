package org.stofkat.battleround.shared.dispatch.updates;

import org.stofkat.battleround.common.UpdateResult;
import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineInterface;

public class ChatUpdate extends UpdateResult {
	private static final long serialVersionUID = 1L;
	
	private long characterId;
	
	private String chatMessage;
	
	public ChatUpdate() {}
	
	public ChatUpdate(long updateNumber, long characterId, String chatMessage) {
		this.updateNumber = updateNumber;
		this.characterId = characterId;
		this.chatMessage = chatMessage;
	}
	
	@Override
	public void processResult(EngineInterface engineInterface) throws EngineException {
		engineInterface.addChatMessage(characterId, chatMessage);
		engineInterface.updateLastUpdateNumber(getUpdateNumber());
	}

	public long getCharacterId() {
		return characterId;
	}

	public void setCharacterId(long characterId) {
		this.characterId = characterId;
	}

	public String getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(String chatMessage) {
		this.chatMessage = chatMessage;
	}
}

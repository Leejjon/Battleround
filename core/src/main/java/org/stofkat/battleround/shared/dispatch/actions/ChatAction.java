package org.stofkat.battleround.shared.dispatch.actions;

import org.stofkat.battleround.shared.dispatch.results.CheckForUpdatesResult;

public class ChatAction extends AbstractAction<CheckForUpdatesResult> {
	private static final long serialVersionUID = 100L;
	
	private long clientId;
	
	private String chatMessage;
	
	public ChatAction() {}
	
	public ChatAction(long clientId, long lastUpdateNumber, String chatMessage) {
		super(lastUpdateNumber);
		this.clientId = clientId;
		this.chatMessage = chatMessage;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public String getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(String chatMessage) {
		this.chatMessage = chatMessage;
	}
}

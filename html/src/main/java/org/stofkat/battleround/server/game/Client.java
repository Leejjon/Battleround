package org.stofkat.battleround.server.game;

public class Client {
	private long clientId;
	private Long characterId = null;
	private String userName;
	
	public Client(long clientId, String userName) {
		this.clientId = clientId;
		this.userName = userName;
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public Long getCharacterId() {
		return characterId;
	}

	public void setCharacterId(Long characterId) {
		this.characterId = characterId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}

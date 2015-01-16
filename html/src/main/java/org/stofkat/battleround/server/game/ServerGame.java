package org.stofkat.battleround.server.game;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.stofkat.battleround.common.Update;
import org.stofkat.battleround.common.level.structure.Level;
import org.stofkat.battleround.shared.dispatch.exceptions.ActionException;
import org.stofkat.battleround.shared.dispatch.exceptions.ClientIdInvalidException;
import org.stofkat.battleround.shared.dispatch.exceptions.GameFullException;
import org.stofkat.battleround.shared.dispatch.exceptions.InvalidLevelException;
import org.stofkat.battleround.shared.dispatch.updates.ChatUpdate;
import org.stofkat.battleround.shared.dispatch.updates.NewPlayerUpdate;

/**
 * Since we don't have a database yet, this class is the database for now. Every
 * time a client does a call, he loads an instance from this class from the
 * HttpSession, does some modifications to this object and stores it again.
 * 
 * @author Leejjon
 */
public class ServerGame {
	public static final String serverKey = "servergame";

	private Client[] clients;

	private Level level;

	private long gameId = 0;

	private long roundNumber = 0;

	private long lastUpdateNumber = 0;
	
	private HashMap<Long, Update> updates = new HashMap<Long, Update>();

	public ServerGame(String levelId) throws ActionException {
		try {
			InputStream inputStream = ServerGame.class.getResourceAsStream(levelId + ".xml");
	
			Serializer serializer = new Persister();
	
			level = serializer.read(Level.class, inputStream);
	
			inputStream.close();
	
			clients = new Client[level.getMaxPlayers()];
		} catch (Exception e) {
			throw new InvalidLevelException();
		}
	}

	/**
	 * Adds the client to the client list if there's a free spot.
	 * 
	 * @param userName
	 *            The nickname of the player.
	 * @return The clientId. This ID needs to be passed on with every call from
	 *         now on, to let the server know what client wants to do what.
	 * @throws GameFullException
	 */
	public long joinGame(String userName) throws GameFullException {
		for (int i = 0; i < clients.length; i++) {
			if (clients[i] == null) {
				clients[i] = new Client(i, userName);
				return (long) i;
			}
		}
		throw new GameFullException();
	}

	/**
	 * Pick a character from the available characters in a level. This method
	 * also checks whether the prefered character has already been chosen by
	 * another player.
	 * 
	 * @param clientId
	 * @param characterId
	 * @return True if the character was picked succesful, false if it was already in use.
	 * @throws ClientIdInvalidException If the client id isn't right, throw an exception.
	 */
	public boolean pickCharacter(Client thisClient, long characterId) throws ClientIdInvalidException {
		for (Client client : clients) {
			if (client != null && client.getCharacterId() != null 
					&& client.getCharacterId().equals(new Long(characterId))) {
				return false;
			}
		}

		thisClient.setCharacterId(new Long(characterId));
		
		NewPlayerUpdate newPlayerUpdate = new NewPlayerUpdate(++lastUpdateNumber, characterId, thisClient.getUserName());
		updates.put(lastUpdateNumber, newPlayerUpdate);
		
		return true;
	}
	
	public void newChatMessage(long clientId, String chatMessage) throws ClientIdInvalidException {
		Client client = getClient(clientId);
		
		ChatUpdate chatUpdate = new ChatUpdate(++lastUpdateNumber, client.getCharacterId(), chatMessage);
		updates.put(lastUpdateNumber, chatUpdate);
	}

	public List<Update> getUpdatesAfter(long lastUpdateNumberFromClient) {
		ArrayList<Update> updatesForTheClient = new ArrayList<Update>();
		
		for (Entry<Long, Update> entry : updates.entrySet()) {
			if (entry.getKey().longValue() > lastUpdateNumberFromClient) {
				updatesForTheClient.add(entry.getValue());
			}
		}
		
		return updatesForTheClient;
	}
	
	/**
	 * Get the current players that have already selected their character.
	 * 
	 * @return A hashmap containing characterId/username entries.
	 */
	public HashMap<Long, String> getCurrentPlayers() {
		HashMap<Long, String> map = new HashMap<Long, String>();

		for (int i = 0; i < clients.length; i++) {
			if (clients[i] != null && clients[i].getCharacterId() != null) {
				map.put(clients[i].getCharacterId(), clients[i].getUserName());
			}
		}
		return map;
	}

	public Client getClient(long clientId) throws ClientIdInvalidException {
		for (Client client : clients) {
			if (client.getClientId() == clientId) {
				return client;
			}
		}
		throw new ClientIdInvalidException();
	}

	/**
	 * If a request comes in from a client to do something in the game, we check
	 * if the client is really in the game.
	 * 
	 * @param clientId
	 *            The id of the client.
	 * @return True or false depending on whether the client is in the client
	 *         list.
	 */
	public boolean isClientAuthorized(long clientId) {
		for (Client client : clients) {
			if (client.getCharacterId() == clientId) {
				return true;
			}
		}
		return false;
	}
	
	public Level getLevel() {
		return level;
	}

	public long getGameId() {
		return gameId;
	}
}

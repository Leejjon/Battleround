package org.stofkat.battleround.core;

import org.stofkat.battleround.common.Direction;
import org.stofkat.battleround.core.util.Point;

public class Player extends GameObject {
	private String characterName;
	private String playerName;
	
	public Player(long id, Point<Integer> positionOnTiles, Direction direction, SpriteAnimationDefinition idleSpriteAnimationDefinition, String characterName, String playerName) {
		super(id, positionOnTiles, direction, idleSpriteAnimationDefinition);
		this.characterName = characterName;
		this.playerName = playerName;
	}

	public String getCharacterName() {
		return characterName;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	/**
	 * Compare every value of this player.
	 * 
	 * @param other The player we're comparing ourselves to.
	 * @return True if we're equal, false if we're different.
	 */
	public boolean equals(Object other) {
		if (other instanceof Player) {
			Player player = (Player) other;
			if (characterName.equals(player.getCharacterName()) && playerName.equals(player.getPlayerName()) &&
				super.getId() == player.getId() && super.positionOnTiles.equals(player.positionOnTiles)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
}

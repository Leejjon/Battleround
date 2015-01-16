package org.stofkat.battleround.common.level.structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "level")
public class Level implements Serializable {
	private static final long serialVersionUID = 100L;
	
	@Element(required = true)
	private String title;

	@Element(required = true)
	private String description;

	@Element(required = true)
	private String designer;

	@Element(required = false)
	private int minPlayers = 3; // The default is 3, but it can be overridden.

	@Element(required = false)
	private int maxPlayers = 10;

	@Element(required = false)
	private int levelWidth = 16;

	@Element(required = false)
	private int levelHeight = 16;
	
	private transient Map<Integer, CharacterDefinition> characterDefinitionsMap;
	
	@ElementList(name = "characterDefinitions", required = true)
	private ArrayList<CharacterDefinition> characterDefinitions;

	@ElementList(name = "monsterDefinitions", required = true)
	private ArrayList<MonsterDefinition> monsterDefinitions;

	@ElementList(name = "objectDefinitions", required = true)
	private ArrayList<PassiveObjectDefinition> objectDefinitions;

	@ElementList(name = "characters", required = true)
	private ArrayList<Character> characters;

	@ElementList(name = "monsters", required = true)
	private ArrayList<Monster> monsters;

	@ElementList(name = "objects", required = true)
	private ArrayList<PassiveObject> passiveObjects;
	
	@ElementList(name = "sprites", required = true)
	private ArrayList<SpriteSheet> spriteSheets;
	
	public Level() {
		characterDefinitionsMap = new HashMap<Integer, CharacterDefinition>();
	}
	
	private void buildCharacterDefinitionMap() {
		for (CharacterDefinition playerDefinition : getCharacterDefinitions()) {
			characterDefinitionsMap.put(playerDefinition.getDefinitionId(), playerDefinition);
		}
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDesigner() {
		return designer;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public int getLevelWidth() {
		return levelWidth;
	}

	public void setLevelWidth(int levelWidth) {
		this.levelWidth = levelWidth;
	}

	public int getLevelHeight() {
		return levelHeight;
	}

	public void setLevelHeight(int levelHeight) {
		this.levelHeight = levelHeight;
	}

	public ArrayList<CharacterDefinition> getCharacterDefinitions() {
		return characterDefinitions;
	}

	public void setCharacterDefinitions(ArrayList<CharacterDefinition> characterDefinitions) {
		this.characterDefinitions = characterDefinitions;
	}
	
	public CharacterDefinition getCharacterDefinition(int definitionId) {
		if (characterDefinitionsMap == null) {
			characterDefinitionsMap = new HashMap<Integer, CharacterDefinition>();
		}
		
		if (characterDefinitionsMap.size() != getCharacterDefinitions().size()) {
			buildCharacterDefinitionMap();
		}
		return characterDefinitionsMap.get(new Integer(definitionId));
	}
	
	public ArrayList<MonsterDefinition> getMonsterDefinitions() {
		return monsterDefinitions;
	}

	public void setMonsterDefinitions(ArrayList<MonsterDefinition> monsterDefinitions) {
		this.monsterDefinitions = monsterDefinitions;
	}

	public ArrayList<PassiveObjectDefinition> getPassiveObjectDefinitions() {
		return objectDefinitions;
	}

	public void setPassiveObjectDefinitions(ArrayList<PassiveObjectDefinition> objectDefinitions) {
		this.objectDefinitions = objectDefinitions;
	}

	public ArrayList<Character> getCharacters() {
		return characters;
	}

	public void setPlayers(ArrayList<Character> players) {
		this.characters = players;
	}

	public ArrayList<Monster> getMonsters() {
		return monsters;
	}

	public void setMonsters(ArrayList<Monster> monsters) {
		this.monsters = monsters;
	}

	public ArrayList<PassiveObject> getPassiveObjects() {
		return passiveObjects;
	}

	public void setPassiveObjects(ArrayList<PassiveObject> objects) {
		this.passiveObjects = objects;
	}

	public ArrayList<PassiveObjectDefinition> getObjectDefinitions() {
		return objectDefinitions;
	}

	public void setObjectDefinitions(ArrayList<PassiveObjectDefinition> objectDefinitions) {
		this.objectDefinitions = objectDefinitions;
	}

	public ArrayList<SpriteSheet> getSpriteSheets() {
		return spriteSheets;
	}

	public void setSpriteSheets(ArrayList<SpriteSheet> spriteSheets) {
		this.spriteSheets = spriteSheets;
	}
}

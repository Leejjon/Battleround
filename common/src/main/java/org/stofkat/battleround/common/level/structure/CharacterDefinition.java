package org.stofkat.battleround.common.level.structure;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class CharacterDefinition implements Serializable {
	private static final long serialVersionUID = 100L;

	@Element(required = true)
	private int definitionId;
	
	@Element(required = true)
	private int idleSpriteSheetId;
	
	@Element(required = true)
	private String defaultName;
	
	@Element(required = true)
	private int defaultLevel;
	
	@Element(required = true)
	private Stats defaultStats;
	
	@Attribute(name = "canDoMelee", required = false)
	private boolean canDoMelee = true;
	
	@Attribute(name = "canDoRange", required = false)
	private boolean canDoRange = true;
	
	@Attribute(name = "canDoGrenade", required = false)
	private boolean canDoGrenade = true;
	
	public int getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(int definitionId) {
		this.definitionId = definitionId;
	}

	public int getIdleSpriteSheetId() {
		return idleSpriteSheetId;
	}

	public void setIdleSpriteSheetId(int idleSpriteSheetId) {
		this.idleSpriteSheetId = idleSpriteSheetId;
	}

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public int getDefaultLevel() {
		return defaultLevel;
	}

	public void setDefaultLevel(int defaultLevel) {
		this.defaultLevel = defaultLevel;
	}

	public Stats getDefaultStats() {
		return defaultStats;
	}

	public void setDefaultStats(Stats defaultStats) {
		this.defaultStats = defaultStats;
	}

	public boolean getCanDoMelee() {
		return canDoMelee;
	}

	public void setCanDoMelee(Boolean canDoMelee) {
		this.canDoMelee = canDoMelee;
	}

	public boolean getCanDoRange() {
		return canDoRange;
	}

	public void setCanDoRange(Boolean canDoRange) {
		this.canDoRange = canDoRange;
	}

	public boolean getCanDoGrenade() {
		return canDoGrenade;
	}

	public void setCanDoGrenade(Boolean canDoMagic) {
		this.canDoGrenade = canDoMagic;
	}
}

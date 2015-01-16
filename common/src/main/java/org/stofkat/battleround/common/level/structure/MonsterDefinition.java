package org.stofkat.battleround.common.level.structure;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class MonsterDefinition implements Serializable {
	private static final long serialVersionUID = 100L;

	@Element(required = true)
	private int definitionId;
	
	@Element(required = true)
	private String spritePath;
	
	@Element(required = true)
	private String defaultName;
	
	@Element(required = true)
	private int defaultLevelRating;
	
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

	public String getSpritePath() {
		return spritePath;
	}

	public void setSpritePath(String spritePath) {
		this.spritePath = spritePath;
	}

	public String getDefaultName() {
		return defaultName;
	}

	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

	public int getDefaultLevelRating() {
		return defaultLevelRating;
	}

	public void setDefaultLevelRating(int defaultLevelRating) {
		this.defaultLevelRating = defaultLevelRating;
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

	public void setCanDoMelee(boolean canDoMelee) {
		this.canDoMelee = canDoMelee;
	}

	public boolean getCanDoRange() {
		return canDoRange;
	}

	public void setCanDoRange(boolean canDoRange) {
		this.canDoRange = canDoRange;
	}

	public boolean getCanDoGrenade() {
		return canDoGrenade;
	}

	public void setCanDoGrenade(boolean canDoGrenade) {
		this.canDoGrenade = canDoGrenade;
	}
}

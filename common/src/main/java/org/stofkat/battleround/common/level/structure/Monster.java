package org.stofkat.battleround.common.level.structure;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Monster implements Serializable {
	private static final long serialVersionUID = 100L;
	
	@Element(required = true)
	private int id;
	
	@Element(required = true)
	private int definitionId;
	
	@Element(required = true)
	private int xCoordinate;
	
	@Element(required = true)
	private int yCoordinate;
	
	@Element(required = false)
	private String customName;
	
	@Element(required = false)
	private int customLevelRating;
	
	@Element(required = false)
	private Stats customStats;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(int definitionId) {
		this.definitionId = definitionId;
	}

	public int getxCoordinate() {
		return xCoordinate;
	}

	public void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public int getyCoordinate() {
		return yCoordinate;
	}

	public void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public int getCustomLevelRating() {
		return customLevelRating;
	}

	public void setCustomLevelRating(int customLevelRating) {
		this.customLevelRating = customLevelRating;
	}

	public Stats getCustomStats() {
		return customStats;
	}

	public void setCustomStats(Stats customStats) {
		this.customStats = customStats;
	}
}

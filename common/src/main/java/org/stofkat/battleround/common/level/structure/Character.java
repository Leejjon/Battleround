package org.stofkat.battleround.common.level.structure;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Character implements Serializable {
	private static final long serialVersionUID = 100L;
	
	@Element(name = "id", required = true)
	private long id;
	
	@Element(required = true)
	private int definitionId;
	
	@Element(required = true)
	private int xCoordinate;
	
	@Element(required = true)
	private int yCoordinate;
	
	@Element(required = false)
	private int direction = 0;
	
	@Element(required = false)
	private String customName;
	
	@Element(required = false)
	private int customLevel;
	
	@Element(required = false)
	private Stats customStats;

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public int getCustomLevel() {
		return customLevel;
	}

	public void setCustomLevel(int customLevel) {
		this.customLevel = customLevel;
	}

	public Stats getCustomStats() {
		return customStats;
	}

	public void setCustomStats(Stats customStats) {
		this.customStats = customStats;
	}
}

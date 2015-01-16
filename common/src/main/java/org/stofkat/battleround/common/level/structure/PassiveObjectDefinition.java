package org.stofkat.battleround.common.level.structure;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class PassiveObjectDefinition implements Serializable {
	private static final long serialVersionUID = 100L;
	
	@Element(required = true)
	private int definitionId;
	
	@Element(required = true)
	private String spritePath;
	
	@Element(required = true)
	private String defaultName;

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
}

package org.stofkat.battleround.common.level.structure;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "spriteSheet")
public class SpriteSheet implements Serializable {
	private static final long serialVersionUID = 100L;
	
	@Element(required = true)
	private int spriteSheetId;
	
	@Element(required = true)
	private String path;
	
	@Element(required = true)
	private int singleSpriteWidth;
	
	@Element(required = true)
	private int singleSpriteHeight;
	
	@Element(required = false)
	private String order;
	
	public SpriteSheet() {};
	
	public int getSpriteSheetId() {
		return spriteSheetId;
	}

	public void setSpriteSheetId(int spriteSheetId) {
		this.spriteSheetId = spriteSheetId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getSingleSpriteWidth() {
		return singleSpriteWidth;
	}

	public void setSingleSpriteWidth(int singleSpriteWidth) {
		this.singleSpriteWidth = singleSpriteWidth;
	}

	public int getSingleSpriteHeight() {
		return singleSpriteHeight;
	}

	public void setSingleSpriteHeight(int singleSpriteHeight) {
		this.singleSpriteHeight = singleSpriteHeight;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
}

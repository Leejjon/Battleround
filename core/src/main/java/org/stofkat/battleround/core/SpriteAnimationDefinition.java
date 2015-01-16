package org.stofkat.battleround.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.stofkat.battleround.common.Direction;
import org.stofkat.battleround.core.EngineException.EngineExceptions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * There's a lot of animations for that can be used by multiple items, monsters
 * or players. So we only create one SpriteAnimationDefinition object that reads
 * the animation from the sprite sheet, and calculates what directions are
 * supported. This SpriteAnimationDefinition objects will be stored in a list of
 * the field, and objects in it can be referred to from any GameObject.
 * 
 * @author Leejjon
 */
public class SpriteAnimationDefinition {
	private final int singleSpriteWidth;
	private final int singleSpriteHeight;
	private int numberOfAnimations;
	private List<Direction> supportedDirections;
	private Texture spriteSheetTexture;
	private HashMap<Direction, List<Sprite>> sprites;

	public SpriteAnimationDefinition(Texture spriteSheetTexture, int singleSpriteWidth, int singleSpriteHeight, String order) throws EngineException {
		this.spriteSheetTexture = spriteSheetTexture;
		this.spriteSheetTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.singleSpriteWidth = singleSpriteWidth;
		this.singleSpriteHeight = singleSpriteHeight;
		/*
		 * The width and height of the texture is always a power of two, and so does the
		 * width and height of a single sprite.
		 */
		if (singleSpriteWidth == 0 || (spriteSheetTexture.getWidth() % singleSpriteWidth) != 0) {
			throw new EngineException(EngineExceptions.SPRITESHEET_DATA_CONTAINS_INVALID_SPRITE_WIDTH);
		} else if (singleSpriteHeight == 0 || (spriteSheetTexture.getHeight() % singleSpriteHeight) != 0) {
			throw new EngineException(EngineExceptions.SPRITESHEET_DATA_CONTAINS_INVALID_SPRITE_HEIGHT);
		}

		numberOfAnimations = spriteSheetTexture.getWidth() / singleSpriteWidth;

		int directionsCount = spriteSheetTexture.getHeight() / singleSpriteHeight;
		// Every sprite row contains the animation for the sprite action in a
		// direction. There's a maximum of eight directions supported, so if the
		// width of the sprite sheet is larger than eight times the sprite
		// height, it's invalid.
		if (directionsCount >= Direction.values().length) {
			throw new EngineException(EngineExceptions.SPRITESHEET_DATA_CONTAINS_INVALID_SPRITE_HEIGHT);
		}
		
		// Create and fill the supportedDirections list.
		sprites = new HashMap<Direction, List<Sprite>>();
		supportedDirections = new ArrayList<Direction>();
		
		int y = 0;
		for (int i = 0; i < directionsCount; i++) {
			Direction supportedDirection = Direction.getDirection(i);
			supportedDirections.add(supportedDirection);
			
			sprites.put(supportedDirection, getAnimationSpritesFromRow(y, order));
			y += singleSpriteHeight;
		}
		
	}
	
	/**
	 * Read a row of sprites from the sprite sheet.
	 * @param y
	 * @return
	 * @throws EngineException 
	 */
	private List<Sprite> getAnimationSpritesFromRow(int y, String order) throws EngineException {
		List<Sprite> spritesForADirection = new ArrayList<Sprite>(); 
		
		int x = 0;
		for (int i = 0; i < numberOfAnimations; i++) {
			TextureRegion textureRegion = new TextureRegion(spriteSheetTexture, x, y, singleSpriteWidth, singleSpriteHeight);
			Sprite sprite = new Sprite(textureRegion);
			
			if (singleSpriteHeight / 2 == singleSpriteWidth) { // If the Height is twice as large, it's legal too.
				sprite.setSize(Tile.width, Tile.width * 2f);
			} else { // If the width and height of sprites are the same.
				sprite.setSize(Tile.width, Tile.width);
			}
			
			spritesForADirection.add(sprite);
			
			x += singleSpriteWidth;
		}
		
		if (order != null && order.contains(",")) {
			ArrayList<Sprite> orderedSpritesForADirection = new ArrayList<Sprite>();
			String[] orderValuesAsStrings = order.split(",");
			for (int i = 0; i < orderValuesAsStrings.length; i++) {
				try {
					// The order contains stuff like 1,1,2,2,3 etc. They order starts counting at 1 instead of 0 like Java does for lists.
					orderedSpritesForADirection.add(spritesForADirection.get(Integer.parseInt(orderValuesAsStrings[i]) - 1));
				} catch (NumberFormatException e) {
					Gdx.app.error("engine", EngineExceptions.SPRITESHEET_ORDER_CONTAINS_INVALID_CHARACTER.getMessage(), e);
					throw new EngineException(EngineExceptions.SPRITESHEET_ORDER_CONTAINS_INVALID_CHARACTER);
				}
			}
			numberOfAnimations = orderedSpritesForADirection.size();
			return orderedSpritesForADirection;
		} else {
			return spritesForADirection;
		}
	}

	public boolean isDirectionSupported(Direction direction) {
		return supportedDirections.contains(direction);
	}
	
	public Sprite getAnimationSprite(Direction direction, int animationNumber) {
		return sprites.get(direction).get(animationNumber);
	}

	public int getNumberOfAnimations() {
		return numberOfAnimations;
	}
}

package org.stofkat.battleround.core;

import org.stofkat.battleround.common.Direction;
import org.stofkat.battleround.core.util.Point;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameObject {
	private long id;
	protected Point<Float> relativeLocation;
	protected Point<Integer> positionOnTiles;
	protected SpriteAnimationDefinition idleSpriteAnimationDefinition;
	protected Direction direction;
	private int animationNumber;
	
	public GameObject(long id, Point<Integer> positionOnTiles, Direction direction, SpriteAnimationDefinition idleSpriteAnimationDefinition) {
		this.id = id;
		this.positionOnTiles = positionOnTiles;
		this.direction = direction;
		this.idleSpriteAnimationDefinition = idleSpriteAnimationDefinition;
	}
	
	public void updateRelativeLocation(Point<Float> location) {
		this.relativeLocation = location;
	}
	
	public float getAbsoluteY() {
		return relativeLocation.getY();
	}
	
	public void draw(SpriteBatch spriteBatch, float parentAlpha) {
		if (animationNumber == idleSpriteAnimationDefinition.getNumberOfAnimations()) {
			animationNumber = 0;
		}
		
		Sprite sprite = idleSpriteAnimationDefinition.getAnimationSprite(direction, animationNumber);
		
		// Make sure the middle of the sprite is at the exact location.
		// Also draw it a little lower so the sprite starts at the bottom of the tile.
		sprite.setPosition(relativeLocation.getX() - (sprite.getWidth() / 2f), relativeLocation.getY() - (Tile.height / 2f));
		sprite.draw(spriteBatch);
		
		animationNumber++;
	}

	public long getId() {
		return id;
	}
	
	public Point<Integer> getPositionOnTiles() {
		return positionOnTiles;
	}
}

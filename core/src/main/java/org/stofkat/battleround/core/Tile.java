package org.stofkat.battleround.core;

import org.stofkat.battleround.core.util.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Tile {
	public static final float width = 0.2f;
	public static final float height = 0.1f;
	private GameObject objectOnThisTile;
	private boolean selected = false;
	private Point<Float> relativeLocation;
	private Point<Float> leftCorner;
	private Point<Float> rightCorner;
	private Point<Float> topCorner;
	private Point<Float> bottomCorner;
	
	public Tile(Point<Float> relativeLocation) {
		this.relativeLocation = relativeLocation;
		
		leftCorner = new Point<Float>(relativeLocation.getX() - (width / 2f), relativeLocation.getY());
		rightCorner = new Point<Float>(relativeLocation.getX() + (width / 2f), relativeLocation.getY());
		topCorner = new Point<Float>(relativeLocation.getX(), relativeLocation.getY() - (height / 2f));
		bottomCorner = new Point<Float>(relativeLocation.getX(), relativeLocation.getY() + (height / 2f));
	}
	
	/**
	 * Draws the border lines of the tile if debug mode is on, and updates the
	 * coordinates of any gameobject on the tile.
	 * 
	 * @param x
	 * @param y
	 * @param tileRendererBatch
	 */
	public void drawDebugLines(ShapeRenderer tileRendererBatch) {
		if (Engine.isDebugModeOn()) {
			tileRendererBatch.setColor(Color.BLACK);

			/*
			 * The code belows draws the lines around the tile.
			 * 
			 * Tile: c / \ d 
			 *       b \ / a
			 */

			// Draws the top / side of the tile (c).
			tileRendererBatch.line(topCorner.getX(), topCorner.getY(), leftCorner.getX(), leftCorner.getY());
			// Draws the top \ side (d).
			tileRendererBatch.line(topCorner.getX(), topCorner.getY(), rightCorner.getX(), rightCorner.getY());
			// Draws the bottom \ side (b).
			tileRendererBatch.line(bottomCorner.getX(), bottomCorner.getY(), leftCorner.getX(), leftCorner.getY());
			// Draws the bottom / side (a).
			tileRendererBatch.line(bottomCorner.getX(), bottomCorner.getY(), rightCorner.getX(), rightCorner.getY());
		}

		GameObject objectOnTile = getObjectOnThisTile();
		if (objectOnTile != null) {
			objectOnTile.updateRelativeLocation(relativeLocation);
		}
	}

	public boolean select(Point<Float> selectedLocation) {
		// Left half of the tile.
		if (xCoordinateIsWithinLeftHalfOfTile(selectedLocation.getX()) && isYCoordinateLowerThanSideC(selectedLocation) && isYCoordinateHigherThanSideB(selectedLocation)) {
			Gdx.app.log("battleround", "Selected location: " + selectedLocation.getX() + ", " + selectedLocation.getY());
			Gdx.app.log("battleround", "Relative location:" + relativeLocation.getX() + ", " + relativeLocation.getY());
			selected = true;
			return true;
		} else if ( // Right half of the tile.
		xCoordinateIsWithinRightHalfOfTile(selectedLocation.getX()) && isYCoordinateLowerThanSideD(selectedLocation) && isYCoordinateHigherThanSideA(selectedLocation)) {
			selected = true;
			return true;
		}

		return false; // TODO: Replace the above calculation by:
//		WithinTileCalculation isThisTileSelected = new WithinTileCalculation(selectedLocation, leftCorner, topCorner, rightCorner, bottomCorner, true);
//		return isThisTileSelected.calculate();
	}

	private boolean xCoordinateIsWithinLeftHalfOfTile(float xCoordinate) {
		return (leftCorner.getX() <= xCoordinate && xCoordinate <= relativeLocation.getX());
	}

	private boolean xCoordinateIsWithinRightHalfOfTile(float xCoordinate) {
		return (relativeLocation.getX() <= xCoordinate && xCoordinate <= rightCorner.getX());
	}

	private boolean isYCoordinateLowerThanSideC(Point<Float> selectedLocation) {
		// Line c is a linear function that goes upward in the form of y=ax+b

		// Calculate a in y=ax+b, which is the scope.
		// (hellingsgetal/richtingscooefficient)
		// The scope is positive because this line is going up.
		float scope = (height / 2f) / (width / 2f);

		// Calculate b in y=ax+b, which can be calculated by entering the
		// coordinate of one of the corners of the tile.
		// y=ax+b (-ax
		// y-ax = b

		// We use the topCorner from side C. The y axis in libgdx is flipped
		// when compared to a normal x/y axis system, so thats why I'm doing *-1
		// before using the topCorner.getY().
		float b = (-1f * topCorner.getY()) - (scope * topCorner.getX());

		// Calculate whether the Y in the coordinate is smaller than the
		// boundary.
		float yBoundary = (scope * selectedLocation.getX()) + b;
		return (selectedLocation.getY() <= yBoundary);
	}

	private boolean isYCoordinateHigherThanSideB(Point<Float> selectedLocation) {
		// Line b is a linear function that goes downward in the form of y=-ax+b

		// Calculate the scope again. We make it negative because
		// it's going down.
		float scope = -1 * ((height / 2f) / (width / 2f));

		// Calculate b in y=ax+b on the same way as with side C.
		float b = (-1f * bottomCorner.getY()) - (scope * bottomCorner.getX());

		// Calculate whether the Y in the coordinate is smaller than the
		// boundary.
		float yBoundary = (scope * selectedLocation.getX()) + b;
		return (selectedLocation.getY() >= yBoundary);
	}

	private boolean isYCoordinateLowerThanSideD(Point<Float> selectedLocation) {
		// Line d is a linear function that goes downward in the form of y=-ax+b
		
		// Calculate the scope again. Again we make it negative.
		float scope = -1 * ((height / 2f) / (width / 2f));
		
		// Calculate d like y=ax+b
		float b = (-1f * topCorner.getY()) - (scope * topCorner.getX());
		
		float yBoundary = (scope * selectedLocation.getX()) + b;
		return (selectedLocation.getY() <= yBoundary);
	}
	
	private boolean isYCoordinateHigherThanSideA(Point<Float> selectedLocation) {
		// Line a is a linear function that goes upward in the form of y=ax+b
		
		// Calculate the scope again. 
		float scope = (height / 2f) / (width / 2f);
		
		// Calculate b like y=ax+b
		float b = (-1f * bottomCorner.getY()) - (scope * bottomCorner.getX());
		
		float yBoundary = (scope * selectedLocation.getX()) + b;
		return (selectedLocation.getY() >= yBoundary);
	}

	public void paintSelectionIndicator(ShapeRenderer selectedTileRenderer) {
		selectedTileRenderer.triangle(leftCorner.getX(), leftCorner.getY(), topCorner.getX(), topCorner.getY(), rightCorner.getX(), rightCorner.getY());
		selectedTileRenderer.triangle(leftCorner.getX(), leftCorner.getY(), bottomCorner.getX(), bottomCorner.getY(), rightCorner.getX(), rightCorner.getY());
	}
	
	public Point<Float> getRelativeLocation() {
		return relativeLocation;
	}

	public GameObject getObjectOnThisTile() {
		return objectOnThisTile;
	}

	public void setObjectOnThisTile(GameObject objectOnThisTile) {
		// TODO: Throw an exception if there's still an object on this tile,
		// because we can't have two!
		this.objectOnThisTile = objectOnThisTile;
	}

	public boolean isSelected() {
		return selected;
	}

	public void deSelect() {
		selected = false;
	}
}

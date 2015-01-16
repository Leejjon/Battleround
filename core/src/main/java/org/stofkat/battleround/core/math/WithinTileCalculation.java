package org.stofkat.battleround.core.math;

import org.stofkat.battleround.core.util.Point;

public class WithinTileCalculation {
	private Point<Float> position, mostWestern, mostNorthern, mostEastern, mostSouthern, middlePoint;

	// For detecting whether the camera goes out of the field, it's not really
	// nice if the camera stops at the exact edge of the field. If this boundary
	// is set, the allowed area will be expanded by the value of this variable.
	private float fieldBoundaryX;
	
	private boolean yAxisInverted;
	
	public WithinTileCalculation(Point<Float> position, Point<Float> mostWestern, Point<Float> mostNorthern, Point<Float> mostEastern, Point<Float> mostSouthern, boolean yAxisInverted) {
		this(position, mostWestern, mostNorthern, mostEastern, mostSouthern, yAxisInverted, 0f);
	}
	
	public WithinTileCalculation(Point<Float> position, Point<Float> mostWestern, Point<Float> mostNorthern, Point<Float> mostEastern, Point<Float> mostSouthern, boolean yAxisInverted, float fieldBoundaryX) {
		this.position = position;
		this.mostWestern = mostWestern;
		this.mostNorthern = mostNorthern;
		this.mostEastern = mostEastern;
		this.mostSouthern = mostSouthern;
		this.yAxisInverted = yAxisInverted;
		this.fieldBoundaryX = fieldBoundaryX;
		
		float totalWidth = mostEastern.getX() - mostWestern.getX(); 
		float middleX = mostWestern.getX() + (totalWidth / 2f);
		float totalHeight = mostNorthern.getY() - mostSouthern.getY();
		float middleY = mostNorthern.getX() + (totalHeight / 2f);
		
		// Calculate the coordinate of the exact middle of the field.
		middlePoint = new Point<Float>(middleX, middleY); 
	}
	
	/*
	 * Sides of a tile will be called a, b, c or d as displayed in ASCII art below.
	 * 
	 * Tile: c / \ d 
	 *       b \ / a
	 */
	public boolean calculate() {
		boolean northWest = isGoingOutOfNorthWestBoundary(position, middlePoint);
		boolean southWest = isGoingOutOfSouthWestBoundary(position, middlePoint);
		boolean northEast = isGoingOutOfNorthEastBoundary(position, middlePoint);
		boolean southEast = isGoingOutOfSouthEastBoundary(position, middlePoint);
		if (northWest || northEast || southWest || southEast) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isGoingOutOfNorthWestBoundary(Point<Float> futureCameraLocation, Point<Float> middlePoint) {
		// Use the differences between the most western tile, and the most northern tile.
		
		float height = (middlePoint.getY() - mostNorthern.getY());
		float width = (middlePoint.getX() - mostWestern.getX());
		float scope = height / width;
		
		// Calculate b
		// y=ax+b (-ax
		// y-ax = b
		// The y axis in libgdx is flipped compared to a normal x/y axis system, so thats why I'm doing *-1
		float b = (mostNorthern.getY() + fieldBoundaryX) - (scope * mostNorthern.getX());
		float yBoundary = (scope * futureCameraLocation.getX()) + b;
		
		if (yAxisInverted) {
			return (futureCameraLocation.getY() < yBoundary);
		} else {
			return (futureCameraLocation.getY() > yBoundary);
		}
	}
	
	private boolean isGoingOutOfSouthWestBoundary(Point<Float> futureCameraLocation, Point<Float> middlePoint) {
		float height = (middlePoint.getY() + mostSouthern.getY());
		float width = (middlePoint.getX() - mostWestern.getX());
		float scope = height / width;
		
		float b = (mostSouthern.getY() - fieldBoundaryX) - (scope * mostSouthern.getX());
		float yBoundary = (scope * futureCameraLocation.getX() + b);
		
		if (yAxisInverted) {
			return (futureCameraLocation.getY() > yBoundary);
		} else {
			return (futureCameraLocation.getY() < yBoundary);
		}
	}
	
	private boolean isGoingOutOfNorthEastBoundary(Point<Float> futureCameraLocation, Point<Float> middlePoint) {
		float height = (middlePoint.getY() - mostNorthern.getY());
		float width = (middlePoint.getX() - mostEastern.getX());
		float scope = height / width;
		
		float b = (mostNorthern.getY() + fieldBoundaryX) - (scope * mostNorthern.getX());
		float yBoundary = (scope * futureCameraLocation.getX()) + b;
		
		if (yAxisInverted) {
			return (futureCameraLocation.getY() < yBoundary);
		} else {
			return (futureCameraLocation.getY() > yBoundary);
		}
	}
	
	private boolean isGoingOutOfSouthEastBoundary(Point<Float> futureCameraLocation, Point<Float> middlePoint) {
		float height = (middlePoint.getY() + mostSouthern.getY());
		float width = (middlePoint.getX() - mostEastern.getX());
		float scope = height / width;
		
		float b = (mostSouthern.getY() - fieldBoundaryX) - (scope * mostSouthern.getX());
		float yBoundary = (scope * futureCameraLocation.getX() + b);
		
		if (yAxisInverted) {
			return (futureCameraLocation.getY() > yBoundary);
		} else {
			return (futureCameraLocation.getY() < yBoundary);
		}
	}
}

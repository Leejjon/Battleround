package org.stofkat.battleround.common;

/**
 * We're going to use integers to store directions. This enum class is used to
 * map numbers to directions nicely.
 * 
 * @author Leejjon
 */
public enum Direction {
	SOUTH(0), // DEFAULT
	NORTH(1), EAST(2), WEST(3), SOUTHEAST(4), SOUTHWEST(5), NORTHWEST(6), NORTHEAST(7);

	private final int direction;

	private Direction(int direction) {
		this.direction = direction;
	}

	public int getConstant() {
		return direction;
	}

	public static Direction getDirection(int constant) {
		for (Direction direction : Direction.values()) {
			if (direction.getConstant() == constant) {
				return direction;
			}
		}
		return null;
	}
}

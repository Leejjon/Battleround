package org.stofkat.battleround.core.util;

/**
 * With this class we no longer need FloatingPoint, DoublePoint or IntegerPoint classes.
 * 
 * @author Leejjon
 *
 * @param <T>
 */
public class Point<T> {
	protected T x;
	protected T y;
	
	public Point(T x, T y) {
		this.x = x;
		this.y = y;
	}
	
	public T getX() {
		return x;
	}

	public T getY() {
		return y;
	}
	
	public boolean equals(Object other) {
		if (other instanceof Point<?>) {
			Point<?> otherPoint = (Point<?>) other;
			if (x.equals(otherPoint.x) && y.equals(otherPoint.y)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}

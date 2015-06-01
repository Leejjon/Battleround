package org.stofkat.battleround.server.captcha;

/**
 * Google App Engine doesn't allow using java.awt.Point, so I made my own.
 * 
 * @author Leejjon
 */
public class Point {
	private int x;
	private int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}

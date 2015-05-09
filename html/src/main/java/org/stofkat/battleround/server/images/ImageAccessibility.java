package org.stofkat.battleround.server.images;

/**
 * This enumeration maps the integer stored in the database to the level of access required to get the image.
 * 
 * @author Leejjon
 */
public enum ImageAccessibility {
	/**
	 * For images from free levels.
	 */
	PUBLIC(0),
	/**
	 * For images from levels that have been purchased.
	 */
	PAYWALL(1),
	/**
	 * For images that have been uploaded but are not used in a level yet, or the level isn't finished/published.
	 */
	PRIVATE(2)
	;
	
	private final int id;
	
	private ImageAccessibility(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public ImageAccessibility cast(int id) {
		for (ImageAccessibility img : ImageAccessibility.values()) {
			if (this.id == id) {
				return img;
			}
		}
		throw new IllegalArgumentException("Invalid ImageAccesibility id: " + id);
	}
}

package org.stofkat.battleround.core;

import org.stofkat.battleround.core.math.WithinTileCalculation;
import org.stofkat.battleround.core.util.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Tiles {
	private Tile[][] tiles;
	
	private Point<Float> startPoint = new Point<Float>(0f,0f); 
	private Point<Integer> selectedTileLocation = null;
	
	private Tile mostWestern;
	private Tile mostEastern;
	private Tile mostNorthern;
	private Tile mostSouthern;
	
	private float fieldBoundaryX = Tile.width;
	
	private final int width;
	private final int height;
	
	public Tiles(int width, int height) {
		this.width = width;
		this.height = height;
		
		fillTilesArray(startPoint);
		calculateBoundaries();
	}
	
	private void fillTilesArray(Point<Float> startPoint) {
		tiles = new Tile[width][height];
		
		// Refill the tiles array with new tiles.
		float tempX = startPoint.getX();
		float tempY = startPoint.getY();
		float nextX;
		float nextY;
		for (int x = 0; x < width; x++) {
			// Already store the absolute location of the first tile from the next column, because the next loop is going to move away from it.
			nextX = tempX - (Tile.width / 2);
			nextY = tempY - (Tile.height / 2);
			for (int y = 0; y < height; y++) {
				tiles[x][y] = new Tile(new Point<Float>(tempX, tempY));
				
				tempX += (Tile.width / 2);
				tempY -= (Tile.height / 2);
			}
			tempX = nextX;
			tempY = nextY;
		}
	}
	
	private void calculateBoundaries() {
		mostNorthern = tiles[0][0];
		mostSouthern = tiles[width-1][height-1];
		mostWestern = tiles[width-1][0];
		mostEastern = tiles[0][height-1];
	}
	
	public void placeGameObjectOnTile(GameObject gameObjectNotOnTheFieldYet) {
		Point<Integer> tileCoordinate = gameObjectNotOnTheFieldYet.getPositionOnTiles();
		
		// TODO: Throw an error if two objects want to be on the same tile.
		// TODO: watch out for ArrayIndexOutOfBoundsExceptions
		tiles[tileCoordinate.getX().intValue()][tileCoordinate.getY().intValue()].setObjectOnThisTile(gameObjectNotOnTheFieldYet);
	}
	
	
	public Tile getTile(int x, int y) {
		// TODO: watch out for ArrayIndexOutOfBoundsExceptions
		return tiles[x][y];
	}
	
	public void paintTiles(ShapeRenderer tileRendererBatch) {
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				// Draw the tile
				tiles[x][y].drawDebugLines(tileRendererBatch);
			}
		}
	}
	
	public void paintSelectedTile(ShapeRenderer selectedTileRenderer) {
		if (selectedTileLocation != null) {
			tiles[selectedTileLocation.getX()][selectedTileLocation.getY()].paintSelectionIndicator(selectedTileRenderer);
		}
	}
	
	public Tile selectTile(Point<Float> location) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (tiles[x][y].select(location)) {
					// Deselect the old selected tile.
					if (selectedTileLocation != null) {
						tiles[selectedTileLocation.getX().intValue()][selectedTileLocation.getY().intValue()].deSelect();
					}
					selectedTileLocation = new Point<Integer>(new Integer(x), new Integer(y));
//					Gdx.app.log("battleround", "Float: x=" + location.getX() + ", y=" + location.getY());
					Gdx.app.log("battleround", "Tile (" + x + ", " + y + ")");
					return tiles[x][y];
				} 
			}
		}
		return null;
	}
	
	public boolean isCameraGoingOutOfTheFieldBoundaries(Point<Float> futureCameraLocation) {
		WithinTileCalculation isTheCameraWithinThisField = new WithinTileCalculation(futureCameraLocation, 
				mostWestern.getRelativeLocation(), mostNorthern.getRelativeLocation(), 
				mostEastern.getRelativeLocation(), mostSouthern.getRelativeLocation(), false, fieldBoundaryX);
		return isTheCameraWithinThisField.calculate();
	}
}

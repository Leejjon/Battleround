package org.stofkat.battleround.client.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.stofkat.battleround.core.GameObject;
import org.stofkat.battleround.core.Player;
import org.stofkat.battleround.core.ServerInterface;
import org.stofkat.battleround.core.Tile;
import org.stofkat.battleround.core.Tiles;
import org.stofkat.battleround.core.util.Point;
import org.stofkat.battleround.core.util.SortListAlgorithm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class InGameStage extends Stage {
	/**
	 * This camera is only used for ingame stuff. For UI we use the camera of the superclass.
	 */
	public Camera inGameCamera;
	
	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	
	private Tiles tiles;
	private HashMap<Long, Player> players = new HashMap<Long, Player>();
	private Table rightInfoScreenTable;
	private Label playerNameLabel;
	private Label asLabel;
	private Label characterNameLabel;
	private TextField playerNameField;
	private ChatTextField chatField;
	
	public InGameStage(float width, float height) {
		super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		
		inGameCamera = new OrthographicCamera(1, height / width);
		
		shapeRenderer = new ShapeRenderer();
		spriteBatch = new SpriteBatch();
	}
	
	public void constructTiles(int width, int height) {
		tiles = new Tiles(width, height);
	}
	
	public void constructUI(Skin uiSkin, ServerInterface server) {
		Table table = new Table();
		table.setFillParent(true);
		
		table.right();
		
		Pixmap pixmap = new Pixmap(128, 512, Format.RGBA8888);
		pixmap.setColor(Color.DARK_GRAY);
		pixmap.fill();
		
		TextureRegion backgroundRegion = new TextureRegion(new Texture(pixmap), 130, Gdx.graphics.getHeight());
		
		rightInfoScreenTable = new Table();
		rightInfoScreenTable.setBackground(new TextureRegionDrawable(backgroundRegion));
		
		playerNameLabel = new Label("", uiSkin);
		
		asLabel = new Label("as", uiSkin);
		asLabel.setVisible(false);
		
		characterNameLabel = new Label("", uiSkin);
		
		rightInfoScreenTable.top();
		rightInfoScreenTable.add(playerNameLabel);
		rightInfoScreenTable.row();
		rightInfoScreenTable.add(asLabel);
		rightInfoScreenTable.row();
		rightInfoScreenTable.add(characterNameLabel);
		rightInfoScreenTable.setVisible(false);
		
		table.add(rightInfoScreenTable).right().colspan(2);
		
		table.row();
		table.bottom();
		
		playerNameField = new TextField("Latest message", uiSkin);
//		playerNameField.setDisabled(true);
		
		chatField = new ChatTextField("Tralala", uiSkin);
		
		chatField.setTextFieldListener(new ChatTextFieldListener(server));
		
		table.add(playerNameField).width(((float) Gdx.graphics.getWidth() / 100) * 25).bottom();
		table.add(chatField).width(((float) Gdx.graphics.getWidth() / 100) * 75/*- playerNameField.getWidth() - rightInfoScreenTable.getWidth()*/);
		
		addActor(table);
	}
	
	public void showInfoScreen(String characterName, String playerName) {
		if (playerName != null) {
			playerNameLabel.setText(playerName);
			playerNameLabel.setVisible(true);
			asLabel.setVisible(true);
		} else {
			playerNameLabel.setVisible(false);
			asLabel.setVisible(false);
		}
		
		characterNameLabel.setText(characterName);
		rightInfoScreenTable.setVisible(true);
	}
	
	public void hideInfoScreen() {
		rightInfoScreenTable.setVisible(false);
	}
	
	@Override
	public void draw() {
		inGameCamera.update();
		
		// Paint the tiles (debug lines)
		shapeRenderer.setProjectionMatrix(inGameCamera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(0, 0, 0, 1);
		tiles.paintTiles(shapeRenderer);
		shapeRenderer.end();
		
		// Light up the selected tile.
		
		// TODO: Check API changes.
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(0, 0, 0, 1);
		tiles.paintSelectedTile(shapeRenderer);
		shapeRenderer.end();
		
		// Paint the actual objects.
		spriteBatch.setProjectionMatrix(inGameCamera.combined);
		spriteBatch.begin();
		paintObjects(spriteBatch);
		spriteBatch.end();
		
		// Draw the UI on top of the game.
		super.draw();
	}
	
	@Override
	public void dispose() {
		shapeRenderer.dispose();
		spriteBatch.dispose();
		super.dispose();
	}
	
	public void paintObjects(SpriteBatch spriteBatch) {
		ArrayList<GameObject> orderedGameObjects = new ArrayList<GameObject>();
		for (GameObject gameObject : players.values()) {
			orderedGameObjects.add(gameObject);
		}
		orderedGameObjects = SortListAlgorithm.orderGameObjectsByYCoordinate(orderedGameObjects);
		
		for (GameObject gameObject : orderedGameObjects) {
			gameObject.draw(spriteBatch, 0f);
		}
	}
	
	public void selectTile(Point<Float> location) {
		Tile selectedTile = tiles.selectTile(location);
		
		if (selectedTile != null && selectedTile.getObjectOnThisTile() != null) {
			GameObject selectedGameObject = selectedTile.getObjectOnThisTile();
			
			if (selectedGameObject instanceof Player) {
				Player player = (Player) selectedGameObject;
				showInfoScreen(player.getCharacterName(), player.getPlayerName());
			}
		}
	}
	
	public boolean isCameraGoingOutOfTheFieldBoundaries(Point<Float> futureCameraLocation) {
		return tiles.isCameraGoingOutOfTheFieldBoundaries(futureCameraLocation);
	}

	public HashMap<Long, GameObject> getGameObjects() {
		HashMap<Long, GameObject> gameObjects = new HashMap<Long, GameObject>();
		for (Player player : players.values()) {
			gameObjects.put(player.getId(), (GameObject) player);
		}
		
		return gameObjects;
	}
	
	public void addPlayer(Player player) {
		if (!this.players.containsKey(player.getId())) {
			this.players.put(player.getId(), player);
			tiles.placeGameObjectOnTile(player);
		} 
	}
	
	public void updateChatTextField(long characterId, String chatMessage) {
		if (players.containsKey(new Long(characterId))) {
			String playerName = players.get(new Long(characterId)).getPlayerName();
			
			// TODO: Cross site scripting validation etc
			playerNameField.setText(playerName + ":");
			chatField.setText(chatMessage);
			chatField.setCursorPosition(chatMessage.length());
			chatField.removeFocus();
		} else {
			// TODO: Throw a nice error.
		}
	}
	
}

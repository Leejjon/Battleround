package org.stofkat.battleround.core;

import java.util.HashMap;
import java.util.List;

import org.stofkat.battleround.client.ui.InGameStage;
import org.stofkat.battleround.client.ui.SelectCharacterStage;
import org.stofkat.battleround.common.Direction;
import org.stofkat.battleround.common.Update;
import org.stofkat.battleround.common.level.structure.CharacterDefinition;
import org.stofkat.battleround.common.level.structure.Level;
import org.stofkat.battleround.common.level.structure.SpriteSheet;
import org.stofkat.battleround.core.util.Point;
import org.stofkat.battleround.shared.dispatch.results.SelectCharacterResult.ResultValue;
import org.stofkat.battleround.shared.dispatch.updates.NewPlayerUpdate;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;

/**
 * I've made this class abstract because method's like loadImages() still need a
 * platform specific implementation.
 * 
 * @author Leejjon
 */
public abstract class Engine extends InputAdapter implements EngineInterface, ServerInterface, ApplicationListener {
	public static boolean debug = false;
	
	protected Level level;
	private String errorMessage;
	
	private HashMap<Integer, SpriteAnimationDefinition> spriteAnimationDefinitions = new HashMap<Integer, SpriteAnimationDefinition>();
	private HashMap<Long, String> characterToPlayerMapping;
	
	protected String levelFileName;
	protected AssetManager levelAssetManager;
	protected Skin uiSkin;
	
	private float w;
	private float h;
	
	protected InGameStage inGameStage;
	private SelectCharacterStage selectCharacterStage;
	
	private InputMultiplexer inputMultiplexer = new InputMultiplexer();
	
	// Flags
	protected boolean hasSelectedCharacter = false;
	protected boolean isLoadingLevel = true;
	private boolean isDoneLoading = false;
	private boolean pause = false;
	private boolean errorOccured = false;
	
	private long clientId;
	private String userName;
	
	private long numberOfSecondsBetweenUpdateCalls = 3L;
	protected long lastUpdateNumber = 0;
	
	private Timer timer;
	
	/**
	 * Load in all spritesheets and put them in the AssetManager.
	 * 
	 * @param spriteSheets
	 * @throws EngineException
	 */
	protected abstract void loadImages() throws EngineException;

	@Override
	public void create () {
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		
		inGameStage = new InGameStage(w, h);
		selectCharacterStage = new SelectCharacterStage(this);
		
		try {
			loadImages();
		} catch (EngineException e) {
			errorOccured = true;
			Gdx.app.error("battleround", e.getMessage(), e);
		}
		
		Gdx.input.setInputProcessor(selectCharacterStage);
	}
	
	@Override
	public void setCharacterSelectedResult(ResultValue result, List<Update> updates) {
		// TODO: Make really sure this collection is ordered by update numbers.
		for (Update update : updates) {
			try {
				update.processResult(this);
			} catch (EngineException e) {
				Gdx.app.log("battleround", "Could not process this update.");
			}
		}

		if (result.equals(ResultValue.SUCCESS)) {
			hasSelectedCharacter = true;
			
			UpdateTask getUpdatesTask = new UpdateTask(this);
			timer = new Timer();
			timer.scheduleTask(getUpdatesTask, numberOfSecondsBetweenUpdateCalls, numberOfSecondsBetweenUpdateCalls);
			
			inputMultiplexer.addProcessor(inGameStage);
			inputMultiplexer.addProcessor(this);
			Gdx.input.setInputProcessor(inputMultiplexer);
		} else {
			updateCharacterToPlayerMapping(updates);
			selectCharacterStage.setCharacterSelectedResult(result);
			selectCharacterStage.updateAlreadySelectedCharacters(characterToPlayerMapping);
		}
	}
	
	private void updateCharacterToPlayerMapping(List<Update> updates) {
		for (Update update : updates) {
			// This is ugly but I can't think of anything prettier right now.
			if (update instanceof NewPlayerUpdate) {
				NewPlayerUpdate newPlayerUpdate = (NewPlayerUpdate) update;
				
				// Only add the update to the mapping if it isn't in there yet.
				// Even though it should not be possible to receive the same
				// updates twice.
				if (!characterToPlayerMapping.containsKey(new Long(newPlayerUpdate.getCharacterId()))) {
					characterToPlayerMapping.put(new Long(newPlayerUpdate.getCharacterId()), newPlayerUpdate.getPlayerName());
				}
			}
		}
	}
	
	/**
	 * Before starting the application we've sent a request for the level file
	 * to the server. Once the server is ready this method is called.
	 * 
	 * @param level
	 * @throws EngineException
	 */
	@Override
	public void initLevel(Level level, HashMap<Long, String> alreadySelectedCharacters) throws EngineException {
		// TODO: Validate level to avoid cross site scripting and throw an
		// engine exception if the level is invalid.
		
		if (this.level == null) {
			this.level = level;
			this.characterToPlayerMapping = alreadySelectedCharacters;
			
			isLoadingLevel = false;
		} else {
			Gdx.app.log("battleround", "Level is already loaded!");
		}
	}

	@Override
	public long getClientId() {
		return clientId;
	}
	
	@Override
	public void setClientId(long clientId) {
		this.clientId = clientId;
	}
	
	@Override
	public long getLastUpdateNumber() {
		return lastUpdateNumber;
	}
	
	@Override
	public void updateLastUpdateNumber(long updateNumber) {
		if (updateNumber > lastUpdateNumber) {
			lastUpdateNumber = updateNumber;
		}
	}
	
	@Override
	public void pauseTimerBecauseWereGonnaUpdate() {
		if (timer != null) {
			timer.clear();
			
			UpdateTask getUpdatesTask = new UpdateTask(this);
			timer.scheduleTask(getUpdatesTask, numberOfSecondsBetweenUpdateCalls * 3, numberOfSecondsBetweenUpdateCalls);
		}
	}

	protected void constructField() throws EngineException {
		if (level == null) {
			selectCharacterStage.displayError(errorMessage, uiSkin);
			isDoneLoading = true;
		} else {
			// Fill the SpriteAnimationDefinitions HashMap so that GameObjects can
			// refer to objects in there.
			for (SpriteSheet spriteSheet : level.getSpriteSheets()) {
				SpriteAnimationDefinition spd = new SpriteAnimationDefinition(levelAssetManager.get(spriteSheet.getPath(), Texture.class), spriteSheet.getSingleSpriteWidth(),
						spriteSheet.getSingleSpriteHeight(), spriteSheet.getOrder());
				spriteAnimationDefinitions.put(new Integer(spriteSheet.getSpriteSheetId()), spd);
			}
			
			// TODO: Make gameobjects for monsters and objects too.
			
			updatePlayers();
			inGameStage.constructTiles(level.getLevelWidth(), level.getLevelHeight());
			
			// Set up ingame UI.
			inGameStage.constructUI(uiSkin, this);
	
			// Loading is done.
			isDoneLoading = true;
		}
	}
	
	public HashMap<Long, Player> updatePlayers() {
		// Construct a HashMap so we can track the objects on the field without
		// having to search on every tile whenever we need a gameobject.
		HashMap<Long, Player> players = new HashMap<Long, Player>();
		HashMap<Long, Player> selectAblePlayers = new HashMap<Long, Player>();
		for (org.stofkat.battleround.common.level.structure.Character player : level.getCharacters()) {
			Point<Integer> playerLocation = new Point<Integer>(player.getxCoordinate(), player.getyCoordinate());
			// Whether the playerdefinition exists should be validated before.
			CharacterDefinition playerDefinition = level.getCharacterDefinition(player.getDefinitionId());
			
			Direction playerDirection = Direction.getDirection(player.getDirection());
			
			String playerName = characterToPlayerMapping != null && characterToPlayerMapping.containsKey(new Long(player.getId())) ? characterToPlayerMapping.get(new Long(player.getId())) : null; 
			Player newPlayer = new Player(player.getId(), playerLocation, playerDirection,
					spriteAnimationDefinitions.get(new Integer(playerDefinition.getIdleSpriteSheetId())), playerDefinition.getDefaultName(),
					playerName);
			if (characterToPlayerMapping != null && characterToPlayerMapping.containsKey(new Long(player.getId()))) {
				players.put(new Long(player.getId()), newPlayer);
			}
			
			if (!hasSelectedCharacter) {
				selectAblePlayers.put(new Long(player.getId()), newPlayer);
			}
		}
		
		if (!hasSelectedCharacter) {
			// Set up character select UI.
			selectCharacterStage.init(selectAblePlayers, uiSkin);
		}
		
		return players;
	}
	
	@Override
	public void addPlayer(long characterId, String playerName) {
		for (org.stofkat.battleround.common.level.structure.Character character : level.getCharacters()) {
			if (character.getId() == characterId) {
				// Whether the playerdefinition exists should be validated before.
				CharacterDefinition playerDefinition = level.getCharacterDefinition(character.getDefinitionId());
				
				Point<Integer> playerLocation = new Point<Integer>(character.getxCoordinate(), character.getyCoordinate());
				Direction playerDirection = Direction.getDirection(character.getDirection());
				
				Player newPlayer = new Player(character.getId(), playerLocation, playerDirection,
						spriteAnimationDefinitions.get(new Integer(playerDefinition.getIdleSpriteSheetId())), playerDefinition.getDefaultName(),
						playerName);
				inGameStage.addPlayer(newPlayer);
			}
		}
		// TODO: If we didn't found the character perhaps display an error?
	}
	
	@Override
	public void addChatMessage(long characterId, String chatMessage) {
		inGameStage.updateChatTextField(characterId, chatMessage);
	}

	@Override
	public void close() {
		dispose();
	}

	@Override
	public void dispose() {
		timer.clear();
		
		if (inGameStage != null) {
			inGameStage.dispose();
		}
		if (selectCharacterStage != null) {
			selectCharacterStage.dispose();
		}
		uiSkin.dispose();
		levelAssetManager.dispose();
	}
	
	@Override
	public void resize (int width, int height) {}

	@Override
	public void render () {
		if (!isDoneLoading || pause || errorOccured) {
			// TODO: Make nice pause, loading and error screens.
			return;
		}

		// Clear color seems to be white.
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		if (hasSelectedCharacter) {
			inGameStage.act(Gdx.graphics.getDeltaTime());
			inGameStage.draw();
		} else {
			// No clue why this calculation is being done
			selectCharacterStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
			selectCharacterStage.draw();
		}
	}

	@Override
	public void pause () {
		pause = true;
	}

	@Override
	public void resume () {
		pause = false;
	}
	
	/**
	 * Easy static method any class in this Application can call to know if
	 * debug mode is on or not.
	 * 
	 * @return True if debug mode is on, false if it's off.
	 */
	public static boolean isDebugModeOn() {
		return debug;
	}
	
	public Level getLevel() {
		return level;
	}
	
	/**
	 * This int is used to keep track of how long a drag has been.
	 */
	private int horizontalDragLength = 0;
	private int verticalDragLength = 0;
	
	private int lastX;
	private int lastY;
	
	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		lastX = x;
		lastY = y;
		
		return false;
	}
	
	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		int xDifferenceInPixels = lastX - x;
		int yDifferenceInPixels = lastY - y;
		
		horizontalDragLength += xDifferenceInPixels;
		verticalDragLength += yDifferenceInPixels;
		
		float xDifferenceRatio = 0;
		float yDifferenceRatio = 0;
		if (xDifferenceInPixels != 0 || yDifferenceInPixels != 0) {
			
			xDifferenceRatio = (new Integer(xDifferenceInPixels)).floatValue() / w;
			yDifferenceRatio = (new Integer(yDifferenceInPixels)).floatValue() / h;
			
			float newCameraX = (inGameStage.inGameCamera.position.x) + (xDifferenceRatio * inGameStage.inGameCamera.viewportWidth);
			float newCameraY = (inGameStage.inGameCamera.position.y) - (yDifferenceRatio * inGameStage.inGameCamera.viewportHeight);
			if (!inGameStage.isCameraGoingOutOfTheFieldBoundaries(new Point<Float>(newCameraX, newCameraY))) {
				inGameStage.inGameCamera.position.x = newCameraX;
				inGameStage.inGameCamera.position.y = newCameraY;
				
				inGameStage.hideInfoScreen(); 
			}
		}
		lastX = x;
		lastY = y;
		return false;
	}
	
	/**
	 * Convert the x/y pixel coordinates to the viewport of the camera in floats.
	 * @return Doesn't matter if we return true or false as we don't use InputMultiplexers.
	 */
	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		// This draglength calculation is quick and dirty, replace it if you feel like diving into it.
		if (horizontalDragLength > 10 || horizontalDragLength < -10) {
			horizontalDragLength = 0;
		} else if (verticalDragLength > 10 || verticalDragLength < -10) {
			verticalDragLength = 0;
		} else {
			float xRatio = 0;
			float yRatio = 0;
			
			if (x != 0) {
				xRatio = (new Integer(x)).floatValue() / w;
			}
			
			if (y != 0) {
				yRatio = (new Integer(y)).floatValue() / h;
			}
			
			// Calculate the relative position on the screen, and take the camera
			// position into account, plus the fact that the camera is always in the
			// middle of the screen.
			float floatX = (inGameStage.inGameCamera.position.x - inGameStage.inGameCamera.viewportWidth / 2) + (xRatio * inGameStage.inGameCamera.viewportWidth);
			
			// Flip the y position of the camera because it's inverted.
			float floatY = ((-1f* inGameStage.inGameCamera.position.y) - inGameStage.inGameCamera.viewportHeight / 2) + (yRatio * inGameStage.inGameCamera.viewportHeight);
	
			inGameStage.selectTile(new Point<Float>(floatX, floatY));
			
			horizontalDragLength = 0;
			verticalDragLength = 0;
			//Gdx.app.log("battleround", "RelativeClickedX: " + floatX + ", RelativeClickedY: " + floatY);
		}
		return false; // Has no meaning.
	}
	
	public void handleError(String errorMessage) {
		if (hasSelectedCharacter) {
			// TODO: Close the game and show the error.
		} else {
			isLoadingLevel = false;
			this.errorMessage = errorMessage;
		}
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
}

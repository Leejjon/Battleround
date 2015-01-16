package org.stofkat.battleround.html;

import java.util.List;

import org.stofkat.battleround.core.Engine;
import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineException.EngineExceptions;
import org.stofkat.battleround.html.engine.files.GwtFileHandleResolver;
import org.stofkat.battleround.html.gwt.dispatch.GwtAsyncCallbackHandler;
import org.stofkat.battleround.html.gwt.dispatch.GwtDispatchServiceAsync;
import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.results.Result;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.gwt.user.client.Timer;

public class GwtEngine extends Engine {
	private final GwtDispatchServiceAsync battleRoundService;
	protected boolean isLoadingImages = true;
	private List<String> preloadedImageNames;

	public GwtEngine(boolean debug, final GwtDispatchServiceAsync battleRoundService) {
		this.battleRoundService = battleRoundService;
		Engine.debug = debug;
	}

	@Override
	public void create() {
		this.preloadedImageNames = GwtLauncher.getPreLoadedImages();
		super.create();
	}
	
	/**
	 * In GWT we cannot simply list the contents of a folder and load in all
	 * files. We need to figure out what files have been preloaded by the
	 * Preloader of the GwtLauncher. So we override the loadImages method to
	 * load the images that have been loaded by the preloader.
	 * 
	 * @throws EngineException
	 */
	@Override
	protected void loadImages() throws EngineException {
		// Load the UI stuff.
		uiSkin = new Skin(Gdx.files.internal("data/ui/uiskin.json"));
		uiSkin.addRegions(new TextureAtlas("data/ui/uiskin.atlas"));
		
		if (levelFileName != null) { 
			// If the levelFileName is null, the user might not be authenticated or authorized to play this level.
			// And thus we will not load the images.
			if (preloadedImageNames != null) {
				
				levelAssetManager = new AssetManager(new GwtFileHandleResolver(levelFileName));
				for (String imageFileName : preloadedImageNames) {
					if (imageFileName.contains("data/levels/" + levelFileName.toLowerCase() + "_images/")) {
						String[] splittedPath = imageFileName.split("/");
						levelAssetManager.load(splittedPath[splittedPath.length - 1], Texture.class);
					} 
				}
				levelAssetManager.finishLoading();
			} else {
				throw new EngineException(EngineExceptions.FAILED_LOADING_IMAGES);
			}
		}
		
		Timer timer = new Timer() {
			
			@Override
			public void run() {
				if (isLoadingLevel) {
					this.schedule(1000);
				} else {
					try {
						constructField();
					} catch (EngineException e) {
						Gdx.app.log("battleround", e.getMessage());
					}
				}
			}
		};
		timer.schedule(10);
	}

	@Override
	public <R extends Result> void executeServerAction(Action<R> action) {
		battleRoundService.execute(action, new GwtAsyncCallbackHandler<R>(this));
	}
	
	
	public void setLevelFile(String levelFileName) {
		super.levelFileName = levelFileName;
	}
}

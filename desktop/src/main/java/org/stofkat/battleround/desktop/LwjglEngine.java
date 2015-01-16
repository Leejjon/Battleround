package org.stofkat.battleround.desktop;

import org.stofkat.battleround.client.http.dispatch.AsyncCallbackHandler;
import org.stofkat.battleround.client.http.dispatch.HttpDispatchServiceAsync;
import org.stofkat.battleround.core.Engine;
import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineException.EngineExceptions;
import org.stofkat.battleround.desktop.files.DesktopFileHandleResolver;
import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.results.Result;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LwjglEngine extends Engine {
	private HttpDispatchServiceAsync server;
	
	public LwjglEngine(String levelFileName, boolean debug, HttpDispatchServiceAsync server) {
		this.server = server;
		super.levelFileName = levelFileName;
		Engine.debug = debug;
	}
	
	/**
	 * Currently we're only reading the internal files because we're testing,
	 * but we're going to read the files from a BattleRound folder in the user
	 * directory.
	 */
	@Override
	protected void loadImages() throws EngineException {
		try {
			uiSkin = new Skin(Gdx.files.internal("./data/ui/uiskin.json"));
			uiSkin.addRegions(new TextureAtlas("./data/ui/uiskin.atlas"));
			
			DesktopFileHandleResolver resolver = new DesktopFileHandleResolver(levelFileName);

			// Load the level images.
			levelAssetManager = new AssetManager(resolver);

			for (FileHandle image : resolver.getDirectory().list()) {
				levelAssetManager.load(image.name(), Texture.class);
			}

			levelAssetManager.finishLoading();

			// Yeah this construction sucks, but this thread is the rendering
			// thread, I can't let the thread that retrieves the level
			// configuration from the server use OpenGL context. That would
			// result in an error: java.lang.RuntimeException: No OpenGL context
			// found in the current thread.
			while (true) {
				// So we'll keep the rendering thread live until the server
				// config file has been retrieved form the server.
				Thread.sleep(1000);

				if (!isLoadingLevel) {
					break;
				}
			}

			constructField();
		} catch (IllegalArgumentException e) {
			throw new EngineException(EngineExceptions.FAILED_LOADING_IMAGES, e);
		} catch (InterruptedException e) {
			throw new EngineException(EngineExceptions.FAILED_LOADING_IMAGES, e);
		}
	}

	@Override
	public <R extends Result> void executeServerAction(Action<R> action) {
		server.execute(action, new AsyncCallbackHandler<R>(this));
	}
}

package org.stofkat.battleround.android;

import org.stofkat.battleround.android.files.AndroidFileHandleResolver;
import org.stofkat.battleround.client.http.dispatch.AsyncCallbackHandler;
import org.stofkat.battleround.client.http.dispatch.HttpDispatchServiceAsync;
import org.stofkat.battleround.core.Engine;
import org.stofkat.battleround.core.EngineException;
import org.stofkat.battleround.core.EngineException.EngineExceptions;
import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.results.Result;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AndroidEngine extends Engine {
	private final HttpDispatchServiceAsync server;
	
	public AndroidEngine(String levelFile, boolean debug, HttpDispatchServiceAsync server) {
		this.server = server;
		this.levelFileName = levelFile;
		Engine.debug = debug;
	}
	
	/**
	 * Currently we're only reading the internal files because we're testing,
	 * but we're going to use a BattleRound folder on the SD card in the future.
	 */
	@Override
	protected void loadImages() throws EngineException {
		try {
			// Load the UI stuff.
			uiSkin = new Skin(Gdx.files.internal("data/ui/uiskin.json"));
			uiSkin.addRegions(new TextureAtlas("data/ui/uiskin.atlas"));

			AndroidFileHandleResolver resolver = new AndroidFileHandleResolver(levelFileName);
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
				// So we'll keep the rendering thread alive until the server
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
	@SuppressWarnings("hiding") // TODO: Find out wth this means.
	public <R extends Result> void executeServerAction(Action<R> action) {
		server.execute(action, new AsyncCallbackHandler<R>(this));
	}
}

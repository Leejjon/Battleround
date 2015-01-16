package org.stofkat.battleround.html;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.stofkat.battleround.html.gwt.dispatch.GwtAsyncCallbackHandler;
import org.stofkat.battleround.html.gwt.dispatch.GwtDispatchService;
import org.stofkat.battleround.html.gwt.dispatch.GwtDispatchServiceAsync;
import org.stofkat.battleround.shared.dispatch.actions.GetLevelAction;
import org.stofkat.battleround.shared.dispatch.results.GetLevelResult;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Keys;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;

public class GwtLauncher extends GwtApplication {
	private final GwtDispatchServiceAsync battleRoundService = GWT.create(GwtDispatchService.class);
	private GwtEngine engine = new GwtEngine(true, battleRoundService);
	public static GwtLauncher thisGwtLauncher;
	
	@Override
	public void onModuleLoad() {
		// Make this instance approachable for the engine.
		thisGwtLauncher = this;
		super.onModuleLoad();
		
		String level = Window.Location.getParameter("level");
		engine.setLevelFile(level);
		
		// Send action to the server to retrieve the level.
		GetLevelAction getLevelAction = new GetLevelAction(level, Cookies.getCookie("JSESSIONID"));
		GwtAsyncCallbackHandler<GetLevelResult> startUpCallbackClass = new GwtAsyncCallbackHandler<GetLevelResult>(engine);
		battleRoundService.execute(getLevelAction, startUpCallbackClass);
	}
	
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(480, 320);
		return cfg;
	}

	@Override
	public ApplicationListener getApplicationListener() {
		return engine;
	}

	/**
	 * Retrieve the file names of the images that were loaded in the GWT
	 * application.
	 * 
	 * @return A list of strings containing filenames.
	 */
	public static List<String> getPreLoadedImages() {
		ObjectMap<String, ImageElement> images = thisGwtLauncher.getPreloader().images;
		
		Keys<String> keys = images.keys();
		Iterator<String> it = keys.iterator();
		
		List<String> imageFileNames = new ArrayList<String>();
		while (it.hasNext()) {
			imageFileNames.add(it.next());
		}
		
		return imageFileNames;
	}
}
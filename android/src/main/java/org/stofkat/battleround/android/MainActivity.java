package org.stofkat.battleround.android;

import java.util.Locale;

import org.stofkat.battleround.client.http.dispatch.AsyncCallbackHandler;
import org.stofkat.battleround.core.Engine;
import org.stofkat.battleround.shared.dispatch.actions.GetLevelAction;
import org.stofkat.battleround.shared.dispatch.results.GetLevelResult;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {
	private AndroidDispatchServiceAsync service;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        String level = getIntent().getCharSequenceExtra("level").toString();
        
        String JSESSIONID = getIntent().getCharSequenceExtra("sessionid").toString();
        
		service = new AndroidDispatchServiceAsync("http://192.168.2.4:8888/" + "BattleRound-http");
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        
        Engine engine = new AndroidEngine(level.toLowerCase(Locale.US), true, service);
        initialize(engine, cfg);
        
        GetLevelAction getLevelAction = new GetLevelAction(level, JSESSIONID);
        AsyncCallbackHandler<GetLevelResult> asyncCallbackHandler = new AsyncCallbackHandler<GetLevelResult>(engine);
        
        
        service.execute(getLevelAction, asyncCallbackHandler);
	}
}

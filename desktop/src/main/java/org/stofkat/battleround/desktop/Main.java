package org.stofkat.battleround.desktop;

import java.net.URL;

import org.stofkat.battleround.client.http.dispatch.AsyncCallbackHandler;
import org.stofkat.battleround.core.Engine;
import org.stofkat.battleround.shared.dispatch.actions.GetLevelAction;
import org.stofkat.battleround.shared.dispatch.results.GetLevelResult;

import netscape.javascript.JSObject;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {
	private static DesktopDispatchServiceAsync service = new DesktopDispatchServiceAsync("http://127.0.0.1:8888" + "/BattleRound-http");

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Battleround client");

		URL loginPageUrl = Main.class.getResource("/pages/login.html");

	    final WebView webview = new WebView();
		final WebEngine webEngine = webview.getEngine();

		webEngine.setJavaScriptEnabled(true);
		webEngine.load(loginPageUrl.toString());

		/*
		 * Alright, this piece of code might be hard to understand. Basically
		 * we're adding a JavaScript object that's actually a Java object. So we
		 * can call Java methods from JavaScript. And we're adding this
		 * javascript object as soon as the page has been fully loaded.
		 */
		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov,
					State t, State t1) {
				if (t1 == Worker.State.SUCCEEDED) {
					JSObject window = (JSObject) webEngine.executeScript("window");
					window.setMember("java", new AuthenticationApplication());
				}
			}
		});

		StackPane root = new StackPane();
		root.getChildren().add(webview);
		primaryStage.setScene(new Scene(root, 400, 500));
		primaryStage.show();
	}

	public class AuthenticationApplication {
		public void start(String JSESSIONID) {
			loadGame(JSESSIONID);
		}
	}

	private static void loadGame(String JSESSIONID) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "BattleRound";
		cfg.useGL20 = false;
		cfg.width = 480;
		cfg.height = 320;

		String hardCodedLevelFileName = "TestLevel";

		Engine engine = new LwjglEngine(hardCodedLevelFileName.toLowerCase(), true, service);
		new LwjglApplication(engine, cfg);

		// TODO: Create a webview in JavaFX and load the login.html to login.
		// Then retrieve the JSESSIONID cookie and pass it here.
		GetLevelAction getLevelAction = new GetLevelAction(hardCodedLevelFileName, JSESSIONID);
		AsyncCallbackHandler<GetLevelResult> asyncCallbackHandler = new AsyncCallbackHandler<GetLevelResult>(engine);

		service.execute(getLevelAction, asyncCallbackHandler);
	}
}

package org.stofkat.battleround.leveldesigner.client;

import org.stofkat.battleround.common.User;
import org.stofkat.battleround.common.level.structure.Level;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LevelDesignerServiceAsync {
	void isAuthenticated(AsyncCallback<User> callback);
	
	void loadExistingLevel(long levelId, AsyncCallback<Level> callback);
}

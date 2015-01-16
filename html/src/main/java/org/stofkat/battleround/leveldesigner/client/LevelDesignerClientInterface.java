package org.stofkat.battleround.leveldesigner.client;

import org.stofkat.battleround.common.User;

public interface LevelDesignerClientInterface {
	void userAuthenticated(User user);
	
	void userFailedToAuthenticate();
	
	void loadLevel();
}

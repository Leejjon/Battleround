package org.stofkat.battleround.leveldesigner.client;

import org.stofkat.battleround.common.User;
import org.stofkat.battleround.shared.dispatch.exceptions.AuthenticationException;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class AuthenticationCallbackHandler implements AsyncCallback<User> {
	private LevelDesignerClientInterface client;

	public AuthenticationCallbackHandler(LevelDesignerClientInterface client) {
		this.client = client;
	}

	@Override
	public void onFailure(Throwable caught) {
		if (caught instanceof AuthenticationException) {
			client.userFailedToAuthenticate();
		} else {
			// TODO: Server not available or something. Make some nice message
			// to let the user know.
		}
	}

	@Override
	public void onSuccess(User userName) {
		client.userAuthenticated(userName);
	}

}

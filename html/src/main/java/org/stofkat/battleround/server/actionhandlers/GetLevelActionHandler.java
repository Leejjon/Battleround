package org.stofkat.battleround.server.actionhandlers;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.stofkat.battleround.common.User;
import org.stofkat.battleround.database.AuthenticationConnection;
import org.stofkat.battleround.server.ActionHandler;
import org.stofkat.battleround.server.ExecutionContext;
//import org.stofkat.battleround.server.LoginSessionCollector;
import org.stofkat.battleround.server.game.ServerGame;
import org.stofkat.battleround.shared.dispatch.actions.GetLevelAction;
import org.stofkat.battleround.shared.dispatch.exceptions.AuthenticationException;
import org.stofkat.battleround.shared.dispatch.exceptions.DispatchException;
import org.stofkat.battleround.shared.dispatch.results.GetLevelResult;

public class GetLevelActionHandler implements ActionHandler<GetLevelAction, GetLevelResult> {

	@Override
	public Class<GetLevelAction> getActionType() {
		return GetLevelAction.class;
	}

	@Override
	public GetLevelResult execute(GetLevelAction action,
			ExecutionContext context, HttpSession session) throws DispatchException {
		ServerGame game;
		Object userObject = null;
		HashMap<Long, String> alreadySelectedCharacters = new HashMap<Long, String>();
		
		boolean isTheSameSession = false;
		// The login process could've been done in a webview, and might not have
		// the same sessionId. So we check if the sessionId from the client
		// matches with the current session. If it doesn't matches, we retrieve
		// the user object from the original session and copy it to the current
		// session.
		if (session.getId().equals(action.getSessionId())) {
			userObject = session.getAttribute(AuthenticationConnection.userObjectKey);
			isTheSameSession = true;
		} else {
//			HttpSession loginSession = LoginSessionCollector.find(action.getSessionId());
//			
//			if (loginSession != null) {
//				userObject = loginSession.getAttribute(AuthenticationConnection.userObjectKey);
//			}
		}
		
		if (userObject != null && userObject instanceof User) {
			User user = (User) userObject;
			if (isTheSameSession) {
				session.setAttribute(AuthenticationConnection.userObjectKey, user);
			}

			if (session.getAttribute(ServerGame.serverKey) == null) {
				// Create a new server game.
				game = new ServerGame(action.getLevelName());
			} else {
				// Use the existing game.
				game = (ServerGame) session.getAttribute(ServerGame.serverKey);
				alreadySelectedCharacters.putAll(game.getCurrentPlayers());
			}
		
			// Obtain a client id. 
			long clientId = game.joinGame(user.getName());
			
			// Store it in the http session (again), we do not have a database yet.
			session.setAttribute(ServerGame.serverKey, game);
			
			// Return the level.
			GetLevelResult getLevelResult = new GetLevelResult(clientId, user.getName(), game.getLevel(), alreadySelectedCharacters);
			
			return getLevelResult;
		} else {
			throw new AuthenticationException();
		}
	}

	@Override
	public void rollback(GetLevelAction action, GetLevelResult result,
			ExecutionContext context) throws DispatchException {
		// No rollback.
	}
	
}

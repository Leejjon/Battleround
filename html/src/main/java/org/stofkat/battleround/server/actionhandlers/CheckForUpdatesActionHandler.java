package org.stofkat.battleround.server.actionhandlers;

import javax.servlet.http.HttpSession;

import org.stofkat.battleround.server.ActionHandler;
import org.stofkat.battleround.server.ExecutionContext;
import org.stofkat.battleround.server.game.ServerGame;
import org.stofkat.battleround.shared.dispatch.actions.CheckForUpdatesAction;
import org.stofkat.battleround.shared.dispatch.exceptions.DispatchException;
import org.stofkat.battleround.shared.dispatch.exceptions.GameNotAvailableOnServerException;
import org.stofkat.battleround.shared.dispatch.results.CheckForUpdatesResult;

public class CheckForUpdatesActionHandler implements ActionHandler<CheckForUpdatesAction, CheckForUpdatesResult> {

	@Override
	public Class<CheckForUpdatesAction> getActionType() {
		return CheckForUpdatesAction.class;
	}

	@Override
	public CheckForUpdatesResult execute(CheckForUpdatesAction action, ExecutionContext context, HttpSession session) throws DispatchException {
		if (session.getAttribute(ServerGame.serverKey) != null) {
			// Use the existing game.
			ServerGame game = (ServerGame) session.getAttribute(ServerGame.serverKey);
			
			// We don't do anything with the client object, but at by calling
			// this method we will at least get an error if the client does not
			// exist.
			game.getClient(action.getClientId());
			
			CheckForUpdatesResult result = new CheckForUpdatesResult(game.getUpdatesAfter(action.getLastUpdateNumber()));
			
			// Store it in the http session (again), we do not have a database yet.
			session.setAttribute(ServerGame.serverKey, game);
			
			return result; 
		} else {
			throw new GameNotAvailableOnServerException();
		}
	}

	@Override
	public void rollback(CheckForUpdatesAction action, CheckForUpdatesResult result, ExecutionContext context) throws DispatchException {
		// We only roll forward jeweetzelf.
	}

}

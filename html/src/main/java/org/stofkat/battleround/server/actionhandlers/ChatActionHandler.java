package org.stofkat.battleround.server.actionhandlers;

import javax.servlet.http.HttpSession;

import org.stofkat.battleround.server.ActionHandler;
import org.stofkat.battleround.server.ExecutionContext;
import org.stofkat.battleround.server.game.ServerGame;
import org.stofkat.battleround.shared.dispatch.actions.ChatAction;
import org.stofkat.battleround.shared.dispatch.exceptions.DispatchException;
import org.stofkat.battleround.shared.dispatch.exceptions.GameNotAvailableOnServerException;
import org.stofkat.battleround.shared.dispatch.results.CheckForUpdatesResult;

public class ChatActionHandler implements ActionHandler<ChatAction, CheckForUpdatesResult> {
	
	@Override
	public Class<ChatAction> getActionType() {
		return ChatAction.class;
	}
	
	@Override
	public CheckForUpdatesResult execute(ChatAction action, ExecutionContext context, HttpSession session) throws DispatchException {
		if (session.getAttribute(ServerGame.serverKey) != null) {
			// Use the existing game.
			ServerGame game = (ServerGame) session.getAttribute(ServerGame.serverKey);
			
			game.newChatMessage(action.getClientId(), action.getChatMessage());
			
			CheckForUpdatesResult result = new CheckForUpdatesResult(game.getUpdatesAfter(action.getLastUpdateNumber()));
			
			// Store it in the http session (again), we do not have a database yet.
			session.setAttribute(ServerGame.serverKey, game);
			
			return result; 
		} else {
			throw new GameNotAvailableOnServerException();
		}
	}

	@Override
	public void rollback(ChatAction action, CheckForUpdatesResult result, ExecutionContext context) throws DispatchException {
		// We only roll forward jeweetzelf.
	}
}

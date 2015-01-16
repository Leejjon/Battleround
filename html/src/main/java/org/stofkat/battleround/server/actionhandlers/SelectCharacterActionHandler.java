package org.stofkat.battleround.server.actionhandlers;

import javax.servlet.http.HttpSession;

import org.stofkat.battleround.server.ActionHandler;
import org.stofkat.battleround.server.ExecutionContext;
import org.stofkat.battleround.server.game.ServerGame;
import org.stofkat.battleround.shared.dispatch.actions.SelectCharacterAction;
import org.stofkat.battleround.shared.dispatch.exceptions.DispatchException;
import org.stofkat.battleround.shared.dispatch.exceptions.GameNotAvailableOnServerException;
import org.stofkat.battleround.shared.dispatch.results.SelectCharacterResult;
import org.stofkat.battleround.shared.dispatch.results.SelectCharacterResult.ResultValue;

public class SelectCharacterActionHandler implements ActionHandler<SelectCharacterAction, SelectCharacterResult> {

	@Override
	public Class<SelectCharacterAction> getActionType() {
		return SelectCharacterAction.class;
	}

	@Override
	public SelectCharacterResult execute(SelectCharacterAction action, ExecutionContext context, HttpSession session) throws DispatchException {
		if (session.getAttribute(ServerGame.serverKey) != null) {
			// Use the existing game.
			ServerGame game = (ServerGame) session.getAttribute(ServerGame.serverKey);
			
			ResultValue resultStatus;
			if (game.pickCharacter(game.getClient(action.getClientId()), action.getCharacterId())) {
				resultStatus = ResultValue.SUCCESS;
			} else {
				resultStatus = ResultValue.CHARACTER_ALREADY_IN_USE;
			}
			
			SelectCharacterResult result = new SelectCharacterResult(resultStatus, game.getUpdatesAfter(action.getLastUpdateNumber()));
			
			// Store it in the http session (again), we do not have a database yet.
			session.setAttribute(ServerGame.serverKey, game);
			
			return result; 
		} else {
			throw new GameNotAvailableOnServerException();
		}
	}

	@Override
	public void rollback(SelectCharacterAction action, SelectCharacterResult result, ExecutionContext context) throws DispatchException {
		// We only roll forward jeweetzelf.
	}

}

package org.stofkat.battleround.leveldesigner.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.stofkat.battleround.common.User;
import org.stofkat.battleround.common.level.structure.Level;
import org.stofkat.battleround.database.AuthenticationConnection;
import org.stofkat.battleround.database.DatabaseException;
import org.stofkat.battleround.database.LevelConnection;
import org.stofkat.battleround.leveldesigner.client.LevelDesignerService;
import org.stofkat.battleround.leveldesigner.exceptions.AuthorizationException;
import org.stofkat.battleround.leveldesigner.exceptions.CouldNotLoadExistingLevelException;
import org.stofkat.battleround.shared.dispatch.exceptions.ActionException;
import org.stofkat.battleround.shared.dispatch.exceptions.AuthenticationException;

import com.google.appengine.api.utils.SystemProperty;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class LevelDesignerServiceImpl extends RemoteServiceServlet implements LevelDesignerService {

	public User isAuthenticated() throws ActionException {
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession httpSession = request.getSession();
		Object user = httpSession.getAttribute(AuthenticationConnection.userObjectKey);
		if (user != null && user instanceof User) {
			return (User) user;
		} else { 
			throw new AuthenticationException();
		}
	}

	@Override
	public Level loadExistingLevel(long levelId) throws ActionException {
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession httpSession = request.getSession();
		Object user = httpSession.getAttribute(AuthenticationConnection.userObjectKey);
		
		if (user != null && user instanceof User) {
			try {
			long userId = ((User) user).getId();
				boolean productionMode = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;
				LevelConnection connection = new LevelConnection(productionMode, true);
				
				Level level = connection.loadExistingLevel(userId, levelId);
				if (level == null) {
					throw new AuthorizationException("The user was not authorized to edit this level.");
				} else {
					return level;
				}
			} catch (DatabaseException e) {
				// TODO: Log this with log4j in a separate log.
				e.printStackTrace();
				throw new CouldNotLoadExistingLevelException("Database error.");
			}
		} else { 
			throw new AuthenticationException();
		}
	}
}

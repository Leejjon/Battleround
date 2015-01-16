package org.stofkat.battleround.leveldesigner.client;

import org.stofkat.battleround.common.User;
import org.stofkat.battleround.common.level.structure.Level;
import org.stofkat.battleround.shared.dispatch.exceptions.ActionException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("servlet")
public interface LevelDesignerService extends RemoteService {
	User isAuthenticated() throws ActionException;
	
	Level loadExistingLevel(long levelId) throws ActionException;
}

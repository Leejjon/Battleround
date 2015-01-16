package org.stofkat.battleround.server.gwt;

import javax.servlet.http.HttpServletRequest;

import org.stofkat.battleround.html.gwt.dispatch.GwtDispatchService;
import org.stofkat.battleround.server.DefaultActionHandlerRegistry;
import org.stofkat.battleround.server.Dispatch;
import org.stofkat.battleround.server.InstanceActionHandlerRegistry;
import org.stofkat.battleround.server.SimpleDispatch;
import org.stofkat.battleround.server.actionhandlers.ChatActionHandler;
import org.stofkat.battleround.server.actionhandlers.CheckForUpdatesActionHandler;
import org.stofkat.battleround.server.actionhandlers.GetLevelActionHandler;
import org.stofkat.battleround.server.actionhandlers.SelectCharacterActionHandler;
import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.exceptions.DispatchException;
import org.stofkat.battleround.shared.dispatch.exceptions.ServiceException;
import org.stofkat.battleround.shared.dispatch.results.Result;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GwtDispatchServlet extends RemoteServiceServlet implements GwtDispatchService {
	private static final long serialVersionUID = 1L;
	
	private Dispatch dispatch;
	
	public GwtDispatchServlet() {
		InstanceActionHandlerRegistry registry = new DefaultActionHandlerRegistry();
		registry.addHandler(new GetLevelActionHandler());
		registry.addHandler(new SelectCharacterActionHandler());
		registry.addHandler(new CheckForUpdatesActionHandler());
		registry.addHandler(new ChatActionHandler());
		dispatch = new SimpleDispatch(registry);
	}
	
	public <R extends Result> R execute( Action<R> action ) throws DispatchException {
        try {            
            if ( dispatch == null ) {
                throw new ServiceException("No dispatch found for servlet '" + getServletName() + "' . Please verify your server-side configuration.");
            }
            HttpServletRequest request = this.getThreadLocalRequest(); 
            return dispatch.execute(action, request.getSession());
        } catch ( RuntimeException e ) {
            log( "Exception while executing " + action.getClass().getName() + ": " + e.getMessage(), e );
            throw new ServiceException(e);
        }
    }
}

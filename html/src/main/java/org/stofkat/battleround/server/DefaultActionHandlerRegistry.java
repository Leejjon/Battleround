package org.stofkat.battleround.server;

import java.util.List;
import java.util.Map;

import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.results.Result;

import com.google.inject.Singleton;

@Singleton
public class DefaultActionHandlerRegistry implements InstanceActionHandlerRegistry {
	private final Map<Class<? extends Action<?>>, ActionHandler<?, ?>> handlers;

    public DefaultActionHandlerRegistry() {
        handlers = new java.util.HashMap<Class<? extends Action<?>>, ActionHandler<?, ?>>( 100 );
    }

    public void addHandler( ActionHandler<?, ?> handler ) {
        handlers.put( handler.getActionType(), handler );
    }

    public boolean removeHandler( ActionHandler<?, ?> handler ) {
        return handlers.remove( handler.getActionType() ) != null;
    }
    
    @SuppressWarnings("unchecked")
    public <A extends Action<R>, R extends Result> ActionHandler<A, R> findHandler( A action ) {
        return (org.stofkat.battleround.server.ActionHandler<A, R> ) handlers.get( action.getClass() );
    }
    
    /**
     * Sets the specified <tt>actionHandlers</tt> as the only action handler held by this registry instance
     * 
     * @param actionHandlers the list of action handler to set
     */
    public void setActionHandlers(List<ActionHandler<?,?>> actionHandlers) {
        
        clearHandlers();
        
        for ( ActionHandler<?,?> actionHandler : actionHandlers )
            addHandler(actionHandler);
    }

    public void clearHandlers() {
        handlers.clear();
    }
}

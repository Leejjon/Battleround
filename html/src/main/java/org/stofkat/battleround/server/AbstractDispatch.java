package org.stofkat.battleround.server;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.exceptions.ActionException;
import org.stofkat.battleround.shared.dispatch.exceptions.DispatchException;
import org.stofkat.battleround.shared.dispatch.exceptions.UnsupportedActionException;
import org.stofkat.battleround.shared.dispatch.results.Result;

/**
 * Abstract base implementation of the {@link Dispatch}
 * 
 * <p>Provides basic action handler lookup and execution support. Lifecycle methods
 * may be overriden by implementors to receive notifications regarding action
 * execution and execution results.</p>
 * 
 * @author David Peterson
 * @author Robert Munteanu
 *
 */
public abstract class AbstractDispatch implements Dispatch {

    private static class DefaultExecutionContext implements ExecutionContext {
        private final AbstractDispatch dispatch;

        private final List<ActionResult<?, ?>> actionResults;

        private DefaultExecutionContext( AbstractDispatch dispatch ) {
            this.dispatch = dispatch;
            this.actionResults = new java.util.ArrayList<ActionResult<?, ?>>();
        }

        public <A extends Action<R>, R extends Result> R execute(A action, HttpSession session) throws DispatchException {
            return execute( action, true, session);
        }

        public <A extends Action<R>, R extends Result> R execute( A action, boolean allowRollback, HttpSession session)
                throws DispatchException {
            R result = dispatch.doExecute( action, this, session);
            if ( allowRollback )
                actionResults.add( new ActionResult<A, R>( action, result ) );
            return result;
        }

        /**
         * Rolls back all logged action/results.
         * 
         * @throws DispatchException
         */
        private void rollback() throws DispatchException {
            for ( int i = actionResults.size() - 1; i >= 0; i-- ) {
                ActionResult<?, ?> actionResult = actionResults.get( i );
                rollback( actionResult );
            }
        }

        private <A extends Action<R>, R extends Result> void rollback( ActionResult<A, R> actionResult )
                throws DispatchException {
            dispatch.doRollback( actionResult.getAction(), actionResult.getResult(), this );
        }

    };

    public <A extends Action<R>, R extends Result> R execute(A action, HttpSession session) throws DispatchException {
        DefaultExecutionContext ctx = new DefaultExecutionContext( this );
        try {
            return doExecute(action, ctx, session);
        } catch ( ActionException e ) {
            ctx.rollback();
            throw e;
        }
    }

    private <A extends Action<R>, R extends Result> R doExecute(A action, ExecutionContext ctx, HttpSession session)
            throws DispatchException {
        ActionHandler<A, R> handler = findHandler( action );
        
        try {
            executing(action, handler, ctx);
            R result  = handler.execute(action, ctx, session);
            executed(action, result, handler, ctx);
            return result;
        } catch ( DispatchException e) {
            failed(action, e, handler, ctx);
            throw e;
        } catch ( RuntimeException e) {
            failed(action, e, handler, ctx);
            throw e;
        } catch ( Error e) {
            failed(action, e, handler, ctx);
            throw e;
        }
        
    }

    private <A extends Action<R>, R extends Result> ActionHandler<A, R> findHandler( A action )
            throws UnsupportedActionException {
        ActionHandler<A, R> handler = getHandlerRegistry().findHandler( action );
        if ( handler == null )
            throw new UnsupportedActionException( action );

        return handler;
    }

    protected abstract ActionHandlerRegistry getHandlerRegistry();
    
    /**
     * Method invoked before executing the specified action with the specified handler.
     * 
     * <p>Any exception thrown from this method will prevent the normal execution of the action
     * and will be propagated.</p>
     * 
     * @param <A> the action type
     * @param <R> the result type
     * @param action the action to execute
     * @param handler the handler to execute it with
     * @param ctx the execution context
     * @throws DispatchException if the action execution should be cancelled 
     */
    protected <A extends Action<R>, R extends Result> void executing(A action, ActionHandler<A,R > handler, ExecutionContext ctx) throws DispatchException {
        
    }

    /**
     * Method invoked after the specified action has been succesfully executed with the specified handler.
     * 
     * <p>This method must not throw any exceptions.</p>
     * 
     * @param <A> the action type
     * @param <R> the result type
     * @param action the action to execute
     * @param result the execution result
     * @param handler the handler to execute it with
     * @param ctx the execution context
     */
    protected <A extends Action<R>, R extends Result> void executed(A action, R result, ActionHandler<A,R> handler, ExecutionContext ctx) {
        
    }
    
    /**
     * Method invoked after the specified action has been unsuccesfully executed with the specified handler.
     * 
     * <p>This method must not throw any exceptions.</p>
     * 
     * @param <A> the action type
     * @param <R> the result type
     * @param action the action to execute
     * @param e the exception thrown by the handler or by the {@link #executing(Action, ActionHandler, ExecutionContext) executing method}
     * @param handler the handler to execute it with
     * @param ctx the execution context
     */    
    protected <A extends Action<R>, R extends Result> void failed(A action, Throwable e, ActionHandler<A,R> handler, ExecutionContext ctx) {
        
    }

    private <A extends Action<R>, R extends Result> void doRollback( A action, R result, ExecutionContext ctx )
            throws DispatchException {
        ActionHandler<A, R> handler = findHandler( action );
        handler.rollback( action, result, ctx );
    }
}

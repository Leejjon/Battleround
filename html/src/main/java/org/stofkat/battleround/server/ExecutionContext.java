package org.stofkat.battleround.server;

import javax.servlet.http.HttpSession;

import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.exceptions.DispatchException;
import org.stofkat.battleround.shared.dispatch.results.Result;

/**
 * ExecutionContext instances are passed to {@link ActionHandler}s, and allows
 * them to execute sub-actions. These actions can be automatically rolled back
 * if any part of the action handler fails.
 * 
 * @author David Peterson, Leejjon
 */
public interface ExecutionContext {
    /**
     * Executes an action in the current context. If
     * <code>rollbackOnException</code> is set to <code>true</code>, the action
     * will be rolled back if the surrounding execution fails.
     * 
     * @param <A>
     *            The action type.
     * @param <R>
     *            The result type.
     * 
     * @param action
     *            The action.
     * @param allowRollback
     *            If <code>true</code>, any failure in the surrounding execution
     *            will trigger a rollback of the action.
     * @param session
     *            The session object which we temporarily abuse to replace the BattleRound database.
     * @return The result.
     * @throws DispatchException
     */
    <A extends Action<R>, R extends Result> R execute( A action, boolean allowRollback, HttpSession session ) throws DispatchException;

    /**
     * Executes an action in the current context. If the surrounding execution
     * fails, the action will be rolled back.
     * 
     * @param <A>
     *            The action type.
     * @param <R>
     *            The result type.
     * @param action
     *            The action.
     * @param session
     *            The session object which we temporarily abuse to replace the BattleRound database.
     * @return The result.
     * @throws DispatchException
     */
    <A extends Action<R>, R extends Result> R execute( A action, HttpSession session) throws DispatchException;
}

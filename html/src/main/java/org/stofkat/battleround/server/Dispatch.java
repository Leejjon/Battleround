package org.stofkat.battleround.server;

import javax.servlet.http.HttpSession;

import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.exceptions.DispatchException;
import org.stofkat.battleround.shared.dispatch.results.Result;

/**
 * Executes actions and returns the results.
 * 
 * @author David Peterson
 */
public interface Dispatch {

    /**
     * Executes the specified action and returns the appropriate result.
     * 
     * @param <T>
     *            The {@link Result} type.
     * @param action
     *            The {@link Action}.
     * @return The action's result.
     * @throws DispatchException
     *             if the action execution failed.
     */
    <A extends Action<R>, R extends Result> R execute(A action, HttpSession session) throws DispatchException;
}

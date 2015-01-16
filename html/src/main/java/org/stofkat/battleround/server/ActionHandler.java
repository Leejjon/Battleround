package org.stofkat.battleround.server;

import javax.servlet.http.HttpSession;

import org.stofkat.battleround.shared.dispatch.actions.Action;
import org.stofkat.battleround.shared.dispatch.exceptions.DispatchException;
import org.stofkat.battleround.shared.dispatch.results.Result;

/**
 * Instances of this interface will handle specific types of {@link Action}
 * classes.
 * 
 * @author David Peterson
 */
public interface ActionHandler<A extends Action<R>, R extends Result> {

	/**
	 * @return The type of {@link Action} supported by this handler.
	 */
	Class<A> getActionType();

	/**
	 * Handles the specified action.
	 * 
	 * @param <T>
	 *            The Result type.
	 * @param action
	 *            The action.
	 * @return The {@link Result}.
	 * @throws DispatchException
	 *             if there is a problem performing the specified action.
	 */
	R execute(A action, ExecutionContext context, HttpSession session) throws DispatchException;

	/**
	 * Attempts to roll back the specified action.
	 * 
	 * @param action
	 *            The action.
	 * @param result
	 *            The result of the action.
	 * @param context
	 *            The execution context.
	 * @throws DispatchException
	 */
	void rollback(A action, R result, ExecutionContext context)
			throws DispatchException;
}
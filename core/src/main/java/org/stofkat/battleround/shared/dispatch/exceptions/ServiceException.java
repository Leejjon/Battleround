package org.stofkat.battleround.shared.dispatch.exceptions;

/**
 * This is thrown by services when there is a low-level problem while processing
 * an action execution.
 * 
 * @author David Peterson
 */
public class ServiceException extends DispatchException {
	private static final long serialVersionUID = 1L;

	protected ServiceException() {}
    
    public ServiceException( String message ) {
        super( message );
    }

    public ServiceException( String message, Throwable cause ) {
        super( message, cause );
    }

    public ServiceException( Throwable cause ) {
        super( cause );
    }
}

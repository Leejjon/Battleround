package org.stofkat.battleround.shared.dispatch.exceptions;

import java.io.Serializable;

/**
 * An abstract superclass for exceptions that can be thrown by the Dispatch
 * system.
 * 
 * @author David Peterson
 */
public abstract class DispatchException extends Exception implements Serializable {
	private static final long serialVersionUID = 1L;
	private String causeClassname;
	
	protected DispatchException() {
    }

    public DispatchException( String message ) {
        super( message );
    }

    public DispatchException( Throwable cause ) {
        super( cause.getMessage() );
        this.causeClassname = cause.getClass().getName();
    }

    public DispatchException( String message, Throwable cause ) {
        super( message + " (" + cause.getMessage() + ")" );
        this.causeClassname = cause.getClass().getName();
    }

    public String getCauseClassname() {
        return causeClassname;
    }

    @Override
    public String toString() {
        return super.toString() + ( causeClassname != null ? " [cause: " + causeClassname + "]" : "" );
    }
}

package org.stofkat.battleround.database;

public class DatabaseException extends Exception {
	/**
	 * If this exception ever is going to be stored in the database, this version number should be increased every update.
	 */
	private static final long serialVersionUID = 1L;
	
	// Possible exception messages.
	public static final String classNotFound = "The database driver could not be found on the classpath.";
	public static final String couldNotLoadConfig = "Could not read the database config file.";
	public static final String couldNotCloseConnection = "Could not close the database connection.";
	public static final String databaseCorruptWarning = "Multiple rows returned while this column should have been unique. Somebody has been messing with the database.";
	public static final String illegalAccess = "No access to connect to the database.";
	public static final String invalidSchema = "Invalid schema name.";
	public static final String transActionFailed = "Transaction failed";
	
	public DatabaseException(String errorMessage) {
		super(errorMessage);
	}
	
	public DatabaseException(String errorMessage, Exception ex) {
		super(errorMessage, ex);
	}
}

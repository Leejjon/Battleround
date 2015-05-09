package org.stofkat.battleround.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	private static final String databaseName = "battleround";
	protected Connection connection;
	protected boolean autoCommit;
	private boolean committed = false;
	private boolean rolledBack = false;
	
	protected DatabaseConnection(boolean productionMode, boolean autoCommit) throws DatabaseException {
		this.autoCommit = autoCommit;
		try {
			if (productionMode) {
				// Load the class that provides the new "jdbc:google:mysql://" prefix.
				Class.forName("com.mysql.jdbc.GoogleDriver");
				connection = DriverManager.getConnection("jdbc:google:mysql://battle-round:battleround/" + databaseName + "?user=root");
			} else {
				String localTestMySqlUser = "root";
				String localTestMySqlPassword = "starwars";
				
				// Local MySQL instance to use during development.
				Class.forName("com.mysql.jdbc.Driver");
				connection = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/" +  databaseName, localTestMySqlUser, localTestMySqlPassword);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(DatabaseException.illegalAccess, e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new DatabaseException(DatabaseException.classNotFound, e);
		} 
	}
	
	protected DatabaseConnection(Connection connection, String schemaName) throws DatabaseException {
		this.connection = connection;
	}
	
	protected String getSchemaName() throws DatabaseException {
		return databaseName;
	}
	
	/**
	 * Commit the transaction.
	 */
	public void commit() {
		if (!autoCommit) {
			try {
				connection.commit();
				committed = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * If the query has failed, roll back the changes so far.
	 */
	public void rollback() {
		try {
			connection.rollback();
			rolledBack = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		if (connection != null) {
			try {
				if (connection != null) {
					if(!autoCommit && !committed && !rolledBack) {
						connection.rollback();
						// TODO: Throw a database exception on rollback, because nothing happened.
					}
					
					connection.close();
				}
			} catch (SQLException e) {
				// Well if we can't close it then we can't help the connection from timing out.
				e.printStackTrace();
			}
		}
	}
}

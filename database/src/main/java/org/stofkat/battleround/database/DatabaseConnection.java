package org.stofkat.battleround.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.stofkat.battleround.database.security.ValidationUtility;

public class DatabaseConnection {
	protected Connection connection;
	protected String schemaName;
	protected boolean autoCommit;
	private boolean committed = false;
	private boolean rolledBack = false;
	
	protected DatabaseConnection(Properties dbInfo, boolean autoCommit) throws DatabaseException {
		this.autoCommit = autoCommit;
		try {
			String hostname = dbInfo.getProperty("hostname");
			
			schemaName = dbInfo.getProperty("schema");
			if (schemaName == null) {
				throw new DatabaseException(DatabaseException.invalidSchema);
			}
			
			Class.forName("org.postgresql.Driver");
			
			// TODO: Also make a parameter for the hostname and port.
			connection = DriverManager.getConnection(
				"jdbc:postgresql://" + hostname + "/battleround", dbInfo);
			connection.setAutoCommit(autoCommit);
			
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
		this.schemaName = schemaName;
		
		if (schemaName == null) {
			throw new DatabaseException(DatabaseException.invalidSchema);
		}
	}
	
	/**
	 * This method should check if the schema name contains any invalid characters. 
	 * 
	 * @return The schema name.
	 * @throws DatabaseException 
	 */
	protected String getSchemaName() throws DatabaseException {
		if(ValidationUtility.onlyContainsCapitals(schemaName)) {
			return schemaName;
		}
		throw new DatabaseException(DatabaseException.invalidSchema);
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
	
	public static Properties getDbConfig() throws DatabaseException {
//		System.out.println(new File("").getAbsolutePath());
//		String dbInfoFileName = new File("").getAbsolutePath() + File.separatorChar + "config" + File.separatorChar + "dbinfo.properties";
		Properties dbInfo = new Properties();
		InputStream in = null;
		try {
//			dbInfo.load(new FileInputStream(dbInfoFileName));
			in = DatabaseConnection.class.getResourceAsStream("testdbinfo.properties");
			dbInfo.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new DatabaseException(DatabaseException.couldNotLoadConfig);
		} catch (IOException e) {
			e.printStackTrace();
			throw new DatabaseException(DatabaseException.couldNotLoadConfig);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
		return dbInfo;
	}
}

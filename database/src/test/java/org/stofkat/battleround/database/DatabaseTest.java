package org.stofkat.battleround.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Assert;

public abstract class DatabaseTest {
	
	/**
	 * Create all schemas, tables and constraints.
	 * 
	 * @throws DatabaseException
	 */
	public void createTables(DatabaseConnection databaseConnection) throws DatabaseException {
		String schemaName = databaseConnection.getSchemaName();
		
		try {
			PreparedStatement createSchema = databaseConnection.connection.prepareStatement("create schema " + schemaName);
			Assert.assertFalse(createSchema.execute());
		} catch (SQLException e) {
			Assert.fail("Failed to create schema: " + e.getMessage());
		} 

		try {
			String createIpAddressTable = "create table " + schemaName + ".ipaddress (" +
					"ipId SERIAL PRIMARY KEY," +
					"ipHash BYTEA UNIQUE NOT NULL" + 
				")";
			PreparedStatement createIpAddressTableStatement = databaseConnection.connection.prepareStatement(createIpAddressTable);
			Assert.assertFalse(createIpAddressTableStatement.execute());
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail("Failed to create the ipaddress table: " + e.getMessage());
		}
		
		try {
			String createAccountTable = "create table " + schemaName + ".account (" +
					"accountId SERIAL PRIMARY KEY," +
					"accountName VARCHAR(20) UNIQUE NOT NULL," +
					"passwordHash BYTEA NOT NULL," +
					"registerTimestamp TIMESTAMP NOT NULL," + 
					"ipIdOnRegister SERIAL REFERENCES " + schemaName + ".ipaddress(ipId)," +
					"emailAddress VARCHAR(80) unique" +
				")";
			PreparedStatement createAccountTableStatement = databaseConnection.connection.prepareStatement(createAccountTable);
			Assert.assertFalse(createAccountTableStatement.execute());
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail("Failed to create the account table: " + e.getMessage());
		}
		
		try {
			String createFailedCaptchaAttemptsTable = "create table " + schemaName + ".failed_captcha_attempts (" +
					"failureTimestamp TIMESTAMP NOT NULL, " +
					"ipId SERIAL REFERENCES " + schemaName + ".ipaddress(ipId)" +
				")";
			PreparedStatement createFailedCaptchaAttemptsTableStatement = databaseConnection.connection.prepareStatement(createFailedCaptchaAttemptsTable);
			Assert.assertFalse(createFailedCaptchaAttemptsTableStatement.execute());
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail("Failed to create the failed captcha attempts table: " + e.getMessage());
		}
		
		try {
			String createLoginsTable = "create table " + schemaName + ".logins (" +
					"loginId SERIAL PRIMARY KEY," +
					"loginTimestamp TIMESTAMP NOT NULL," +
					"ipId SERIAL REFERENCES " + schemaName + ".ipaddress(ipId)," +
					"successful BOOLEAN NOT NULL," +
 					"accountId SERIAL REFERENCES " + schemaName + ".account(accountId)" +
				")"; 
			PreparedStatement createLoginsTableStatement = databaseConnection.connection.prepareStatement(createLoginsTable);
			Assert.assertFalse(createLoginsTableStatement.execute());
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail("Failed to create the logins table: " + e.getMessage());
		}
		
		try {
			String createLevelTable = "create table " + schemaName + ".level (" +
					"levelId SERIAL PRIMARY KEY," +
					"levelName VARCHAR(50) NOT NULL," +
					"levelDescription VARCHAR(400) NULL, " +
					"minPlayers SMALLINT NOT NULL DEFAULT 3, " +
					"maxPlayers SMALLINT NOT NULL DEFAULT 9," +
					"levelWidth SMALLINT NOT NULL DEFAULT 16," +
					"levelHeight SMALLINT NOT NULL DEFAULT 16," +
					"forkable BOOLEAN DEFAULT TRUE, " +
					"ownerId SERIAL REFERENCES " + schemaName + ".account(accountId) " +
				")";
			PreparedStatement createLevelTableStatement = databaseConnection.connection.prepareStatement(createLevelTable);
			Assert.assertFalse(createLevelTableStatement.execute());
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail("Failed to create the level table: " + e.getMessage());
		}
		
		try {
			String createImageTabel = "create table " + schemaName + ".image (" +
					"imageId SERIAL PRIMARY KEY," +
					"imageFileName VARCHAR(50) NOT NULL," +
					"ownerId SERIAL REFERENCES " + schemaName + ".account(accountId)," +
					"usableByOtherThanOwner BOOLEAN NOT NULL," +
					"imageFile BYTEA NOT NULL" +
				")";
			PreparedStatement createImageTableStatement = databaseConnection.connection.prepareStatement(createImageTabel);
			Assert.assertFalse(createImageTableStatement.execute());
		} catch (SQLException e) {
			e.printStackTrace();
			Assert.fail("Failed to create the image table: " + e.getMessage());
		}
	}

	/**
	 * Drops the one and only schema containing all important tables, if it exists.
	 */
	public void dropSchemaIfExists(DatabaseConnection databaseConnection) {
		try {
			String schemaName = databaseConnection.getSchemaName();
			PreparedStatement createSchema = databaseConnection.connection.prepareStatement("drop schema if exists " + schemaName + " cascade");
			Assert.assertFalse(createSchema.execute());
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		} catch (DatabaseException e) {
			Assert.fail(e.getMessage());
		}
	}
}

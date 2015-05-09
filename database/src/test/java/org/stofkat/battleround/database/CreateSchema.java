package org.stofkat.battleround.database;

import org.junit.Assert;

/**
 * This class is meant to be run from Eclipse as Java Application to quickly create a database.
 * 
 * @author Leejjon
 */
public class CreateSchema extends DatabaseTest {
	private DatabaseConnection databaseConnection;
	
	public static void main(String[] args) {
		CreateSchema createDatabase = new CreateSchema();
		createDatabase.close();
	}
	
	public CreateSchema() {
		try {
			databaseConnection = new DatabaseConnection(false, true);
			
			createTables(databaseConnection);
		} catch (DatabaseException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} 
	}
	
	public void close() {
		databaseConnection.close();
	}
}

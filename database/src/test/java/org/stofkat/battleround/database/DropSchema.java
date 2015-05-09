package org.stofkat.battleround.database;

import org.junit.Assert;

/**
 * This class is meant to be run from Eclipse as Java Application to quickly delete a database schema.
 * 
 * @author Leejjon
 */
public class DropSchema extends DatabaseTest {
	private DatabaseConnection databaseConnection;
	
	public static void main(String[] args) {
		DropSchema dropSchema = new DropSchema();
		dropSchema.close();
	}
	
	public DropSchema() {
		try {
			databaseConnection = new DatabaseConnection(false, true);
			
		} catch (DatabaseException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} 
	}
	
	public void close() {
		dropSchemaIfExists(databaseConnection);
		databaseConnection.close();
	}
}

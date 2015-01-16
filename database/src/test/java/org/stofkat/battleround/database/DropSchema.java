package org.stofkat.battleround.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
			Properties dbInfo = new Properties();
			
			InputStream inputStream = DatabaseTest.class.getResourceAsStream("testdbinfo.properties");
			dbInfo.load(inputStream);
			inputStream.close();
			
			databaseConnection = new DatabaseConnection(dbInfo, true);
			
		} catch (DatabaseException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} 
	}
	
	public void close() {
		dropSchemaIfExists(databaseConnection);
		databaseConnection.close();
	}
}

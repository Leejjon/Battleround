package org.stofkat.battleround.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
			Properties dbInfo = new Properties();
			
			InputStream inputStream = DatabaseTest.class.getResourceAsStream("testdbinfo.properties");
			dbInfo.load(inputStream);
			inputStream.close();
			
			databaseConnection = new DatabaseConnection(dbInfo, false, true);
			
			createTables(databaseConnection);
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
		databaseConnection.close();
	}
}

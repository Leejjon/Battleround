package org.stofkat.battleround.database;

import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.stofkat.battleround.database.security.ValidationException;
import org.stofkat.battleround.server.security.EncryptionUtility;

public class LevelConnectionITCase extends DatabaseTest {
	private static AuthenticationConnection connection;
	private static byte[] ipAddress;
	private String username = "Leejjon";
	private String password = "starwars";
	private String email = "leejjon@gmail.com";
	private String levelName = "Space quest";
	
	@Before
	public void setupDatabaseConnection() {
		try {
			connection = new AuthenticationConnection(false, true);
			
			// First drop the previous database.
			dropSchemaIfExists(connection);
			
			// Now create the database in a clean state again for the tests.
			createTables(connection);
		} catch (DatabaseException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} 
	}
	
	@Test
	public void testLevelQueries() throws NoSuchAlgorithmException, DatabaseException, ValidationException {
		ipAddress = EncryptionUtility.getSHA512HashAsByteArray("1.3.3.7");
		
		long userId = connection.registerUser(username, password, 
				ipAddress, email);
		
		Assert.assertNotSame(0, userId);
		
		LevelConnection levelConnection = new LevelConnection(connection.connection, connection.getSchemaName());
		
		long levelId = levelConnection.createLevel(levelName, userId);
		Assert.assertNotSame(0, levelId);
	}
	
	@After
	public void closeConnection() {
		connection.commit();
		connection.close();
	}
}

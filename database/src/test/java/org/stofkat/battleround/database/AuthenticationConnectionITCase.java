package org.stofkat.battleround.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.stofkat.battleround.common.User;
import org.stofkat.battleround.database.security.ValidationException;
import org.stofkat.battleround.server.security.EncryptionUtility;

public class AuthenticationConnectionITCase extends DatabaseTest {
	private static AuthenticationConnection authenticationConnection;
	private static byte[] ipAddress;
	private String username = "Leejjon";
	private String password = "starwars";
	private String email = "leejjon@gmail.com";
	
	@Before
	public void setupDatabaseConnection() {
		try {
			Properties dbInfo = new Properties();
			
			InputStream inputStream = DatabaseTest.class.getResourceAsStream("testdbinfo.properties");
			dbInfo.load(inputStream);
			inputStream.close();
			
			authenticationConnection = new AuthenticationConnection(dbInfo, false, true);
			
			// First drop the previous database.
			dropSchemaIfExists(authenticationConnection);
			
			// Now create the database in a clean state again for the tests.
			createTables(authenticationConnection);
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
	
	@Test
	public void testAuthenticationMechanism() throws SQLException {
		// Test the captcha authentication.
		try {
			ipAddress = EncryptionUtility.getSHA512HashAsByteArray("1.3.3.7");
			try {
				Assert.assertTrue(authenticationConnection.isAllowedToAttemptCaptcha(ipAddress));
				System.out.println("We were allowed to attempt the captcha.");
			}  catch (DatabaseException e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
			
			int i = 0;
			while (i < 4) {
				try {
					authenticationConnection.saveFailedCaptchaAttempt(ipAddress);
					System.out.println("Failed captcha attempt stored succesfully.");
				} catch (DatabaseException e) {
					e.getCause().printStackTrace();
					Assert.fail(e.getMessage());
				}
				i++;
			}
			
			try {
				Assert.assertFalse(authenticationConnection.isAllowedToAttemptCaptcha(ipAddress));
				System.out.println("We aren't allowed to attempt answering the captcha for the next 10 minutes.");
			} catch (DatabaseException e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		// Test registering a user.
		try {
			if(!authenticationConnection.checkIfUserExists(username)) {
				if(authenticationConnection.checkIfEmailIsInUse(email)) {
					Assert.fail("E-mail address already in use.");
				} 
				long userId = authenticationConnection.registerUser(username, password, 
						ipAddress, email);
				
				Assert.assertNotSame(userId, 0);
				
				System.out.println("Account created succesfully!");
			} else {
				Assert.fail("Account already exists.");
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (DatabaseException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (ValidationException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		// Test logging in with the user we've just registered.
		try {
			User user = authenticationConnection.login(username, password, ipAddress);
			Assert.assertNotNull(user);
		} catch (DatabaseException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@After
	public void closeConnection() {
		authenticationConnection.commit();
		authenticationConnection.close();
	}
}

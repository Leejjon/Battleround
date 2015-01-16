package org.stofkat.battleround.database;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

import org.stofkat.battleround.database.security.ValidationException;
import org.stofkat.battleround.database.security.ValidationUtility;
import org.stofkat.battleround.server.security.EncryptionUtility;
import org.stofkat.battleround.common.User;
import org.stofkat.battleround.common.resources.Resources;

public class AuthenticationConnection extends DatabaseConnection {
	public static final int maxAccountNameLength = 20;
	private final int minAccountNameLength = 1;
	public static final int maxEmailAddressLength = 80;
	private final int minEmailAddressLength = 7; // Something like 'a@az.nl' Would be the shortest possible e-mail address.
	public static final int maxPasswordLength = 20;
	private final int minPasswordLength = 6;
	public static final String userObjectKey = "user";
	
	public AuthenticationConnection(Properties dbConfig, boolean autoCommit) throws DatabaseException {
		super(dbConfig, autoCommit);
	}
	
	public AuthenticationConnection(Connection connection, String schemaName) throws DatabaseException {
		super(connection, schemaName);
	}
	
	/**
	 * A simple query that checks if a user already exists or not.
	 * 
	 * @param userName
	 *            The user name we need to check.
	 * @throws DatabaseException
	 *             Is thrown if something goes wrong with connecting to the
	 *             database.
	 * @throws DatabaseException
	 *             , ValidationException.
	 */
	public boolean checkIfUserExists(String userName) throws DatabaseException, ValidationException {
		validateUserName(userName);
		
		ResultSet doesUserNameExistsResultSet = null;
		try {
			String checkIfUserExistsQuery = "select accountName from " + schemaName
					+ ".ACCOUNT where UPPER(accountName) = UPPER(?)";
			PreparedStatement checkIfUserExistsStatement = connection.prepareStatement(checkIfUserExistsQuery);
			checkIfUserExistsStatement.setFetchSize(1);
			checkIfUserExistsStatement.setString(1, userName);
			
			doesUserNameExistsResultSet = checkIfUserExistsStatement.executeQuery();
			
			if (doesUserNameExistsResultSet.next()) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			// TODO: Fix logging
			throw new DatabaseException(e.getMessage());
		} finally {
			if (doesUserNameExistsResultSet != null) {
				try {
					doesUserNameExistsResultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Check if the ip address is already stored.
	 * @param ipAddress
	 * @return The ipId primary key of the ip address record. Returns 0 if the ip address isn't found yet.
	 * @throws DatabaseException
	 */
	public int getIpAddressId(byte[] ipAddressHash) throws DatabaseException {
		String isIpAddressKnownQuery = "select ipId from " + getSchemaName() + ".ipaddress where ipHash = ?";
		try {
			PreparedStatement isIpAddressKnownStatement = connection.prepareStatement(isIpAddressKnownQuery);
			isIpAddressKnownStatement.setBytes(1, ipAddressHash);
			
			ResultSet ipIdResultSet = isIpAddressKnownStatement.executeQuery();
			
			if (ipIdResultSet.next()) {
				int ipId = ipIdResultSet.getInt(1);
				ipIdResultSet.close();
				return ipId;
			} else {
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
	}
	
	/**
	 * This query checks how many captcha attempts have been failed from this IP
	 * address. If more than three times have failed since the last 10 minutes,
	 * we won't have access.
	 * 
	 * @param ipAddressHash
	 * @return True if it's allowed, false if not.
	 * @throws DatabaseException
	 */
	public boolean isAllowedToAttemptCaptcha(byte[] ipAddressHash) throws DatabaseException {
		final int maximumNumberOfFailedCaptchaAttempts = 3;
		// This query returns how many failed captcha attempts there have been within the last 10 minutes.
		String isAllowedToAttemptCaptchaQuery = "select count(*) as numberOfFailedAttemptsWithinLimit " +
				"from " + getSchemaName() + ".FAILED_CAPTCHA_ATTEMPTS FCA inner join " +
				getSchemaName() + ".IPADDRESS IPS on FCA.ipId = IPS.ipId " +
				"where IPS.ipHash = ? " +
				"and FCA.FAILURETIMESTAMP > CURRENT_TIMESTAMP - INTERVAL '10 minutes'";
		try {
			PreparedStatement isAllowedToAttemptCaptchaStatement = connection.prepareStatement(isAllowedToAttemptCaptchaQuery);
			isAllowedToAttemptCaptchaStatement.setBytes(1, ipAddressHash);
			ResultSet isAllowedToAttemptCaptchaResultSet = isAllowedToAttemptCaptchaStatement.executeQuery();
			
			isAllowedToAttemptCaptchaResultSet.next();
			
			if (isAllowedToAttemptCaptchaResultSet.getInt(1) <= maximumNumberOfFailedCaptchaAttempts) {
				isAllowedToAttemptCaptchaResultSet.close();
				return true;
			} else {
				isAllowedToAttemptCaptchaResultSet.close();
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
	}
	
	/**
	 * This query checks how many failed logins have occured from this IP
	 * address. If more than 5 times have failed in the last 10 minutes, you're not allowed to login.
	 * @param ipAddressHash
	 * @return
	 * @throws DatabaseException 
	 */
	public boolean isAllowedToLogin(int ipId) throws DatabaseException {
		final int maximumNumberOfFailedLogins = 5;
		// This query returns how many failed login attempts there have been within the last 10 minutes.
		String isAllowedToLoginQuery = "select count(*) as numberOfFailedAttemptsWithinLimit " +
				"from " + getSchemaName() + ".LOGINS LI inner join " + getSchemaName() +
				".IPADDRESS IPS on LI.ipId = IPS.ipId " +
				"where IPS.ipId = ? and LI.successful = false " +
				"and LI.loginTimestamp > CURRENT_TIMESTAMP - INTERVAL '10 minutes'"; 
		try {
			PreparedStatement isAllowedToLoginStatement = connection.prepareStatement(isAllowedToLoginQuery);
			isAllowedToLoginStatement.setInt(1, ipId);
			ResultSet isAllowedToLoginResultSet = isAllowedToLoginStatement.executeQuery();
			
			isAllowedToLoginResultSet.next();
			
			if (isAllowedToLoginResultSet.getInt(1) <= maximumNumberOfFailedLogins) {
				isAllowedToLoginResultSet.close();
				return true;
			} else {
				isAllowedToLoginResultSet.close();
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
	}
	
	/**
	 * Executes all queries for the login procedure.
	 * 
	 * @param userName
	 * @param passwordHash
	 * @param ipAddressHash
	 * @return A user object with information on the user.
	 * @throws DatabaseException
	 */
	public User login(String userName, String password, byte[] ipAddressHash) throws DatabaseException {
		User user = null;
		int accountId;
		boolean isNewIp = false;
		boolean status = false;
		try {
			// See if it's a known IP address.
			int ipId = getIpAddressId(ipAddressHash);
			
			if (ipId == 0) {
				isNewIp = true;
				ipId = saveIpAddress(ipAddressHash);
			}
			
			// If it's a new IP address, it's not in any blocking list either,
			// so it may login. If it's an existing, see if it's blocked
			// anywhere.
			if (isNewIp || isAllowedToLogin(ipId)) {
				
				String loginQuery = "select accountId, accountName, emailAddress from " + getSchemaName() + ".account " +
						"where UPPER(accountName) = UPPER(?) and passwordHash = ?";
				PreparedStatement loginStatement = connection.prepareStatement(loginQuery);
				loginStatement.setString(1, userName);
				loginStatement.setBytes(2, EncryptionUtility.getSHA512HashAsByteArray(password, userName.toLowerCase()));
				ResultSet loginResultSet = loginStatement.executeQuery();
				
				if (loginResultSet.next()) {
					accountId = loginResultSet.getInt("accountId");
					userName = loginResultSet.getString("accountName");
					String email = loginResultSet.getString("emailAddress");
					
					user = new User(accountId, userName, email, ipAddressHash);
					
					String lastLoginQuery = "select loginTimestamp, ipHash from " + getSchemaName() + ".logins li " +
								"inner join " + getSchemaName() + ".ipaddress ips on li.ipId = ips.ipId " +
								"where loginTimestamp = (select max(loginTimestamp) from " + getSchemaName() + ".logins li2 " + 
								"where li2.successful = true and li2.accountId = ?)";
					PreparedStatement lastLoginStatement = connection.prepareStatement(lastLoginQuery);
					lastLoginStatement.setInt(1, accountId);
					
					ResultSet lastLoginResultSet = lastLoginStatement.executeQuery();
					
					if (lastLoginResultSet.next()) {
						user.setLastLoginTimestamp(lastLoginResultSet.getTimestamp("loginTimestamp").getTime());
						user.setLastLoginIPAddress(lastLoginResultSet.getBytes("ipHash"));
					} // else: There is no last login, this must be the first one.
					
					status = true;
					lastLoginResultSet.close();
					loginResultSet.close();
				} else {
					loginResultSet.close();
					user = null; // There's no user because the login failed.
					
					// The user name/password didn't match, but still check if the account exists. 
					// So we can warn the user people are trying to guess his password? Or something like that.
					String getUserIdIfValidUserQuery = "select accountId from " + getSchemaName() + ".account " +
							"where UPPER(accountName) = UPPER(?)";
					PreparedStatement getAccountIdIfValidAccountStatement = connection.prepareStatement(getUserIdIfValidUserQuery);
					getAccountIdIfValidAccountStatement.setString(1, userName);
					
					ResultSet getAccountIdResultSet = getAccountIdIfValidAccountStatement.executeQuery();
					if (getAccountIdResultSet.next()) {
						accountId = getAccountIdResultSet.getInt("accountId");
					} else {
						accountId = 0;
					}
					getAccountIdResultSet.close();
				}
				
				// No matter if the login succeeded or not, save it in the database.
				saveLoginAttempt(accountId, ipId, status);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(DatabaseException.transActionFailed, e);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new DatabaseException(DatabaseException.illegalAccess, e);
		} 
		
		return user;
	}
	
	/**
	 * Registers a user. Only call this method if both the user name and e-mail
	 * address have been validated, and they do not already exist in the
	 * database.
	 * 
	 * @param username Desired user name.
	 * @param emailAddress Can be null.
	 * @param ipAddressHash SHA 512 hash of the original ip address.
	 * @return The userId of the user that was just inserted.
	 * @throws DatabaseException
	 * @throws ValidationException
	 * @throws NoSuchAlgorithmException Probably won't ever occur.
	 */
	public long registerUser(String username, String password, byte[] ipAddressHash, String emailAddress) throws DatabaseException, ValidationException, NoSuchAlgorithmException {
		boolean useEmail = emailAddress != null;
		validateUserName(username);
		validatePassword(password);
		
		if (useEmail) {
			validateEmailAddress(emailAddress);
		}

		String registerUserQuery = "insert into " + getSchemaName()
				+ ".account(accountName, passwordHash, registerTimestamp, ipIdOnRegister";
		if(useEmail) {
			registerUserQuery += ", emailAddress";
		}
		registerUserQuery += ") values (?,?,?,?";
		if(useEmail) {
			registerUserQuery += ",?";
		}
		registerUserQuery += ")";
		
		PreparedStatement registerUserStatement;
		long userId = 0;
		try {
			long ipId = getIpAddressId(ipAddressHash);
			
			if (ipId == 0) {
				ipId = saveIpAddress(ipAddressHash);
			}
			
			registerUserStatement = connection.prepareStatement(registerUserQuery, Statement.RETURN_GENERATED_KEYS);
			registerUserStatement.setString(1, username);
			registerUserStatement.setBytes(2, EncryptionUtility.getSHA512HashAsByteArray(password, username.toLowerCase()));
			registerUserStatement.setTimestamp(3, new Timestamp(new Date().getTime()));
			registerUserStatement.setLong(4, ipId);
			if(useEmail) {
				registerUserStatement.setString(5, emailAddress);
			}
			
			registerUserStatement.executeUpdate();
			ResultSet rs = registerUserStatement.getGeneratedKeys();

			rs.next();
			userId = rs.getLong(1);
			rs.close();
			return userId;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		} 
	}
	
	/**
	 * If an attempt to answer the Captcha failed, we add the attempt to the database so we can block the user if he tries too many times.
	 * @param ipAddressHash
	 * @throws NoSuchAlgorithmException 
	 */
	public void saveFailedCaptchaAttempt(byte[] ipAddressHash) throws DatabaseException, NoSuchAlgorithmException {
		int ipId = getIpAddressId(ipAddressHash);
		
		if (ipId == 0) {
			ipId = saveIpAddress(ipAddressHash);
		}
		
		String saveFailedCaptchaAttemptQuery = "insert into " + getSchemaName() + ".failed_captcha_attempts(failureTimestamp, ipId) "
				+ "values(?, ?)";
		
		try {
			PreparedStatement saveFailedCaptchaAttemptStatement = connection.prepareStatement(saveFailedCaptchaAttemptQuery);
			saveFailedCaptchaAttemptStatement.setTimestamp(1, new Timestamp(new Date().getTime()));
			saveFailedCaptchaAttemptStatement.setInt(2, ipId);
			saveFailedCaptchaAttemptStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
	}
	
	/**
	 * Store the hash of the IP address in the ipaddress table.
	 * @param ipAddressHash
	 * @return The ipId primary key of the value that just got inserted.
	 * @throws DatabaseException
	 */
	public int saveIpAddress(byte[] ipAddressHash) throws DatabaseException {
		String saveIpAddressQuery = "insert into " + getSchemaName() + ".ipaddress(ipHash) values(?)";
		
		int ipId = 0;
		try {
			PreparedStatement saveIpAddressStatement = connection.prepareStatement(saveIpAddressQuery, Statement.RETURN_GENERATED_KEYS);
			saveIpAddressStatement.setBytes(1, ipAddressHash);
			
			saveIpAddressStatement.executeUpdate();
			ResultSet rs = saveIpAddressStatement.getGeneratedKeys();

			rs.next();
			ipId = rs.getInt(1);
			rs.close();
			return ipId;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		} 
	}
	
	/**
	 * If a login succeeds or fails, we save the login. This way we keep a
	 * history of logins so users can see if weird ip's have logged on their
	 * account. And after five failed login attempts we can block the client
	 * from trying for a few minutes. (to prevent brute forcing)
	 * 
	 * @param accountId
	 * @param ipId
	 * @param succesful A boolean, true means succeeded and false means failed.
	 * @throws DatabaseException
	 */
	public long saveLoginAttempt(int accountId, int ipId, boolean succesful) throws DatabaseException {
		long loginId = 0;
		try {
			String saveLoginQuery = "insert into " + getSchemaName() + ".logins (loginTimestamp, ipId, successful";
			if (accountId != 0) {
				saveLoginQuery += ", accountId";
			}
			saveLoginQuery += ") values (?,?,?";
			if(accountId != 0) {
				saveLoginQuery += ",?";
			}
			saveLoginQuery += ")";
			
			PreparedStatement saveLoginStatement = connection.prepareStatement(saveLoginQuery, Statement.RETURN_GENERATED_KEYS);
			saveLoginStatement.setTimestamp(1, new Timestamp(new Date().getTime()));
			saveLoginStatement.setInt(2, ipId);
			// 1 = succeeded, 0 = failed.
			saveLoginStatement.setBoolean(3, succesful ? true : false);
			if (accountId != 0) {
				saveLoginStatement.setInt(4, accountId);
			}
			saveLoginStatement.executeUpdate();
			
			ResultSet rs = saveLoginStatement.getGeneratedKeys();
			
			rs.next();
			loginId = rs.getLong(1);
			rs.close();
			return loginId;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
	}
	
	/**
	 * Validate the entered user name to see if it's safe to use in query's.
	 * 
	 * @param username
	 * @throws ValidationException
	 */
	private void validateUserName(String username) throws ValidationException {
		if (username == null) {
			throw new ValidationException(Resources.REGISTER_USERNAME_IS_NULL);
		}

		if (!ValidationUtility.onlyContainsLettersAndNumbers(username)) {
			throw new ValidationException(Resources.REGISTER_USERNAME_CONTAINS_INVALID_CHARACTERS);
		}

		if (username.length() > maxAccountNameLength || username.length() < minAccountNameLength) {
			throw new ValidationException(Resources.REGISTER_USERNAME_SIZE_INVALID);
		}
	}
	
	/**
	 * Validate the entered password.
	 * 
	 * @param password
	 * @throws ValidationException
	 */
	private void validatePassword(String password) throws ValidationException {
		if (password == null) {
			throw new ValidationException(Resources.REGISTER_PASSWORD_IS_NULL);
		}
		
		if (password.length() > maxPasswordLength || password.length() < minPasswordLength) {
			throw new ValidationException(Resources.REGISTER_PASSWORD_SIZE_INVALID);
		}
		
		if (!ValidationUtility.isThisPasswordValid(password)) {
			throw new ValidationException(Resources.REGISTER_PASSWORD_CONTAINS_INVALID_CHARACTERS);
		}
	}

	/**
	 * Validate the entered user name to see if it's safe to use in query's.
	 * 
	 * @param emailAddress
	 * @throws ValidationException
	 */
	private void validateEmailAddress(String emailAddress) throws ValidationException {
		if (!ValidationUtility.isThisEmailAddressValid(emailAddress)) {
			throw new ValidationException(Resources.REGISTER_EMAIL_INVALID);
		}

		if (emailAddress.length() > maxEmailAddressLength || emailAddress.length() < minEmailAddressLength) {
			throw new ValidationException(Resources.REGISTER_EMAIL_SIZE_INVALID);
		}
	}

	public boolean checkIfEmailIsInUse(String emailParameter) {
		// TODO Auto-generated method stub
		return false;
	}
}

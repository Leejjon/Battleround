package org.stofkat.battleround.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.stofkat.battleround.common.level.structure.Level;
import org.stofkat.battleround.common.resources.Resources;
import org.stofkat.battleround.database.security.ValidationException;
import org.stofkat.battleround.database.security.ValidationUtility;

public class LevelConnection extends DatabaseConnection {
	public LevelConnection(Properties dbConfig, boolean autoCommit) throws DatabaseException {
		super(dbConfig, autoCommit);
	}
	
	protected LevelConnection(Connection connection, String schemaName) throws DatabaseException {
		super(connection, schemaName);
	}
	
	public long createLevel(String levelName, long ownerId) throws DatabaseException, ValidationException {
		String createLevelQuery = "insert into " + getSchemaName() + ".level (levelName, ownerId) values (?, ?)";
		levelName = validateLevelName(levelName);
		
		ResultSet rs = null;
		try {
			PreparedStatement createLevelStatement = connection.prepareStatement(createLevelQuery, Statement.RETURN_GENERATED_KEYS);
			createLevelStatement.setString(1, levelName);
			createLevelStatement.setLong(2, ownerId);
			
			createLevelStatement.execute();
			
			rs = createLevelStatement.getGeneratedKeys();
			
			rs.next();
			return rs.getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Could not create level.");
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {}
			}
		}
	}
	
	/**
	 * Loads the level into the level object if the user is authorized.
	 * 
	 * @param userId
	 * @param levelId
	 * @return The level if the level with the levelId belongs to the user with the userId. Null if otherwise.
	 * @throws DatabaseException 
	 */
	public Level loadExistingLevel(long userId, long levelId) throws DatabaseException {
		String loadExistingLevelQuery = "select levelName, levelDescription, minPlayers, maxPlayers, levelWidth, levelHeight, forkable "
				+ "from " + getSchemaName() + ".level where ownerId = ? and levelId = ?";
		
		ResultSet rs = null;
		
		try {
			PreparedStatement loadExistingLevelStatement = connection.prepareStatement(loadExistingLevelQuery);
			loadExistingLevelStatement.setLong(1, userId);
			loadExistingLevelStatement.setLong(2, levelId);
			
			rs = loadExistingLevelStatement.executeQuery();
			
			if (rs.next()) {
				String levelName = rs.getString("levelName");
				String levelDescription = rs.getString("levelDescription");
				int minPlayers = rs.getInt("minPlayers");
				int maxPlayers = rs.getInt("maxPlayers");
				int levelWidth = rs.getInt("levelWidth");
				int levelHeight = rs.getInt("levelHeight");
				
				// TODO: Validate whether the level name and description don't contain any dangerous characters.
				
				Level level = new Level();
				level.setTitle(levelName);
				level.setDescription(levelDescription);
				return level;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Error loading existing level: " + levelId);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {}
			}
		}
		
		return null;
	}
	
	private String validateLevelName(String levelName) throws ValidationException {
		if (levelName == null) {
			throw new ValidationException(Resources.LEVEL_NAME_IS_NULL);
		}
		
		return ValidationUtility.escapeDangerousCharacters(levelName);
	}
}

package org.stofkat.battleround.database;

import java.sql.Connection;
import java.util.Properties;

public class SpriteConnection extends DatabaseConnection {
	
	public SpriteConnection(Properties dbConfig, boolean autoCommit) throws DatabaseException {
		super(dbConfig, autoCommit);
	}
	
	protected SpriteConnection(Connection connection, String schemaName) throws DatabaseException {
		super(connection, schemaName);
	}
	
//	public z
}

package org.stofkat.battleround.database;

import java.sql.Connection;

public class SpriteConnection extends DatabaseConnection {
	
	public SpriteConnection(boolean productionMode, boolean autoCommit) throws DatabaseException {
		super(productionMode, autoCommit);
	}
	
	protected SpriteConnection(Connection connection, String schemaName) throws DatabaseException {
		super(connection, schemaName);
	}
	
//	public z
}

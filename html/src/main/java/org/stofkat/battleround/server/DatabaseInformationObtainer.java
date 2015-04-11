package org.stofkat.battleround.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.stofkat.battleround.database.DatabaseException;

/**
 * Needed this method in every servlet so moved it here.
 * 
 * There's better ways to store database credentials via Context xml files on
 * for example tomcat, but I'm probably going to migrate to Google App Engine
 * anyway so this is enough for now during development.
 * 
 * @author Leejjon
 */
public class DatabaseInformationObtainer {
	private static final Logger log = Logger
			.getLogger(DatabaseInformationObtainer.class);

	public static Properties getDbInfo(ServletContext ctx)
			throws DatabaseException {
		Properties dbInfo = new Properties();
		InputStream configStream = ctx
				.getResourceAsStream("/WEB-INF/defaultdbinfo.properties");
		try {
			dbInfo.load(configStream);
		} catch (IOException e) {
			log.error("", e);
			throw new DatabaseException(DatabaseException.couldNotLoadConfig);
		}
		return dbInfo;
	}
}

package org.stofkat.battleround.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.stofkat.battleround.database.DatabaseException;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * Needed this method in every servlet so moved it here.
 * 
 * I wanted to have a proper way to store my database configuration details.
 * Important things were: 
 * 
 * <ul>
 * <li>Not having to manually change the database configuration in a file every time I deploy.</li>
 * <li>Not have the password hardcoded into the source code (and version control).</li>
 * </ul>
 * I followed the approach described in this blog:
 * http://pseudony.ms/blags/secret-keys-gae.html
 * 
 * The first time deploying on app engine, you need to modify the
 * defaultdbinfo.properties into the correct details. After that 
 * 
 * @author Leejjon
 */
public class DatabaseInformationObtainer {
	private static final Logger log = Logger
			.getLogger(DatabaseInformationObtainer.class);
	
	private static final String DATABASE_INFO_KEY = "DATABASE_INFO";
	private static final String DATABASE_INFO_KIND= "DATABASE_INFO_KIND";

	public static Properties getDbInfo(ServletContext ctx)
			throws DatabaseException {
		Properties dbInfo = new Properties();
		
		// We have to use the "createKey" method every time to get this key. 
		// Kind of confusing because this method makes it look like we are generating a new key.
		Key databaseInfoKey = KeyFactory.createKey(DATABASE_INFO_KIND, DATABASE_INFO_KEY);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity databaseInfoEntity = null;
		
		try {
			databaseInfoEntity = datastore.get(databaseInfoKey);
			for (Map.Entry<String, Object> dbInfoProperty : databaseInfoEntity.getProperties().entrySet()) {
				dbInfo.setProperty(dbInfoProperty.getKey(), dbInfoProperty.getValue().toString());
			}
		} catch (EntityNotFoundException entityNotFound) {
			Properties dbInfoFromDefaultFile = new Properties();
			InputStream configStream = ctx
					.getResourceAsStream("/WEB-INF/defaultdbinfo.properties");
			try {
				dbInfoFromDefaultFile.load(configStream);
			} catch (IOException e) {
				log.error(DatabaseException.couldNotLoadConfig, e);
				throw new DatabaseException(DatabaseException.couldNotLoadConfig);
			}
			
			databaseInfoEntity = new Entity(databaseInfoKey);
			for (Map.Entry<Object, Object> property : dbInfoFromDefaultFile.entrySet()) {
				databaseInfoEntity.setProperty(property.getKey().toString(), property.getValue());
				dbInfo.put(property.getKey(), property.getValue());
			}
			
			datastore.put(databaseInfoEntity);
		} 
		return dbInfo;
	}
}

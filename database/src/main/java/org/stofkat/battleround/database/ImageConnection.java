package org.stofkat.battleround.database;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

/**
 * 
 * @author Leejjon
 */
public class ImageConnection extends DatabaseConnection {
	private static final String spriteKind = "sprite"; 
	
	private enum ImageProperties {
		image(0),
		
		accessibility(2);
		
		private int id;
		
		private ImageProperties(int identifier) {
			id = identifier;
		}
		
		
	}
	
	public ImageConnection(boolean productionMode, boolean autoCommit) throws DatabaseException {
		super(productionMode, autoCommit);
	}
	
	protected ImageConnection(Connection connection, String schemaName) throws DatabaseException {
		super(connection, schemaName);
	}
	
	public void storeImage(String url, int imageAccessibility) throws MalformedURLException, IOException {
		URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();
		
		// Fetch the image at the location given by the url query string parameter
        HTTPResponse fetchResponse = fetchService.fetch(new URL(url));
        
        String fetchResponseContentType = null;
        for (HTTPHeader header : fetchResponse.getHeaders()) {
            // For each request header, check whether the name equals
            // "Content-Type"; if so, store the value of this header
            // in a member variable
            if (header.getName().equalsIgnoreCase("content-type")) {
                fetchResponseContentType = header.getValue();
                break;
            }
        }

        if (fetchResponseContentType != null) {
        	fetchResponse.getContent();
        }
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity imageEntity = new Entity(spriteKind);
		
		Map<String, Object> ImageEntityData = imageEntity.getProperties();
		
		datastore.put(imageEntity);
	}
}

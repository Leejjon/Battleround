package org.stofkat.battleround.common.resources;

/**
 * Copied from my CMS. Should be merged with a general resources file some time.
 *  
 * @author Leejjon
 */
public class StaticResources {
	// Resource paths.
	public static final String resourcesFile = "resources.properties";
	
	// Static error messages
	public final static String cannotLoadPropertiesFile = "Cannot load the properties from the resources.properties file.";
	public static String cannotFindEnumValueForKey(String fileName) {
		return "The " + fileName + " field does not contain a propertie with the given message key.";
	}
}

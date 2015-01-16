package org.stofkat.battleround.xml.parser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.stofkat.battleround.common.level.structure.Level;
import org.stofkat.battleround.xml.parser.XMLParseException.XMLParseExceptions;

public class XMLParseUtil {
	/**
	 * This method uses the XML inside the file to
	 * generate level objects from it.
	 * 
	 * @param levelFile
	 *            An file containing all the XML.
	 * @return The Level object constructed from the XML.
	 * @throws XMLParseException
	 *             If something went wrong with the unmarshalling, this
	 *             exception contains info.
	 */
	public static Level getLevelFromXml(InputStream levelFile) throws XMLParseException {
		if (levelFile != null) {
			try {
				Serializer serializer = new Persister();
				return serializer.read(Level.class, levelFile);
			} catch (Exception e) {
				System.out.println("getLevelFromXml(InputStream levelFile)" + XMLParseExceptions.INPUT_IS_NOT_VALID_WITH_XML_FORMAT.getMessage() + e);
				throw new XMLParseException(XMLParseExceptions.INPUT_IS_NOT_VALID_WITH_XML_FORMAT, e);
			}
		} else {
			System.out.println("getLevelFromXml(InputStream levelFile)" + XMLParseExceptions.INPUT_IS_NULL.getMessage());
			throw new XMLParseException(XMLParseExceptions.INPUT_IS_NULL);
		}
	}
	
	/**
	 * This method creates XML that can be written to a level file, from a level object.
	 * 
	 * @param level The level object
	 * @return Warning, perhaps you need to close it.
	 * @throws XMLParseException Only appears if the Level parameter is null.
	 */
	public static ByteArrayOutputStream getXmlFileFromLevel(Level level) throws XMLParseException {
		if (level != null) {
			try {
				Serializer serializer = new Persister();
				ByteArrayOutputStream output = new ByteArrayOutputStream();
			    serializer.write(level, output);
			    return output;
			} catch (Exception e) {
				System.out.println("getXmlFileFromLevel(Level level)" + XMLParseExceptions.INPUT_IS_NOT_VALID_WITH_XML_FORMAT.getMessage() + e);
				throw new XMLParseException(XMLParseExceptions.INPUT_IS_NOT_VALID_WITH_XML_FORMAT, e);
			}
		} else {
			System.out.println("getXmlFileFromLevel(Level level)" + XMLParseExceptions.INPUT_IS_NULL.getMessage());
			throw new XMLParseException(XMLParseExceptions.INPUT_IS_NULL);
		}
	}
	
	/**
	 * I needed a way to read XML from files into a string, this is just google copy pasta.
	 * 
	 * @see http://stackoverflow.com/questions/2492076/android-reading-from-an-input-stream-efficiently
	 * @param inputStream
	 * @return String containing XML.
	 * @throws XMLParseException Is thrown when we cannot read the file.
	 */
	public static String inputStreamToString(InputStream inputStream) throws XMLParseException {
		if (inputStream != null) {
			StringBuilder total = new StringBuilder();
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
				String line;
				while ((line = r.readLine()) != null) {
					total.append(line);
				}
			} catch (IOException e) {
				String fileError = "Could not read the file.";
				System.out.println("inputStreamToString(InputStream inputStream)->" + fileError + e);
				throw new XMLParseException(fileError, e);
			}
			return total.toString();
		} else {
			System.out.println("inputStreamToString(InputStream inputStream)->" + XMLParseExceptions.INPUT_IS_NULL.getMessage());
			throw new XMLParseException(XMLParseExceptions.INPUT_IS_NULL);
		}
	}
}

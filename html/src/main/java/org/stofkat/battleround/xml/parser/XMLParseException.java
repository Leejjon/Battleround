package org.stofkat.battleround.xml.parser;

/**
 * This XMLParse exception should be used for errors while
 * marshalling/unmarshalling.
 * 
 * @author Leejjon
 */
public class XMLParseException extends Exception {
	/**
	 * In case this exception will be serialized.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This enumeration contains all of the hardcoded errormessages related to
	 * XML parsing.
	 * 
	 * @author Leejjon
	 */
	public static enum XMLParseExceptions {
		INPUT_IS_NULL("Don't give me a parameter that is null."), 
		INPUT_IS_NOT_VALID_WITH_XML_FORMAT("This input is not compliant with our XML format.");

		private final String message;

		private XMLParseExceptions(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	public XMLParseException(String customMessage) {
		super(customMessage);
	}

	public XMLParseException(String customMessage, Throwable throwable) {
		super(customMessage, throwable);
	}

	public XMLParseException(XMLParseExceptions knownErrorMessage) {
		super(knownErrorMessage.getMessage());
	}

	public XMLParseException(XMLParseExceptions knownErrorMessage, Throwable throwable) {
		super(knownErrorMessage.getMessage(), throwable);
	}
}

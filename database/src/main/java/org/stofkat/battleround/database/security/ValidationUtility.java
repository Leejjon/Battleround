package org.stofkat.battleround.database.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class comes from my home-made CMS for the leejjon.net website.
 *  
 * @author Leejjon
 */
public class ValidationUtility {
	private static final int lowestCapital = 65;
	private static final int highestCapital = 90;
	private static final int lowestNumber = 48;
	private static final int highestNumber = 57;
	private static final int lowestNonCapital = 97;
	private static final int highestNonCapital = 122;
	private static final int lowestPasswordCharacter = 33;
	private static final int highestPasswordCharacter = 126;
	private static final int hexColorLength = 6;

	/**
	 * The following potential dangerous characters will be escaped:
	 * < > " ' \
	 * @param input The string that may contain dangerous characters.
	 * @return The input string with it's dangerous characters escaped.
	 */
	public static String escapeDangerousCharacters(String input) {
		String output = "";
		char[] inputArray = input.toCharArray();
		
		for (int i = 0; i < inputArray.length; i++) {
			if (inputArray[i] == '<') {
				output += "&lt;";
			} else if (inputArray[i] == '>') {
				output += "&gt;";
			} else if (inputArray[i] == '"') {
				output += "&quot;";
			} else if (inputArray[i] == '\'') {
				output += "&#039;";
			} else if (inputArray[i] == '\\') {
				output += "&#092;";
			} else {
				output += String.valueOf(inputArray[i]);
			}
		}
		return output;
	}
	
	/**
	 * The following characters are allowed in passwords.
	 * ! " ' # % & ( ) * + , - . / ~ 0 1 2 3 4
	 * : ; < = > ? @ [ \ ] ^ _ ` { | } 5 6 7 8 9
	 * a b c d e f g h i j k l m n o p q r s t u v w x y z
	 * A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
	 * @param input
	 * @return True if valid, false otherwise.
	 */
	public static boolean isThisPasswordValid(String input) {
		char[] inputArray = input.toCharArray();
		
		for (char character : inputArray) {
			if (character < lowestPasswordCharacter || character > highestPasswordCharacter) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Ok, I really hate regex but the internet said this was a good one for validating e-mail addresses.
	 * @param emailAddress
	 * @return A boolean if the e-mail is valid or not.
	 */
	public static boolean isThisEmailAddressValid(String emailAddress) {
		if(emailAddress == null) {
			return false;
		}
		
		String expression = "^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = emailAddress;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}
	
	/**
	 * Checks if a color is valid.
	 * @param hexColor The color in hexadecimal form.
	 * @return
	 */
	public static boolean isColorValid(String hexColor) {
		final char firstHexLetter = 'A';
		final char lastHexLetter = 'F';
		if (hexColor != null && hexColor.length() == hexColorLength) {
			for (char c : hexColor.toCharArray()) {
				if (!Character.isDigit(c)) {
					if (!(c >= firstHexLetter && c <= lastHexLetter)) {
						return false;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if the input string contains anything else than capitals.
	 * 
	 * @param input
	 *            The string to be validated.
	 * @throws ValidationException
	 */
	public static boolean onlyContainsCapitals(String input) {
		char[] inputArray = input.toCharArray();

		for (char character : inputArray) {
			if (character < lowestCapital || character > highestCapital) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the input string contains anything else than letters or
	 * numbers.
	 * 
	 * @param input
	 *            The string to be validated.
	 * @returns A boolean that is true if the input string only contains letters
	 *          and/or numbers.
	 */
	public static boolean onlyContainsLettersAndNumbers(String input) {
		char[] inputArray = input.toCharArray();

		for (char character : inputArray) {
			if (character < lowestNumber || (character > highestNumber && character < lowestCapital)
					|| (character > highestCapital && character < lowestNonCapital) || character > highestNonCapital) {
				return false;
			}
		}
		return true;
	}
}

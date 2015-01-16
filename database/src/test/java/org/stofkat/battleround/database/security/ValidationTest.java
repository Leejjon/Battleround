package org.stofkat.battleround.database.security;

import org.junit.Assert;
import org.junit.Test;

public class ValidationTest {
	private final String invalidValuePassed = "Invalid value passed the validate method.";
	private final String validValueDidntPass = "Valid value contains invalid characters.";

	@Test
	public void testCapitalValidation() {
		String validValue = "AADDASDS";
		String[] invalidValues = new String[] { "AADFfSS", "#@$", "898908" };

		for (int i = 0; i < invalidValues.length; i++) {
			if (ValidationUtility.onlyContainsCapitals(invalidValues[i])) {
				Assert.fail(invalidValuePassed);
			}
		}

		if (!ValidationUtility.onlyContainsCapitals(validValue)) {
			Assert.fail(validValueDidntPass);
		}
	}
	
	@Test
	public void testLettersAndNumbersValidations() {
		String validValue = "hahaOK09";
		String[] invalidValues = new String[] { "newline\n", "newline\r", "tab\t", "<", ">", "\"", "/", "-" };

		for (int i = 0; i < invalidValues.length; i++) {
			if (ValidationUtility.onlyContainsLettersAndNumbers(invalidValues[i])) {
				Assert.fail(invalidValuePassed);
			}
		}

		if (!ValidationUtility.onlyContainsLettersAndNumbers(validValue)) {
			Assert.fail(validValueDidntPass);
		}
	}
	
	@Test
	public void testPasswordCharacters() {
		String[] validValues = new String[] { "`~!@#$", "123456", "starwars", "1234567890asdfghjkl;" };
		String[] invalidValues = new String[] { "\r", " ", "\t"};
		
		for (String invalidValue : invalidValues) {
			Assert.assertEquals(false, ValidationUtility.isThisPasswordValid(invalidValue));
		}
		
		for (String validValue : validValues) {
			Assert.assertEquals(true, ValidationUtility.isThisPasswordValid(validValue));
		}
	}

	@Test
	public void testEmailAdresses() {
		String[] validEmails = new String[] { "nospam@gmail.com", "name.surname@jkj.nl", "429861@student.inholland.nl" };
		String[] invalidEmails = new String[] { null, "<script>alert('hoi')</script>@gmail.com", "@hjhdfsd@blb.com",
				"fxfd@.com", "\"@blabla.com", "" };

		for (int i = 0; i < validEmails.length; i++) {
			if(!ValidationUtility.isThisEmailAddressValid(validEmails[i])){ 
				Assert.fail(validValueDidntPass);
			}
		}

		for (int i = 0; i < invalidEmails.length; i++) {
			if(ValidationUtility.isThisEmailAddressValid(invalidEmails[i])) {
				Assert.fail(invalidValuePassed);
			}
		}
	}
	
	@Test
	public void testEscapeDangerousCharacters() {
		String[][] pairs = new String[][] { {"tralalala", "tralalala"}, {"trala<trala", "trala&lt;trala"}, {"< > \" \' \\", "&lt; &gt; &quot; &#039; &#092;"}};
		
		for (int i = 0; i < pairs.length; i++) {
			Assert.assertEquals(pairs[i][1], ValidationUtility.escapeDangerousCharacters(pairs[i][0]));
		}
	}
}

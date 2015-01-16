// Constants, do not modify.
var lowestCapital = 65;
var highestCapital = 90;
var lowestNumber = 48;
var highestNumber = 57;
var lowestNonCapital = 97;
var highestNonCapital = 122;
var lowestPasswordCharacter = 33;
var highestPasswordCharacter = 126;

/**
 * Returns true if the input does only contains letters and/or numbers, false if invalid characters have been found.
 * @param input The input string 
 */
function onlyContainsLettersAndNumbers(input) {
	for (var i = 0; i < input.length; i++) {
		if (input.charCodeAt(i) < lowestNumber
				|| (input.charCodeAt(i) > highestNumber && input.charCodeAt(i) < lowestCapital)
				|| (input.charCodeAt(i) > highestCapital && input.charCodeAt(i) < lowestNonCapital)
				|| input.charCodeAt(i) > highestNonCapital) {
			return false;
		}
	}
	return true;
}

/**
 * The following characters are allowed in passwords.
 * ! " ' # % & ( ) * + , - . / ~ 0 1 2 3 4
 * : ; < = > ? @ [ \ ] ^ _ ` { | } 5 6 7 8 9
 * a b c d e f g h i j k l m n o p q r s t u v w x y z
 * A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
 * @param input
 */
function isThisPasswordValid(input) {
	for (var i = 0; i < input.length; i++) {
		if (input.charCodeAt(i) < lowestPasswordCharacter || input.charCodeAt(i) > highestPasswordCharacter) {
			return false;
		}
	}
	return true;
}

/**
 * I suck at regex so I don't have clue if this is flawless.
 * Source: http://www.white-hat-web-design.co.uk/blog/javascript-validation/
 * @param email
 * @returns {Boolean}
 */
function isThisEmailValid(email) {
	var emailRegularExpression = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
	if(emailRegularExpression.test(email)) {
		return true;
	} else {
		return false;
	}
}
// The id's of the input fields hardcoded.
var registerInputs = new Array("usernameInputField", "passwordInputField", "emailInputField", "captchaInputField", "registerButton");

// Returns an array of key/value pairs. 
function getResources(key) {
	var resources = {};
	
	resources["register.captcha.answer.invalid"] = "Please enter the correct number of sheep.";
	resources["register.captcha.images.not.found"] = "No sheep found!";
	resources["register.captcha.refresh"] = "You're watching to a cached image, refresh it first!";
	resources["register.captcha.too.many.failed.attempts"] = "You have failed three times, you can try again in 10 minutes.";
	resources["register.captcha.wrong.answer"] = "Wrong number of sheep, please try again!";
	resources["register.database.could.not.connect"] = "Could not connect to the database. Please try again later.";
	resources["register.email.already.in.use"] = "The entered e-mail address is already in use.";
	resources["register.email.invalid"] = "The entered e-mail address is invalid.";
	resources["register.email.size.invalid"] = "The entered e-mail address size is invalid. It should be minimal 7 characters and maximal 80 characters.";
	resources["register.password.contains.invalid.characters"] = "The password may only contain letters and/or numbers.";
	resources["register.password.is.null"] = "Please enter a password.";
	resources["register.password.size.invalid"] = "The entered password should have a size of minimal 5 and maximal 20 characters.";
	resources["register.succeeded"] = "Registration complete. You can now <a href=\"login.html\">login</a>!";
	resources["register.unable.to.register"] = "An error occurred, you were unable to register.";
	resources["register.username.already.exists"] = "This user name has already been taken, please try another.";
	resources["register.username.contains.invalid.characters"] = "The user name may only contain letters and/or numbers.";
	resources["register.username.is.null"] = "Please enter a valid user name.";
	resources["register.username.size.invalid"] = "The given user name size is invalid. It's size should be at least one character and no more than 20 characters.";
	
	return resources[key];
}

/**
 * Simply try to register.
 */
function tryToRegister() {
	if(!validateRegisterUsername(true)) {
		return;
	} else { // This clearing only has to happen when validating the first field.
		document.getElementById("validationMessage").innerHTML = ""; // To overwrite the old message.
	}
	if(!validateRegisterPassword(true)) {
		return;
	} 
	if(!validateEmailAddress(true)) {
		return;
	} 
	if(!validateCaptchaNumber(true)) {
		return;
	}
	
	var userName = document.getElementById("usernameInputField").value;
	var password = document.getElementById("passwordInputField").value;
	var emailAddress = document.getElementById("emailInputField").value;
	var captchaNumber = document.getElementById("captchaInputField").value;
	register(userName, password, emailAddress, captchaNumber);
}

function register(userName, password, email, captchaNumber) {
	var xmlHttp = new XMLHttpRequest();

	if (window.ActiveXObject) {
		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	var url = "http://192.168.2.9:8888/Register";
	var params = "username=" + userName + "&password=" + password;
	
	if(email.length != 0) {
		params += "&email=" + email;
	}
	
	params += "&captcha=" + captchaNumber;

	xmlHttp.open("POST", url, true);

	// Send the proper header information along with the request
	xmlHttp.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	xmlHttp.setRequestHeader("Content-length", params.length);
	xmlHttp.setRequestHeader("Connection", "close");

	xmlHttp.onreadystatechange = function() {// Call a function when the state changes.
		if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
			if(xmlHttp.responseText == "register.succeeded") {
				document.getElementById("registerForm").innerHTML = getResources("register.succeeded");
				return;
			} 
			
			var captchaImage = document.getElementById("captchaImage");
			captchaImage.src = "/Captcha?t=" + new Date().getTime();
			document.getElementById("captchaInputField").value = "";
			var captchaIcon = document.getElementById("captchaValidIcon");
			captchaIcon.className = "";
			// TODO: Write a better way to check if it starts with register.username than above.
			if(xmlHttp.responseText.indexOf("register.username") != -1) {
				document.getElementById("usernameValidIcon").className = "invalidIcon";
			} else if(xmlHttp.responseText.indexOf("register.password") != -1) {
				document.getElementById("passwordValidIcon").className = "invalidIcon";
			} else if(xmlHttp.responseText.indexOf("register.email") != -1) {
				document.getElementById("emailValidIcon").className = "invalidIcon";
			}
			document.getElementById("validationMessage").innerHTML = getResources(xmlHttp.responseText);
		}
	};
	xmlHttp.send(params);
}

/**
 * Validate the user name on not being null, having the right length, and containing valid characters.
 * @returns {Boolean}
 */
function validateRegisterUsername(showWarning) {
	var maxAccountNameLength = 20;
	var minAccountNameLength = 1;
	var userNameElement = document.getElementById("usernameInputField");
	var input = userNameElement.value;
	if (input.length < minAccountNameLength || input.length > maxAccountNameLength) {
		if(showWarning) {
			document.getElementById("validationMessage").innerHTML = getResources("register.username.size.invalid");
			userNameElement.focus();
		}
	} else if (!onlyContainsLettersAndNumbers(input)) {
		if(showWarning) {
			document.getElementById("validationMessage").innerHTML = getResources("register.username.contains.invalid.characters");
			userNameElement.focus();
		}
	} else {
		document.getElementById("usernameValidIcon").className = "validIcon";
		return true;
	}
	document.getElementById("usernameValidIcon").className = "invalidIcon";
	return false;
}

/**
 * Validate the password on not being null, having the right length, and containing valid characters.
 * @returns {Boolean}
 */
function validateRegisterPassword(showWarning) {
	var maxPasswordLength = 20;
	var minPasswordLength = 6;
	var passwordElement = document.getElementById("passwordInputField");
	var input = passwordElement.value;
	if (input.length < minPasswordLength || input.length > maxPasswordLength) {
		if(showWarning) {
			document.getElementById("validationMessage").innerHTML = getResources("register.password.size.invalid");
			passwordElement.focus();
		}
	} else if(!isThisPasswordValid(input)) {
		if(showWarning) {
			document.getElementById("validationMessage").innerHTML = getResources("register.password.contains.invalid.characters");
			passwordElement.focus();
		}
	} else {
		document.getElementById("passwordValidIcon").className = "validIcon";
		return true;
	}
	document.getElementById("passwordValidIcon").className = "invalidIcon";
	return false;
}

/**
 * Validate the e-mail address on having the right length and containing valid characters.
 * @returns {Boolean}
 */
function validateEmailAddress(showWarning) {
	var maxEmailAddressLength = 80;
	var minEmailAddressLength = 7; // Something like 'a@az.nl' Would be the shortest possible e-mail address.
	var emailElement = document.getElementById("emailInputField");
	var input = emailElement.value;
	if (input.length != 0 && (input.length < minEmailAddressLength || input.length > maxEmailAddressLength)) {
		if(showWarning) {
			document.getElementById("validationMessage").innerHTML = getResources("register.email.size.invalid");
			emailElement.focus();
		}
	} else if (input.length != 0 && !isThisEmailValid(input)) {
		if(showWarning) {
			document.getElementById("validationMessage").innerHTML = getResources("register.email.invalid");
			emailElement.focus();
		}
	} else {
		if(input.length != 0) {
			document.getElementById("emailValidIcon").className = "validIcon";
			return true;
		} else {
			document.getElementById("emailValidIcon").className = "";
			emailElement.focus();
			return false;
		}
	}
	document.getElementById("emailValidIcon").className = "invalidIcon";
	return false;
}

/**
 * Check if the user has entered a number that could be valid.
 * @returns {Boolean}
 */
function validateCaptchaNumber(showWarning) {
	var highestNumberOfSheepsPossible = 9;
	var captchaElement = document.getElementById("captchaInputField");
	var captchaValue = parseInt(captchaElement.value);
	if (captchaValue <= 0 || captchaValue > highestNumberOfSheepsPossible || isNaN(captchaValue)) {
		if(showWarning) {
			document.getElementById("validationMessage").innerHTML = getResources("register.captcha.answer.invalid");
		}
		captchaElement.focus();
	} else {
		document.getElementById("captchaValidIcon").className = "validIcon";
		return true;
	}
	document.getElementById("captchaValidIcon").className = "invalidIcon";
	return false;
}
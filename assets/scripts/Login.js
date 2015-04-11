// Returns an array of key/value pairs. 
function getResources(key) {
	var resources = {};
	
	resources["login.already.logged.in"] = "You are already logged in! Log out first if you want to try again.";
	resources["login.error"] = "The login failed because an error occurred, Sorry!";
	resources["login.failed"] = "User name and password do not match.";
	resources["login.successful"] = "Login succeeded.";
	
	return resources[key];
}

/**
 * The login function.
 */
function tryToLogin() {
	var userName = document.getElementById("usernameInputField").value;
	
	if (userName.length <= 0 && !onlyContainsLettersAndNumbers(userName)) {
		document.getElementById("usernameValidIcon").className = "invalidIcon";
		return;
	} else {
		document.getElementById("usernameValidIcon").className = "";
		document.getElementById("validationMessage").innerHTML = ""; // To overwrite the old message.
	}
	
	var minPasswordLength = 6;
	var password = document.getElementById("passwordInputField").value;
	if (password.length < minPasswordLength) {
		document.getElementById("passwordValidIcon").className = "invalidIcon";
	} else {
		document.getElementById("passwordValidIcon").className = "";
	}
	
	var xmlHttp = new XMLHttpRequest();
	if (window.ActiveXObject) {
		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
	
	var url = "http://127.0.0.1:8080/Login";
	var params = "username=" + userName + "&password=" + password;
	xmlHttp.open("POST", url, true);
	
	// Send the proper header information along with the request
	xmlHttp.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	xmlHttp.setRequestHeader("Content-length", params.length);
	xmlHttp.setRequestHeader("Connection", "close");
	xmlHttp.withCredentials = "true";
	xmlHttp.onreadystatechange = function() {// Call a function when the state changes.
		if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
			if(xmlHttp.responseText == "login.successful") {
				document.getElementById("loginForm").innerHTML = getResources("login.successful");
				var setCookieHeader = xmlHttp.getResponseHeader('Set-Cookie');
				
				// If we're running in a JavaFX webview, we're running on Windows, Linux or OS X.
				// This means we can start the LWJGL version of the game rather than the HTML5 version.
				if (typeof java !== 'undefined') {
					java.start(setCookieHeader.split(";")[0].split("=")[1]);
				} else {
					window.location.href = "http://127.0.0.1:8080/assets/pages/levels.html";
				}	
			} else {
				document.getElementById("validationMessage").innerHTML = getResources(xmlHttp.responseText);
			}
		}
	};
	xmlHttp.send(params);
}

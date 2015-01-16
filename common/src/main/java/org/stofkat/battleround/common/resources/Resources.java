package org.stofkat.battleround.common.resources;

/**
 * This enum class contains most messages to the user. They all have a message
 * key which can be used to retrieve the specific piece of text from the
 * resources.properties.
 * 
 * @author Leejjon
 */
public enum Resources {
	//  Resources that can be used on any page.
	AUTHENTICATION_WRONG_IP_FOR_THIS_SESSION(ResourceCategory.General, "authentication.wrong.ip.for.this.session", true),
	
	LEVEL_NAME_IS_NULL(ResourceCategory.Level, "level.name.is.null", false),
	
	LOGIN_ALREADY_LOGGED_IN(ResourceCategory.Login, "login.already.logged.in", true),
	LOGIN_ERROR(ResourceCategory.Login, "login.error", true),
	LOGIN_FAILED(ResourceCategory.Login, "login.failed", true),
	LOGIN_SUCCESSFUL(ResourceCategory.Login, "login.successful", true),
	LOGOUT_ALREADY_LOGGED_OUT(ResourceCategory.Logout, "logout.already.logged.out", true),
	LOGOUT_FAILED(ResourceCategory.Logout, "logout.failed", true),
	LOGOUT_SUCCEEDED(ResourceCategory.Logout, "logout.succeeded", true),
	
	REGISTER_CAPTCHA_REFRESH(ResourceCategory.Register, "register.captcha.refresh", true),
	REGISTER_CAPTCHA_TOO_MANY_FAILED_ATTEMPTS(ResourceCategory.Register, "register.captcha.too.many.failed.attempts", true),
	REGISTER_CAPTCHA_WRONG_ANSWER(ResourceCategory.Register, "register.captcha.wrong.answer", true),
	REGISTER_DATABASE_COULD_NOT_CONNECT(ResourceCategory.Register, "register.database.could.not.connect"),
	REGISTER_EMAIL_ALREADY_IN_USE(ResourceCategory.Register, "register.email.already.in.use", true), 
	REGISTER_EMAIL_INVALID(ResourceCategory.Register, "register.email.invalid", true), 
	REGISTER_EMAIL_SIZE_INVALID(ResourceCategory.Register, "register.email.size.invalid", true), 
	REGISTER_PASSWORD_CONTAINS_INVALID_CHARACTERS(ResourceCategory.Register, "register.password.contains.invalid.characters", true),
	REGISTER_PASSWORD_IS_NULL(ResourceCategory.Register, "register.password.is.null", true),
	REGISTER_PASSWORD_SIZE_INVALID(ResourceCategory.Register, "register.password.size.invalid", true),
	REGISTER_SUCCEEDED(ResourceCategory.Register, "register.succeeded", true),
	REGISTER_UNABLE_TO_REGISTER(ResourceCategory.Register, "register.unable.to.register", true),
	REGISTER_USERNAME(ResourceCategory.Register, "register.username"),
	REGISTER_USERNAME_ALREADY_EXISTS(ResourceCategory.Register, "register.username.already.exists", true), 
	REGISTER_USERNAME_CONTAINS_INVALID_CHARACTERS(ResourceCategory.Register, "register.username.contains.invalid.characters", true), 
	REGISTER_USERNAME_IS_NULL(ResourceCategory.Register, "register.username.is.null", true), 
	REGISTER_USERNAME_SIZE_INVALID(ResourceCategory.Register, "register.username.size.invalid", true);
	
	private final ResourceCategory category;
	
	/**
	 * If this boolean is true, it's a label. If the boolean is false, it's a user message that has to be usable in the JavaScript.
	 */
	private boolean forJavaScript = false;
	private final String key;

	/**
	 * Each user message has a unique key to retrieve it from the
	 * resources.properties file.
	 */
	private Resources(ResourceCategory messageCategory, String messageKey) {
		category = messageCategory;
		key = messageKey;
	}

	/**
	 * The same constructor, but with a extra boolean option, so I know it it's a label.
	 */
	private Resources(ResourceCategory messageCategory, String messageKey, boolean forJavaScript) {
		category = messageCategory;
		this.forJavaScript = forJavaScript;
		key = messageKey;
	}
	
	/**
	 * Get a user friendly message from the bundle based on the key.
	 * @return
	 */
	@Override
	public String toString() {
//		Properties properties = new Properties();
//		try {
//			String resourcesFile = Resources.class.getResource(StaticResources.resourcesFile).toString();
//			properties.load(new FileInputStream(File.separator + FileUtil.getResourceFilePath(resourcesFile)));
//			// The property will be null if the key isn't in the file.
//			String property = properties.getProperty(key);
//			if (property != null) {
//				return ValidationUtility.escapeDangerousCharacters(property);
//			} else {
//				logger.error(StaticResources.cannotFindEnumValueForKey(resourcesFile));
//			}
//		} catch (IOException e) {
//			logger.error(StaticResources.cannotLoadPropertiesFile);
//			logger.error(e);
//		}
		return "TODO";//key;
	}

	/**
	 * Returns the key of the value.
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Returns the category of the value.
	 * 
	 * @return
	 */
	public ResourceCategory getGroup() {
		return category;
	}
	
	/**
	 * Returns true if it's a user message that has to be used via JavaScript, returns false if not.
	 * @return
	 */
	public boolean isForJavaScript() {
		return forJavaScript;
	}
}

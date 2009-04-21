package com.deepshiftlabs.sf_tests;

public final class Settings {
	

// login options
	public static final String SF_LOGIN = "your_SF_login";
	public static final String SF_PASSWORD = "your_SF_password";
	
// selenium params
    public static final String TIMEOUT = "180000";
	public static final String SELENIUM_HOST = "localhost";	
	public static final int SELENIUM_PORT = 4444;	
	public static final String BROWSER = "*chrome";	
	public static final String WEB_SITE = "https://login.salesforce.com";    

// tests options
     public static final int LIMIT_CHECK_VALUES_COUNT_TO = -1;  // set -1 to run all values
     public static final Boolean USE_FAST_NEW_RECORD = false;
     
     
// log options
     public static final String SCREENSHOTS_PATH = "C:\\trillium\\logs\\";
     public static final Boolean USE_SCREENSHOTS = true;
     public static final Boolean LOG_INFOS = true;
     public static final Boolean LOG_VERBOSE = false;
     
     public static final String SCREENSHOTS_PREFIX = "screen_";
     public static final String SCREENSHOTS_POSTFIX_FORMAT = "HHmmssSSS_ddMMyy";
     
// fatal errors options     
     public static final Boolean IS_CHECKSEQUENCE_FATAL = true;
     public static final int FATAL_ELEMENT_ERRORS_COUNT = 2;
}

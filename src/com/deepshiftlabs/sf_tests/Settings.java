package com.deepshiftlabs.sf_tests;

public final class Settings {
	

// login options
	public static final String SF_LOGIN = "login";
	public static final String SF_PASSWORD = "passw";
	
// selenium params
    public static final String TIMEOUT = "180000";
	public static final String SELENIUM_HOST = "localhost";	
	public static final int SELENIUM_PORT = 4444;	
	public static final String BROWSER = "*chrome";	
	public static final String WEB_SITE = "https://login.salesforce.com";    

// tests options
     public static final int LIMIT_CHECK_VALUES_COUNT_TO = 1;  // set -1 to run all values, don't set to 0
     public static final Boolean USE_FAST_NEW_RECORD = false;
     
// log options
     public static final String SCREENSHOTS_PATH = "logs\\screenshots\\";
     public static final String REPORT_PATH = "logs\\html\\";
     
     public static final String MY_LOCALE = "ru";     
     
//     public static final Boolean USE_SCREENSHOTS = true;
     public static final int LOG_LEVEL = 0;     
     public static final Boolean LOG_INFOS = true;
     public static final Boolean LOG_VERBOSE = true;
     
     public static final String SCREENSHOTS_PREFIX = "screen_";
     public static final String SCREENSHOTS_POSTFIX_FORMAT = "HHmmssSSS_ddMMyy";
     
// fatal errors options     
     public static final Boolean IS_CHECKSEQUENCE_FATAL = true;
     public static final int FATAL_ELEMENT_ERRORS_COUNT = 2;
}

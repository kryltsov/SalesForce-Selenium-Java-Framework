package com.deepshiftlabs.sf_tests;

public final class settings {
	

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
     
     
// log options
     public static final String SCREENSHOTS_PATH = "C:\\trillium\\logs\\";
     public static final Boolean USE_SCREENSHOTS = true;
     public static final Boolean LOG_INFOS = false;
     public static final Boolean LOG_VERBOSE = false;
     
     public static final String SCREENSHOTS_PREFIX = "screen_";
     public static final String SCREENSHOTS_POSTFIX_FORMAT = "HHmmssSSS_ddMMyy";
}

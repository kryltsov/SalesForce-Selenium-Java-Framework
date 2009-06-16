package com.deepshiftlabs.sf_tests;

/**
 * Represent a class which contains main settings. Please set at least login settings.
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 */
public final class Settings {

// login options - please, change to your SalesForce credentials
	public static final String SF_LOGIN = "login";
	public static final String SF_PASSWORD = "password";
	
// selenium params
	/** Time to wait a long operation completion, in milliseconds.*/
    public static final String TIMEOUT = "180000";

    /** IP or Name of selenium server (hub or RC).*/
	public static final String SELENIUM_HOST = "localhost";
	
	/** Selenium server port.*/	
	public static final int SELENIUM_PORT = 4444;
	
	/** Wanted environment (browser). Can be *opera, *iehta etc. */	
	public static final String BROWSER = "*chrome";
	
	/** Start page of site under test. */	
	public static final String WEB_SITE = "https://login.salesforce.com";    

// tests options
	/** Limitation of test values number. Set -1 to run all values, or set to any number>0.*/
     public static final int LIMIT_CHECK_VALUES_COUNT_TO = 1;
     
     /** Set true to start filling fields of record as they have been loaded (not waiting to whole page load).*/     
     public static final Boolean USE_FAST_NEW_RECORD = false;
     
// log options
     /** Path to dir where HTML reports will be stored. */
     public static final String REPORT_PATH = "logs\\html\\";
     
     /** Path to dir where screenshots will be saved. Path can be absolute (like c:\\scr\\) or relative (like \\scr).
      * In second case path will be added to your build.xml location. */
     public static final String SCREENSHOTS_PATH = "logs\\screenshots\\";

     /** This prefix will be added to screenshot filename.*/    
     public static final String SCREENSHOTS_PREFIX = "screen_";
     
     /** This format will be used to generate screenshot filename using current date and time. 
      * This constant will be a parameter of SimpleDateFormat constructor.*/
     public static final String SCREENSHOTS_POSTFIX_FORMAT = "HHmmssSSS_ddMMyy";     
     
     /** Reports will be generated in accordance to locale. Now "en" and "ru" are available. 
      * You can edit GoalsBundle_**.properties files to increase number of localized strings*/     
     public static final String MY_LOCALE = "ru";     
     
// this parameters used to adjust log levels of console output
     public static final int LOG_LEVEL = 0;     
     public static final Boolean LOG_INFOS = true;
     public static final Boolean LOG_VERBOSE = true;

// 	Definitions of what problems should be assumed as FATAL
     /** Set true if errors in common sequence check are fatal.*/     
     public static final Boolean IS_CHECKSEQUENCE_FATAL = true;
     
     /** Number of errors per element to assume test FATAL (should be >0).*/     
     public static final int FATAL_ELEMENT_ERRORS_COUNT = 5;
}
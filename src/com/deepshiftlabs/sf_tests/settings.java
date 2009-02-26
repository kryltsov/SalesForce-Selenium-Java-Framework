package com.deepshiftlabs.sf_tests;

public final class settings {
	public static final String SF_LOGIN = "your_SF_login";
	public static final String SF_PASSWORD = "your_SF_password";	
	public static final String SELENIUM_HOST = "192.168.232.13";	
	public static final int SELENIUM_PORT = 4444;	
	public static final String BROWSER = "*chrome";	
	public static final String WEB_SITE = "https://login.salesforce.com";
	
	public static final int RET_OK = 0;
	public static final int RET_SKIPPED = -100;
	public static final int RET_ERROR = -1;
//	public static final int RET_PAGE_BROKEN = -2;
	public static final int RET_PAGE_BROKEN_OK = -3;
	public static final int RET_PAGE_BROKEN_ERROR = -4;
	public static final int RET_SOMETHING_STRANGE = -50;

}

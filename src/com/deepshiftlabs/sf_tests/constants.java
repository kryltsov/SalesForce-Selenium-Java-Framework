package com.deepshiftlabs.sf_tests;

public final class constants {

// TODO when working with xpath we should not use ' in locators	
	
	
// return values
	public static final int RET_OK = 0;
	public static final int RET_SKIPPED = -100;
	public static final int RET_ERROR = -1;
	public static final String RET_ERROR_STRING = "e_rror_occured_";
	public static final int RET_PAGE_BROKEN_OK = -3;
	public static final int RET_PAGE_BROKEN_ERROR = -4;
	public static final int RET_SOMETHING_STRANGE = -50;
//	public static final int RET_PAGE_BROKEN = -2;
	
	
// standard parameters	
	public static final boolean REQUIRED = true;
	public static final boolean IT_IS_VALID_VALUE = true;
	public static final boolean IT_IS_INVALID_VALUE = false;
	public static final String ONLY_GENERAL_ERROR = "";
	public static final String STANDARD_INVALID_VALUE_ERROR = "";	
	public static final String RESERVED_PARAMETER = "reserved_parameter";
	
	
//statuses
	public static final int NOT_CHECKED = 0;
	public static final int CHECKED = 1;
	
	public static final boolean CHECK_OK = true;
	public static final boolean CHECK_ERROR = false;

// SF constants

	// i can't understand why "Review all error messages below to correct your data." is not working in xpath
	public static final String GENERAL_PAGE_ERROR = "Error: Invalid Data.";
    public static final String SAVE_RECORD_LOCATOR = "save";
    public static final String EDIT_RECORD_LOCATOR = "edit";
    public static final String SAVE_AND_NEW_LOCATOR = "save_new";
    public static final String CANCEL_LOCATOR = "cancel";
    public static final String CLONE_LOCATOR = "clone";
    public static final String DELETE_LOCATOR = "del";
    public static final String LOGIN_LOCATOR = "//input[@id='Login']";
    public static final String LOGOUT_LOCATOR = "//a[contains(@href, '/secur/logout.jsp')]";
    
    public static final String LOGIN_FAILED_ERROR = "Your login attempt has failed";
    public static final String BAD_IP_ERROR = "You are attempting to access salesforce.com from an unrecognized computer.";
    public static final String HOME_PAGE_TITLE = "Salesforce - Developer Edition";
    
    public static final String INVALID_LOGIN_VALUE = "IN-v(al!d login value";
    
    public static final String TITLE_LOGIN_PAGE = "Salesforce.com - Customer Secure Login Page";
    public static final String TITLE_LOGOUT_PAGE = "Salesforce - Developer Edition";
}
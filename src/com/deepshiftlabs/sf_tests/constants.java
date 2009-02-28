package com.deepshiftlabs.sf_tests;

public final class constants {

// TODO when working with xpath we should not use ' in locators	
	
// return values
	public static final int RET_OK = 0;
	public static final int RET_SKIPPED = -100;
	public static final int RET_ERROR = -1;
	public static final int RET_PAGE_BROKEN_OK = -3;
	public static final int RET_PAGE_BROKEN_ERROR = -4;
	public static final int RET_SOMETHING_STRANGE = -50;
//	public static final int RET_PAGE_BROKEN = -2;
	
	
// standart parameters	
	public static final boolean REQUIRED = true;
	public static final boolean IT_IS_VALID_VALUE = true;
	public static final boolean IT_IS_INVALID_VALUE = false;
	public static final String ONLY_GENERAL_ERROR = "";
	
	
//statuses
	public static final int NOT_CHECKED = 0;
	public static final int CHECKED = 1;
	
	public static final boolean CHECK_OK = true;
	public static final boolean CHECK_ERROR = false;

// SF constants

	// i can't understand why "Review all error messages below to correct your data." is not working in xpath
	public static final String GENERAL_PAGE_ERROR = "Error: Invalid Data.";
	public static final String STANDARD_INVALID_VALUE_ERROR = "";
}
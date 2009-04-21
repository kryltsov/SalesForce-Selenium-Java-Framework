package com.deepshiftlabs.sf_tests;

public class CheckValue {
	String value;
	boolean shouldBeValid;
	int status;
	boolean validCheckResult;
	boolean displayedRightResult;
	String shouldBeErrorMessage;
	String shouldBeDisplayed;

	CheckValue (String a_value, boolean a_shouldBeValid, String a_shouldBeErrorMessage, String a_shouldBeDisplayed){
		value=a_value;

		if (a_shouldBeDisplayed=="")
			shouldBeDisplayed = a_value; 
		else
			shouldBeDisplayed = a_shouldBeDisplayed;
		
		shouldBeValid = a_shouldBeValid;
		status = Constants.NOT_CHECKED;

		if (a_shouldBeErrorMessage=="")
			shouldBeErrorMessage = Constants.STANDARD_INVALID_VALUE_ERROR;
		else
			shouldBeErrorMessage = a_shouldBeErrorMessage;
	}
	
	CheckValue (String a_value, boolean a_shouldBeValid, String a_shouldBeErrorMessage){
		value=a_value;

		shouldBeDisplayed = a_value; 
		
		shouldBeValid = a_shouldBeValid;
		status = Constants.NOT_CHECKED;

		if (a_shouldBeErrorMessage=="")
			shouldBeErrorMessage = Constants.STANDARD_INVALID_VALUE_ERROR;
		else
			shouldBeErrorMessage = a_shouldBeErrorMessage;
	}	
	
	CheckValue (String a_value, boolean a_shouldBeValid){

		value=a_value;
		shouldBeDisplayed = a_value; 
		
		shouldBeValid = a_shouldBeValid;
		status = Constants.NOT_CHECKED;

		shouldBeErrorMessage = Constants.STANDARD_INVALID_VALUE_ERROR;
	}
}

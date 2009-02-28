package com.deepshiftlabs.sf_tests;

public class checkValue {
	String value;
	boolean shouldBeValid;
	int status;
	boolean validCheckResult;
	boolean displayedRightResult;
	String shouldBeErrorMessage;
	String shouldBeDisplayed;

	checkValue (String a_value, boolean a_shouldBeValid, String a_shouldBeErrorMessage, String a_shouldBeDisplayed){
		value=a_value;

		if (a_shouldBeDisplayed=="")
			shouldBeDisplayed = a_value; 
		else
			shouldBeDisplayed = a_shouldBeDisplayed;
		
		shouldBeValid = a_shouldBeValid;
		status = constants.NOT_CHECKED;

		if (a_shouldBeErrorMessage=="")
			shouldBeErrorMessage = constants.STANDARD_INVALID_VALUE_ERROR;
		else
			shouldBeErrorMessage = a_shouldBeErrorMessage;
	}
	
	checkValue (String a_value, boolean a_shouldBeValid, String a_shouldBeErrorMessage){
		value=a_value;

		shouldBeDisplayed = a_value; 
		
		shouldBeValid = a_shouldBeValid;
		status = constants.NOT_CHECKED;

		if (a_shouldBeErrorMessage=="")
			shouldBeErrorMessage = constants.STANDARD_INVALID_VALUE_ERROR;
		else
			shouldBeErrorMessage = a_shouldBeErrorMessage;
	}	
	
	checkValue (String a_value, boolean a_shouldBeValid){

		value=a_value;
		shouldBeDisplayed = a_value; 
		
		shouldBeValid = a_shouldBeValid;
		status = constants.NOT_CHECKED;

		shouldBeErrorMessage = constants.STANDARD_INVALID_VALUE_ERROR;
	}
}

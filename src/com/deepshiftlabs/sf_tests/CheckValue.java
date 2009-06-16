package com.deepshiftlabs.sf_tests;

/**
 * Represent a one check value.
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
public class CheckValue {
	String value;
	boolean shouldBeValid;
	int status;
	boolean validCheckResult;
	boolean displayedRightResult;
	String shouldBeErrorMessage;
	String shouldBeDisplayed;

	/**
	 * @param a_value value, which will be entered during test 
	 * @param a_shouldBeValid true if this value is valid
	 * @param a_shouldBeErrorMessage if not an empty string, such error string should be present on page after saving attempt. 
	 * @param a_shouldBeDisplayed how should value be displayed after salesforce will process it. If empty, assumed equal to a_value. 
	 */
	CheckValue (String a_value, boolean a_shouldBeValid, String a_shouldBeErrorMessage, String a_shouldBeDisplayed){
		value=a_value;

		if (a_shouldBeDisplayed.equals(""))
			shouldBeDisplayed = a_value; 
		else
			shouldBeDisplayed = a_shouldBeDisplayed;
		
		shouldBeValid = a_shouldBeValid;
		status = Constants.NOT_CHECKED;

		if (a_shouldBeErrorMessage.equals(""))
			shouldBeErrorMessage = Constants.STANDARD_INVALID_VALUE_ERROR;
		else
			shouldBeErrorMessage = a_shouldBeErrorMessage;
	}
	
	CheckValue (String a_value, boolean a_shouldBeValid, String a_shouldBeErrorMessage){
		value=a_value;

		shouldBeDisplayed = a_value; 
		
		shouldBeValid = a_shouldBeValid;
		status = Constants.NOT_CHECKED;

		if (a_shouldBeErrorMessage.equals(""))
			shouldBeErrorMessage = Constants.STANDARD_INVALID_VALUE_ERROR;
		else
			shouldBeErrorMessage = a_shouldBeErrorMessage;
	}	

	/**
	 * If it's invalid message, test will look for Constants.STANDARD_INVALID_VALUE_ERROR string on page.
	 * Displayed value assumed equal to a_value. 
	 * @param a_value value, which will be entered during test 
	 * @param a_shouldBeValid true if this value is valid
	 */	
	CheckValue (String a_value, boolean a_shouldBeValid){

		value=a_value;
		shouldBeDisplayed = a_value; 
		
		shouldBeValid = a_shouldBeValid;
		status = Constants.NOT_CHECKED;

		shouldBeErrorMessage = Constants.STANDARD_INVALID_VALUE_ERROR;
	}
}
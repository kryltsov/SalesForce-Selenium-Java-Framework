package com.deepshiftlabs.sf_tests;

public class numberElement extends genericNumber {

	public numberElement(String name, String sfId, String objectType,
			 boolean a_isRequired, int a_intPlaces, int a_decimalPlaces) {
		super(name, sfId, objectType, a_isRequired, a_intPlaces, a_decimalPlaces);
        
		for (int i=0; i<values.size();i++){
        	checkValue tempValue;
        	tempValue = values.get(i);
        	if (tempValue.shouldBeValid==constants.IT_IS_INVALID_VALUE &&
        			tempValue.shouldBeErrorMessage=="")
        		tempValue.shouldBeErrorMessage="Invalid number";
        }		
	}
}

package com.deepshiftlabs.sf_tests;

public class NumberElement extends GenericNumber {

	public NumberElement(String name, String sfId, String objectType,
			 boolean a_isRequired, int a_intPlaces, int a_decimalPlaces) {
		super(name, sfId, objectType, a_isRequired, a_intPlaces, a_decimalPlaces);
        
		for (int i=0; i<values.size();i++){
        	CheckValue tempValue;
        	tempValue = values.get(i);
        	if (tempValue.shouldBeValid==Constants.IT_IS_INVALID_VALUE &&
        			tempValue.shouldBeErrorMessage.equals(""))
        		tempValue.shouldBeErrorMessage="Invalid number";
        }		
	}
}

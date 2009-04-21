package com.deepshiftlabs.sf_tests;

public class CurrencyElement extends GenericNumber {
	
	public CurrencyElement(String name, String sfId, String objectType,
			 boolean a_isRequired, int a_intPlaces, int a_decimalPlaces) {
		super(name, sfId, objectType, a_isRequired, a_intPlaces, a_decimalPlaces);
		
        for (int i=0; i<values.size();i++){
        	CheckValue tempValue;
        	tempValue = values.get(i);
        	if (tempValue.shouldBeValid==Constants.IT_IS_INVALID_VALUE &&
        			tempValue.shouldBeErrorMessage=="")
        		tempValue.shouldBeErrorMessage="Invalid currency";
        	
        	if (tempValue.shouldBeDisplayed.length()>0 &&
        			tempValue.shouldBeDisplayed.charAt(0)!='$')
        		tempValue.shouldBeDisplayed="$"+tempValue.shouldBeDisplayed;
        }
        
        values.add(new CheckValue("$$44", Constants.IT_IS_INVALID_VALUE, "Invalid currency"));
        values.add(new CheckValue("$446", Constants.IT_IS_VALID_VALUE, "", "$446.00"));
        values.add(new CheckValue("477", Constants.IT_IS_VALID_VALUE, "", "$477.00"));
	}
}

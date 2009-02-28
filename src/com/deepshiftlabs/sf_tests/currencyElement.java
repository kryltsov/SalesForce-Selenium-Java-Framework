package com.deepshiftlabs.sf_tests;

public class currencyElement extends genericNumber {
	
	public currencyElement(String name, String sfId, String objectType,
			 String value, boolean a_isRequired) {
		super(name, sfId, objectType, value, a_isRequired);
		
		values.add(new checkValue("bear", constants.IT_IS_INVALID_VALUE, "Invalid currency"));
		values.add(new checkValue("beASfw<>··ar", constants.IT_IS_INVALID_VALUE));
	}	
}

package com.deepshiftlabs.sf_tests;

/**
 * Represents Salesforce Number element.
 * @author Bear
 *
 */
public class NumberElement extends GenericNumber {

	/**
     * @param a_elementName Salesforce name of element 
     * @param a_parentObjectType name of salesforce object which contains this element 
     * @param a_isRequired determines if element should be filled with value to store record
	 * @param a_intPlaces assumed number of digits before dot
	 * @param a_decimalPlaces assumed number of digits after dot
	 */
	public NumberElement(String a_elementName, String a_parentObjectType, boolean a_isRequired,
			 int a_intPlaces, int a_decimalPlaces) {
		super(a_elementName, a_parentObjectType, a_isRequired, a_intPlaces, a_decimalPlaces);
        
		for (int i=0; i<values.size();i++){
        	CheckValue tempValue;
        	tempValue = values.get(i);
        	// if it's invalid value and shouldBeErrorMessage is empty, we set standard for this element error message.
        	if (tempValue.shouldBeValid==Constants.IT_IS_INVALID_VALUE &&
        			tempValue.shouldBeErrorMessage.equals(""))
        		tempValue.shouldBeErrorMessage="Invalid number";
        }		
	}
}

package com.deepshiftlabs.sf_tests;

/**
 * Represents Salesforce Text element.
 * @author Bear
 *
 */
public class TextElement extends GenericTextElement{
	private boolean isUnique=false;
	private boolean isUniqueCaseSensitive=false;
	
    /**
     * @param a_elementName Salesforce name of element 
     * @param a_parentObjectType name of salesforce object which contains this element 
     * @param a_isRequired determines if element should be filled with value to store record
     * @param a_maxLength assumed maximal length of element
     */
    TextElement(String a_elementName, String a_parentObjectType, boolean a_isRequired, int a_maxLength){
        super(a_elementName, a_parentObjectType, a_isRequired, a_maxLength);
        
        values.add(new CheckValue("test <>\" test", Constants.IT_IS_VALID_VALUE));
    }

    /**
     * Allows to mark element as "Unique" (so there can not be two records with same value in this elements).
     * @param isCaseSensitive
     */
    public void setUnique (boolean isCaseSensitive){
    	isUnique = true;
    	isUniqueCaseSensitive = isCaseSensitive;
    }
}
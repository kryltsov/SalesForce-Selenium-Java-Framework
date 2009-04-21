package com.deepshiftlabs.sf_tests;

public class TextElement extends GenericTextElement{
	private boolean isUnique=false;
	private boolean isUniqueCaseSensitive=false;
	
    TextElement(String a_elementName, String a_elementSfId, String a_parentObjectType, boolean a_isRequired, int a_maxLength){
        super(a_elementName, a_elementSfId,a_parentObjectType, a_isRequired, a_maxLength);
        
        values.add(new CheckValue("test <>\" test", Constants.IT_IS_VALID_VALUE));
    }

    public void setUnique (boolean isCaseSensitive){
    	isUnique = true;
    	isUniqueCaseSensitive = isCaseSensitive;
    }
}
package com.deepshiftlabs.sf_tests;

/**
 * Represents Salesforce URL element.
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
public class UrlElement extends GenericTextElement {

    /**
     * @param a_elementName Salesforce name of element 
     * @param a_parentObjectType name of salesforce object which contains this element 
     * @param a_isRequired determines if element should be filled with value to store record
     */
	UrlElement(String a_elementName, String a_parentObjectType, boolean a_isRequired){
        super(a_elementName, a_parentObjectType,a_isRequired, 255);
        
// TODO we should not add http:// if it's already here!
        // for check values that are inherited from parents we should add "http://" to shouldBeDisplayed value.
        for (int i=0; i<values.size();i++){
        	if (values.get(i).shouldBeDisplayed.length()>0)
        		values.get(i).shouldBeDisplayed="http://"+values.get(i).shouldBeDisplayed;
        }
        
//        readLocator = "//*[@class='labelCol' and text()='"+a_elementName+"']/following::a";
        	
        values.add(new CheckValue("http:/adr<e>ss.com", Constants.IT_IS_VALID_VALUE, "", "http://http:/adress.com"));        
        values.add(new CheckValue("http://domain", Constants.IT_IS_VALID_VALUE, "", "http://domain"));
        values.add(new CheckValue("ftp://domain", Constants.IT_IS_VALID_VALUE, "", "ftp://domain"));
        values.add(new CheckValue("https://do\"ma\"in", Constants.IT_IS_VALID_VALUE, "", "https://domain"));
    }
    
	/* 
	 * Call parent method and provide self check if stored value is displayed as link 
	 * @see com.deepshiftlabs.sf_tests.GenericElement#checkIsDisplayedRight(com.deepshiftlabs.sf_tests.CheckValue)
	 */
	protected int checkIsDisplayedRight(CheckValue theValue){
		Event event = action.startEvent("checkIsDisplayedRight", name);		
		int retValue;
		
		retValue = super.checkIsDisplayedRight(theValue);
     	if (retValue!=Constants.RET_PAGE_BROKEN_OK){
     		errorsCount++;
     		action.closeEventError(event);
     		return retValue;
     	}
     	// if we have link with our value, all is OK
     	if ( action.isElementPresent("link="+ theValue.shouldBeDisplayed)){
    		action.closeEventOk(event);
    		return Constants.RET_PAGE_BROKEN_OK;
    	} else {
    		errorsCount++;
    		action.closeEventError(event, "Element is not displayed as link!");
    		return Constants.RET_PAGE_BROKEN_ERROR;
    	}
    }
}

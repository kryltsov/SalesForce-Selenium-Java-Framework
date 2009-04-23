package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.DefaultSelenium;

public class UrlElement extends GenericTextElement {

	UrlElement(String a_elementName, String a_elementSfId, String a_parentObjectType, boolean a_isRequired){
        super(a_elementName, a_elementSfId,a_parentObjectType, a_isRequired, 255);
        
        for (int i=0; i<values.size();i++){
        	if (values.get(i).shouldBeDisplayed.length()>0)
        		values.get(i).shouldBeDisplayed="http://"+values.get(i).shouldBeDisplayed;
        }
        
        readLocator = "//*[@class='labelCol' and text()='"+a_elementName+"']/following::a";
        	
        values.add(new CheckValue("http:/adr<e>ss.com", Constants.IT_IS_VALID_VALUE, "", "http://http:/adress.com"));        
        values.add(new CheckValue("http://domain", Constants.IT_IS_VALID_VALUE, "", "http://domain"));
        values.add(new CheckValue("ftp://domain", Constants.IT_IS_VALID_VALUE, "", "ftp://domain"));
        values.add(new CheckValue("https://do\"ma\"in", Constants.IT_IS_VALID_VALUE, "", "https://domain"));
    }
    
	protected int checkIsDisplayedRight(CheckValue theValue){
		int retValue;
		
		retValue = super.checkIsDisplayedRight(theValue);
     	if (retValue!=Constants.RET_PAGE_BROKEN_OK){
     		action.warn("Check if url field displayed as url SKIPPED for element _"+elementName+"_ because checkIsDisplayedRight FAILED)");
     		return retValue;
     	}
     	if ( action.isElementPresent("link="+ theValue.shouldBeDisplayed)){
    		action.info("Value _"+theValue.shouldBeDisplayed+"_ for element (URL) _"+elementName+"_ is displayed as URL (OK)");
    		return Constants.RET_PAGE_BROKEN_OK;
    	} else {
    		action.error("Value _"+theValue.shouldBeDisplayed+"_ for element (URL) _"+elementName+"_ is NOT displayed as URL (ERROR)");
    		action.getScreenshot(true);
    		return Constants.RET_PAGE_BROKEN_ERROR;
    	}
    }
}

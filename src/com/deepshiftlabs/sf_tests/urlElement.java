package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.DefaultSelenium;

public class urlElement extends genericTextElement {

	urlElement(String a_elementName, String a_elementSfId, String a_parentObjectType, boolean a_isRequired){
        super(a_elementName, a_elementSfId,a_parentObjectType, a_isRequired, 255);
        
        for (int i=0; i<values.size();i++){
        	if (values.get(i).shouldBeDisplayed.length()>0)
        		values.get(i).shouldBeDisplayed="http://"+values.get(i).shouldBeDisplayed;
        }
        
        readLocator = "//*[@class='labelCol' and text()='"+a_elementName+"']/following::a";
        	
        values.add(new checkValue("http:/adr<e>ss.com", constants.IT_IS_VALID_VALUE, "", "http://http:/adress.com"));        
        values.add(new checkValue("http://domain", constants.IT_IS_VALID_VALUE, "", "http://domain"));
        values.add(new checkValue("ftp://domain", constants.IT_IS_VALID_VALUE, "", "ftp://domain"));
        values.add(new checkValue("https://do\"ma\"in", constants.IT_IS_VALID_VALUE, "", "https://domain"));
    }
    
	protected int checkIsDisplayedRight(DefaultSelenium selInstance, checkValue theValue){
		int retValue;
		
		retValue = super.checkIsDisplayedRight(selInstance, theValue);
     	if (retValue!=constants.RET_PAGE_BROKEN_OK){
     		action.warn("Check if url field displayed as url SKIPPED for element _"+elementName+"_ because checkIsDisplayedRight FAILED)");
     		return retValue;
     	}
     	if ( action.isElementPresent(selInstance, "link="+ theValue.shouldBeDisplayed)){
    		action.info("Value _"+theValue.shouldBeDisplayed+"_ for element (URL) _"+elementName+"_ is displayed as URL (OK)");
    		return constants.RET_PAGE_BROKEN_OK;
    	} else {
    		action.error("Value _"+theValue.shouldBeDisplayed+"_ for element (URL) _"+elementName+"_ is NOT displayed as URL (ERROR)");
    		action.getScreenshot(selInstance, true);
    		return constants.RET_PAGE_BROKEN_ERROR;
    	}
    }
}

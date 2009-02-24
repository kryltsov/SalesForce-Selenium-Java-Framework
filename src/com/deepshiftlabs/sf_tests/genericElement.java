package com.deepshiftlabs.sf_tests;


import java.util.ArrayList;

import com.thoughtworks.selenium.*;
//import org.testng.annotations.*;


public class genericElement {
    protected String elementName;
    protected String elementSfId;
    protected String parentObjectType;
    protected String validValue;
    protected boolean isRequired;
    protected boolean determinesRecordId = false;
    String recordId = "";
    commonActions action = new commonActions();
    
    
    genericElement(String a_elementName, String a_elementSfId, String a_parentObjectType, String a_validValue, boolean a_isRequired) {
        elementName = a_elementName;
        parentObjectType = a_parentObjectType;
        elementSfId = a_elementSfId;
        validValue = a_validValue;
        isRequired = a_isRequired;
    }
        
/*    public int preparePage(DefaultSelenium selInstance){
        action.openTab(selInstance parentTabID);
        if (selInstance.isElementPresent("name="+parentID)){
                selInstance.click("//a[contains(text(),'"+parentID+"')]");            
                waitForPageToLoad(30000);
            return 0;
        }
    }   */
    
    public String getElementName(){
    	return elementName;
    }
    
    public void forceToDetermineRecordID(String a_recordId){
    	determinesRecordId = true;
    	recordId = a_recordId;
    }
    
    public int checkPresence (DefaultSelenium selInstance){
        String tempLocator;
        
        tempLocator = "//input[@id='"+elementSfId+"']";
        
        if (selInstance.isElementPresent(tempLocator)){
            action.info ("Element name = "+elementName + " is PRESENT");
            return settings.RET_OK;
        }
        action.error ("Element name = "+elementName + " is NOT PRESENT. Locator was _"+tempLocator+"_");
        return settings.RET_ERROR;
    }
    
    public int fillByValidValue (DefaultSelenium selInstance){
        String tempLocator;
        String tempValidValue = validValue;
        
        if (determinesRecordId) tempValidValue = recordId;
        
        tempLocator = "//input[@id='"+elementSfId+"']";
        
        selInstance.type(tempLocator, tempValidValue);
        action.info ("Filling Element _"+ elementName + "_ by valid value = _"+tempValidValue+"_");
       return settings.RET_OK;
    }
    
    public int fillByNull (DefaultSelenium selInstance){
        String tempLocator;
        
        tempLocator = "//input[@id='"+elementSfId+"']";
        
        selInstance.type(tempLocator, "");
        action.info ("Filling Element _"+ elementName + "_ by NULL");
       return settings.RET_OK;
    }
    
    public int  checkAll (DefaultSelenium selInstance){
        action.info ("Starting checking all");
    	if (checkRequired(selInstance)== settings.RET_PAGE_BROKEN) return settings.RET_PAGE_BROKEN;
       return settings.RET_OK;
    }
   
    private int checkRequiredRunCount=0;
    public int checkRequired (DefaultSelenium selInstance){
    	if (checkRequiredRunCount>0) {
    		action.info("CheckRequired for element _"+elementSfId+"_ already was performed, skipping");
    		return settings.RET_SKIPPED;
    	}
    	checkRequiredRunCount++;
    	if (isRequired){
	        action.info ("Starting checkRequired for _"+elementName+"_");
	    	fillByNull(selInstance);
	    	action.saveRecord(selInstance);
	    	if (selInstance.isTextPresent("Error: Invalid Data.")){
	    		if (selInstance.isTextPresent("Error: Invalid Data."))
	    			action.info("Element _"+ elementName + "_is required!");
	    			action.getScreenshot(selInstance, false);
	    			return settings.RET_OK;
	    	}
	    	action.error("Element _"+ elementName + "_is not required!");
	    	action.getScreenshot(selInstance, true);
	    	return settings.RET_PAGE_BROKEN;
    	} else {
    		action.info ("CheckRequired for _"+elementName+"_ was not performed, element is optional");
    		return settings.RET_OK;
    		// here should be check if optional elements are obligatory in real
    	}
    }
}
      


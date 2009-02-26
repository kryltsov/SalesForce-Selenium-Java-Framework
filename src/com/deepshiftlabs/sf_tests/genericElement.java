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
        action.infoV ("Filling Element _"+ elementName + "_ by valid value = _"+tempValidValue+"_");
       return settings.RET_OK;
    }
    
    public int fillByNull (DefaultSelenium selInstance){
        String tempLocator;
        
        tempLocator = "//input[@id='"+elementSfId+"']";
        
        selInstance.type(tempLocator, "");
        action.infoV ("Filling Element _"+ elementName + "_ by NULL");
       return settings.RET_OK;
    }
    
    public int  checkAll (DefaultSelenium selInstance){
    	int returnedValue;
        action.infoV ("Starting checking all for element _"+elementSfId+"_");
        returnedValue = checkRequired(selInstance); 
        if ((returnedValue == settings.RET_PAGE_BROKEN_OK)||
    			(returnedValue== settings.RET_PAGE_BROKEN_ERROR)) 
    		return returnedValue;
        
        
       return settings.RET_OK;
    }
   
    private int checkRequiredRunCount=0;
    public int checkRequired (DefaultSelenium selInstance){
    	boolean realRequired = false;
    	if (checkRequiredRunCount>0) {
    		action.info("CheckRequired for element _"+elementSfId+"_ already was performed, skipping");
    		return settings.RET_SKIPPED;
    	}
    	checkRequiredRunCount++;
	        action.infoV ("Starting checkRequired for _"+elementName+"_");
	    	fillByNull(selInstance);
	    	action.saveRecord(selInstance);
	    	if (selInstance.isTextPresent("Review all error messages below to correct your data.")){
	    		if (selInstance.isTextPresent("Error: You must enter a value"))
	    			realRequired = true;
	    	}

	    	if (realRequired==true){
	    		if (isRequired==true){
	    	    	action.info("Element _"+ elementName + "_ is required!(OK)");
	    			action.getScreenshot(selInstance, false);
	    			return settings.RET_OK;
	    		}else{
	    			action.error("Element _"+ elementName + "_ is required!(ERROR)");
	    			action.getScreenshot(selInstance, true);
	    			return settings.RET_ERROR;
	    		}
	    	}
	    	if (realRequired==false){
	    		if (isRequired==false){
	    	    	action.info("Element _"+ elementName + "_ is NOT required!(OK)");
	    			action.getScreenshot(selInstance, false);
	    			return settings.RET_PAGE_BROKEN_OK;
	    		}else{
	    			action.error("Element _"+ elementName + "_ is NOT required!(ERROR)");
	    			action.getScreenshot(selInstance, true);
	    			return settings.RET_PAGE_BROKEN_ERROR;
	    		}
	    	}
	    	return settings.RET_SOMETHING_STRANGE;
    }
}
      


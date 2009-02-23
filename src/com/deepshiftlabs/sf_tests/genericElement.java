package com.deepshiftlabs.sf_tests;


import com.thoughtworks.selenium.*;
//import org.testng.annotations.*;


public class genericElement {
    String elementName;
    String elementSfId;
    String parentObjectType;
    String parentID;
    String validValue;
    boolean isRequired;
    commonActions action = new commonActions();
    
    genericElement(String a_elementName, String a_elementSfId, String a_parentObjectType, String a_parentId, String a_validValue, boolean a_isRequired) {
        elementName = a_elementName;
        parentObjectType = a_parentObjectType;
        parentID = a_parentId;
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
    
    public boolean checkPresence (DefaultSelenium selInstance){
        String tempLocator;
        
        tempLocator = "//input[@id='"+elementSfId+"']";
        
        if (selInstance.isElementPresent(tempLocator)){
            action.info ("Element name = "+elementName + " is PRESENT");
            return true;
        }
        action.error ("Element name = "+elementName + " is NOT PRESENT. Locator was _"+tempLocator+"_");
        return false;
    }
    
    public boolean fillByValidValue (DefaultSelenium selInstance){
        String tempLocator;
        
        tempLocator = "//input[@id='"+elementSfId+"']";
        
        selInstance.type(tempLocator, validValue);
        action.info ("Filling Element _"+ elementName + "_ by valid value = _"+validValue+"_");
       return true;
    }
    
    public boolean fillByNull (DefaultSelenium selInstance){
        String tempLocator;
        
        tempLocator = "//input[@id='"+elementSfId+"']";
        
        selInstance.type(tempLocator, "");
        action.info ("Filling Element _"+ elementName + "_ by NULL");
       return true;
    }
    
    public int  checkAll (DefaultSelenium selInstance){
        action.info ("Starting checking all");
    	checkRequired(selInstance);
       return 0;
    }
    
    public boolean checkRequired (DefaultSelenium selInstance){
    	if (isRequired){
	        action.info ("Starting checkRequired for _"+elementName+"_");
	    	fillByNull(selInstance);
	    	action.saveRecord(selInstance);
	    	if (selInstance.isTextPresent("Error: Invalid Data.")){
	    		if (selInstance.isTextPresent("Error: Invalid Data."))
	    			action.info("Element _"+ elementName + "_is required!");
	    			action.getScreenshot(selInstance, false);
	    			return true;
	    	}
	    	action.error("Element _"+ elementName + "_is not required!");
	    	action.getScreenshot(selInstance, true);
	    	return false;
    	} else {
    		action.info ("CheckRequired for _"+elementName+"_ was not performed, element is optional");
    		return true;
    		// here should be check if optional elements are obligatory in real
    	}
    }
}
      


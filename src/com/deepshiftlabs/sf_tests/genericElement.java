package com.deepshiftlabs.sf_tests;


import java.util.ArrayList;
import java.util.Iterator;

import com.thoughtworks.selenium.*;
//import org.testng.annotations.*;


public class genericElement {
    protected String elementName;
    protected String elementSfId;
    protected String parentObjectType;
    protected String validValue;
    protected String elementLocator;
    protected String lastEnteredValue;
    protected int inputLength;
    protected boolean isRequired;
    protected boolean determinesRecordId = false;
    
    protected ArrayList <checkValue> values = new ArrayList <checkValue> ();
    
    String recordId = "";
    commonActions action = new commonActions();
    
    genericElement(String a_elementName, String a_elementSfId, String a_parentObjectType, boolean a_isRequired) {
        elementName = a_elementName;
        parentObjectType = a_parentObjectType;
        elementSfId = a_elementSfId;
        isRequired = a_isRequired;
//        elementLocator = "//input[@id='"+elementSfId+"']";
        elementLocator = elementSfId;
        lastEnteredValue = "";
        inputLength = 32000+100;
    }
        
    public String getElementName(){
    	return elementName;
    }
    
    public String getLastEnteredValue(){
    	return lastEnteredValue;
    }    
    
    public void forceToDetermineRecordID(String a_recordId){
    	determinesRecordId = true;
    	recordId = a_recordId;
    }
    
    public int checkPresence (DefaultSelenium selInstance){
        
        if (selInstance.isElementPresent(elementLocator)){
            action.info ("Element name = "+elementName + " is PRESENT");
            return constants.RET_OK;
        }
        action.error ("Element name = "+elementName + " is NOT PRESENT. Locator was _"+elementLocator+"_");
        return constants.RET_ERROR;
    }
    
    public int fillByValidValue (DefaultSelenium selInstance){
        String tempValidValue = validValue;
        
        if (determinesRecordId) tempValidValue = recordId;
        
        selInstance.type(elementLocator, tempValidValue);
        lastEnteredValue = tempValidValue;
        action.infoV ("Filling Element _"+ elementName + "_ by valid value = _"+tempValidValue+"_");
       return constants.RET_OK;
    }
    
    public int fillByNull (DefaultSelenium selInstance){
        selInstance.type(elementLocator, "");
        lastEnteredValue = "";
        action.infoV ("Filling Element _"+ elementName + "_ by NULL");
       return constants.RET_OK;
    }

    public boolean isValueValidForThisElementLength(checkValue theValue){
    	return true;
    }
    
    public int  checkAll (DefaultSelenium selInstance){
    	int returnedValue;
        action.infoV ("Starting checking all for element _"+elementName+"_");

        returnedValue = checkRequired(selInstance); 
        if ((returnedValue == constants.RET_PAGE_BROKEN_OK)||
    			(returnedValue== constants.RET_PAGE_BROKEN_ERROR)) 
    		return returnedValue;

        returnedValue = checkAllValues(selInstance); 
        if ((returnedValue == constants.RET_PAGE_BROKEN_OK)||
    			(returnedValue== constants.RET_PAGE_BROKEN_ERROR)) 
    		return returnedValue;

        return constants.RET_OK;
    }
   
    private int checkRequiredRunCount=0;
    public int checkRequired (DefaultSelenium selInstance){
    	boolean realRequired = false;
    	if (checkRequiredRunCount>0) {
    		action.infoV("CheckRequired for element _"+elementName+"_ already was performed, skipping");
    		return constants.RET_SKIPPED;
    	}
    	checkRequiredRunCount++;
	        action.infoV ("Starting checkRequired for _"+elementName+"_");
	    	fillByNull(selInstance);
	    	action.saveRecord(selInstance);
	    	
	    	realRequired = action.isErrorPresent(selInstance, "You must enter a value");

	    	if (realRequired==true){
	    		if (isRequired==true){
	    	    	action.info("Element _"+ elementName + "_ is required!(OK)");
	    			action.getScreenshot(selInstance, false);
	    			return constants.RET_OK;
	    		}else{
	    			action.error("Element _"+ elementName + "_ is required!(ERROR)");
	    			action.getScreenshot(selInstance, true);
	    			return constants.RET_ERROR;
	    		}
	    	}
	    	else{
	    		if (isRequired==false){
	    	    	action.info("Element _"+ elementName + "_ is NOT required!(OK)");
	    			action.getScreenshot(selInstance, false);
	    			return constants.RET_PAGE_BROKEN_OK;
	    		}else{
	    			action.error("Element _"+ elementName + "_ is NOT required!(ERROR)");
	    			action.getScreenshot(selInstance, true);
	    			return constants.RET_PAGE_BROKEN_ERROR;
	    		}
	    	}
    }
    
 // TODO - Text element we should check if theValue.value.length()>maxLength and then skip this value
    private int checkOneValue(DefaultSelenium selInstance, checkValue theValue){
    	boolean isValid = false;
    	boolean isValueOK = false;
    	boolean pageBroken = false;
    	
    	theValue.status = constants.CHECKED;
    	selInstance.type (elementLocator, theValue.value);
    	lastEnteredValue = theValue.value;
    	
    	action.saveRecord(selInstance);
    	isValid = !(action.isErrorPresent(selInstance, theValue.shouldBeErrorMessage));
    	pageBroken = isValid;
    	
    	isValueOK = (isValid==theValue.shouldBeValid);
    	if (isValueOK) theValue.validCheckResult= constants.CHECK_OK;
    	else theValue.validCheckResult = constants.CHECK_ERROR;

// logging
		String isValidInRealString = "invalid";
		if (isValid) 
			isValidInRealString = "valid";
		
    	if (isValueOK)
    		action.info("Value _"+theValue.value+"_ for element _"+elementName+"_ is "+isValidInRealString+" (OK)");
    	else {
    		action.error("Value _"+theValue.value+"_ for element _"+elementName+"_ is "+isValidInRealString+" (ERROR)");
    		action.getScreenshot(selInstance, true);
    	}
// end logging    	
    	
    	if (pageBroken){
    		if (isValueOK) return constants.RET_PAGE_BROKEN_OK;
    		else return constants.RET_PAGE_BROKEN_ERROR;
    	}

    	if (isValueOK) return constants.RET_OK;
    	else return constants.RET_ERROR;
    }

    protected int checkIsDisplayedRight(DefaultSelenium selInstance, checkValue theValue){
    	if (selInstance.isTextPresent(theValue.shouldBeDisplayed)){
    		action.infoV("Value _"+theValue.value+"_ for element _"+elementName+"_ is displayed as _"+theValue.shouldBeDisplayed+"_ (OK)");
    		theValue.displayedRightResult = constants.CHECK_OK;
    		return constants.RET_PAGE_BROKEN_OK;
    	}
    	
    	theValue.displayedRightResult = constants.CHECK_ERROR;
    	action.infoV("Value _"+theValue.value+"_ for element _"+elementName+"_ is NOT displayed as _"+theValue.shouldBeDisplayed+"_ (ERROR)");
    	action.getScreenshot(selInstance, true);
    	return constants.RET_PAGE_BROKEN_ERROR;
    }
    
    private int checkAllValuesRunCount=0;
    public int checkAllValues (DefaultSelenium selInstance){
    	int retValue;
    	checkValue tempCheckValue;
    	int countOfValuesToRun = 0;
    	
    	countOfValuesToRun = values.size();
    	
    	if (checkAllValuesRunCount>countOfValuesToRun-1) {
    		action.infoV("checkAllValues _"+elementName+"_ already was performed, skipping");
    		return constants.RET_SKIPPED;
    	}
    	
    	if (settings.LIMIT_CHECK_VALUES_COUNT_TO > 0 &&
    			settings.LIMIT_CHECK_VALUES_COUNT_TO<countOfValuesToRun){
        			countOfValuesToRun = settings.LIMIT_CHECK_VALUES_COUNT_TO;
        			action.warn("CountOfValuesToRun is limited to " + countOfValuesToRun);
        	}    	

    	while (checkAllValuesRunCount<countOfValuesToRun){
    		tempCheckValue = values.get(checkAllValuesRunCount);

    		if (isValueValidForThisElementLength(tempCheckValue)== false){
    			checkAllValuesRunCount++;
//TODO implement function to divide long entries
    		}else{
		    	retValue = checkOneValue(selInstance, tempCheckValue);
		    	if (retValue==constants.RET_PAGE_BROKEN_OK)
		    		retValue = checkIsDisplayedRight(selInstance, tempCheckValue);
	
		    	checkAllValuesRunCount++;
		    	if (retValue==constants.RET_PAGE_BROKEN_ERROR || retValue==constants.RET_PAGE_BROKEN_OK)
		    		return retValue;
    		}
    	}
    	return constants.RET_OK;
    }
}
      


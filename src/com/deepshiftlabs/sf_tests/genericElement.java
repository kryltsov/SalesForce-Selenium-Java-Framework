package com.deepshiftlabs.sf_tests;

import java.util.ArrayList;
import com.thoughtworks.selenium.*;

/** 
 * @class genericElement
 * This class is base for all other elements classes.
 * It contains all common methods and properties
 * @author Bear
 * @author bear@deepshiftlabs.com

*/
public class genericElement {
    protected String elementName;
    protected String elementSfId;
    protected String parentObjectType;
    protected String validValue;
    protected String writeLocator;    
    protected String readLocator;
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
        elementSfId = a_elementSfId;  // now this is reserved param
        isRequired = a_isRequired;
        writeLocator = "//label[text()='"+a_elementName+"']/following::input";
        readLocator = "//*[@class='labelCol' and text()='"+a_elementName+"']/following::*[@class]";
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
    
// you should use this func only on add/edit page becouse of writeLocator using     
    public int checkPresence (DefaultSelenium selInstance){
        
        if (action.isElementPresent(selInstance, writeLocator)){
            action.info ("Element name = "+elementName + " is PRESENT");
            return constants.RET_OK;
        }
        action.error ("Element name = "+elementName + " is NOT PRESENT. Locator was _"+writeLocator+"_");
        return constants.RET_ERROR;
    }
    
    public int fillByValidValue (DefaultSelenium selInstance){
        String tempValidValue = validValue;
        
        if (determinesRecordId) tempValidValue = recordId;
        
        action.typeText(selInstance, writeLocator, tempValidValue);
        lastEnteredValue = tempValidValue;
        action.infoV ("Filling Element _"+ elementName + "_ by valid value = _"+tempValidValue+"_");
       return constants.RET_OK;
    }
    
    public int fillByNull (DefaultSelenium selInstance){
        action.typeText(selInstance, writeLocator, "");
        lastEnteredValue = "";
        action.infoV ("Filling Element _"+ elementName + "_ by NULL");
       return constants.RET_OK;
    }

    // only template - each subclass will implement own 
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
	    	action.pressButton(selInstance, constants.SAVE_RECORD_LOCATOR);
	    	
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
    	action.typeText(selInstance, writeLocator, theValue.value);
    	lastEnteredValue = theValue.value;
    	
    	action.pressButton(selInstance, constants.SAVE_RECORD_LOCATOR);
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
    	String displayedText;
    	
    	displayedText = action.readText(selInstance, readLocator);
    	if  ( displayedText == constants.RET_ERROR_STRING ){
    		theValue.displayedRightResult = constants.CHECK_ERROR;
        	action.error("Can't get text for element _"+elementName);
        	action.getScreenshot(selInstance, true);
        	return constants.RET_PAGE_BROKEN_ERROR;    		
    	}
    	
    	if ( theValue.shouldBeDisplayed.equals(displayedText)){
    		action.infoV("Value _"+theValue.value+"_ for element _"+elementName+"_ is displayed as _"+displayedText+"_ (OK)");
    		theValue.displayedRightResult = constants.CHECK_OK;
    		return constants.RET_PAGE_BROKEN_OK;
    	}
    	
    	theValue.displayedRightResult = constants.CHECK_ERROR;
    	action.error("Value _"+theValue.value+"_ for element _"+elementName+"_ is displayed as _"+displayedText+"_ (should be _"+theValue.shouldBeDisplayed+"_)(ERROR)");
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
      


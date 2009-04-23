package com.deepshiftlabs.sf_tests;

import java.util.ArrayList;
import com.thoughtworks.selenium.*;

/** 
 * @class GenericElement
 * This class is base for all other elements classes.
 * It contains all common methods and properties
 * @author Bear
 * @author bear@deepshiftlabs.com

*/
public class GenericElement {
    protected String elementName;
    protected String elementSfId;
    protected String parentObjectType;
    protected String validValue;
    protected String invalidValue;
    protected String writeLocator;    
    protected String readLocator;
    protected String lastEnteredValue;
    protected int inputLength;
    protected boolean isRequired;
    protected boolean determinesRecordId = false;
    protected int errorsCount = -1;
    
    protected ArrayList <CheckValue> values = new ArrayList <CheckValue> ();
    
    String recordId = "";
    CommonActions action = new CommonActions();
    
    GenericElement(String a_elementName, String a_elementSfId, String a_parentObjectType, boolean a_isRequired) {
        elementName = a_elementName;
        parentObjectType = a_parentObjectType;
        elementSfId = a_elementSfId;  // now this is reserved param
        isRequired = a_isRequired;
        writeLocator = "//label[text()='"+a_elementName+"']/following::input";
        readLocator = "//*[@class='labelCol' and text()='"+a_elementName+"']/following::*[@class]";
        lastEnteredValue = "";
        inputLength = 32000+100;
    }
    
    public void init(CommonActions a_action){
    	action = a_action;
    }
        
    public String getElementName(){
    	return elementName;
    }
    
    public String getLastEnteredValue(){
    	return lastEnteredValue;
    }
    
    public void setValidValue(String a_validValue){
    	validValue = a_validValue;
    }
    
    public void setInvalidValue(String a_invalidValue){
    	invalidValue = a_invalidValue;
    }
    
    public int getErrorsCount(){
    	return errorsCount;
    }        
    
    public void forceToDetermineRecordID(String a_recordId){
    	determinesRecordId = true;
    	recordId = a_recordId;
    }
    
// you should use this func only on add/edit page because of writeLocator using     
    public int checkPresence (){
        
        if (action.isElementPresent(writeLocator)){
            action.info ("Element name = "+elementName + " is PRESENT");
            return Constants.RET_OK;
        }
        action.error ("Element name = "+elementName + " is NOT PRESENT. Locator was _"+writeLocator+"_");
        errorsCount++;
        return Constants.RET_ERROR;
    }
    
    public int fillByValidValue (){
        String tempValidValue = validValue;
        
        if (determinesRecordId) tempValidValue = recordId;
        
        if (action.typeText(writeLocator, tempValidValue) == Constants.RET_ERROR){
        	errorsCount++;
        	return Constants.RET_ERROR;
        }
        lastEnteredValue = tempValidValue;
        action.infoV ("Filling Element _"+ elementName + "_ by valid value = _"+tempValidValue+"_");
       return Constants.RET_OK;
    }
    
    public int fillByInvalidValue (){
        String tempInvalidValue = invalidValue;
        
        if (determinesRecordId) tempInvalidValue = recordId;
        
        if (action.typeText(writeLocator, tempInvalidValue) == Constants.RET_ERROR){
        	errorsCount++;
        	return Constants.RET_ERROR;
        }
// TODO: in future next string may became wrong 
        lastEnteredValue = tempInvalidValue;
        action.infoV ("Filling Element _"+ elementName + "_ by invalid value = _"+tempInvalidValue+"_");
       return Constants.RET_OK;
    }    
    
    public int fillByNull (){
        if (action.typeText(writeLocator, "") == Constants.RET_ERROR){
        	errorsCount++;
        	return Constants.RET_ERROR;
        }
        lastEnteredValue = "";
        action.infoV ("Filling Element _"+ elementName + "_ by NULL");
       return Constants.RET_OK;
    }

    // only template - each subclass will implement own 
    public boolean isValueValidForThisElementLength(CheckValue theValue){
    	return true;
    }
    
    public int  checkAll (){
    	int returnedValue;
        action.infoV ("Starting checking all for element _"+elementName+"_");

        returnedValue = checkRequired(); 
        if ((returnedValue == Constants.RET_PAGE_BROKEN_OK)||
    			(returnedValue== Constants.RET_PAGE_BROKEN_ERROR)) 
    		return returnedValue;

        returnedValue = checkAllValues(); 
        if ((returnedValue == Constants.RET_PAGE_BROKEN_OK)||
    			(returnedValue== Constants.RET_PAGE_BROKEN_ERROR)) 
    		return returnedValue;

        return Constants.RET_OK;
    }
   
    private int checkRequiredRunCount=0;
    public int checkRequired (){
    	boolean realRequired = false;
    	if (checkRequiredRunCount>0) {
    		action.infoV("CheckRequired for element _"+elementName+"_ already was performed, skipping");
    		return Constants.RET_SKIPPED;
    	}
    	checkRequiredRunCount++;
	        action.infoV ("Starting checkRequired for _"+elementName+"_");
	    	fillByNull();
	    	action.pressButton(Constants.SAVE_RECORD_LOCATOR);
	    	
	    	realRequired = action.isErrorPresent("You must enter a value");

	    	if (realRequired==true){
	    		if (isRequired==true){
	    	    	action.info("Element _"+ elementName + "_ is required!(OK)");
	    			action.getScreenshot(false);
	    			return Constants.RET_OK;
	    		}else{
	    			action.error("Element _"+ elementName + "_ is required!(ERROR)");
	    			action.getScreenshot(true);
	    			errorsCount++;
	    			return Constants.RET_ERROR;
	    		}
	    	}
	    	else{
	    		if (isRequired==false){
	    	    	action.info("Element _"+ elementName + "_ is NOT required!(OK)");
	    			action.getScreenshot(false);
	    			return Constants.RET_PAGE_BROKEN_OK;
	    		}else{
	    			action.error("Element _"+ elementName + "_ is NOT required!(ERROR)");
	    			action.getScreenshot(true);
	    			errorsCount++;
	    			return Constants.RET_PAGE_BROKEN_ERROR;
	    		}
	    	}
    }
    
 // TODO - Text element we should check if theValue.value.length()>maxLength and then skip this value
    private int checkOneValue(CheckValue theValue){
    	boolean isValid = false;
    	boolean isValueOK = false;
    	boolean pageBroken = false;
    	theValue.status = Constants.CHECKED;
    	action.typeText(writeLocator, theValue.value);
    	lastEnteredValue = theValue.value;
    	
    	action.pressButton(Constants.SAVE_RECORD_LOCATOR);
    	isValid = !(action.isErrorPresent(theValue.shouldBeErrorMessage));
    	pageBroken = isValid;
    	
    	isValueOK = (isValid==theValue.shouldBeValid);
    	if (isValueOK) theValue.validCheckResult= Constants.CHECK_OK;
    	else theValue.validCheckResult = Constants.CHECK_ERROR;

// logging
		String isValidInRealString = "invalid";
		if (isValid) 
			isValidInRealString = "valid";
		
    	if (isValueOK)
    		action.info("Value _"+theValue.value+"_ for element _"+elementName+"_ is "+isValidInRealString+" (OK)");
    	else {
    		errorsCount++;
    		action.error("Value _"+theValue.value+"_ for element _"+elementName+"_ is "+isValidInRealString+" (ERROR)");
    		action.getScreenshot(true);
    	}
// end logging    	
    	
    	if (pageBroken){
    		if (isValueOK) return Constants.RET_PAGE_BROKEN_OK;
    		else return Constants.RET_PAGE_BROKEN_ERROR;
    	}

    	if (isValueOK) return Constants.RET_OK;
    	else {
    		errorsCount++;
    		return Constants.RET_ERROR;
    	}
    }

    protected int checkIsDisplayedRight(CheckValue theValue){
    	String displayedText;
    	
    	displayedText = action.readText(readLocator);
    	if  ( displayedText == Constants.RET_ERROR_STRING ){
    		theValue.displayedRightResult = Constants.CHECK_ERROR;
    		errorsCount++;
    		action.error("Can't get text for element _"+elementName);
        	action.getScreenshot(true);
        	return Constants.RET_PAGE_BROKEN_ERROR;    		
    	}
    	
    	if ( theValue.shouldBeDisplayed.equals(displayedText)){
    		action.infoV("Value _"+theValue.value+"_ for element _"+elementName+"_ is displayed as _"+displayedText+"_ (OK)");
    		theValue.displayedRightResult = Constants.CHECK_OK;
    		return Constants.RET_PAGE_BROKEN_OK;
    	}
    	
    	theValue.displayedRightResult = Constants.CHECK_ERROR;
    	errorsCount++;
    	action.error("Value _"+theValue.value+"_ for element _"+elementName+"_ is displayed as _"+displayedText+"_ (should be _"+theValue.shouldBeDisplayed+"_)(ERROR)");
    	action.getScreenshot(true);
    	return Constants.RET_PAGE_BROKEN_ERROR;
    }
    
    private int checkAllValuesRunCount=0;
    public int checkAllValues (){
    	int retValue;
    	CheckValue tempCheckValue;
    	int countOfValuesToRun = 0;
    	
    	countOfValuesToRun = values.size();
    	
    	if (checkAllValuesRunCount>countOfValuesToRun-1) {
    		action.infoV("checkAllValues _"+elementName+"_ already was performed, skipping");
    		return Constants.RET_SKIPPED;
    	}
    	
    	if (Settings.LIMIT_CHECK_VALUES_COUNT_TO > 0 &&
    			Settings.LIMIT_CHECK_VALUES_COUNT_TO<countOfValuesToRun){
        			countOfValuesToRun = Settings.LIMIT_CHECK_VALUES_COUNT_TO;
        			action.warn("CountOfValuesToRun is limited to " + countOfValuesToRun);
        	}    	

    	while (checkAllValuesRunCount<countOfValuesToRun){
    		tempCheckValue = values.get(checkAllValuesRunCount);

    		if (isValueValidForThisElementLength(tempCheckValue)== false){
    			checkAllValuesRunCount++;
//TODO implement function to divide long entries
    		}else{
		    	retValue = checkOneValue(tempCheckValue);
		    	if (retValue==Constants.RET_PAGE_BROKEN_OK)
		    		retValue = checkIsDisplayedRight(tempCheckValue);
	
		    	checkAllValuesRunCount++;
		    	if (retValue==Constants.RET_PAGE_BROKEN_ERROR || retValue==Constants.RET_PAGE_BROKEN_OK)
		    		return retValue;
    		}
    	}
    	return Constants.RET_OK;
    }
}
      


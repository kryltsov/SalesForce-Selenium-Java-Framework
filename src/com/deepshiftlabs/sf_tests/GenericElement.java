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
    protected String name;
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
        name = a_elementName;
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
    	return name;
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
    	Event event = action.startEvent(name, "checkPresence");
        
        if (action.isElementPresent(writeLocator)){
            action.info ("Element name = "+name + " is PRESENT");
            action.closeEventOk(event);
            return Constants.RET_OK;
        }
        action.error ("Element name = "+name + " is NOT PRESENT. Locator was _"+writeLocator+"_");
        action.closeEventError(event);
        errorsCount++;
        return Constants.RET_ERROR;
    }
    
    public int fillByValidValue (){
    	Event event = action.startEvent(name, "fillByValidValue");
        String tempValidValue = validValue;
        
        if (determinesRecordId) tempValidValue = recordId;
        
        if (action.typeText(writeLocator, tempValidValue) == Constants.RET_ERROR){
        	errorsCount++;
        	action.closeEventError(event);
        	return Constants.RET_ERROR;
        }
        lastEnteredValue = tempValidValue;
        action.infoV ("Filling Element _"+ name + "_ by valid value = _"+tempValidValue+"_");
        action.closeEventOk(event);
       return Constants.RET_OK;
    }
    
    public int fillByInvalidValue (){
    	Event event = action.startEvent(name, "fillByInvalidValue");
        String tempInvalidValue = invalidValue;
        
        if (determinesRecordId) tempInvalidValue = recordId;
        
        if (action.typeText(writeLocator, tempInvalidValue) == Constants.RET_ERROR){
        	errorsCount++;
        	action.closeEventError(event);
        	return Constants.RET_ERROR;
        }
// TODO: in future next string may became wrong 
        lastEnteredValue = tempInvalidValue;
        action.infoV ("Filling Element _"+ name + "_ by invalid value = _"+tempInvalidValue+"_");
        action.closeEventOk(event);
       return Constants.RET_OK;
    }    
    
    public int fillByNull (){
    	Event event = action.startEvent(name, "fillByNull");
        if (action.typeText(writeLocator, "") == Constants.RET_ERROR){
        	errorsCount++;
        	action.closeEventError(event);
        	return Constants.RET_ERROR;
        }
        lastEnteredValue = "";
        action.infoV ("Filling Element _"+ name + "_ by NULL");
        action.closeEventOk(event);
       return Constants.RET_OK;
    }

    // only template - each subclass will implement own 
    public boolean isValueValidForThisElementLength(CheckValue theValue){
    	return true;
    }
    
    private Event checkAllEvent = null;
    public int  checkAll (){
    	if (checkAllEvent==null){
    		checkAllEvent = action.startEvent(name, "checkAll"); 
    	}
    	int returnedValue;
        action.infoV ("Starting checking all for element _"+name+"_");

        returnedValue = checkRequired(); 
        if ((returnedValue == Constants.RET_PAGE_BROKEN_OK)||
    			(returnedValue== Constants.RET_PAGE_BROKEN_ERROR)){ 
    		return returnedValue;
        }

        returnedValue = checkAllValues(); 
        if ((returnedValue == Constants.RET_PAGE_BROKEN_OK)||
    			(returnedValue== Constants.RET_PAGE_BROKEN_ERROR)){ 
    		return returnedValue;
        }

        action.closeEventOk(checkAllEvent); 
        return Constants.RET_OK;
    }
   
    private int checkRequiredRunCount=0;
    public int checkRequired (){
    	boolean realRequired = false;
    	if (checkRequiredRunCount>0) {
    		action.infoV("CheckRequired for element _"+name+"_ already was performed, skipping");
    		return Constants.RET_SKIPPED;
    	}
    	checkRequiredRunCount++;
    	Event event = action.startEvent(name, "checkRequired");
    	event.setWaitedValue(new Boolean(isRequired).toString());
    	
	        action.infoV ("Starting checkRequired for _"+name+"_");
	    	fillByNull();
	    	action.pressButton(Constants.SAVE_RECORD_LOCATOR);
	    	
	    	realRequired = action.isErrorPresent("You must enter a value");
	    	event.setRealValue(new Boolean(realRequired).toString());

	    	if (realRequired==true){
	    		if (isRequired==true){
	    	    	action.info("Element _"+ name + "_ is required!(OK)");
	    			action.getScreenshot(false);
	    			action.closeEventOk(event);
	    			return Constants.RET_OK;
	    		}else{
	    			action.error("Element _"+ name + "_ is required!(ERROR)");
	    			action.getScreenshot(true);
	    			errorsCount++;
	    			action.closeEventError(event);
	    			return Constants.RET_ERROR;
	    		}
	    	}
	    	else{
	    		if (isRequired==false){
	    	    	action.info("Element _"+ name + "_ is NOT required!(OK)");
	    			action.getScreenshot(false);
	    			action.closeEventOk(event);
	    			return Constants.RET_PAGE_BROKEN_OK;
	    		}else{
	    			action.error("Element _"+ name + "_ is NOT required!(ERROR)");
	    			action.getScreenshot(true);
	    			errorsCount++;
	    			action.closeEventError(event);
	    			return Constants.RET_PAGE_BROKEN_ERROR;
	    		}
	    	}
    }
    
 // TODO - Text element we should check if theValue.value.length()>maxLength and then skip this value
    private int checkValueValidity(CheckValue theValue){
    	Event event = action.startEvent(name, "checkValueValidity");
    	boolean isValid = false;
    	boolean isValueOK = false;
    	boolean pageBroken = false;
    	
    	event.setValue(theValue.value);
    	event.setWaitedValue(new Boolean(theValue.shouldBeValid).toString());
    	
    	theValue.status = Constants.CHECKED;
    	action.typeText(writeLocator, theValue.value);
    	lastEnteredValue = theValue.value;
    	
    	action.pressButton(Constants.SAVE_RECORD_LOCATOR);
    	isValid = !(action.isErrorPresent(theValue.shouldBeErrorMessage));
    	event.setRealValue(new Boolean(isValid).toString());
    	pageBroken = isValid;
    	
    	isValueOK = (isValid==theValue.shouldBeValid);
    	if (isValueOK) theValue.validCheckResult= Constants.CHECK_OK;
    	else theValue.validCheckResult = Constants.CHECK_ERROR;

// logging
		String isValidInRealString = "invalid";
		if (isValid) 
			isValidInRealString = "valid";
		
    	if (isValueOK)
    		action.info("Value _"+theValue.value+"_ for element _"+name+"_ is "+isValidInRealString+" (OK)");
    	else {
    		errorsCount++;
    		action.error("Value _"+theValue.value+"_ for element _"+name+"_ is "+isValidInRealString+" (ERROR)");
    		action.getScreenshot(true);
    	}
    	
    	if (isValueOK)
			action.closeEventOk(event);
		else
			action.closeEventError(event);    	
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
    	Event event = action.startEvent(name, "checkIsDisplayedRight");
    	String displayedText;
    	
    	event.setValue(theValue.value);
    	event.setWaitedValue(theValue.shouldBeDisplayed);
    	
    	displayedText = action.readText(readLocator);
    	event.setRealValue(displayedText);
    	if  ( displayedText == Constants.RET_ERROR_STRING ){
    		theValue.displayedRightResult = Constants.CHECK_ERROR;
    		errorsCount++;
    		action.error("Can't get text for element _"+name);
        	action.getScreenshot(true);
        	action.closeEventError(event);
        	return Constants.RET_PAGE_BROKEN_ERROR;    		
    	}
    	
    	if ( theValue.shouldBeDisplayed.equals(displayedText)){
    		action.infoV("Value _"+theValue.value+"_ for element _"+name+"_ is displayed as _"+displayedText+"_ (OK)");
    		theValue.displayedRightResult = Constants.CHECK_OK;
    		action.closeEventOk(event);
    		return Constants.RET_PAGE_BROKEN_OK;
    	}
    	
    	theValue.displayedRightResult = Constants.CHECK_ERROR;
    	errorsCount++;
    	action.error("Value _"+theValue.value+"_ for element _"+name+"_ is displayed as _"+displayedText+"_ (should be _"+theValue.shouldBeDisplayed+"_)(ERROR)");
    	action.getScreenshot(true);
    	action.closeEventError(event);
    	return Constants.RET_PAGE_BROKEN_ERROR;
    }
    
    private Event checkAllValuesEvent = null;
    private int checkAllValuesRunCount=0;
    public int checkAllValues (){
    	if (checkAllValuesEvent==null){
    		checkAllValuesEvent = action.startEvent(name, "checkAllValues"); 
    	}
    	int retValue;
    	CheckValue tempCheckValue;
    	int countOfValuesToRun = 0;
    	
// TODO this line should be combined with limits from settings
    	countOfValuesToRun = values.size();
    	
    	if (checkAllValuesRunCount>countOfValuesToRun-1) {
    		action.infoV("checkAllValues _"+name+"_ already was performed, skipping");
    		return Constants.RET_SKIPPED;
    	}

// TODO implement count limit 0    	
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
		    	retValue = checkValueValidity(tempCheckValue);
		    	if (retValue==Constants.RET_PAGE_BROKEN_OK)
		    		retValue = checkIsDisplayedRight(tempCheckValue);
	
		    	checkAllValuesRunCount++;
		    	if (retValue==Constants.RET_PAGE_BROKEN_ERROR || retValue==Constants.RET_PAGE_BROKEN_OK)
		    		return retValue;
    		}
    	}
    	action.closeEventOk(checkAllValuesEvent);
    	return Constants.RET_OK;
    }
}
      


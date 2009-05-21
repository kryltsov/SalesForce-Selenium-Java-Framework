package com.deepshiftlabs.sf_tests;

import java.util.ArrayList;

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
    CommonActions action = null;
    
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
    	Event event = action.startEvent("checkPresence", name);
        
        if (action.isElementPresent(writeLocator)){
            action.closeEventOk(event);
            return Constants.RET_OK;
        }
        action.closeEventError(event);
        errorsCount++;
        return Constants.RET_ERROR;
    }
    
    public int fillByValidValue (){
    	Event event = action.startEvent("fillByValidValue", name);
        String tempValidValue = validValue;
        
        if (determinesRecordId) tempValidValue = recordId;
        
        if (action.typeText(writeLocator, tempValidValue) == Constants.RET_ERROR){
        	errorsCount++;
        	action.closeEventError(event);
        	return Constants.RET_ERROR;
        }
        lastEnteredValue = tempValidValue;
        action.closeEventOk(event);
       return Constants.RET_OK;
    }
    
    public int fillByInvalidValue (){
    	Event event = action.startEvent("fillByInvalidValue", name);
        String tempInvalidValue = invalidValue;
        
        if (determinesRecordId) tempInvalidValue = recordId;
        
        if (action.typeText(writeLocator, tempInvalidValue) == Constants.RET_ERROR){
        	errorsCount++;
        	action.closeEventError(event);
        	return Constants.RET_ERROR;
        }
// TODO: in future next string may became wrong 
        lastEnteredValue = tempInvalidValue;
        action.closeEventOk(event);
       return Constants.RET_OK;
    }    
    
    public int fillByNull (){
    	Event event = action.startEvent("fillByNull", name);
        if (action.typeText(writeLocator, "") == Constants.RET_ERROR){
        	errorsCount++;
        	action.closeEventError(event);
        	return Constants.RET_ERROR;
        }
        lastEnteredValue = "";
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
    		checkAllEvent = action.startEvent("checkAll", name); 
    	}
    	int returnedValue;

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
    	Boolean realRequired = false;
    	if (checkRequiredRunCount>0) {
    		return Constants.RET_SKIPPED;
    	}
    	checkRequiredRunCount++;
    	Event event = action.startEvent("checkRequired", name);
    	event.setWaitedValue("should be required = " + new Boolean(isRequired).toString());
    	
	    	fillByNull();
	    	action.pressButton(Constants.SAVE_RECORD_LOCATOR);
	    	
	    	realRequired = action.isErrorPresent("You must enter a value");
	    	event.setRealValue("required = " + realRequired.toString());
	    	
	    	if (isRequired ^ realRequired){
	    		action.closeEventError(event);
	    	}
	    	else {
	    		action.closeEventOk(event);
	    	}

	    	if (realRequired){
	    		if (isRequired){
	    			return Constants.RET_OK;
	    		}else{
	    			errorsCount++;
	    			return Constants.RET_ERROR;
	    		}
	    	}
	    	else{
	    		if (!isRequired){
	    			return Constants.RET_PAGE_BROKEN_OK;
	    		}else{
	    			errorsCount++;
	    			return Constants.RET_PAGE_BROKEN_ERROR;
	    		}
	    	}
    }
    
 // TODO - Text element we should check if theValue.value.length()>maxLength and then skip this value
    private int checkValueValidity(CheckValue theValue){
    	Event event = action.startEvent("checkValueValidity", name);
    	Boolean isValid = false;
    	boolean pageBroken = false;
    	
    	event.setValue(theValue.value);
    	event.setWaitedValue("should be valid : " + new Boolean(theValue.shouldBeValid).toString());
    	
    	theValue.status = Constants.CHECKED;
    	action.typeText(writeLocator, theValue.value);
    	lastEnteredValue = theValue.value;
    	
    	action.pressButton(Constants.SAVE_RECORD_LOCATOR);
    	isValid = !(action.isErrorPresent(theValue.shouldBeErrorMessage));

    	event.setRealValue("is valid : " + isValid.toString());

    	pageBroken = isValid;  // because if value is valid in real, we just saved record    	
    	if (isValid==theValue.shouldBeValid){
    		theValue.validCheckResult= Constants.CHECK_OK;
    		action.closeEventOk(event);
    		
    		if (pageBroken) 
    			return Constants.RET_PAGE_BROKEN_OK;
    		else
    			return Constants.RET_OK;
    	}	
    	else {
    		theValue.validCheckResult = Constants.CHECK_ERROR;
    		errorsCount++;
    		action.closeEventError(event);
    		if (pageBroken) 
    			return Constants.RET_PAGE_BROKEN_ERROR;
    		else
    			return Constants.RET_ERROR;    		
    	}
	}

    protected int checkIsDisplayedRight(CheckValue theValue){
    	Event event = action.startEvent("checkIsDisplayedRight", name);
    	String displayedText;
    	
    	event.setValue(theValue.value);
    	event.setWaitedValue(theValue.shouldBeDisplayed);
    	
    	displayedText = action.readText(readLocator);
    	
    	if  ( displayedText.equals(Constants.RET_ERROR_STRING) ){
    		theValue.displayedRightResult = Constants.CHECK_ERROR;
    		errorsCount++;
        	action.closeEventError(event);
        	return Constants.RET_PAGE_BROKEN_ERROR;    		
    	}
    	
    	event.setRealValue(displayedText);    	
    	
    	if ( theValue.shouldBeDisplayed.equals(displayedText)){
    		theValue.displayedRightResult = Constants.CHECK_OK;
    		action.closeEventOk(event);
    		return Constants.RET_PAGE_BROKEN_OK;
    	}
    	else {
	    	theValue.displayedRightResult = Constants.CHECK_ERROR;
	    	errorsCount++;
	    	action.closeEventError(event);
	    	return Constants.RET_PAGE_BROKEN_ERROR;
    	}
    }
    
    private Event checkAllValuesEvent = null;
    private int checkAllValuesRunCount=0;
    public int checkAllValues (){
    	if (checkAllValuesEvent==null){
    		checkAllValuesEvent = action.startEvent("checkAllValues", name); 
    	}
    	int retValue;
    	CheckValue tempCheckValue;
    	int countOfValuesToRun = 0;
    	
// TODO this line should be combined with limits from settings
    	countOfValuesToRun = values.size();
    	
    	if (checkAllValuesRunCount>countOfValuesToRun-1) {
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
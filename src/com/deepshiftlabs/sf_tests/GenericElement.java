package com.deepshiftlabs.sf_tests;

import java.util.ArrayList;

/** 
 * Represent a class which is base for all other elements classes.
 * It contains all common methods and properties.
 * After construction, you have to call init method.
 * @author Bear
 * @author bear@deepshiftlabs.com

*/
public class GenericElement {
    protected String name;
    protected String parentObjectType;
    
    /**
     * Known as valid value.
     */
    protected String validValue;

    /**
     * Known as invalid value.
     */
    protected String invalidValue;
    protected String writeLocator;    
    protected String readLocator;

    /**
     * This variable is used when element determines record Id and we want to delete saved record. 
     */
    protected String lastEnteredValue;
    protected int inputLength;
    protected boolean isRequired;

    /**
     * if true, value of this element will determine whole record Id.
     * Only 1 element can be determining.
     */
    protected boolean determinesRecordId = false;
    protected int errorsCount = -1;
     
    
    /**
     * This list contains all values which should be used to test element.
     */
    protected ArrayList <CheckValue> values = new ArrayList <CheckValue> ();
    
    String recordId = "";
    
    /**
     * Link to CommonActions object. Will be inited in init method. Each element of this test has the same link.  
     */
    CommonActions action = null;
    
    
    /**
     * @param a_elementName Salesforce name of element 
     * @param a_parentObjectType name of salesforce object which contains this element 
     * @param a_isRequired determines if element should be filled with value to store record
     */
    GenericElement(String a_elementName, String a_parentObjectType, boolean a_isRequired) {
        name = a_elementName;
        parentObjectType = a_parentObjectType;
        isRequired = a_isRequired;
        writeLocator = "//label[text()='"+a_elementName+"']/following::input";
        readLocator = "//*[@class='labelCol' and text()='"+a_elementName+"']/following::*[@class]";
        lastEnteredValue = "";
        inputLength = 32000+100;
    }
    
    
    /**
     * Call of this method is obligatory after construction of element.
     */
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
    
    /**
     * Determines if element is presented on page. 
     * This function is ONLY for edit (or add) record pages because it uses writeLocator of element to search.
     */
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
    
    /**
     * Fills element with known-as-good value.
     * ! Note that if element determines recordId, valid value will be taken from recordId field.
     * This field should be set by parent genericObject of this element. 
     */
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
    
    /**
     * Fills element with known-as-invalid value.
     */
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

  
    /**
     * Only template in this parent class - each subclass will implement own.
     * Used to determine if test value is more than element input length.
     * @return true.
     */
    public boolean isValueValidForThisElementLength(CheckValue theValue){
    	return true;
    }
    
    /**
     * Because method checkAll may be called many times for one element, we can't simple create Event object inside it.
     * So we declare variable here and at first call of checkAll instantiate object.
     * CheckAll method can't generate errors itself, so we use this event only to block all elements checks. 
     * @see #checkAll()
     */
    private Event checkAllEvent = null;

    /**
     * This method performs all checks that element has.
     * When it is called, add or edit record page should be opened, with all elements filled by valid values,
     * so we should not think about other elements, only about current.
     * Some checks which are called in this method can lead to saving of record and opening Show record page 
     * (e.g. if we examine valid value of element). As this, one element level of test know anything about other elements,
     * we should ask parent object to delete saved record and create new, and then call this method again to continue with
     * next check.
     * So this method may be executed several times for one element. 
     *    
     * @return RET_PAGE_BROKEN_OK when page was saved and it was waited,  
     * RET_PAGE_BROKEN_ERROR when page was saved and it was NOT waited,
     * RET_OK if all checks done, 
     * RET_ERROR if there was some fatal error in element checks.
     */
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
        if (returnedValue == Constants.RET_ERROR){
        	action.closeEventFatal(checkAllEvent, "Fatal error when doing checkAllValues for element. May be too much errors per element.");
        	return returnedValue;
        }
        
        if ((returnedValue == Constants.RET_PAGE_BROKEN_OK)||
    			(returnedValue== Constants.RET_PAGE_BROKEN_ERROR)){ 
    		return returnedValue;
        }

        action.closeEventOk(checkAllEvent); 
        return Constants.RET_OK;
    }
    
    /**
     * Because method checkAll may be called many times for one element, and each time in it checkRequired method called,
     * we should improve mechanism to skip already executed checks.
     * This variable used to determine should we process or skip function checkRequired.
     * @see #checkAll()
     * 
     */
    private int checkRequiredRunCount=0;
    
    /**
     * Checks if element which should be required is really required and vice versa
     * @see #checkRequiredRunCount
     * 
     * @return RET_OK if element considered required is required in real, 
     * RET_ERROR if element considered required is not required in real, 
     * RET_PAGE_BROKEN_OK if element considered not required is not required in real, 
     * RET_PAGE_BROKEN_ERROR if element considered required is not required in real, 
     * RET_SKIPPED if this check was done before (checkRequiredRunCount>0).
     * 
     */
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
    
    /**
     * Checks if value considered as valid is valid in real and vice versa.
     * @return RET_OK if value considered valid is valid in real, 
     * RET_ERROR if value considered valid is not valid in real, 
     * RET_PAGE_BROKEN_OK if value considered not valid is not valid in real, 
     * RET_PAGE_BROKEN_ERROR if value considered valid is not valid in real.
     */
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
    	
// TODO inspect and comment next string    	
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

    /**
     * Checks if valid value after saving record is displayed as it is waited.
     * RET_PAGE_BROKEN_OK if value is displayed as waited, 
     * RET_PAGE_BROKEN_ERROR if value is displayed wrong.
     */
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
    
    /**
     * @see #checkAll()
     * @see #checkAllEvent 
     */
    private Event checkAllValuesEvent = null;
    
    /**
     * Because method checkAll may be called many times for one element, and each time in it checkAllValues method called,
     * we should improve mechanism to skip already checked values.
     * This variable used to determine how many values in list are already checked in checkAllValues.
     * @see #checkAll()
     */
    private int checkAllValuesRunCount=0;
    
    /**
     * Checks each value to be really valid or invalid, for valid values checks if stored value is displayed correctly,
     * for invalid values checks that certain errors are displayed.
     * @see #checkAllValuesRunCount
     * @see #checkAll()
     * @return RET_OK if all values have been checked
     * RET_PAGE_BROKEN_OK if some value was considered valid and was valid in real - so record was saved,  
     * RET_PAGE_BROKEN_ERROR if some value was considered invalid and was valid in real - so record was saved,  
     * RET_ERROR if fatal error happened (for example number of element's error became > FATAL_ELEMENT_ERRORS_COUNT)
     * @see Settings#FATAL_ELEMENT_ERRORS_COUNT
     * 
     */
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
    	// of in Settings we limit the number of values to check
    	if (Settings.LIMIT_CHECK_VALUES_COUNT_TO > 0 &&
    			Settings.LIMIT_CHECK_VALUES_COUNT_TO<countOfValuesToRun){
        			countOfValuesToRun = Settings.LIMIT_CHECK_VALUES_COUNT_TO;
        			action.warn("CountOfValuesToRun is limited to " + countOfValuesToRun);
        	}    	
    	// if we have unchecked values in list
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
		    	
		    	if (errorsCount >= Settings.FATAL_ELEMENT_ERRORS_COUNT){
		    		action.closeEventFatal(checkAllValuesEvent, "Too many errors per element");
		    		return Constants.RET_ERROR;
		    	}
		    	if (retValue==Constants.RET_PAGE_BROKEN_ERROR || retValue==Constants.RET_PAGE_BROKEN_OK)
		    		return retValue;
    		}
    	}
    	action.closeEventOk(checkAllValuesEvent);
    	return Constants.RET_OK;
    }
}
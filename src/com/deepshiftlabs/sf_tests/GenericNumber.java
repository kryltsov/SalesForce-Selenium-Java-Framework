package com.deepshiftlabs.sf_tests;

/**
 * Represents parent class for Numbers branch of elements tree.
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
public class GenericNumber extends GenericElement {
	protected int intPlaces;
	protected int decimalPlaces;


	/**
     * @param a_elementName Salesforce name of element 
     * @param a_parentObjectType name of salesforce object which contains this element 
     * @param a_isRequired determines if element should be filled with value to store record
	 * @param a_intPlaces assumed number of digits before dot
	 * @param a_decimalPlaces assumed number of digits after dot
	 */
	public GenericNumber(String a_elementName, String a_parentObjectType, boolean a_isRequired,
			 int a_intPlaces, int a_decimalPlaces) {
		super(a_elementName, a_parentObjectType, a_isRequired);
		validValue = "1";
		setInputLength(a_intPlaces, a_decimalPlaces);

    	values.add(new CheckValue("78.7245", Constants.IT_IS_VALID_VALUE,"","78.7"));  // to check decimal places
    	
		values.add(new CheckValue("be", Constants.IT_IS_INVALID_VALUE));
		values.add(new CheckValue("1-e", Constants.IT_IS_INVALID_VALUE));
		values.add(new CheckValue("10'0", Constants.IT_IS_INVALID_VALUE));
		values.add(new CheckValue("1.0.0", Constants.IT_IS_INVALID_VALUE));
		values.add(new CheckValue("100,", Constants.IT_IS_INVALID_VALUE));  // check 3
		
// TODO I should implement function, that will validate shouldBeDisplayed field of CheckValue in accordance with decimalPlaces
// and in accordance to triplets delimiter "," before we will use them
		
		values.add(new CheckValue(" 00 00 123", Constants.IT_IS_VALID_VALUE,"","123"));  // check 2
		values.add(new CheckValue("4,5,6", Constants.IT_IS_VALID_VALUE,"","456"));   // check 3
		values.add(new CheckValue("78.7 ", Constants.IT_IS_VALID_VALUE,"","78.7"));  // check 4
	}
	
    private void setInputLength (int a_intPlaces, int a_decimalPlaces){
    	intPlaces =  a_intPlaces;
    	decimalPlaces = a_decimalPlaces;
     }
    
    /**
     * @param theString string containing number with dot
     * @return Number of digits after dot.
     */
    public int getDecimalPlacesCount(String theString){
// TODO should check case where there are not dot     	
    	return theString.length()-theString.indexOf('.')-1;
    }
    
    /* 
     * Used to determine if integer part of test check Value is not longer than element intPlaces
     * @see com.deepshiftlabs.sf_tests.GenericElement#isValueValidForThisElementLength(com.deepshiftlabs.sf_tests.CheckValue)
     * @see GenericElement#intPlaces
     * @return true if integer digits count is less or equal to element intPlaces
     */
    public boolean isValueValidForThisElementLength(CheckValue theValue){
    	char tempChar;
    	// count of digits in integer part of value
    	int count=0;
    	int afterDollar=0;
    	boolean leadingZerosEnded = false;
    	
    	Event event = action.startEvent("isValueValidForThisElementLength", theValue.value);    	
    	
    	// number values can include leading $ symbols, so afterDollar is first digit of number 
    	if (theValue.value.indexOf('$')>0)
    		afterDollar=theValue.value.indexOf('$')+1;
    		
    	for (int i=afterDollar; i<theValue.value.length(); i++){
    		tempChar = theValue.value.charAt(i);
    		if (tempChar=='.') 
    			i = theValue.value.length(); // to end cycle
    		// if we have leading zeros, we should skip them
    		if (Character.isDigit(tempChar)){
    			if (tempChar!='0') 
    				leadingZerosEnded = true;
    			if (leadingZerosEnded)
    				count++;
    		}
    	}
    	if (count>intPlaces){
    		action.closeEventError(event, "value is greater than setted element lenght");
    		return false;
    	}
    	action.closeEventOk(event);    	
    	return true;
    }    
    
    /**
     * Because method checkAll may be called many times for one element, and each time in it checkDecimalPlacesCount method called,
     * we should improve mechanism to skip already executed checks.
     * This variable used to determine should we process or skip function checkDecimalPlacesCount.
     * @see #checkAll()
     */
    public int checkDecimalPlacesCountRunCount=0;
    
    /**
     * Checks if displayed digits after dot is equal to assumed.
     * @return RET_OK, 
     * RET_ERROR, 
     * RET_SKIPPED if this check was done before (checkDecimalPlacesCountRunCount>0).
     */
    public int checkDecimalPlacesCount(String displayedValue){
    	if (checkDecimalPlacesCountRunCount>0) {
       		action.info("checkDecimalPlacesCount for element _"+name+"_ already was performed, skipping");      		
         	return Constants.RET_SKIPPED;
         }
    	checkDecimalPlacesCountRunCount++;
    	
		Event event = action.startEvent("checkDecimalPlacesCount", displayedValue);
		
		Integer count = getDecimalPlacesCount(displayedValue);
		
		event.setWaitedValue(new Integer(decimalPlaces).toString());		
		event.setRealValue(count.toString());
		
		if (count==decimalPlaces){
        	action.closeEventOk(event);
        	return Constants.RET_OK;    			
		}else{
        	action.closeEventError(event);
        	return Constants.RET_ERROR;    			
		}
    }
    
 // // TODO optimize and comment!    
    /**
     * This variable used to determine should we process or skip function checkMaxLength.
     * @see #checkAll()
     */
    public int checkMaxLengthRunCount=0;
    
    /**
     * Checks accordance of assumed displayed intPlaces and decimalPlaces of check value with real values.
     * @return RET_OK, 
     * RET_ERROR, 
     * RET_SKIPPED if this check was done before (checkDecimalPlacesCountRunCount>0).
     */
    public int checkMaxLength(){
        String testString;
        char validChar;
    	
    	if (checkMaxLengthRunCount>1) {
   		action.info("checkMaxLength for element _"+name+"_ already was performed, skipping");      		
     		return Constants.RET_SKIPPED;
     	}
     	checkMaxLengthRunCount++;
     	
     	Event event = action.startEvent("checkMaxLength", name);    
    	event.setWaitedValue(Integer.toString(intPlaces));
     	
        if (validValue.length()>1){
          	 action.closeEventError(event, "validValue is not one digit");
          	 return Constants.RET_ERROR;
        }     	

        validChar = validValue.charAt(0);
        testString = validValue;
 
// TODO this IF is ALWAYS true - check this function
        if (checkMaxLengthRunCount==1){
        	// while got the exact number of intPlaces in testString
	        while (testString.length() < (intPlaces) ){
	       	 	testString = validChar+testString;
	        }
	        
	        action.typeText(writeLocator, testString);
	        action.pressButton(Constants.SAVE_RECORD_LOCATOR);
	        if (action.isErrorPresent("Number is too large.")){
	        	checkMaxLengthRunCount=2; // no need in next step of check
	        	errorsCount++;
	        	action.closeEventError(event, "decimal plases check skipped");
	        	return Constants.RET_ERROR;
	        } else {
	        	action.closeEventOk(event);
	        	return Constants.RET_PAGE_BROKEN_OK;        	
	        }
        }else{
        	// while got the exact number+1 of intPlaces in testString
	        while (testString.length() < (intPlaces+1) ){
	       	 testString = validChar+testString;
	        }
	        action.typeText(writeLocator, testString);
	        action.pressButton(Constants.SAVE_RECORD_LOCATOR);
	        if (action.isErrorPresent("Number is too large.")){
	        	checkDecimalPlacesCount(action.readValue(writeLocator));
//  TODO maybe we should edit retValue depending on checkDecimalPlacesCount()
	        	action.closeEventOk(event);
	        	return Constants.RET_OK;        	
	        } else {
	        	errorsCount++;
	        	action.closeEventError(event, "decimal plases check skipped");
	        	return Constants.RET_PAGE_BROKEN_ERROR;
	        }        	
        }
    }

    /**
     * This variable used to determine should we process or skip function checkForTooBig.
     * @see #checkAll()
     */    
    public int checkForTooBigRunCount=0;    

    /**
     * Checks displaying #Too Big! error if many symbols entered to number field.
     * @return RET_OK, 
     * RET_ERROR if record is not saved, but there are not #Too Big! string, 
     * RET_PAGE_BROKEN_ERROR if record was saved without any problems, 
     * RET_SKIPPED if this check was done before (checkForTooBigRunCount>0).
     */
    public int checkForTooBig(){
        String testString;
        char validChar;
    	
    	if (checkForTooBigRunCount>0) {
   		action.info("checkForTooBig for element _"+name+"_ already was performed, skipping");      		
     		return Constants.RET_SKIPPED;
     	}
    	checkForTooBigRunCount++;
    	
    	Event event = action.startEvent("checkForTooBig", name);
     	
        validChar = validValue.charAt(0);
        testString = validValue;
        
// while got the number of intPlaces > 19
	        while (testString.length() < 20){
	       	 testString = validChar+testString;
	        }
	        
	        action.typeText(writeLocator, testString);
	        action.pressButton(Constants.SAVE_RECORD_LOCATOR);
	        if (action.isErrorPresent("Number is too large.")){
	        	if (action.readValue(writeLocator).equals("#Too Big!")){
	        		action.closeEventOk(event);
		        	return Constants.RET_OK;
	        	}
	        	errorsCount++;
	        	action.closeEventError(event);
	        	return Constants.RET_ERROR;
	        }
        	action.closeEventError(event);
        	return Constants.RET_PAGE_BROKEN_ERROR;	        
    }
    
    /**
     * Because method checkAll may be called many times for one element, we can't simple create Event object inside it.
     * So we declare variable here and at first call of checkAll instantiate object.
     * CheckAll method can't generate errors itself, so we use this event only to block all elements checks. 
     * @see #checkAll()
     */
    private Event checkAllEventNumber = null;
    
    /* 
     * @see com.deepshiftlabs.sf_tests.GenericElement#checkAll()
     */
    public int  checkAll (){
    	if (checkAllEventNumber==null){
    		checkAllEventNumber = action.startEvent("checkAll", name); 
    	}
	   	int returnedValue;
	   	returnedValue = super.checkAll();
	   	if (returnedValue==Constants.RET_ERROR){
	   		action.closeEventFatal(checkAllEventNumber);
	   		return Constants.RET_ERROR;	   	
	   	}
	   	
	   	if (returnedValue!=Constants.RET_OK)
	   		return returnedValue;
	
	   	returnedValue = checkMaxLength();   	
    	if (errorsCount >= Settings.FATAL_ELEMENT_ERRORS_COUNT){
    		action.closeEventFatal(checkAllEventNumber, "Too many errors per element");
    		return Constants.RET_ERROR;
    	}  	   	
	   	if ((returnedValue==Constants.RET_PAGE_BROKEN_OK) ||
	   			(returnedValue==Constants.RET_PAGE_BROKEN_ERROR))
	   		return returnedValue;
	   	
	   	returnedValue = checkForTooBig();
    	if (errorsCount >= Settings.FATAL_ELEMENT_ERRORS_COUNT){
    		action.closeEventFatal(checkAllEventNumber, "Too many errors per element");
    		return Constants.RET_ERROR;
    	}  	   	
	   	if (returnedValue==Constants.RET_PAGE_BROKEN_ERROR)
	   		return returnedValue;	   	
	
	   	action.closeEventOk(checkAllEventNumber);	   	
	   	return Constants.RET_OK;
    }             
}

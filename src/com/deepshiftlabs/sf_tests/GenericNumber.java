package com.deepshiftlabs.sf_tests;

public class GenericNumber extends GenericElement {
	protected int intPlaces;
	protected int decimalPlaces;

	public GenericNumber(String name, String sfId, String objectType,
			 boolean a_isRequired, int a_intPlaces, int a_decimalPlaces) {
		super(name, sfId, objectType, a_isRequired);
		validValue = "1";
    	intPlaces =  a_intPlaces;
    	decimalPlaces = a_decimalPlaces;

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
	
    public void setInputLength (int a_intPlaces, int a_decimalPlaces){
    	intPlaces =  a_intPlaces;
    	decimalPlaces = a_decimalPlaces;
     }
    
    public int getDecimalPlacesCount(String theString){
    	return theString.length()-theString.indexOf('.')-1;
    }
    
    public boolean isValueValidForThisElementLength(CheckValue theValue){
    	char tempChar;
    	int count=0;
    	int afterDollar=0;
    	boolean leadingZerosEnded = false;
    	
    	Event event = action.startEvent("isValueValidForThisElementLength", theValue.value);    	
    	
    	if (theValue.value.indexOf('$')>0)
    		afterDollar=theValue.value.indexOf('$')+1;
    		
    	for (int i=afterDollar; i<theValue.value.length(); i++){
    		tempChar = theValue.value.charAt(i);
    		if (tempChar=='.') 
    			i = theValue.value.length();
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
    
    
    public int checkDecimalPlacesCountRunCount=0;
    public int checkDecimalPlacesCount(String displayedValue){
    	if (checkDecimalPlacesCountRunCount>0) {
       		action.info("checkDecimalPlacesCount for element _"+elementSfId+"_ already was performed, skipping");      		
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
    
 // TODO optimize!    
    public int checkMaxLengthRunCount=0;
    public int checkMaxLength(){
        String testString;
        char validChar;
    	
    	if (checkMaxLengthRunCount>1) {
   		action.info("checkMaxLength for element _"+elementSfId+"_ already was performed, skipping");      		
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

    public int checkForTooBigRunCount=0;    
    public int checkForTooBig(){
        String testString;
        char validChar;
    	
    	if (checkForTooBigRunCount>0) {
   		action.info("checkForTooBig for element _"+elementSfId+"_ already was performed, skipping");      		
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
    
    private Event checkAllEventNumber = null;    
    public int  checkAll (){
    	if (checkAllEventNumber==null){
    		checkAllEventNumber = action.startEvent("checkAll", name); 
    	}
	   	int returnedValue;
	   	returnedValue = super.checkAll();
	   	if (returnedValue!=Constants.RET_OK)
	   		return returnedValue;
	
	   	returnedValue = checkMaxLength();   	
	   	if ((returnedValue==Constants.RET_PAGE_BROKEN_OK) ||
	   			(returnedValue==Constants.RET_PAGE_BROKEN_ERROR))
	   		return returnedValue;
	   	
	   	returnedValue = checkForTooBig();   	
	   	if (returnedValue==Constants.RET_PAGE_BROKEN_ERROR)
	   		return returnedValue;	   	
	
	   	action.closeEventOk(checkAllEventNumber);	   	
	   	return Constants.RET_OK;
    }             
}

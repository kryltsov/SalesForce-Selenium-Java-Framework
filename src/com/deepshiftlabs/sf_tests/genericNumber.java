package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.DefaultSelenium;

public class genericNumber extends genericElement {
	protected int intPlaces;
	protected int decimalPlaces;
	private String standartInvalidMessage;

	public genericNumber(String name, String sfId, String objectType,
			 String value, boolean a_isRequired, int a_intPlaces, int a_decimalPlaces) {
		super(name, sfId, objectType, a_isRequired);
		validValue = "1";
    	intPlaces =  a_intPlaces;
    	decimalPlaces = a_decimalPlaces;
    	
		values.add(new checkValue("be", constants.IT_IS_INVALID_VALUE));
		values.add(new checkValue("1-e", constants.IT_IS_INVALID_VALUE));
		values.add(new checkValue("10'0", constants.IT_IS_INVALID_VALUE));
		values.add(new checkValue("1.0.0", constants.IT_IS_INVALID_VALUE));
		values.add(new checkValue("100,", constants.IT_IS_INVALID_VALUE));  // check 3
		
// TODO I should implement function, that will validate shouldBeDisplayed field of checkValue in accordance with decimalPlaces
// before we will use them
		
		values.add(new checkValue(" 00 00 123", constants.IT_IS_VALID_VALUE,"","123"));  // check 2
		values.add(new checkValue("4,5,6", constants.IT_IS_VALID_VALUE,"","456"));   // check 3
		values.add(new checkValue("78.7 ", constants.IT_IS_VALID_VALUE,"","78.7"));  // check 4
	}
	
    public void setInputLength (int a_intPlaces, int a_decimalPlaces){
    	intPlaces =  a_intPlaces;
    	decimalPlaces = a_decimalPlaces;
     }
    
    public int getDecimalPlacesCount(String theString){
    	return theString.length()-theString.indexOf('.')-1;
    }
    
    public boolean isValueValidForThisElementLength(checkValue theValue){
    	char tempChar;
    	int count=0;
    	int afterDollar=0;
    	boolean leadingZerosEnded = false;
    	
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
    		action.error("Length of int places in  _"+theValue.value+"_ is greater then in element _"+elementName+"_ max size ("+inputLength+"), value skipped.");
    		return false;
    	}
    	return true;
    }    
    
    
    public int checkDecimalPlacesCountRunCount=0;
    public int checkDecimalPlacesCount(String displayedValue){
    	if (checkDecimalPlacesCountRunCount>0) {
       		action.info("checkDecimalPlacesCount for element _"+elementSfId+"_ already was performed, skipping");      		
         		return constants.RET_SKIPPED;
         	}
    		int count = getDecimalPlacesCount(displayedValue);
    		checkDecimalPlacesCountRunCount++;
    		if (count==decimalPlaces){
	        	action.error("Real decimal places numb of _"+ elementName + "_ is OK ("+decimalPlaces+" )");
	        	return constants.RET_OK;    			
    		}else{//
	        	action.error("Real decimal places numb of _"+ elementName + "_ is "+count+" (ERROR). Should be (should be "+decimalPlaces+" )");
//TODO here I don't get screenshot
	        	return constants.RET_ERROR;    			
    		}
    }
    
    public int checkMaxLengthRunCount=0;
    public int checkMaxLength(DefaultSelenium selInstance){
        String testString;
        char validChar;
    	
    	if (checkMaxLengthRunCount>1) {//
   		action.info("checkMaxLength for element _"+elementSfId+"_ already was performed, skipping");      		
     		return constants.RET_SKIPPED;
     	}
     	checkMaxLengthRunCount++;
     	
        if (validValue.length()>1){
          	 action.error("Can't perform check because validValue is not one digit.");
          	 return constants.RET_ERROR;
        }     	

        validChar = validValue.charAt(0);
        testString = validValue;
        
        if (checkMaxLengthRunCount==1){
        	// while got the exact number of intPlaces in testString
	        while (testString.length() < (intPlaces) ){
	       	 testString = validChar+testString;
	        }
	        
	        selInstance.type(elementLocator, testString);
	        action.saveRecord(selInstance);
	        if (action.isErrorPresent(selInstance, "Number is too large.")){
	        	checkMaxLengthRunCount=2; // no need in next step of check
	        	action.error("Real integer places numb of _"+ elementName + "_ is less than should be (should be "+intPlaces+")");
	        	action.getScreenshot(selInstance, true);
	        	action.warn("Because of error count of decimal plases will not be checked.");	        	
	        	return constants.RET_ERROR;
	        } else {
	        	action.info("Real integer places numb of _"+ elementName + "_ is not less than should be("+intPlaces+")");
	        	return constants.RET_PAGE_BROKEN_OK;        	
	        }
        }else{
        	// while got the exact number+1 of intPlaces in testString
	        while (testString.length() < (intPlaces+1) ){
	       	 testString = validChar+testString;
	        }
	        selInstance.type(elementLocator, testString);
	        action.saveRecord(selInstance);
	        if (action.isErrorPresent(selInstance, "Number is too large.")){
	        	action.info("Real integer places numb of _"+ elementName + "_ is OK! ("+intPlaces+")");

	        	checkDecimalPlacesCount(selInstance.getValue(elementLocator));
//  TODO maybe we should edit retValue depending on checkDecimalPlacesCount()	        	
	        	return constants.RET_OK;        	
	        } else {
	        	action.error("Real integer places numb of _"+ elementName + "_ is more than should be (should be "+intPlaces+")");
	        	action.getScreenshot(selInstance, true);
	        	action.warn("Because of error count of decimal plasec will not be checked.");	        	
	        	return constants.RET_PAGE_BROKEN_ERROR;
	        }        	
        }
    }

    public int checkForTooBigRunCount=0;    
    public int checkForTooBig(DefaultSelenium selInstance){
        String testString;
        char validChar;
    	
    	if (checkForTooBigRunCount>0) {//
   		action.info("checkForTooBig for element _"+elementSfId+"_ already was performed, skipping");      		
     		return constants.RET_SKIPPED;
     	}
    	checkForTooBigRunCount++;
     	
        validChar = validValue.charAt(0);
        testString = validValue;
        
// while got the number of intPlaces > 19
	        while (testString.length() < 20){
	       	 testString = validChar+testString;
	        }
	        
	        selInstance.type(elementLocator, testString);
	        action.saveRecord(selInstance);
	        if (action.isErrorPresent(selInstance, "Number is too large.")){
	        	if (selInstance.getValue(elementLocator).equals("#Too Big!")){
	        		action.info("checkForTooBig of _"+ elementName + "_ is OK");
		        	return constants.RET_OK;
	        	}
	        	action.info("checkForTooBig of _"+ elementName + "_ is ERROR");
	        	action.getScreenshot(selInstance, true);
	        	return constants.RET_ERROR;
	        }
        	action.info("checkForTooBig of _"+ elementName + "_ is ERROR");
        	action.getScreenshot(selInstance, true);
        	return constants.RET_PAGE_BROKEN_ERROR;	        
    }
    
    public int  checkAll (DefaultSelenium selInstance){
	   	int returnedValue;
	
	   	returnedValue = super.checkAll(selInstance);
	   	if (returnedValue!=constants.RET_OK)
	   		return returnedValue;
	
	   	returnedValue = checkMaxLength(selInstance);   	
	   	if ((returnedValue==constants.RET_PAGE_BROKEN_OK) ||
	   			(returnedValue==constants.RET_PAGE_BROKEN_ERROR))
	   		return returnedValue;
	   	
	   	returnedValue = checkForTooBig(selInstance);   	
	   	if (returnedValue==constants.RET_PAGE_BROKEN_ERROR)
	   		return returnedValue;	   	
	
	   	return constants.RET_OK;
    }             
}

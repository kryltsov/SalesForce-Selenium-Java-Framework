package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.DefaultSelenium;

public class GenericNumber extends GenericElement {
	protected int intPlaces;
	protected int decimalPlaces;
	private String standartInvalidMessage;

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
         		return Constants.RET_SKIPPED;
         	}
    		int count = getDecimalPlacesCount(displayedValue);
    		checkDecimalPlacesCountRunCount++;
    		if (count==decimalPlaces){
	        	action.info("Real decimal places numb of _"+ elementName + "_ is OK ("+decimalPlaces+")");
	        	return Constants.RET_OK;    			
    		}else{//
	        	action.error("Real decimal places numb of _"+ elementName + "_ is "+count+" (ERROR). Should be "+decimalPlaces);
//TODO here I don't get screenshot
	        	return Constants.RET_ERROR;    			
    		}
    }
    
    public int checkMaxLengthRunCount=0;
    public int checkMaxLength(DefaultSelenium selInstance){
        String testString;
        char validChar;
    	
    	if (checkMaxLengthRunCount>1) {//
   		action.info("checkMaxLength for element _"+elementSfId+"_ already was performed, skipping");      		
     		return Constants.RET_SKIPPED;
     	}
     	checkMaxLengthRunCount++;
     	
        if (validValue.length()>1){
          	 action.error("Can't perform checkMaxLength because validValue is not one digit.");
          	 return Constants.RET_ERROR;
        }     	

        validChar = validValue.charAt(0);
        testString = validValue;
        
        if (checkMaxLengthRunCount==1){
        	// while got the exact number of intPlaces in testString
	        while (testString.length() < (intPlaces) ){
	       	 testString = validChar+testString;
	        }
	        
	        action.typeText(selInstance, writeLocator, testString);
	        action.pressButton(selInstance, Constants.SAVE_RECORD_LOCATOR);
	        if (action.isErrorPresent(selInstance, "Number is too large.")){
	        	checkMaxLengthRunCount=2; // no need in next step of check
	        	action.error("Real integer places numb of _"+ elementName + "_ is less than should be (should be "+intPlaces+")");
	        	action.getScreenshot(selInstance, true);
	        	action.warn("Because of error count of decimal plases will not be checked.");	        	
	        	return Constants.RET_ERROR;
	        } else {
	        	action.info("Real integer places numb of _"+ elementName + "_ is not less than should be("+intPlaces+")");
	        	return Constants.RET_PAGE_BROKEN_OK;        	
	        }
        }else{
        	// while got the exact number+1 of intPlaces in testString
	        while (testString.length() < (intPlaces+1) ){
	       	 testString = validChar+testString;
	        }
	        action.typeText(selInstance, writeLocator, testString);
	        action.pressButton(selInstance, Constants.SAVE_RECORD_LOCATOR);
	        if (action.isErrorPresent(selInstance, "Number is too large.")){
	        	action.info("Real integer places numb of _"+ elementName + "_ is OK! ("+intPlaces+")");

	        	checkDecimalPlacesCount(action.readValue(selInstance, writeLocator));
//  TODO maybe we should edit retValue depending on checkDecimalPlacesCount()	        	
	        	return Constants.RET_OK;        	
	        } else {
	        	action.error("Real integer places numb of _"+ elementName + "_ is more than should be (should be "+intPlaces+")");
	        	action.getScreenshot(selInstance, true);
	        	action.warn("Because of error count of decimal plasec will not be checked.");	        	
	        	return Constants.RET_PAGE_BROKEN_ERROR;
	        }        	
        }
    }

    public int checkForTooBigRunCount=0;    
    public int checkForTooBig(DefaultSelenium selInstance){
        String testString;
        char validChar;
    	
    	if (checkForTooBigRunCount>0) {//
   		action.info("checkForTooBig for element _"+elementSfId+"_ already was performed, skipping");      		
     		return Constants.RET_SKIPPED;
     	}
    	checkForTooBigRunCount++;
     	
        validChar = validValue.charAt(0);
        testString = validValue;
        
// while got the number of intPlaces > 19
	        while (testString.length() < 20){
	       	 testString = validChar+testString;
	        }
	        
	        action.typeText(selInstance, writeLocator, testString);
	        action.pressButton(selInstance, Constants.SAVE_RECORD_LOCATOR);
	        if (action.isErrorPresent(selInstance, "Number is too large.")){
	        	if (action.readValue(selInstance, writeLocator).equals("#Too Big!")){
	        		action.info("checkForTooBig of _"+ elementName + "_ is OK");
		        	return Constants.RET_OK;
	        	}
	        	action.info("checkForTooBig of _"+ elementName + "_ is ERROR");
	        	action.getScreenshot(selInstance, true);
	        	return Constants.RET_ERROR;
	        }
        	action.info("checkForTooBig of _"+ elementName + "_ is ERROR");
        	action.getScreenshot(selInstance, true);
        	return Constants.RET_PAGE_BROKEN_ERROR;	        
    }
    
    public int  checkAll (DefaultSelenium selInstance){
	   	int returnedValue;
	
	   	returnedValue = super.checkAll(selInstance);
	   	if (returnedValue!=Constants.RET_OK)
	   		return returnedValue;
	
	   	returnedValue = checkMaxLength(selInstance);   	
	   	if ((returnedValue==Constants.RET_PAGE_BROKEN_OK) ||
	   			(returnedValue==Constants.RET_PAGE_BROKEN_ERROR))
	   		return returnedValue;
	   	
	   	returnedValue = checkForTooBig(selInstance);   	
	   	if (returnedValue==Constants.RET_PAGE_BROKEN_ERROR)
	   		return returnedValue;	   	
	
	   	return Constants.RET_OK;
    }             
}

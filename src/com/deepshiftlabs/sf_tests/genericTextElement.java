package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.DefaultSelenium;

public class genericTextElement extends genericElement {
	
    private int inputLength;
    
	public genericTextElement(String name, String sfId, String objectType,
			 String value, boolean a_isRequired) {
		super(name, sfId, objectType, a_isRequired);
		validValue = "a";
		setInputLength(255);
	}
	
    public genericTextElement(String a_elementName, String a_elementSfId, String a_parentObjectType, boolean a_isRequired, int a_maxLength){
        super(a_elementName, a_elementSfId,a_parentObjectType, a_isRequired);
        validValue = "a";
        setInputLength(a_maxLength);
    }    
    
    public void setInputLength (int a_length){
        inputLength =  a_length;
     }
     
     public int getInputLength (){
       return inputLength;
     }
     
     public int checkMaxLengthRunCount=0;
     public int checkMaxLength(DefaultSelenium selInstance){
      	if (checkMaxLengthRunCount>0) {
    		action.info("checkMaxLength for element _"+elementSfId+"_ already was performed, skipping");      		
      		return constants.RET_SKIPPED;
      	}
      	checkMaxLengthRunCount++;    	 
    	 
         String testString;
         int realLength;
         char validChar;
         
         if (validValue.length()>inputLength){
        	 action.error("Can't perform check because max Length is less than validValue");
        	 return constants.RET_ERROR;
         } 
         validChar = validValue.charAt(0);
         testString = validValue;
         
         while (testString.length() < (inputLength+1) ){
        	 testString = validChar+testString;
         }
         
         selInstance.type(elementLocator, testString);
         realLength = selInstance.getValue(elementLocator).length();
         if (realLength != inputLength){
             while (testString.length() < 256){
            	 testString = testString+testString;
             }
             selInstance.type(elementLocator, testString);
             realLength = selInstance.getValue(elementLocator).length();
        	 action.error ("Real maxLenght of  _"+ elementName + "_ is "+realLength+" (should be "+inputLength+" )");
        	 action.getScreenshot(selInstance, true);        	 
         }
        action.info ("Real maxLenght for _"+ elementName + "_ is OK.");
        action.getScreenshot(selInstance, false);        
        return constants.RET_OK;
     }
    
     public int  checkAll (DefaultSelenium selInstance){
    	int returnedValue;

    	returnedValue = super.checkAll(selInstance);
    	if (returnedValue!=constants.RET_OK)
    		return returnedValue;
    	
     	checkMaxLength(selInstance);     	
     	return constants.RET_OK;
     }         
}

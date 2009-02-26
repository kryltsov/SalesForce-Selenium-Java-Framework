package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.DefaultSelenium;

public class genericTextElement extends genericElement {
	
    private int inputLength;
    
	public genericTextElement(String name, String sfId, String objectType,
			 String value, boolean a_isRequired) {
		super(name, sfId, objectType, "a", a_isRequired);
		setInputLength(255);
	}
	
    public genericTextElement(String a_elementName, String a_elementSfId, String a_parentObjectType, String a_validValue, boolean a_isRequired, int a_maxLength){
        super(a_elementName, a_elementSfId,a_parentObjectType, "a", a_isRequired);
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
      		return settings.RET_SKIPPED;
      	}
      	checkMaxLengthRunCount++;    	 
    	 
         String tempLocator;
         String testString;
         int realLength;
         char validChar;
         
         if (validValue.length()>inputLength){
        	 action.error("Can't perform check because max Length is less than validValue");
        	 return settings.RET_ERROR;
         } 
         
         tempLocator = "//input[@id='"+elementSfId+"']";
         
         validChar = validValue.charAt(0);
         testString = validValue;
         
         while (testString.length() < (inputLength+1) ){
        	 testString = validChar+testString;
         }
         
         selInstance.type(tempLocator, testString);
         realLength = selInstance.getValue(tempLocator).length();
         if (realLength != inputLength){
             while (testString.length() < 256){
            	 testString = testString+testString;
             }
             selInstance.type(tempLocator, testString);
             realLength = selInstance.getValue(tempLocator).length();
        	 action.error ("Real maxLenght of  _"+ elementName + "_ is "+realLength+" (should be "+inputLength+" )");
        	 action.getScreenshot(selInstance, true);        	 
         }
        action.info ("Real maxLenght for _"+ elementName + "_ is OK.");
        action.getScreenshot(selInstance, false);        
        return settings.RET_OK;
     }
    
     public int  checkAll (DefaultSelenium selInstance){
    	int returnedValue;

    	returnedValue = super.checkAll(selInstance);
    	if (returnedValue!=settings.RET_OK)
    		return returnedValue;
    	
     	checkMaxLength(selInstance);     	
     	return settings.RET_OK;
     }         
}

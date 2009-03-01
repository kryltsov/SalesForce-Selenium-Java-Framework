package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.DefaultSelenium;

public class genericTextElement extends genericElement {
	
	public genericTextElement(String name, String sfId, String objectType,
			 String value, boolean a_isRequired) {
		super(name, sfId, objectType, a_isRequired);
		validValue = "a";
		setInputLength(255);
//TODO should check when we go here and cut this
		action.error("Check TODO.");
	}
	
    public genericTextElement(String a_elementName, String a_elementSfId, String a_parentObjectType, boolean a_isRequired, int a_maxLength){
        super(a_elementName, a_elementSfId,a_parentObjectType, a_isRequired);
        validValue = "a";
        setInputLength(a_maxLength);

// there we can't place symbols <> and "  because urlElement can't display them 		

        values.add(new checkValue("test", constants.IT_IS_VALID_VALUE));
        values.add(new checkValue("test!@#", constants.IT_IS_VALID_VALUE));
        values.add(new checkValue("test··ar", constants.IT_IS_VALID_VALUE));
        values.add(new checkValue("test...!+=%$*()!@#$%^&*?{}[]", constants.IT_IS_VALID_VALUE));		
        values.add(new checkValue("debug test to find if we corect work with lenghts of check values more than input maxsize (there are near 100 symbols here)", constants.IT_IS_VALID_VALUE));

    }    
    
    public void setInputLength (int a_length){
        inputLength =  a_length;
     }
     
     public int getInputLength (){
       return inputLength;
     }
     
    public boolean isValueValidForThisElementLength(checkValue theValue){
    	if (theValue.value.length()>inputLength){
    		action.error("Length of check value _"+theValue.value+"_ is greater then element _"+elementName+"_ max size ("+inputLength+"), value skipped.");
    		return false;
    	}
    	return true;
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

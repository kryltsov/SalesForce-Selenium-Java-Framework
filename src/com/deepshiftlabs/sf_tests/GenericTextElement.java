package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.DefaultSelenium;

public class GenericTextElement extends GenericElement {
	
    public GenericTextElement(String a_elementName, String a_elementSfId, String a_parentObjectType, boolean a_isRequired, int a_maxLength){
        super(a_elementName, a_elementSfId,a_parentObjectType, a_isRequired);
        validValue = "a";
        setInputLength(a_maxLength);

// there we can't place symbols <> and "  because UrlElement can't display them 		

        values.add(new CheckValue("test", Constants.IT_IS_VALID_VALUE));
        values.add(new CheckValue("test!@#", Constants.IT_IS_VALID_VALUE));
        values.add(new CheckValue("test··ar", Constants.IT_IS_VALID_VALUE));
        values.add(new CheckValue("test...!+=%$*()!@#$%^&*?{}[]", Constants.IT_IS_VALID_VALUE));		
        values.add(new CheckValue("debug test to find if we corect work with lenghts of check values more than input maxsize (there are near 100 symbols here)", Constants.IT_IS_INVALID_VALUE));
    }    
    
    public void setInputLength (int a_length){
        inputLength =  a_length;
     }
     
     public int getInputLength (){
       return inputLength;
     }
     
    public boolean isValueValidForThisElementLength(CheckValue theValue){
    	Event event = action.startEvent("isValueValidForThisElementLength", theValue.value);
    	
    	if (theValue.value.length()>inputLength){
    		action.closeEventError(event, "value is greater than setted element lenght");
    		return false;
    	}
    	action.closeEventOk(event);
    	return true;
    }     

// TODO optimize!    
     public int checkMaxLengthRunCount=0;
     public int checkMaxLength(DefaultSelenium selInstance){
      	if (checkMaxLengthRunCount>0) {
    		action.info("checkMaxLength for element _"+elementSfId+"_ already was performed, skipping");      		
      		return Constants.RET_SKIPPED;
      	}
      	checkMaxLengthRunCount++;    	 
    	
      	Event event = action.startEvent("checkMaxLength", name);
         String testString;
         int realLength;
         char validChar;
         
         if (validValue.length()>inputLength){
        	 action.closeEventError(event, "max Length is less than validValue");
        	 return Constants.RET_ERROR;
         } 
         event.setWaitedValue(Integer.toString(inputLength));
         
         validChar = validValue.charAt(0);
         testString = validValue;
         
         while (testString.length() < (inputLength+1) ){
        	 testString = validChar+testString;
         }
         
         action.typeText(writeLocator, testString);
         realLength = action.readValue(writeLocator).length();
         if (realLength != inputLength){
             while (testString.length() < 256){
            	 testString = testString+testString;
             }
             action.typeText(writeLocator, testString);
             realLength = action.readValue(writeLocator).length();
         }
        
         event.setRealValue(Integer.toString(realLength));
         
        if (realLength != inputLength) {
        	errorsCount++;
	        action.closeEventError(event);
	        return Constants.RET_ERROR;        	
        }
        else
        {
	        action.closeEventOk(event);
	        return Constants.RET_OK;
        }
     }
    
     private Event checkAllEventGenericText = null;
     public int  checkAll (DefaultSelenium selInstance){
    	int returnedValue;
    	if (checkAllEventGenericText==null){
    		checkAllEventGenericText = action.startEvent("checkAll", name); 
    	}    	

    	returnedValue = super.checkAll();
    	if (returnedValue!=Constants.RET_OK)
    		return returnedValue;
    	
     	checkMaxLength(selInstance);
	   	action.closeEventOk(checkAllEventGenericText);     	
     	return Constants.RET_OK;
     }         
}

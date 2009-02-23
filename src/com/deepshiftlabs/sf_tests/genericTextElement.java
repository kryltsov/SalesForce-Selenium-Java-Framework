package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.DefaultSelenium;

public class genericTextElement extends genericElement {
	
    private int inputLength;

	public genericTextElement(String name, String sfId, String objectType,
			String id, String value, boolean a_isRequired) {
		super(name, sfId, objectType, id, "a", a_isRequired);
		setInputLength(255);
	}
	
    public genericTextElement(String a_elementName, String a_elementSfId, String a_parentObjectType, String a_parentId, String a_validValue, boolean a_isRequired, int a_maxLength){
        super(a_elementName, a_elementSfId,a_parentObjectType,a_parentId, "a", a_isRequired);
        setInputLength(a_maxLength);
    }    
    
    public void setInputLength (int a_length){
        inputLength =  a_length;
     }
     
     public int getInputLength (){
       return inputLength;
     }
     
     public boolean fillByValidValue (DefaultSelenium selInstance){
         String tempLocator;
         
         tempLocator = "//input[@id='"+elementSfId+"']";
         selInstance.type(tempLocator, validValue);
         action.info ("Filling Element _"+ elementName + "_ by valid value = _"+validValue+"_");
        return true;
     }
     
     public boolean checkMaxLength(DefaultSelenium selInstance){
         String tempLocator;
         String testString;
         int realLength;
         char validChar;
         
         if (validValue.length()>inputLength){
        	 action.error("Can't perform check because max Length is less than validValue");
        	 return false;
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
        return true;
     }
    
     public int  checkAll (DefaultSelenium selInstance){
     	super.checkAll(selInstance);
     	checkMaxLength(selInstance);
     	return 0;
     }         
}

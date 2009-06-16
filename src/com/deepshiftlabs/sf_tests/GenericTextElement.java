package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * Represents parent class for text branch of elements tree.
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
public class GenericTextElement extends GenericElement {
	
    /**
     * @param a_elementName Salesforce name of element 
     * @param a_parentObjectType name of salesforce object which contains this element 
     * @param a_isRequired determines if element should be filled with value to store record
     * @param a_maxLength assumed maximal length of element
     */
    public GenericTextElement(String a_elementName, String a_parentObjectType, boolean a_isRequired, int a_maxLength){
        super(a_elementName, a_parentObjectType,a_isRequired);
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
     
    /* 
     * @see com.deepshiftlabs.sf_tests.GenericElement#isValueValidForThisElementLength(com.deepshiftlabs.sf_tests.CheckValue)
     */
    public boolean isValueValidForThisElementLength(CheckValue theValue){
    	Event event = action.startEvent("isValueValidForThisElementLength", theValue.value);
    	
    	if (theValue.value.length()>inputLength){
    		action.closeEventError(event, "value is greater than setted element lenght");
    		return false;
    	}
    	action.closeEventOk(event);
    	return true;
    }     

// TODO optimize and comment!    
    /**
     * This variable used to determine should we process or skip function checkMaxLength.
     * @see #checkAll()
     */
    public int checkMaxLengthRunCount=0;

    /**
     * Checks accordance of assumed element length with its real value.
     * @return RET_OK,
     * RET_ERROR, 
     * RET_SKIPPED if this check was done before (checkMaxLengthRunCount>0).
     */
    public int checkMaxLength(){
      	if (checkMaxLengthRunCount>0) {
    		action.info("checkMaxLength for element _"+name+"_ already was performed, skipping");      		
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
    
    /**
     * Because method checkAll may be called many times for one element, we can't simple create Event object inside it.
     * So we declare variable here and at first call of checkAll instantiate object.
     * CheckAll method can't generate errors itself, so we use this event only to block all elements checks. 
     * @see #checkAll()
     */
    private Event checkAllEventGenericText = null;
    
    /* 
     * @see com.deepshiftlabs.sf_tests.GenericElement#checkAll()
     */
    public int  checkAll (){
    	int returnedValue;
    	if (checkAllEventGenericText==null){
    		checkAllEventGenericText = action.startEvent("checkAll", name); 
    	}    	

    	returnedValue = super.checkAll();
	   	if (returnedValue==Constants.RET_ERROR){
	   		action.closeEventFatal(checkAllEventGenericText);
	   		return Constants.RET_ERROR;	   	
	   	}    	
    	
    	if (returnedValue!=Constants.RET_OK)
    		return returnedValue;
    	
     	checkMaxLength();
    	if (errorsCount >= Settings.FATAL_ELEMENT_ERRORS_COUNT){
    		action.closeEventFatal(checkAllEventGenericText, "Too many errors per element");
    		return Constants.RET_ERROR;
    	}     	
	   	action.closeEventOk(checkAllEventGenericText);     	
     	return Constants.RET_OK;
     }         
}

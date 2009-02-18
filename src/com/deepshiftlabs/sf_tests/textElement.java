package com.deepshiftlabs.sf_tests;


import com.thoughtworks.selenium.*;
import org.testng.annotations.*;


public class textElement extends genericElement{
    
    private int inputLength = 255;
    private String validValue = "abcdef";
    
    textElement(String a_elementName, String a_elementSfId, String a_parentObjectType, String a_parentId){
        super(a_elementName, a_elementSfId,a_parentObjectType,a_parentId);
    }
    
    public void setInputLength (int a_length){
       inputLength =  a_length;
    }
    
    public int getInputLength (){
      return inputLength;
    }
    
    public int fillByValidValue (DefaultSelenium selInstance){
      selInstance.type("//input[@id='" + elementName + "']", validValue);
      return 0;
    }
}
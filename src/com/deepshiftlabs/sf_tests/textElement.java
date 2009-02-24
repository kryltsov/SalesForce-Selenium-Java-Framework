package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.DefaultSelenium;


//import com.thoughtworks.selenium.*;
//import org.testng.annotations.*;


public class textElement extends genericTextElement{
    textElement(String a_elementName, String a_elementSfId, String a_parentObjectType, String a_validValue, boolean a_isRequired, int a_maxLength){
        super(a_elementName, a_elementSfId,a_parentObjectType, a_validValue, a_isRequired, a_maxLength);
    }
}
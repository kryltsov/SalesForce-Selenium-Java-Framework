package com.deepshiftlabs.sf_tests;


import com.thoughtworks.selenium.*;
import org.testng.annotations.*;


public class genericElement {
    String elementName;
    String elementSfId;
    String parentObjectType;
    String parentID;
    commonActions action = new commonActions();
    
    genericElement(String a_elementName, String a_elementSfId, String a_parentObjectType, String a_parentId) {
        elementName = a_elementName;
        parentObjectType = a_parentObjectType;
        parentID = a_parentId;
        elementSfId = a_elementSfId;
    }
        
/*    public int preparePage(DefaultSelenium selInstance){
        action.openTab(selInstance parentTabID);
        if (selInstance.isElementPresent("name="+parentID)){
                selInstance.click("//a[contains(text(),'"+parentID+"')]");            
                waitForPageToLoad(30000);
            return 0;
        }
    }   */
    
    public boolean checkPresence (DefaultSelenium selInstance){
        String tempLocator;
        
        tempLocator = "//input[@id='"+elementSfId+"']";
        
        if (selInstance.isElementPresent(tempLocator)){
            action.info ("Element name = "+elementName + " is PRESENT");
            return true;
        }
        action.warn ("Element name = "+elementName + " is NOT PRESENT. Locator was _"+tempLocator+"_");
        return false;
    }
}
      


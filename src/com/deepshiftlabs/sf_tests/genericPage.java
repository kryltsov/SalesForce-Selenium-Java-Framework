package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.*;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.Iterator;


public class genericPage {
    String parentObjectType;
    String parentTabID;
    ArrayList elements = new ArrayList();
    commonActions action = new commonActions();
    DefaultSelenium sInstance;
    
    genericPage(String a_parentObjectType, String a_parentTabID) {
        parentObjectType = a_parentObjectType;
        parentTabID = a_parentTabID;
    }
    
    public int prepareBrowser()
    {
        sInstance = action.getSelenium("192.168.232.13", 4444, "*chrome", "https://login.salesforce.com");
        return 0;
    };
    
    public int freeBrowser()
    {
        action.freeSelenium(sInstance);
        return 0;
    };    
        
    public int addElement(genericElement el){
        elements.add(el);
        return elements.size();
    }
    
    public int checkElementsPresence (){
        genericElement tempElement;
        int missed = 0;

        Iterator iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (genericElement)iterator.next();
		     if (!tempElement.checkPresence(sInstance)){
		        missed++;
		    }
	    }
	    return missed;
    }
    
    public int createNewEmptyRecord(){
        prepareBrowser();
        action.login(sInstance, "bearoffl_dev@rambler.ru", "bear1212");
        action.openTab(sInstance, parentTabID);
        sInstance.click("//input[@name='new']");
        sInstance.waitForPageToLoad("30000") ;
        
        action.info("New record created, title is " + sInstance.elemegetTitle());
        return 0;
    }
}
      


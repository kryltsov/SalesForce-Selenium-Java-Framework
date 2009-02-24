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
    settings privateSettings;
    DefaultSelenium sInstance;
    
    genericPage(String a_parentObjectType, String a_parentTabID) {
        parentObjectType = a_parentObjectType;
        parentTabID = a_parentTabID;
    }
    
    public int prepareBrowser()
    {
        sInstance = action.getSelenium("192.168.232.1", 4444, "*chrome", "https://login.salesforce.com");
        return 0;
    };
    
    public int freeBrowser()
    {
    	action.logout(sInstance);
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
    
    public int fillElementsByValidValues(){
        genericElement tempElement;

        Iterator iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (genericElement)iterator.next();
		     tempElement.fillByValidValue(sInstance);
	    }
	    return 0;
    }
    
    public int checkAllElements(){
        genericElement tempElement;

        Iterator iterator = elements.iterator();
//	    tempElement = (genericElement)iterator.next();
//	    tempElement.checkAll(sInstance);
	    
	    while (iterator.hasNext()) {
	    	 fillElementsByValidValues();	    	
		     tempElement = (genericElement)iterator.next();
		     tempElement.checkAll(sInstance);
	    }
	    return 0;
    }
    
    public int createNewEmptyRecord(){
    	action.login(sInstance, privateSettings.SF_LOGIN, privateSettings.SF_PASSWORD);
        action.openTab(sInstance, parentTabID);
        sInstance.click("//input[@name='new']");
        sInstance.waitForPageToLoad("30000") ;
        
        action.info("New record created, title is " + sInstance.getTitle());
        return 0;
    }
}
      


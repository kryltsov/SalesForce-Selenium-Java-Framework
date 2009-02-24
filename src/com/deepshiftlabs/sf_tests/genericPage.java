package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.*;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.Iterator;


public class genericPage {
    String parentObjectType;
    String parentTabID;
    String myRecordId;    
    ArrayList elements = new ArrayList();
    commonActions action = new commonActions();
    settings privateSettings;
    DefaultSelenium sInstance;
    
    genericPage(String a_parentObjectType, String a_parentTabID, String a_myRecordId ) {
        parentObjectType = a_parentObjectType;
        parentTabID = a_parentTabID;
        myRecordId = a_myRecordId;
    }
    
    public int prepareBrowser(String hub, int port, String browser, String url)
    {
        sInstance = action.getSelenium(hub, port, browser, url);
        return settings.RET_OK;
    };
    
    public int logout(){
    	action.logout(sInstance);
        return settings.RET_OK;    	
    }
    public int freeBrowser()
    {
        action.freeSelenium(sInstance);
        return settings.RET_OK;
    };    
        
    public int addElement(genericElement el){
        elements.add(el);
        return elements.size();
    }
    
    public int findElementByName(String elementName){
        genericElement tempElement;
        int index = 0;

        Iterator iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (genericElement)iterator.next();
		     if (tempElement.getElementName().equals(elementName)){
		        return index;
		    }
	    }
	    return settings.RET_ERROR;
    }

    public int setDeterminingRecordIdField(String elementName){
    	int elementIndexInList;
        genericElement tempElement;    	
    	
    	elementIndexInList = findElementByName(elementName);
    	if (elementIndexInList!=settings.RET_ERROR){
    		tempElement = (genericElement)elements.get(elementIndexInList);
    		tempElement.forceToDetermineRecordID(myRecordId);
    		return settings.RET_OK;
    	}
        return settings.RET_ERROR;        
    }    
    
    public int checkElementsPresence (){
        genericElement tempElement;
        int missed = 0;

        Iterator iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (genericElement)iterator.next();
		     if (tempElement.checkPresence(sInstance)==settings.RET_ERROR){
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
	    return settings.RET_OK;
    }
    
    public int checkAllElements(){
        genericElement tempElement;
        int returnValue = settings.RET_OK;

        Iterator iterator = elements.iterator();
	    
	    while (iterator.hasNext()) {
	    	 fillElementsByValidValues();	    	
		     tempElement = (genericElement)iterator.next();
		     tempElement.checkAll(sInstance);
	    }
	    return settings.RET_OK;
    }
    
    public int login(){
    	action.login(sInstance, privateSettings.SF_LOGIN, privateSettings.SF_PASSWORD);
    	return settings.RET_OK;
    }
    
    public int createNewEmptyRecord(){
    	action.createNewEmptyRecord(sInstance, parentTabID);
    	return settings.RET_OK;    	
    }
    
    public int recreateRecord(){
    	action.deleteRecord(sInstance, parentTabID, myRecordId);
    	action.createNewEmptyRecord(sInstance, parentTabID);    	
        return settings.RET_OK;
    }    
    
    public int saveRecord(){
    	action.saveRecord(sInstance);
        return settings.RET_OK;
    }
}  


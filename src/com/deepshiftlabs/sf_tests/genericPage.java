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
    
    public int findElementIndexByName(String elementName){
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
    	
    	elementIndexInList = findElementIndexByName(elementName);
    	if (elementIndexInList!=settings.RET_ERROR){
    		tempElement = (genericElement)elements.get(elementIndexInList);
    		tempElement.forceToDetermineRecordID(myRecordId);
    		return settings.RET_OK;
    	}
        return settings.RET_ERROR;        
    }    
    
    public int setUnique(String elementName, boolean isCaseSens){
    	int elementIndexInList;
        genericElement tempElement;    	
    	
    	elementIndexInList = findElementIndexByName(elementName);
    	if (elementIndexInList!=settings.RET_ERROR){
    		tempElement = (genericElement)elements.get(elementIndexInList);
    		if (tempElement instanceof textElement){
    			((textElement)tempElement).setUnique(isCaseSens);
    			return settings.RET_OK;
    		}
    		action.error("Can't setUnique - it isn't textElement!");
    		return settings.RET_ERROR;
    	}
    	action.error("Can't setUnique - there are no such element");
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
        int returnedValue;

        Iterator iterator = elements.iterator();
	    
	    while (iterator.hasNext()) {
	    	 fillElementsByValidValues();	    	
		     tempElement = (genericElement)iterator.next();

		     returnedValue = settings.RET_ERROR;
		     while (returnedValue!=settings.RET_OK){
		    	 returnedValue = tempElement.checkAll(sInstance);
		    	 if (returnedValue==settings.RET_PAGE_BROKEN_ERROR || 
		    			 returnedValue==settings.RET_PAGE_BROKEN_OK){
		    		recreateRecord();
		    	 	fillElementsByValidValues();
		    	 }
		     }
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


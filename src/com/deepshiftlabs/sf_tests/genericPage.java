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
    DefaultSelenium sInstance;
    
    private int determiningRecordFieldIndex = -1;
    
    genericPage(String a_parentObjectType, String a_parentTabID, String a_myRecordId ) {
        parentObjectType = a_parentObjectType;
        parentTabID = a_parentTabID;
        myRecordId = a_myRecordId;
    }
    
    public int prepareBrowser(String hub, int port, String browser, String url)
    {
        sInstance = action.getSelenium(hub, port, browser, url);
        return constants.RET_OK;
    };
    
    public int logout(){
    	action.logout(sInstance);
        return constants.RET_OK;    	
    }
    public int freeBrowser()
    {
        action.freeSelenium(sInstance);
        return constants.RET_OK;
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
	    return constants.RET_ERROR;
    }

    public int setDeterminingRecordIdField(String elementName){
        genericElement tempElement;    	
    	
        determiningRecordFieldIndex = findElementIndexByName(elementName);
    	if (determiningRecordFieldIndex!=constants.RET_ERROR){
    		tempElement = (genericElement)elements.get(determiningRecordFieldIndex);
    		tempElement.forceToDetermineRecordID(myRecordId);
    		return constants.RET_OK;
    	}
        return constants.RET_ERROR;        
    }    
    
    public int setUnique(String elementName, boolean isCaseSens){
    	int elementIndexInList;
        genericElement tempElement;    	
    	
    	elementIndexInList = findElementIndexByName(elementName);
    	if (elementIndexInList!=constants.RET_ERROR){
    		tempElement = (genericElement)elements.get(elementIndexInList);
    		if (tempElement instanceof textElement){
    			((textElement)tempElement).setUnique(isCaseSens);
    			return constants.RET_OK;
    		}
    		action.error("Can't setUnique - it isn't textElement!");
    		return constants.RET_ERROR;
    	}
    	action.error("Can't setUnique - there are no such element");
        return constants.RET_ERROR;
    }       
    
    public int checkElementsPresence (){
        genericElement tempElement;
        int missed = 0;

        Iterator iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (genericElement)iterator.next();
		     if (tempElement.checkPresence(sInstance)==constants.RET_ERROR){
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
	    return constants.RET_OK;
    }
    
    public int checkAllElements(){
        genericElement tempElement;
        int returnedValue;

        Iterator iterator = elements.iterator();
	    
	    while (iterator.hasNext()) {
	    	 fillElementsByValidValues();	    	
		     tempElement = (genericElement)iterator.next();

		     returnedValue = constants.RET_ERROR;
		     while (returnedValue!=constants.RET_OK){
		    	 returnedValue = tempElement.checkAll(sInstance);
		    	 if (returnedValue==constants.RET_PAGE_BROKEN_ERROR || 
		    			 returnedValue==constants.RET_PAGE_BROKEN_OK){
		    		recreateRecord();
		    	 	fillElementsByValidValues();
		    	 }
		     }
	    }
	    return constants.RET_OK;
    }
    
    public int login(){
    	action.login(sInstance, settings.SF_LOGIN, settings.SF_PASSWORD);
    	return constants.RET_OK;
    }
    
    public int createNewEmptyRecord(){
    	action.createNewEmptyRecord(sInstance, parentTabID);
    	return constants.RET_OK;    	
    }
    
    String getIdOfStoredRecord(){
        genericElement tempElement;    	
    	
        if (determiningRecordFieldIndex==-1)
        	return myRecordId;
        if (determiningRecordFieldIndex>=0 || determiningRecordFieldIndex< elements.size()){
        	tempElement = (genericElement)elements.get(determiningRecordFieldIndex);
        	return tempElement.getLastEnteredValue(); 
        }
        else {
        	action.error("No such element");        	
        	return myRecordId;
        }
    }
    
    public int recreateRecord(){
    	action.deleteRecord(sInstance, parentTabID, getIdOfStoredRecord());
    	action.createNewEmptyRecord(sInstance, parentTabID);    	
        return constants.RET_OK;
    }    
    
    public int saveRecord(){
    	action.saveRecord(sInstance);
        return constants.RET_OK;
    }
}  


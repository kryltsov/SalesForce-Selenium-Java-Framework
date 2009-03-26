package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.*;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.Iterator;


public class genericObject {
    String parentTabID;
    String myRecordId;    
    
    String defaultTitleSingular;
    String defaultTitlePlural;
    
    ArrayList elements = new ArrayList();
    commonActions action = new commonActions();
    DefaultSelenium sInstance;
    
    private int determiningRecordFieldIndex = -1;
    
    genericObject(String a_parentTabID, String a_myRecordId, String a_defaultTitleSingular ) {
        parentTabID = a_parentTabID;
        myRecordId = a_myRecordId;
        defaultTitleSingular = a_defaultTitleSingular;
        defaultTitlePlural = parentTabID;
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
    
    public int checkTitle(String shouldBeTitle, String titleKind){
    	String tempString;
    	
    	tempString = action.getTitle(sInstance);
    	if (!shouldBeTitle.equals(tempString)){
    		action.error(titleKind + " title of page _"+ parentTabID +"_ is _"+tempString+"_ (should be _"+shouldBeTitle+"_)");
    		return constants.RET_ERROR;
    	}      	
    	action.info(titleKind + " title of page _"+ parentTabID +"_ is _"+tempString+" (OK)");
    	return constants.RET_OK;
    }
    
    public int checkTitles(){
    	int retValue = constants.RET_OK;
    	
    	String homeTitle = defaultTitlePlural+": Home ~ Salesforce - Developer Edition";;
    	String newTitle = defaultTitleSingular+" Edit: New "+ defaultTitleSingular+" ~ Salesforce - Developer Edition";
    	String editTitle = defaultTitleSingular+" Edit: "+ myRecordId+ " ~ Salesforce - Developer Edition";
    	String afterSaveTitle = defaultTitleSingular+": "+ myRecordId+" ~ Salesforce - Developer Edition";
    	String afterDeleteTitle = homeTitle;
    	
    	action.openTab(sInstance, parentTabID);
    	
    	retValue = checkTitle(homeTitle, "Home");
    	
    	if (action.createNewEmptyRecord(sInstance, parentTabID) == constants.RET_ERROR){
    		action.error("Cant't create new record of _"+parentTabID+"_, checkTitles skipped");
    		return constants.RET_ERROR;
    	}
    	
    	retValue = checkTitle(newTitle, "New record");
    	
    	// TODO - should provide error if can't fill
    	fillElementsByValidValues();
    	
    	action.isElementPresent(sInstance, constants.SAVE_AND_NEW_LOCATOR);    	
    	action.isElementPresent(sInstance, constants.CANCEL_LOCATOR);
    	

    	if (action.pressButton(sInstance, constants.SAVE_RECORD_LOCATOR) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	
    	retValue = checkTitle(afterSaveTitle, "After save");

    	action.isElementPresent(sInstance, constants.DELETE_LOCATOR);    	
    	action.isElementPresent(sInstance, constants.CLONE_LOCATOR);

    	if (action.pressButton(sInstance, constants.EDIT_RECORD_LOCATOR) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	
    	retValue = checkTitle(editTitle, "Edit title");

    	action.isElementPresent(sInstance, constants.SAVE_RECORD_LOCATOR);    	
    	action.isElementPresent(sInstance, constants.CANCEL_LOCATOR);
    	
    	if (action.pressButton(sInstance, constants.SAVE_AND_NEW_LOCATOR) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	
    	retValue = checkTitle(newTitle, "After SaveAndNew");
    	
    	if (action.pressButton(sInstance, constants.CANCEL_LOCATOR) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	
    	retValue = checkTitle(afterSaveTitle, "After cancel");    	
    	
    	if (action.pressButton(sInstance, constants.CLONE_LOCATOR) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	
    	retValue = checkTitle(newTitle, "After clone");
    	
    	if (action.pressButton(sInstance, constants.CANCEL_LOCATOR) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	if (action.pressDelete(sInstance) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	
    	retValue = checkTitle(afterDeleteTitle, "After delete");
    	
    	return retValue;
    }    
    
    public int checkAll(){
    	checkTitles();
    	
    	createNewEmptyRecord();
        checkElementsPresence();
        fillElementsByValidValues();
        
        checkAllElements();    	
    	
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
    	action.pressButton(sInstance, constants.SAVE_RECORD_LOCATOR);
        return constants.RET_OK;
    }
}  


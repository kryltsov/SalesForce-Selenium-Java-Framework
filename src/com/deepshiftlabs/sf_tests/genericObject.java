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
	ArrayList <String> writeLocatorsList = new ArrayList <String>();	
	String waitCondition = "";
	String tempLocator = "//input[@id='Login']";
	String title = "";
    
    commonActions action = new commonActions();
    DefaultSelenium sInstance;
    
    private boolean isThereUndeletedRecord=false;
    private int determiningRecordFieldIndex = -1;
    
    genericObject(String a_parentTabID, String a_myRecordId, String a_defaultTitleSingular ) {
        parentTabID = a_parentTabID;
        myRecordId = a_myRecordId;
        defaultTitleSingular = a_defaultTitleSingular;
        defaultTitlePlural = parentTabID;
    }
    
    public int prepareBrowser(String hub, int port, String browser, String url) throws sftestException
    {
        sInstance = action.getSelenium(hub, port, browser, url);
        if (sInstance==null){
        	throw new sftestException("Can't connect to selenium server!");
        }
        return constants.RET_OK;
    };
    
    public int logout(){
    	action.logout(sInstance);
        return constants.RET_OK;    	
    }
    public int freeBrowser()
    {
        action.freeSelenium(sInstance);
        sInstance = null;
        return constants.RET_OK;
    };    
        
    public void updateLocatorsLists(){
    	writeLocatorsList.clear();
    	genericElement tempElement;
    	for (int i=0; i<elements.size();i++){
    		tempElement = (genericElement)elements.get(i);
    		writeLocatorsList.add(utils.prepareForJavaScript(tempElement.writeLocator));
    	}
    	waitCondition = utils.prepareCondition(writeLocatorsList);
    }
    	
    
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
    
    public int fillElementsByValidValues() throws sftestException {
        genericElement tempElement;

        Iterator iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (genericElement)iterator.next();
		     if (tempElement.fillByValidValue(sInstance)==constants.RET_ERROR){
		    	 throw new sftestException("Can't fill all elements by valid values.");
		     }
	    }
	    return constants.RET_OK;
    }
    
    public int checkAllElements() throws sftestException {
        genericElement tempElement;
        int returnedValue;

        Iterator iterator = elements.iterator();
        
    	createNewEmptyRecordFast();
	    
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
    
    public int checkIsRecordSavable() throws sftestException {
    	String afterSaveTitle = defaultTitleSingular+": "+ myRecordId+" ~ Salesforce - Developer Edition";
    	String newTitle = defaultTitleSingular+" Edit: New "+ defaultTitleSingular+" ~ Salesforce - Developer Edition";
    	int retValue;
    	
    	if (action.createNewEmptyRecord(sInstance, parentTabID) == constants.RET_ERROR){
    		action.fatal("Cant't create new record of _"+parentTabID+"_, can't perform check IsRecordSavable");
    		action.getScreenshot(sInstance, true);
    		throw new sftestException("Can't create or save new record.");
    	}
    	
    	if ((checkTitle(newTitle, "New record")==constants.RET_ERROR) ||
    	    	(!action.isElementPresent(sInstance, constants.SAVE_RECORD_LOCATOR))) {
    		action.fatal("Cant't create new record of _"+parentTabID+"_, can't perform check IsRecordSavable");
    		action.getScreenshot(sInstance, true);    		
    		throw new sftestException("Can't create or save new record.");
    	}    	
    	
    	retValue=checkElementsPresence();
    	if (retValue>0){
    		action.fatal("Can't find "+retValue+" element(s), can't perform check IsRecordSavable");
    		action.getScreenshot(sInstance, true);    		
    		throw new sftestException("Can't create or save new record.");    		
    	}
    	
    	if (fillElementsByValidValues()==constants.RET_ERROR){
    		action.fatal("Cant't fill all elements in _"+parentTabID+"_, can't perform check IsRecordSavable");
    		action.getScreenshot(sInstance, true);    		
    		throw new sftestException("Can't create or save new record.");
    	}
    	
    	if (action.pressButton(sInstance, constants.SAVE_RECORD_LOCATOR) == constants.RET_ERROR){
    		action.getScreenshot(sInstance, true);    		
    		throw new sftestException("Can't create or save new record.");
    	}    	

    	if (checkTitle(afterSaveTitle, "After save")==constants.RET_ERROR){ 
    		action.fatal("Cant't save new record of _"+parentTabID+"_, can't end check IsRecordSavable. Check if all elements are in object and all of them have right type (see screenshot for details).");
    		action.getScreenshot(sInstance, true);    		
    		throw new sftestException("Can't create or save new record.");
    	}       	
    	
// TODO should check if delete is successful
    	action.deleteRecord(sInstance, parentTabID, getIdOfStoredRecord());    	
        
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
    
    public int checkSequence() throws sftestException {
    	int retValue = constants.RET_OK;
    	
    	String homeTitle = defaultTitlePlural+": Home ~ Salesforce - Developer Edition";;
    	String newTitle = defaultTitleSingular+" Edit: New "+ defaultTitleSingular+" ~ Salesforce - Developer Edition";
    	String editTitle = defaultTitleSingular+" Edit: "+ myRecordId+ " ~ Salesfo1rce - Developer Edition";
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
    	
    	if (checkTitle(afterSaveTitle, "After save")==constants.RET_ERROR)
    			return constants.RET_ERROR;
    	isThereUndeletedRecord = true;
    	
    	action.isElementPresent(sInstance, constants.DELETE_LOCATOR);    	
    	action.isElementPresent(sInstance, constants.CLONE_LOCATOR);

    	if (action.pressButton(sInstance, constants.EDIT_RECORD_LOCATOR) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	
    	if (checkTitle(editTitle, "Edit title")==constants.RET_ERROR)
    			return constants.RET_ERROR;

    	action.isElementPresent(sInstance, constants.SAVE_RECORD_LOCATOR);    	
    	action.isElementPresent(sInstance, constants.CANCEL_LOCATOR);
    	
    	if (action.pressButton(sInstance, constants.SAVE_AND_NEW_LOCATOR) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	
    	if (checkTitle(newTitle, "After SaveAndNew")==constants.RET_ERROR)
    			return constants.RET_ERROR;
    	
    	if (action.pressButton(sInstance, constants.CANCEL_LOCATOR) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	
    	if (checkTitle(afterSaveTitle, "After cancel")==constants.RET_ERROR)
    			return constants.RET_ERROR;
    	
    	if (action.pressButton(sInstance, constants.CLONE_LOCATOR) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	
    	if (checkTitle(newTitle, "After clone")==constants.RET_ERROR)
    			return constants.RET_ERROR;
    	
    	if (action.pressButton(sInstance, constants.CANCEL_LOCATOR) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	if (action.pressDelete(sInstance) == constants.RET_ERROR){
    		return constants.RET_ERROR;
    	}    	
    	
    	if (checkTitle(afterDeleteTitle, "After delete")==constants.RET_ERROR)
    			return constants.RET_ERROR;
    	isThereUndeletedRecord=false;
    	
    	return retValue;
    }    
    
    public int checkAll() throws sftestException{
    	checkIsRecordSavable();
    	
    	if (checkSequence()==constants.RET_ERROR){
    		action.error("Non-fatal error while checking common sequense.");
    		action.getScreenshot(sInstance, true);
    		if (isThereUndeletedRecord){
    			action.deleteRecord(sInstance, parentTabID, getIdOfStoredRecord());
    			isThereUndeletedRecord = false;
    		}
    	}
        
        checkAllElements();    	
    	
    	return constants.RET_OK;
    }
    
    public int login()throws sftestException{
    	if (action.login(sInstance, settings.SF_LOGIN, settings.SF_PASSWORD) == constants.RET_ERROR){
    		throw new sftestException("Login failed.");
    	}
    	return constants.RET_OK;
    }
    
    public int createNewEmptyRecord() throws sftestException {
    	if (action.createNewEmptyRecord(sInstance, parentTabID)==constants.RET_ERROR){
    		throw new sftestException("Can't create new record.");
    	}
    	return constants.RET_OK;    	
    }
    
    public int createNewEmptyRecordFast() throws sftestException {
    	if (action.createNewEmptyRecordFast(sInstance, parentTabID, waitCondition)==constants.RET_ERROR){
    		throw new sftestException("Can't create new record (fast).");
    	}
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
    
    public int recreateRecord()throws sftestException {
    	action.deleteRecord(sInstance, parentTabID, getIdOfStoredRecord());
    	createNewEmptyRecordFast();    	
        return constants.RET_OK;
    }    
    
    public int saveRecord(){
    	action.pressButton(sInstance, constants.SAVE_RECORD_LOCATOR);
        return constants.RET_OK;
    }
}  


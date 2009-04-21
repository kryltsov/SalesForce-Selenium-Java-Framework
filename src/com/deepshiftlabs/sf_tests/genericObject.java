package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.*;

import java.util.ArrayList;
import java.util.Iterator;


public class GenericObject {
    String parentTabID;
    String myRecordId;    
    
    String defaultTitleSingular;
    String defaultTitlePlural;
    
    ArrayList <GenericElement> elements = new ArrayList <GenericElement>();
	ArrayList <String> writeLocatorsList = new ArrayList <String>();	
	String waitCondition = "";
	String tempLocator = "//input[@id='Login']";
	String title = "";
    
    CommonActions action = new CommonActions();
    DefaultSelenium sInstance;
    
    private boolean isThereUndeletedRecord=false;
    private int determiningRecordFieldIndex = -1;
    
    GenericObject(String a_parentTabID, String a_myRecordId, String a_defaultTitleSingular ) {
        parentTabID = a_parentTabID;
        myRecordId = a_myRecordId;
        defaultTitleSingular = a_defaultTitleSingular;
        defaultTitlePlural = parentTabID;
    }
    
    public int prepareBrowser(String hub, int port, String browser, String url) throws SftestException
    {
        sInstance = action.getSelenium(hub, port, browser, url);
        if (sInstance==null){
        	throw new SftestException("Can't connect to selenium server!");
        }
        return Constants.RET_OK;
    };
    
    public int logout(){
    	action.logout(sInstance);
        return Constants.RET_OK;    	
    }
    public int freeBrowser()
    {
        action.freeSelenium(sInstance);
        sInstance = null;
        return Constants.RET_OK;
    };    
        
    public void updateLocatorsLists(){
    	writeLocatorsList.clear();
    	GenericElement tempElement;
    	for (int i=0; i<elements.size();i++){
    		tempElement = (GenericElement)elements.get(i);
    		writeLocatorsList.add(Utils.prepareForJavaScript(tempElement.writeLocator));
    	}
    	waitCondition = Utils.prepareCondition(writeLocatorsList);
    }
    	
    public int addElement(GenericElement el){
        elements.add(el);
        return elements.size();
    }
    
    public int findElementIndexByName(String elementName){
        GenericElement tempElement;
        int index = 0;

        Iterator <GenericElement> iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (GenericElement)iterator.next();
		     if (tempElement.getElementName().equals(elementName)){
		        return index;
		    }
		    index++;
	    }
	    return Constants.RET_ERROR;
    }

    public int setDeterminingRecordIdField(String elementName){
        GenericElement tempElement;    	
    	
        determiningRecordFieldIndex = findElementIndexByName(elementName);
    	if (determiningRecordFieldIndex!=Constants.RET_ERROR){
    		tempElement = (GenericElement)elements.get(determiningRecordFieldIndex);
    		tempElement.forceToDetermineRecordID(myRecordId);
    		return Constants.RET_OK;
    	}
        return Constants.RET_ERROR;        
    }    
    
    public int setUnique(String elementName, boolean isCaseSens){
    	int elementIndexInList;
        GenericElement tempElement;    	
    	
    	elementIndexInList = findElementIndexByName(elementName);
    	if (elementIndexInList!=Constants.RET_ERROR){
    		tempElement = (GenericElement)elements.get(elementIndexInList);
    		if (tempElement instanceof TextElement){
    			((TextElement)tempElement).setUnique(isCaseSens);
    			return Constants.RET_OK;
    		}
    		action.error("Can't setUnique - it isn't TextElement!");
    		return Constants.RET_ERROR;
    	}
    	action.error("Can't setUnique - there are no such element");
        return Constants.RET_ERROR;
    }       
    
    public int checkElementsPresence (){
        GenericElement tempElement;
        int missed = 0;

        Iterator <GenericElement> iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (GenericElement)iterator.next();
		     if (tempElement.checkPresence(sInstance)==Constants.RET_ERROR){
		        missed++;
		    }
	    }
	    return missed;
    }
    
    public int fillElementsByValidValues() throws SftestException {
        GenericElement tempElement;

        Iterator <GenericElement> iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (GenericElement)iterator.next();
		     if (tempElement.fillByValidValue(sInstance)==Constants.RET_ERROR){
		    	 throw new SftestException("Can't fill all elements by valid values.");
		     }
	    }
	    return Constants.RET_OK;
    }
    
    public int fillElementsByInvalidValues() throws SftestException {
        GenericElement tempElement;

        Iterator <GenericElement> iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (GenericElement)iterator.next();
		     if (tempElement.fillByInvalidValue(sInstance)==Constants.RET_ERROR){
		    	 throw new SftestException("Can't fill all elements by invalid values.");
		     }
	    }
	    return Constants.RET_OK;
    }    
    
    public int checkAllElements() throws SftestException {
        GenericElement tempElement;
        int returnedValue;

        Iterator <GenericElement> iterator = elements.iterator();
        
    	createNewEmptyRecord();
	    
	    while (iterator.hasNext()) {
	    	 fillElementsByValidValues();	    	
		     tempElement = (GenericElement)iterator.next();

		     returnedValue = Constants.RET_ERROR;
		     while (returnedValue!=Constants.RET_OK){
		    	 returnedValue = tempElement.checkAll(sInstance);
		    	 if (tempElement.getErrorsCount()>=Settings.FATAL_ELEMENT_ERRORS_COUNT){
		     		action.fatal("Too many errors per element _"+tempElement.getElementName()+"_ ("+tempElement.getErrorsCount()+")");
		    		action.getScreenshot(sInstance, true);    		
		    		throw new SftestException("Too many errors per element!");		    		 
		    	 }
		    	 if (returnedValue==Constants.RET_PAGE_BROKEN_ERROR || 
		    			 returnedValue==Constants.RET_PAGE_BROKEN_OK){
		    		recreateRecord();
		    	 	fillElementsByValidValues();
		    	 }
		     }
	    }
	    return Constants.RET_OK;
    }
    
    public int checkIsRecordSavable() throws SftestException {
    	String afterSaveTitle = defaultTitleSingular+": "+ myRecordId+" ~ Salesforce - Developer Edition";
    	String newTitle = defaultTitleSingular+" Edit: New "+ defaultTitleSingular+" ~ Salesforce - Developer Edition";
    	int retValue;
    	
    	if (action.createNewEmptyRecord(sInstance, parentTabID) == Constants.RET_ERROR){
    		action.fatal("Cant't create new record of _"+parentTabID+"_, can't perform check IsRecordSavable");
    		action.getScreenshot(sInstance, true);
    		throw new SftestException("Can't create or save new record.");
    	}
    	
    	if ((checkTitle(newTitle, "New record")==Constants.RET_ERROR) ||
    	    	(!action.isElementPresent(sInstance, Constants.SAVE_RECORD_LOCATOR))) {
    		action.fatal("Cant't create new record of _"+parentTabID+"_, can't perform check IsRecordSavable");
    		action.getScreenshot(sInstance, true);    		
    		throw new SftestException("Can't create or save new record.");
    	}    	
    	
    	retValue=checkElementsPresence();
    	if (retValue>0){
    		action.fatal("Can't find "+retValue+" element(s), can't perform check IsRecordSavable");
    		action.getScreenshot(sInstance, true);    		
    		throw new SftestException("Can't create or save new record.");    		
    	}
    	
    	if (fillElementsByValidValues()==Constants.RET_ERROR){
    		action.fatal("Cant't fill all elements in _"+parentTabID+"_, can't perform check IsRecordSavable");
    		action.getScreenshot(sInstance, true);    		
    		throw new SftestException("Can't create or save new record.");
    	}
    	
    	if (action.pressButton(sInstance, Constants.SAVE_RECORD_LOCATOR) == Constants.RET_ERROR){
    		action.getScreenshot(sInstance, true);    		
    		throw new SftestException("Can't create or save new record.");
    	}    	

    	if (checkTitle(afterSaveTitle, "After save")==Constants.RET_ERROR){ 
    		action.fatal("Cant't save new record of _"+parentTabID+"_, can't end check IsRecordSavable. Check if all elements are in object and all of them have right type (see screenshot for details).");
    		action.getScreenshot(sInstance, true);    		
    		throw new SftestException("Can't create or save new record.");
    	}       	
    	
// TODO should check if delete is successful
    	action.deleteRecord(sInstance, parentTabID, getIdOfStoredRecord());    	
        
        return Constants.RET_OK;
    }
    
    public int checkTitle(String shouldBeTitle, String titleKind){
    	String tempString;
    	
    	tempString = action.getTitle(sInstance);
    	if (!shouldBeTitle.equals(tempString)){
    		action.error(titleKind + " title of page _"+ parentTabID +"_ is _"+tempString+"_ (should be _"+shouldBeTitle+"_)");
    		return Constants.RET_ERROR;
    	}      	
    	action.info(titleKind + " title of page _"+ parentTabID +"_ is _"+tempString+" (OK)");
    	return Constants.RET_OK;
    }
    
    public int checkSequence() throws SftestException {
    	int retValue = Constants.RET_OK;
    	
    	String homeTitle = defaultTitlePlural+": Home ~ Salesforce - Developer Edition";;
    	String newTitle = defaultTitleSingular+" Edit: New "+ defaultTitleSingular+" ~ Salesforce - Developer Edition";
    	String editTitle = defaultTitleSingular+" Edit: "+ myRecordId+ " ~ Salesforce - Developer Edition";
    	String afterSaveTitle = defaultTitleSingular+": "+ myRecordId+" ~ Salesforce - Developer Edition";
    	String afterDeleteTitle = homeTitle;
    	
    	action.openTab(sInstance, parentTabID);
    	
    	if (checkTitle(homeTitle, "Home")==Constants.RET_ERROR)
			return Constants.RET_ERROR;    	
    	if (action.createNewEmptyRecord(sInstance, parentTabID) == Constants.RET_ERROR){
    		action.error("Cant't create new record of _"+parentTabID+"_, checkTitles skipped");
    		return Constants.RET_ERROR;
    	}
    	if (checkTitle(newTitle, "New record")==Constants.RET_ERROR)
			return Constants.RET_ERROR;    	
    	
    	// TODO - should provide error if can't fill
    	fillElementsByValidValues();
    	
    	action.isElementPresent(sInstance, Constants.SAVE_AND_NEW_LOCATOR);    	
    	action.isElementPresent(sInstance, Constants.CANCEL_LOCATOR);
    	

    	if (action.pressButton(sInstance, Constants.SAVE_RECORD_LOCATOR) == Constants.RET_ERROR){
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(afterSaveTitle, "After save")==Constants.RET_ERROR)
    			return Constants.RET_ERROR;
    	isThereUndeletedRecord = true;
    	
    	action.isElementPresent(sInstance, Constants.DELETE_LOCATOR);    	
    	action.isElementPresent(sInstance, Constants.CLONE_LOCATOR);

    	if (action.pressButton(sInstance, Constants.EDIT_RECORD_LOCATOR) == Constants.RET_ERROR){
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(editTitle, "Edit title")==Constants.RET_ERROR)
    			return Constants.RET_ERROR;

    	action.isElementPresent(sInstance, Constants.SAVE_RECORD_LOCATOR);    	
    	action.isElementPresent(sInstance, Constants.CANCEL_LOCATOR);
    	
    	if (action.pressButton(sInstance, Constants.SAVE_AND_NEW_LOCATOR) == Constants.RET_ERROR){
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(newTitle, "After SaveAndNew")==Constants.RET_ERROR)
    			return Constants.RET_ERROR;
    	if (action.pressButton(sInstance, Constants.CANCEL_LOCATOR) == Constants.RET_ERROR){
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(afterSaveTitle, "After cancel")==Constants.RET_ERROR)
    			return Constants.RET_ERROR;
    	if (action.pressButton(sInstance, Constants.CLONE_LOCATOR) == Constants.RET_ERROR){
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(newTitle, "After clone")==Constants.RET_ERROR)
    			return Constants.RET_ERROR;
    	if (action.pressButton(sInstance, Constants.CANCEL_LOCATOR) == Constants.RET_ERROR){
    		return Constants.RET_ERROR;
    	}    	
    	if (action.pressDelete(sInstance) == Constants.RET_ERROR){
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(afterDeleteTitle, "After delete")==Constants.RET_ERROR)
    			return Constants.RET_ERROR;
    	isThereUndeletedRecord=false;
    	
    	return retValue;
    }    
    
    public int checkAll() throws SftestException{
    	checkIsRecordSavable();
    	
    	if (checkSequence()==Constants.RET_ERROR){
    		if (Settings.IS_CHECKSEQUENCE_FATAL){
    			action.fatal("Fatal error while checking common sequense.");    			
    			throw new SftestException("Check sequence error!"); 
    		}
    		action.error("Non-fatal error while checking common sequense (unfinished actions was skipped).");
    		action.getScreenshot(sInstance, true);
    		if (isThereUndeletedRecord){
    			action.deleteRecord(sInstance, parentTabID, getIdOfStoredRecord());
    			isThereUndeletedRecord = false;
    		}
    	}
        
        checkAllElements();    	
    	
    	return Constants.RET_OK;
    }
    
    public void printErrorsSummary(){
    	GenericElement tempElement;
    	action.warn("");
    	action.warn("Error summary for object _"+defaultTitleSingular+"_:");
    	action.warn("Err. count    Element name");
    	for (int i=0; i<elements.size();i++){
    		tempElement = (GenericElement)elements.get(i);
    		action.warn(tempElement.getErrorsCount() + "              " +tempElement.getElementName());
    	}    	
    }
    
    public int login()throws SftestException{
    	if (action.login(sInstance, Settings.SF_LOGIN, Settings.SF_PASSWORD) == Constants.RET_ERROR){
    		throw new SftestException("Login failed.");
    	}
    	return Constants.RET_OK;
    }
    
    public int createNewEmptyRecord() throws SftestException {
    	int retValue;
    	if (Settings.USE_FAST_NEW_RECORD){
    		retValue = action.createNewEmptyRecordFast(sInstance, parentTabID, waitCondition); 
    	} else {
    		retValue = action.createNewEmptyRecord(sInstance, parentTabID); 
    	}
    	
    	if (retValue == Constants.RET_ERROR){
    		throw new SftestException("Can't create new record.");
    	}
    	return Constants.RET_OK;    	
    }
    
    String getIdOfStoredRecord(){
        GenericElement tempElement;    	
    	
        if (determiningRecordFieldIndex==-1)
        	return myRecordId;
        if (determiningRecordFieldIndex>=0 || determiningRecordFieldIndex< elements.size()){
        	tempElement = (GenericElement)elements.get(determiningRecordFieldIndex);
        	return tempElement.getLastEnteredValue(); 
        }
        else {
        	action.error("No such element");        	
        	return myRecordId;
        }
    }
    
    public int recreateRecord()throws SftestException {
    	action.deleteRecord(sInstance, parentTabID, getIdOfStoredRecord());
    	createNewEmptyRecord();    	
        return Constants.RET_OK;
    }    
    
    public int saveRecord(){
    	action.pressButton(sInstance, Constants.SAVE_RECORD_LOCATOR);
        return Constants.RET_OK;
    }
}  


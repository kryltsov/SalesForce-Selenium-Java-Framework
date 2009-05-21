package com.deepshiftlabs.sf_tests;

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
	String name = "";
    
    CommonActions action;
    
    private boolean isThereUndeletedRecord=false;
    private int determiningRecordFieldIndex = -1;
    
    GenericObject(String a_parentTabID, String a_myRecordId, String a_defaultTitleSingular ) {
        parentTabID = a_parentTabID;
        myRecordId = a_myRecordId;
        defaultTitleSingular = a_defaultTitleSingular;
        defaultTitlePlural = parentTabID;
        name = defaultTitleSingular;
    }
    
    public void init(CommonActions a_action){
    	action = a_action;
    }
    
    public int prepareBrowser(String hub, int port, String browser, String url) throws SftestException
    {
    	Event event = action.startEvent("prepareBrowser", hub+":"+port);    	
        if (action.startSelenium(hub, port, browser, url)==Constants.RET_ERROR){
        	action.closeEventFatal(event);
        	throw new SftestException("Can't connect to selenium server!");
        }
        action.closeEventOk(event);
        return Constants.RET_OK;
    };
    
    public int logout(){
    	action.logout();
        return Constants.RET_OK;    	
    }
    
    public int freeBrowser()
    {
        action.freeSelenium();
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
    	el.init(action);
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
    
    public int missedElementsCount (){
    	Event event = action.startEvent("missedElementsCount", name);
        GenericElement tempElement;
        Integer missed = 0;

        Iterator <GenericElement> iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (GenericElement)iterator.next();
		     if (tempElement.checkPresence()==Constants.RET_ERROR){
		        missed++;
		    }
	    }
	    if (missed>0){
	    	event.setRealValue("missed: " + missed.toString());
	    	action.closeEventError(event);
	    }
	    else {
	    	action.closeEventOk(event);
	    }
	    return missed;
    }
    
    public int fillElementsByValidValues(){
    	Event event = action.startEvent("fillElementsByValidValues", name);
        GenericElement tempElement;

        Iterator <GenericElement> iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (GenericElement)iterator.next();
		     if (tempElement.fillByValidValue()==Constants.RET_ERROR){
		    	 action.closeEventError(event);
		    	 return Constants.RET_ERROR;
		     }
	    }
	    action.closeEventOk(event);
	    return Constants.RET_OK;
    }
    
    public int fillElementsByInvalidValues() {
    	Event event = action.startEvent("fillElementsByInvalidValues", name);
        GenericElement tempElement;

        Iterator <GenericElement> iterator = elements.iterator();
	    while (iterator.hasNext()) {
		     tempElement = (GenericElement)iterator.next();
		     if (tempElement.fillByInvalidValue()==Constants.RET_ERROR){
		    	 action.closeEventFatal(event);
		    	 return Constants.RET_ERROR;
		     }
	    }
	    action.closeEventOk(event);
	    return Constants.RET_OK;
    }    
    
    public int checkAllElements(){
    	Event event = action.startEvent("checkAllElements", name);
        GenericElement tempElement;
        int returnedValue;

        Iterator <GenericElement> iterator = elements.iterator();
        
	    	if (createNewEmptyRecord() == Constants.RET_ERROR){
	    		action.closeEventFatal(event);
	    		return Constants.RET_ERROR;
	    	}
		    
		    while (iterator.hasNext()) {
		    	 if (fillElementsByValidValues() == Constants.RET_ERROR){
		    		 action.closeEventFatal(event);
		    		 return Constants.RET_ERROR;	    	
		    	 }
			     tempElement = (GenericElement)iterator.next();
	
			     returnedValue = Constants.RET_ERROR;
			     while (returnedValue!=Constants.RET_OK){
			    	 returnedValue = tempElement.checkAll();
			    	 if (tempElement.getErrorsCount()>=Settings.FATAL_ELEMENT_ERRORS_COUNT){
			     		action.closeEventFatal(event, "too many errors per element");
			     		return Constants.RET_ERROR;		    		 
			    	 }
			    	 if (returnedValue==Constants.RET_PAGE_BROKEN_ERROR || 
			    			 returnedValue==Constants.RET_PAGE_BROKEN_OK){
				    	if (recreateRecord() == Constants.RET_ERROR){
				    		action.closeEventFatal(event);
				    		return Constants.RET_ERROR;
				    	}
				    	if (fillElementsByValidValues() == Constants.RET_ERROR){
				    		action.closeEventFatal(event);
				    		return Constants.RET_ERROR;
				    	}
			    	 }
			     }
		    }
        
	    action.closeEventOk(event);
	    return Constants.RET_OK;
    }
    
    public int checkIsRecordSavable(){
    	Event event = action.startEvent("checkIsRecordSavable", name);

    	String afterSaveTitle = defaultTitleSingular+": "+ myRecordId+" ~ Salesforce - Developer Edition";
    	String newTitle = defaultTitleSingular+" Edit: New "+ defaultTitleSingular+" ~ Salesforce - Developer Edition";
    	
    	if (createNewEmptyRecord() == Constants.RET_ERROR){
    		action.closeEventFatal(event);
    		return Constants.RET_ERROR;
    	}
    	
    	if ((checkTitle(newTitle, "New record")==Constants.RET_ERROR) ||
    	    	(!action.isElementPresent(Constants.SAVE_RECORD_LOCATOR))) {
    		action.closeEventFatal(event);
    		return Constants.RET_ERROR;
    	}    	
    	
    	if (missedElementsCount()>0){
    		action.closeEventFatal(event);
    		return Constants.RET_ERROR;    		
    	}
    	
    	if (fillElementsByValidValues()==Constants.RET_ERROR){
    		action.closeEventFatal(event);
    		return Constants.RET_ERROR;
    	}
    	
    	if (action.pressButton(Constants.SAVE_RECORD_LOCATOR) == Constants.RET_ERROR){
    		action.closeEventFatal(event);
    		return Constants.RET_ERROR;
    	}    	

    	if (checkTitle(afterSaveTitle, "After save")==Constants.RET_ERROR){ 
    		event.advice = "(check if all elements are in object and all of them have right type)";
    		action.closeEventFatal(event);
    		return Constants.RET_ERROR;
    	}       	
    	
// TODO should check if delete is successful
    	action.deleteRecord(parentTabID, getIdOfStoredRecord());    	
        
    	action.closeEventOk(event);
        return Constants.RET_OK;
    }
    
    public int checkTitle(String shouldBeTitle, String titleKind){
    	Event event = action.startEvent("checkTitle", titleKind);
    	event.setWaitedValue(shouldBeTitle);
    	String tempString;
    	
    	tempString = action.getTitle();
		event.setRealValue(tempString);
		
    	if (!shouldBeTitle.equals(tempString)){
    		action.closeEventError(event);
    		return Constants.RET_ERROR;
    	}      	
    	action.closeEventOk(event);
    	return Constants.RET_OK;
    }
    
    public int checkSequence(){
    	Event event = action.startEvent("checkSequence", name);

    	// TODO should place this masks to constants and use format function here
    	String homeTitle = defaultTitlePlural+": Home ~ Salesforce - Developer Edition";;
    	String newTitle = defaultTitleSingular+" Edit: New "+ defaultTitleSingular+" ~ Salesforce - Developer Edition";
    	String editTitle = defaultTitleSingular+" Edit: "+ myRecordId+ " ~ Salesforce - Developer Edition";
    	String afterSaveTitle = defaultTitleSingular+": "+ myRecordId+" ~ Salesforce - Developer Edition";
    	String afterDeleteTitle = homeTitle;
    	
    	action.openTab(parentTabID);
    	
    	if (checkTitle(homeTitle, "Home")==Constants.RET_ERROR)
			return Constants.RET_ERROR;    	
    	if (createNewEmptyRecord() == Constants.RET_ERROR){
    		action.closeEventError(event, "checkSequence skipped");
    		return Constants.RET_ERROR;
    	}
    	if (checkTitle(newTitle, "New record")==Constants.RET_ERROR){
    		action.closeEventError(event);
			return Constants.RET_ERROR;    	
    	}
    	
    	if (fillElementsByValidValues() == Constants.RET_ERROR){
    		action.closeEventError(event);
    		return Constants.RET_ERROR;	    	
    	}
    	
    	action.isElementPresent(Constants.SAVE_AND_NEW_LOCATOR);    	
    	action.isElementPresent(Constants.CANCEL_LOCATOR);
    	

    	if (action.pressButton(Constants.SAVE_RECORD_LOCATOR) == Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(afterSaveTitle, "After save")==Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}
    	isThereUndeletedRecord = true;
    	
    	action.isElementPresent(Constants.DELETE_LOCATOR);    	
    	action.isElementPresent(Constants.CLONE_LOCATOR);

    	if (action.pressButton(Constants.EDIT_RECORD_LOCATOR) == Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(editTitle, "Edit title")==Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}

    	action.isElementPresent(Constants.SAVE_RECORD_LOCATOR);    	
    	action.isElementPresent(Constants.CANCEL_LOCATOR);
    	
    	if (action.pressButton(Constants.SAVE_AND_NEW_LOCATOR) == Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(newTitle, "After SaveAndNew")==Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}
    	if (action.pressButton(Constants.CANCEL_LOCATOR) == Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(afterSaveTitle, "After cancel")==Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}
    	if (action.pressButton(Constants.CLONE_LOCATOR) == Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(newTitle, "After clone")==Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}
    	if (action.pressButton(Constants.CANCEL_LOCATOR) == Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}    	
    	if (action.pressDelete() == Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}    	
    	if (checkTitle(afterDeleteTitle, "After delete")==Constants.RET_ERROR){
    		action.closeEventError(event, "CheckSequence skipped");
    		return Constants.RET_ERROR;
    	}
    	isThereUndeletedRecord=false;
    	
    	action.closeEventOk(event);
    	return Constants.RET_OK;
    }    
    
    public int checkAll() throws SftestException{
    	Event event = action.startEvent("checkAll", name);
    	
        if (checkIsRecordSavable()==Constants.RET_ERROR){
			action.closeEventFatal(event);
			throw new SftestException("Saving record error");    	
        }
    	
    	if (checkSequence()==Constants.RET_ERROR){
    		if (Settings.IS_CHECKSEQUENCE_FATAL){
    			action.closeEventFatal(event);
    			throw new SftestException("Check sequence error!"); 
    		}

    		if (isThereUndeletedRecord){
    			action.deleteRecord(parentTabID, getIdOfStoredRecord());
    			isThereUndeletedRecord = false;
    		}
    	}
        
        if (checkAllElements()==Constants.RET_ERROR){
			action.closeEventFatal(event);
			throw new SftestException("Check all elements error!");    	
        }
    	
        action.closeEventOk(event);
    	return Constants.RET_OK;
    }
    
    public int login()throws SftestException{
    	Event event = action.startEvent("login", name);
    	if (action.login(Settings.SF_LOGIN, Settings.SF_PASSWORD) == Constants.RET_ERROR){
    		action.closeEventFatal(event);
    		throw new SftestException("Login failed.");
    	}
    	action.closeEventOk(event);
    	return Constants.RET_OK;
    }
    
    public int createNewEmptyRecord(){
    	Event event = action.startEvent("createNewEmptyRecord", name);
    	int retValue;
    	
    	if (Settings.USE_FAST_NEW_RECORD){
    		retValue = action.createNewEmptyRecordFast(parentTabID, waitCondition); 
    	} else {
    		retValue = action.createNewEmptyRecord(parentTabID); 
    	}
    	
    	if (retValue == Constants.RET_ERROR){
    		action.closeEventFatal(event);
    		return Constants.RET_ERROR;
    	}
    	
    	action.closeEventOk(event);
    	return Constants.RET_OK;    	
    }
    
    public String getIdOfStoredRecord(){
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
    
    public int recreateRecord() {
    	Event event = action.startEvent("recreateRecord", name);
    	action.deleteRecord(parentTabID, getIdOfStoredRecord());
    	if (createNewEmptyRecord()== Constants.RET_ERROR){
    		action.closeEventFatal(event);
    		return Constants.RET_OK;
    	}
    	action.closeEventOk(event);
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
}  


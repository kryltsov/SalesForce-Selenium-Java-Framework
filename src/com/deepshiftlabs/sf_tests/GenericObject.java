package com.deepshiftlabs.sf_tests;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents GenericObject for tests. 
 * It stores needed elements and check sequences, handle elements errors.   
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
public class GenericObject {
    /**
     * Stores name of tab with current objects, "Employment websites", for example.
     */
    String parentTabID;
    
    /**
     * Stores the wanted name for record saving. It will be used throw the element which determines recordId.
     * @see GenericElement#fillByValidValue()  
     */
    String myRecordId;    
    
    /**
     * Contains Salesforce title for singular object (it's just name of object)
     */
    String defaultTitleSingular;

    /**
     * Contains Salesforce title for plural objects (it's used for object Tab naming)
     */
    String defaultTitlePlural;
    
    /**
     * This list stores all elements that we'll check for this object.
     * For each of them their method checkAll will be called by checkAllElements method.   
     */
    ArrayList <GenericElement> elements = new ArrayList <GenericElement>();

    /**
	 * This list stores write locators of each element in "elements" list. 
	 * It's used to prepare JavaScript condition for fast record creation.
	 * @see #createNewEmptyRecord()
	 * @see #waitCondition
	 */
	ArrayList <String> writeLocatorsList = new ArrayList <String>();
	
	
	/**
	 * Stores string of JavaScript code with condition to suppose page loaded
	 * @see #createNewEmptyRecord() 
	 */
	String waitCondition = "";

	/**
	 * Will be used in Event generation. 
	 */
	String name = "";
    
    /**
     * Link to CommonActions object. Will be inited in init method. 
     * Each element of this object should be initialized by this link too.  
     */
	CommonActions action;
    
    /**
     * Used to determine if stored record was not deleted because of error during sequence check.
     * If yes, record will be deleted. 
     */
    private boolean isThereUndeletedRecord=false;
    
    /**
     * Index of element in "elements" list which value should determine recordId (name) when saving. 
     */
    private int determiningRecordFieldIndex = -1;

    
    /**
     * @param a_parentTabID tab name, where objects of this type are. Is equal to plural name of object
     * @param a_myRecordId wanted name for record saving. It will be used throw the element which determines recordId
     * @param a_defaultTitleSingular Salesforce title for singular object (it's just name of object)
     */
    GenericObject(String a_parentTabID, String a_myRecordId, String a_defaultTitleSingular ) {
        parentTabID = a_parentTabID;
        myRecordId = a_myRecordId;
        defaultTitleSingular = a_defaultTitleSingular;
        defaultTitlePlural = parentTabID;
        name = defaultTitleSingular;
    }
    
    /**
     * You have to call this method after before start using this object. 
     */
    public void init(CommonActions a_action){
    	action = a_action;
    }
    
    /**
     * Tries to connect to Selenium server and open browser window.
     * @return RET_OK
     * @throws SftestException if some problems was with connection.
     * @see CommonActions#startSelenium(String, int, String, String)
     */
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
    
    /**
     * Do salesforce logout sequence.
     */
    public int logout(){
    	action.logout();
        return Constants.RET_OK;    	
    }
    
    /**
     * Frees browser and disconnect from selenium server.
     */
    public int freeBrowser()
    {
        action.freeSelenium();
        return Constants.RET_OK;
    };    
        
    /**
     * Adds write locator of each element to writeLocatorsList.
     * @see #writeLocatorsList
     */
    public void updateLocatorsLists(){
    	writeLocatorsList.clear();
    	GenericElement tempElement;
    	for (int i=0; i<elements.size();i++){
    		tempElement = (GenericElement)elements.get(i);
    		writeLocatorsList.add(Utils.prepareForJavaScript(tempElement.writeLocator));
    	}
    	waitCondition = Utils.prepareCondition(writeLocatorsList);
    }
    	
    /**
     * Adds new element to elements list.
     * @return Current size of elements list.
     * @see #elements
     */
    public int addElement(GenericElement el){
    	el.init(action);
        elements.add(el);
        return elements.size();
    }
    
    /**
     * @param elementName name of element to search
     * @return Index of element with given name if found, RET_ERROR otherwise.  
     */
    public int findElementIndexByName(String elementName){
        GenericElement tempElement;
        int index = 0;
// TODO optimize - do without iterators
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

    /**
     * @param elementName name of element which should determine recordId
     * @return RET_OK, RET_ERROR if can't find element with given name. 
     * @see #determiningRecordFieldIndex
     */
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
    
    /**
     * Will be used to mark record as unique.
     */
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
    
    /**
     * Checks presence of each element from elements list on page.
     * @return Number of missed elements, so 0 means that all elements present. 
     */
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
    
    /**
     * Fill each element on page with it's valid value. 
     * Note that element which determines recordId will be filled by recordId value.
     * @see GenericElement#fillByValidValue() 
     */
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
    
    /**
     * Fill each element on page with it's invalid value.
     */
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
    
    /**
     * Calls checkAll method of each element in elements list.
     * Before each call (and in case if one of element's checks saved record and need in its deleting and creating new) 
     * method prepares New record edit page:
     * opens New record edit page and fills all elements with valid values.
     * Element can't prepare page itself because it does not know about other elements, but this elements should be 
     * filled by valid values to exclude their influence.
     *   
     * @return RET_OK, RET_ERROR in case of error occur when record is prepared,
     *  or if fatal error was during element element checks.
     *  
     *  @see Settings#FATAL_ELEMENT_ERRORS_COUNT
     *  @see GenericElement#checkAll()
     */
    public int checkAllElements(){
    	Event event = action.startEvent("checkAllElements", name);
        GenericElement tempElement;
        int returnedValue;

        Iterator <GenericElement> iterator = elements.iterator();

        // creation of new record
    	if (createNewEmptyRecord() == Constants.RET_ERROR){
    		action.closeEventFatal(event);
    		return Constants.RET_ERROR;
    	}
	   

	    while (iterator.hasNext()) {
	    	 if (fillElementsByValidValues() == Constants.RET_ERROR){
	    		 action.closeEventFatal(event);
	    		 return Constants.RET_ERROR;	    	
	    	 }
	     	// we take each element
		     tempElement = (GenericElement)iterator.next();

		     // next loop will work until each check of element will be done.
		     // Note that one of check can broke page (save it) - then checkAll() will return 
		     // RET_PAGE_BROKEN_ERROR or RET_PAGE_BROKEN_OK. In this case save record will be deleted, 
		     // new record with filled values will be created and control will be returned to element's checkAll() again.  
		     returnedValue = Constants.RET_ERROR;
		     while (returnedValue!=Constants.RET_OK){
		    	 returnedValue = tempElement.checkAll();
		    	 if (returnedValue == Constants.RET_ERROR){
		    		 action.closeEventFatal(event);
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
    
    /**
     * It's most common sequence that allows to determine global errors in test settings or in Salesforce platform work.
     * This check creates new record, fills it by valid values and tries to save. 
     * After it check if title of page is correct and deletes record.
     * If all is OK it means that our KGV are really valid, locators to main navigation elements are good etc.   
     */
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
    
    /**
     * @param shouldBeTitle waited value of title
     * @param titleKind describes in which situation this check was called. This parameter is used only for information in Event. 
     * @return RET_OK if current title is equal to wanted, RET_ERROR otherwise.
     */
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
    
    /**
     * Do "observe", more detail tour of Salesforce platform than checkIsRecordSavable() with current GenericObject as guide.
     * Tries to save, delete, clone, save and new records etc. Checks if titles are good on each stage.
     * @return RET_OK, RET_ERROR if some errors occur.
     */
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
    
    /**
     * Checks all common object sequences like create-store-delete record.
     * After it checks each element separately.
     * @return RET_OK
     * @throws SftestException with "Check sequence error!" message if main sequence check fails, 
     * with "Saving record error" message if can't save record with all elements filled by KGV, 
     * with "Check all elements error!" message if a element check caused a fatal error (like max errors count exceeding).  
     */
    public int checkAll() throws SftestException{
    	Event event = action.startEvent("checkAll", name);
    	
 /*       if (checkIsRecordSavable()==Constants.RET_ERROR){
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
    	}*/
        
        if (checkAllElements()==Constants.RET_ERROR){
			action.closeEventFatal(event);
			throw new SftestException("Check all elements error!");    	
        }
    	
        action.closeEventOk(event);
    	return Constants.RET_OK;
    }
    
    /**
     * Do Salesforce login sequence 
     * @return RET_OK
     * @throws SftestException if there were some errors. 
     */
    public int login()throws SftestException{
    	Event event = action.startEvent("login", name);
    	if (action.login(Settings.SF_LOGIN, Settings.SF_PASSWORD) == Constants.RET_ERROR){
    		action.closeEventFatal(event);
    		throw new SftestException("Login failed.");
    	}
    	action.closeEventOk(event);
    	return Constants.RET_OK;
    }
    
    /**
     * Creates new record of object type.
     * In dependence of USE_FAST_NEW_RECORD value will wait for load of whole page or only for elements in "elements" list.
     * @see Settings#USE_FAST_NEW_RECORD  
     */
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
    
    /**
     * If record was saved, gets id (name) of it throw lastEnteredValue of element which determines recirdId.
     * @return id (name) of stored record
     * @see GenericElement#lastEnteredValue
     */
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
    
    /**
     * If record was saved and we need in new to continue checks, deletes saved record and creates new.
     */
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
    
    /**
     * Prints number of errors for each elements.
     */
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
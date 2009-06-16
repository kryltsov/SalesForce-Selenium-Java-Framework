// TODO in low-level functions fill event field Target with locators

package com.deepshiftlabs.sf_tests;

import org.testng.Reporter;

import java.util.ArrayList;

/*import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;*/

import com.thoughtworks.selenium.*;

/**
 * Represents all functions for work with Selenium driver, some common browsing sequences, some common log functions.
 * Have the exemplar of DefaultSelenium which is working with single test object (like WebsiteObject).
 * @author Bear, Jan 15, 2009
 */
public class CommonActions {
    Utils ut = new Utils();
    ArrayList <Event> events = new ArrayList <Event>();
    EventProcessor eventProc = new EventProcessor();
    DefaultSelenium seleniumInstance;

    // this name is used in report generation as event's parent.
    String name = "Selenium driver";
    
    String screenshotsPath = "";
    
    // this variable is used in closing event. 
    String lastScreenshotFilename = "";
    int lastEventId = 0;
    
    /**
     * You have to call this function after creating a new CommonActions object.  
     */
    public void init (){
    	eventProc.init(events);
		Utils.prepareDir(Settings.REPORT_PATH);
		screenshotsPath = Utils.prepareDir(Settings.SCREENSHOTS_PATH);
    }

//**************            FUNCTIONS WICH USE SELENIUM DRIVER    *********************//
    
    /**
     * Creates instance of DefaultSelenium and tries to connect to Selenium server.
     * If connection is set, browser window will be opened. 
     * @return RET_OK, RET_ERROR if some errors happened. Exception message will be saved to corresponding event. 
     */
    protected int startSelenium(String seleniumHost, int seleniumPort, String browser, String webSite){
        seleniumInstance = new DefaultSelenium(seleniumHost, seleniumPort, browser, webSite);
        Event event = startEvent("startSelenium", seleniumHost+":"+seleniumPort+", "+ browser+":"+webSite);
        try {
        	seleniumInstance.start();
        } 
        catch (Exception e){
        	seleniumInstance = null;

        	event.exceptionMessage = e.toString();
        	event.advice = "(check if Hub or RC with requested environment is present)";
        	closeEventFatal(event, "can't start selenium driver");
        	
        	return Constants.RET_ERROR;
        };
        
    	seleniumInstance.setTimeout(Settings.TIMEOUT);

    	event.realValue = seleniumInstance.toString();
    	closeEventOk(event);
    	return Constants.RET_OK;
   }

    /**
     * If seleniumInstance is not null, will stop it (it'll close browser windows and disconnect from Selenium server)
     */
    public void freeSelenium() {
    	Event event;
    	if (seleniumInstance==null){
    		event = startEvent("freeSelenium", "null driver");
    	}
    	else {
    		event = startEvent("freeSelenium", seleniumInstance.toString());
	        seleniumInstance.stop();
	        seleniumInstance = null;
    	}
    	closeEventOk(event);
    }
    
    public int openUrl (String url){
    	Event event = startEvent("openUrl", url);
        try {
        	seleniumInstance.open(url);
        } 
        catch (Exception e){

        	event.exceptionMessage = e.toString();
        	closeEventWarn(event);
        	return Constants.RET_ERROR;
        };    	
        getScreenshot();
        closeEventOk(event);
    	return Constants.RET_OK;
    }

	/**
	 * Do standard sequence to log into Salesforce. Does title and errors checks.
	 * @param a_login Salesforce login 
	 * @param a_password Salesforce password
	 * @return RET_ERROR if login failed, RET_OK if login succeed.
	 */
	protected int login(String a_login, String a_password) {
			Event event = startEvent("login", a_login+"/"+a_password);
			ArrayList <String> locatorsList = new ArrayList <String>();		
			String tempLocator = Constants.LOGIN_LOCATOR;
			String title = "";
	        locatorsList.add("username");
	        locatorsList.add("password");
			
	        info("Login started");

	        seleniumInstance.setTimeout(Constants.MIN_SELENIUM_TIMEOUT);
	        openUrl("/");
	        seleniumInstance.setTimeout(Settings.TIMEOUT);
	        
	        if (!waitForListOfElements(locatorsList, Settings.TIMEOUT)){
				closeEventFatal(event, "cant found inputs");
				return Constants.RET_ERROR;
	        }
	        
	        typeText("username",a_login);
	        typeText("password",a_password);
	        
			if (click(tempLocator)==Constants.RET_ERROR){
				closeEventFatal(event);
				return Constants.RET_ERROR;				
			}
			
	        waitForPageToLoad();
	        
			if (isTextPresent(Constants.LOGIN_FAILED_ERROR))
			{
				event.advice = "(check your login and password)";
				closeEventFatal(event);
				return Constants.RET_ERROR;				
			}
			
			title = getTitle();
			
			if (!title.equals(Constants.HOME_PAGE_TITLE))
			{
				closeEventFatal(event);
				return Constants.RET_ERROR;				
			}				
			
	        closeEventOk(event);
	        return Constants.RET_OK;
	    }

	/**
	 * @return RET_ERROR if waiting failed.
	 */
	protected int waitForPageToLoad(){
		Event event = startEvent("waitForPageToLoad", name+"(timeout "+new Integer (Settings.TIMEOUT).toString()+")");
		try {
			seleniumInstance.waitForPageToLoad(Settings.TIMEOUT);
		}
		catch (Exception e){
			getScreenshot(true);
			event.exceptionMessage = e.toString();
			closeEventError(event);
			return Constants.RET_ERROR;
		}
// TODO check if we need in screenshot here
		getScreenshot();
		closeEventOk(event);
		return Constants.RET_OK;
	}
	
	/**
	 * @param a_locator locator which defines place from where driver should read text  
	 * @return Read string or RET_ERROR_STRING in case of error.
	 */
	protected String readText(String a_locator) {
		Event event = startEvent("readText", a_locator);

		String tempString;
		
		try {
			tempString = seleniumInstance.getText(a_locator);
		}
		catch (Exception e){
			event.exceptionMessage = e.toString();
			closeEventError(event);
			return Constants.RET_ERROR_STRING;
		}
		event.setRealValue(tempString);
		closeEventOk(event);
		return tempString;
	}  	
	
	protected int typeText(String a_locator, String a_text) {            
		Event event = startEvent("typeText", a_locator);
		event.setValue(a_text);
		
		try {
			seleniumInstance.type(a_locator, a_text);
		}
		catch (Exception e){
			event.exceptionMessage = e.toString();
			getScreenshot(true);
			closeEventError(event);			
			return Constants.RET_ERROR;
		}
		getScreenshot();
		closeEventOk(event);
		return Constants.RET_OK;
	}
	
	protected String readValue(String a_locator) {  
		Event event = startEvent("readValue", a_locator);

		String tempString;
		
		try {
			tempString = seleniumInstance.getValue(a_locator);
		}
		catch (Exception e){
			event.exceptionMessage = e.toString();
			closeEventError(event);
			return Constants.RET_ERROR_STRING;
		}
		event.setRealValue(tempString);
		closeEventOk(event);
		return tempString;
	}
	
	protected boolean isElementPresent(String a_locator) {
		Event event = startEvent("isElementPresent", a_locator);
		Boolean result = false;
		
		try {
			result = seleniumInstance.isElementPresent(a_locator);
		}
		catch (Exception e){
			event.exceptionMessage = e.toString();
			closeEventError(event);
		}
		event.setRealValue(result.toString());
		closeEventOk(event);
		return result;
	}  
	
	protected boolean isTextPresent(String a_text) {
		Event event = startEvent("isTextPresent", a_text);
		Boolean result = false;
		
		try {
			result = seleniumInstance.isTextPresent(a_text);
		}
		catch (Exception e){
			event.exceptionMessage = e.toString();
			closeEventError(event);
		}
		event.setRealValue(result.toString());
		closeEventOk(event);
		return result;
	}  	
	
	/**
	 * Before click check of element presence called.
	 * @param a_locator element which should be clicked
	 */
	protected int click(String a_locator) {   
		Event event = startEvent("click", a_locator);
		try {
			if (!isElementPresent(a_locator)){
				closeEventError(event, "element is not present");
				return Constants.RET_ERROR;
			}
			
			seleniumInstance.click(a_locator);
		}
		catch (Exception e){
			event.exceptionMessage = e.toString();
			closeEventError(event);
			return Constants.RET_ERROR;
		}
		getScreenshot();
		closeEventOk(event);
		return Constants.RET_OK;
	}

	/**
	 * This function should be used when clicking on element show an JS confirmation like Delete confirmation.
	 * @param a_locator element which should be clicked
	 */
	protected int clickWithConfirmation(String a_locator) {   
		Event event = startEvent("clickWithConfirmation", a_locator);
		try {
			if (!isElementPresent(a_locator)){
				closeEventError(event, "element is not present");
				return Constants.RET_ERROR;
			}
			seleniumInstance.chooseOkOnNextConfirmation();			
			seleniumInstance.click(a_locator);
			try {
				seleniumInstance.getConfirmation();
			}
			catch (Exception e){
				warn("Some error with conf");
			}
		}
		catch (Exception e){
			event.exceptionMessage = e.toString();
			closeEventError(event);
			return Constants.RET_ERROR;
		}
		getScreenshot();
		closeEventOk(event);
		return Constants.RET_OK;
	} 	
	
	/**
	 * Modifies lastScreenshotFilename variable.
	 * @param isError if set to true, adds (ERR) to end of screenshot filename
	 * @return Filename of screenshot. 
	 */
	protected String getScreenshot(boolean isError) {            
		String filename;
		filename = Utils.generateScreenshotName(isError);
		
		Event event = startEvent("getScreenshot", filename);
		event.beforeScreenshot = "";
		
	    try{
	    	seleniumInstance.captureEntirePageScreenshot(screenshotsPath + filename,"args");
	    }
	    catch(Exception e){
	    	lastScreenshotFilename = "";
	    	event.exceptionMessage = e.toString();
	    	closeEventError(event);
	    	return Constants.RET_ERROR_STRING;
	    };
	    lastScreenshotFilename = filename; 

	    closeEventOk(event);
	    return filename;
	}
	
	protected String getScreenshot() {            
	    return getScreenshot(false);
	}	

	// TODO this function can miss exception
	protected String getTitle() {
		String tempString = "";
		Event event = startEvent("getTitle", name);
		tempString = seleniumInstance.getTitle();
		event.setRealValue(tempString);
		closeEventOk(event);
	    return tempString;
	}	
	
	// TODO this function should be optimized
	/**
	 * Before looking for errorMessage string, Constants.GENERAL_PAGE_ERROR string presence will be checked. 
	 * @param errorMessage string which should be present on page
	 * @return true if both strings are present and visible, false otherwise.
	 */
	public boolean isErrorPresent (String errorMessage){
		Event event = startEvent("isErrorPresent", errorMessage);

		Boolean isPresent = false; 
		String tempLocator;
		tempLocator = "//*[contains(text(),'"+Constants.GENERAL_PAGE_ERROR+"')]";
		if (isElementPresent(tempLocator)&&
				seleniumInstance.isVisible(tempLocator)){
					tempLocator = "//*[contains(text(),'"+errorMessage+"')]";
					if (isElementPresent(tempLocator)&&
							seleniumInstance.isVisible(tempLocator)){
						isPresent = true;
					}
		}
		event.setRealValue(isPresent.toString());
		closeEventOk(event);
		return isPresent;
	}
	
	/**
	 * @param condition Condition on JavaScript, which will be executed on browser side.
	 * @param timeout Max time to wait for condition.
	 * @return true if condition carried out, false otherwise.
	 */
	public boolean waitForCondition(String condition, String timeout){
		Event event = startEvent("waitForCondition", condition);
		try {
			seleniumInstance.waitForCondition(condition, timeout);
		} catch (Exception e){
			getScreenshot();
			event.exceptionMessage = e.toString();
			closeEventError(event);
			return false;
		}
		getScreenshot();
		closeEventOk(event);
		return true;
	}

// TODO I think it's duplicated code (see utils?)	
	/**
	 * Prepares a JavaScript code which checks if all elements are present on page, and then use it to wait till elements load.    
	 * @param locators List of locators of elements which will be waited on page
	 * @param timeout Max time to wait elements
	 * @return true if all elements loaded. 
	 */
	public boolean waitForListOfElements(ArrayList <String> locators, String timeout){
		Boolean result = false;

		String tempScript = "var result = false; ";
		if (locators.size()>0) {
			tempScript = tempScript+ "result = selenium.isElementPresent('"+locators.get(0)+"');";
		}
		for (int i=1; i<locators.size(); i++){
			tempScript = tempScript+ "result = result && selenium.isElementPresent('"+locators.get(i)+"');";
		}
		
		Event event = startEvent("waitForListOfElements", tempScript);
		
		result = waitForCondition(tempScript, timeout);

		event.setRealValue(result.toString());
		closeEventOk(event);
		return result;
	}
	
//**************            FUNCTIONS WICH DO ACTIONS WITH BROWSER   *********************//	
	
	/**
	 * Do logout from Salesforce sequence.
	 */
	protected int logout() {
			Event event = startEvent("logout", name);
			String tempLocator = Constants.LOGOUT_LOCATOR;
			
	        info("Logout started");
			if (click(tempLocator)==Constants.RET_ERROR){
				closeEventError(event);
				return Constants.RET_ERROR;				
			}
			waitForPageToLoad();
			getTitle();
	        closeEventOk(event);
	        return Constants.RET_OK;
	    }

	protected int openTab(String tabName) {            
			Event event = startEvent("openTab", tabName);
			String tempLocator = "link="+tabName;
		
			if (click(tempLocator)==Constants.RET_ERROR){
				closeEventError(event);
				return Constants.RET_ERROR;	
			}
			waitForPageToLoad();
			getTitle();
	        closeEventOk(event);
	        return Constants.RET_OK;
	    }    
	
	protected int pressButton(String a_locator) {            
		Event event = startEvent("pressButton", a_locator);
		
		if (click(a_locator)==Constants.RET_ERROR){
			closeEventError(event);
			return Constants.RET_ERROR;
		}
		waitForPageToLoad();
	    closeEventOk(event);
	    return Constants.RET_OK;
	}   
	
	/**
	 * Checks for record presence in application. 
	 * @param tabName corresponding to type of record tab 
	 * @param recordId record identifier
	 * @return true if record is present.
	 */
	protected boolean isRecordPresence(String tabName, String recordId) {
		Event event = startEvent("checkRecordPresence", recordId);
	    String tempLocator;
	    Boolean result = false;
	    
	    openTab(tabName);
	
	    tempLocator = "//a[contains(text(),'"+recordId+"')]";
	    
	    result = isElementPresent(tempLocator);
	    
	    event.setRealValue(result.toString());
	    closeEventOk(event);
	    
	    return result;
	}
	
	/**
	 * Sequence to create new record of stated type. After executing New record edit page is opened.
	 * @param tabName corresponding to type of record tab
	 * @return RET_OK if record created successfully.
	 */
	public int createNewEmptyRecord(String tabName){
		Event event = startEvent("createNewEmptyRecord", tabName);
		String tempLocator =  "//input[@name='new']";
		
		openTab(tabName);

		if (click(tempLocator)==Constants.RET_ERROR){
			closeEventError(event);
			return Constants.RET_ERROR;				
		}	 		
		
		waitForPageToLoad() ;
	    
		closeEventOk(event);
	    return Constants.RET_OK;
	}
	
	/**
	 * Sequence to create new record of stated type. After executing New record edit page is opened.
	 * Function waits for condition fulfillment, not for entire page loaded. 
	 * @param tabName corresponding to type of record tab
	 * @param condition javaScript code which determines key elements presence
	 * @return RET_OK if record created successfully.
	 */
	public int createNewEmptyRecordFast(String tabName, String condition){
		Event event = startEvent("createNewEmptyRecordFast", tabName);

		String tempLocator =  "//input[@name='new']";
		
		openTab(tabName);

		if (click(tempLocator)==Constants.RET_ERROR){
			closeEventError(event);
			return Constants.RET_ERROR;				
		}	 		
		
        if (!waitForCondition(condition, Settings.TIMEOUT)){
			closeEventError(event);
			return Constants.RET_ERROR;	
        }		
	    
		closeEventOk(event);
	    return Constants.RET_OK;
	}	

	/**
	 * Sequence to delete stated record.
	 * @param tabName corresponding to type of record tab 
	 * @param recordId record identifier
	 * @return RET_OK if record was deleted without problems, RET_ERROR if some problems were. 
	 * Note that success of deletion is determined as absence of record with the recordId after operation.
	 * So if there more than one record with same recordId, function returns RET_ERROR too.
	 */
	protected int deleteRecord(String tabName, String recordId) {
		Event event = startEvent("deleteRecord", recordId);
    	String tempLocator;

	    openTab(tabName);
	    
	    tempLocator = "//a[contains(text(),'"+recordId+"')]";
	    
	    if (isElementPresent(tempLocator)){
			if (click(tempLocator)==Constants.RET_ERROR){
				closeEventError(event);
				return Constants.RET_ERROR;				
			}	 	        
	    	waitForPageToLoad();
	
	    	pressDelete();
	    	
	    	if (!isRecordPresence(tabName, recordId)){
	    		closeEventOk(event);
	    		return Constants.RET_OK;
	    	}else{
	    			event.advice="(may be record was present before test started)";
	    			closeEventError(event);
	    			return Constants.RET_ERROR;
	   		}
	    }
	    else {
	    	closeEventError(event);
	    	return Constants.RET_ERROR;
	    }
	}
	
	/**
	 * Sequence to press Delete button on Edit record page. It uses clickWithConfirmation method.
	 */
	protected int pressDelete() {
		String tempLocator = Constants.DELETE_LOCATOR;

		Event event = startEvent("pressDelete", tempLocator);		
		
		if (clickWithConfirmation(tempLocator)==Constants.RET_ERROR){
			closeEventError(event);
			return Constants.RET_ERROR;		
		}

		waitForPageToLoad();	    
	    closeEventOk(event);
	   return Constants.RET_OK;
	}		
	
//**************            LOG FUNCTIONS     *********************//	
	public void info (String message){
			if (!Settings.LOG_INFOS) return;
			ut.info(message);
	        Reporter.log(message);
	    }
	
	public void warn (String message){
	        ut.warn(message);
	        Reporter.log(message);
	    }
	    
	public void error (String message){
			ut.error(message);
			Reporter.log(message);
	    }
	
	public void fatal (String message){
        ut.fatal(message);
        Reporter.log("(FATAL)"+message);
    }	
	
	public void infoV (String message){
		if (!Settings.LOG_VERBOSE) return;
	        ut.info("(V)"+message);
	        Reporter.log("(V)"+message);
	}

//**************            FUNCTIONS WITH EVENTS AND EVENTS LIST     *********************//
	
	/**
	 * Creates a new Event object with given parameters and adds it to event list. 
	 * Fills beforeScreenshot and id field of it  
	 * @param eventName event name, which is usually chosen as method name in which event is created 
	 * @param target definite object or element on which influence will be done due event, or name of method's class   
	 * @return Link to just created and added to list event. It's not the only way to get access to the event. 
	 * Access can be realized by events array scanning.
	 */
	public Event startEvent(String eventName, String target)
	{
		Event tempEvent = new Event(++lastEventId, eventName, target);
		events.add(tempEvent);
		tempEvent.beforeScreenshot = lastScreenshotFilename;
		logEvent (tempEvent);
		return tempEvent;
	}
	
	/**
	 * Creates a new Event object with given parameters and adds it to event list.
	 * Before creations does screenshot.
	 * @deprecated
	 * @param eventName event name, which is usually chosen as method name in which event is created  
	 * @param target definite object or element on which influence will be done due event, or name of method's class   
	 * @return Link to just created and added to list event.
	 */
	public Event startEventS(String eventName, String target)
	{
		getScreenshot();
		return startEvent(eventName, target);
	}
	
	/**
	 * Make event closed.
	 * @param a_event event to be closed
	 * @param a_logLevel error level of closed event
	 * @param a_message description of event closing
	 */
	public void closeEvent(Event a_event, int a_logLevel, String a_message){
		a_event.close(a_logLevel, a_message, lastScreenshotFilename, lastEventId);
		logEvent (a_event);
	}
	
	public void closeEventOk(Event a_event){
		closeEvent(a_event, Constants.OK, "");
	}

	public void closeEventWarn(Event a_event){
		closeEvent(a_event, Constants.WARN, "");
	}		

	public void closeEventError(Event a_event){
		closeEvent(a_event, Constants.ERROR, "");
	}
	
	public void closeEventError(Event a_event, String a_message){
		closeEvent(a_event, Constants.ERROR, a_message);
	}	
	
	public void closeEventFatal(Event a_event){
		closeEvent(a_event, Constants.FATAL, "");
	}
	
	public void closeEventFatal(Event a_event, String a_message){
		closeEvent(a_event, Constants.FATAL, a_message);
	}	

	// action.closeEventOk(event);
	
	/**
	 * Prints event to console if logging of it's log level is switched on. 
	 * @param a_event event to print
	 */
	public void logEvent(Event a_event){
		switch( a_event.logLevel )
	    {
	      case Constants.STARTED: infoV(a_event.toConsole()); break;
	      case Constants.OK: info(a_event.toConsole()); break;
	      case Constants.INFOV: infoV(a_event.toConsole()); break;
	      case Constants.INFO: info(a_event.toConsole()); break;
	      case Constants.WARN: warn(a_event.toConsole()); break;
	      case Constants.ERROR: error(a_event.toConsole()); break;
	      case Constants.FATAL: fatal(a_event.toConsole()); break;
	    }
	}
	
	/**
	 * Generates simple report to console. Prints only events with certain log level. 
	 */
	public void generateReport(){
		Event tempEvent;
		
		ut.warn("			Report:			");
		for (int i=0; i<events.size(); i++){
			tempEvent = events.get(i);
			if (tempEvent.logLevel>=Settings.LOG_LEVEL)
				logEvent(tempEvent);
		}
	}	
	
	/**
	 * Prepares HTML report of all events in events list.
	 * @param a_name name of HTML report file
	 */
	public void eventsToHtml(String a_name){
		eventProc.eventsToHtml(a_name);
	}

//**************            OTHER FUNCTIONS            *********************//	
	
}
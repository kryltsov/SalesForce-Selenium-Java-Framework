// TODO in low-level functions fill event field Target with locators

package com.deepshiftlabs.sf_tests;

import org.testng.Reporter;

import java.util.ArrayList;

/*import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;*/

import com.thoughtworks.selenium.*;

/**
 *
 * @author Bear, Jan 15, 2009
 * TODO Now CommonActions can have several instances in runtime. It's not optimal, may be it should be something like singleton * 
 */

public class CommonActions {

    Utils ut = new Utils();
    ArrayList <Event> events = new ArrayList <Event>();
    EventProcessor eventProc = new EventProcessor();
    DefaultSelenium seleniumInstance;
    String name = "Selenium driver";
    String screenshotsPath = "none";
    String lastScreenshotFilename = "";
    
    public void init (){
    	eventProc.init(events);
		Utils.prepareDir(Settings.REPORT_PATH);
		screenshotsPath = Utils.prepareDir(Settings.SCREENSHOTS_PATH);
    }

//**************            FUNCTIONS WICH USE SELENIUM DRIVER    *********************//
    
    protected int startSelenium(String seleniumHost, int seleniumPort, String browser, String webSite){
        seleniumInstance = new DefaultSelenium(seleniumHost, seleniumPort, browser, webSite);
        Event event = startEvent("startSelenium", seleniumHost+":"+seleniumPort+", "+ browser+":"+webSite);
        try {
        	seleniumInstance.start();
        } 
        catch (Exception e){
        	seleniumInstance = null;

        	event.exceptionMessage = e.toString();
        	event.advice = "check if Hub or RC with requested environment is present";
        	closeEventFatal(event, "can't start selenium driver");
        	
        	return Constants.RET_ERROR;
        };
        
    	seleniumInstance.setTimeout(Settings.TIMEOUT);

    	event.realValue = seleniumInstance.toString();
    	closeEventOk(event);
    	return Constants.RET_OK;
   }

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
        	closeEventError(event);
        	return Constants.RET_ERROR;
        };    	
        closeEventOk(event);
    	return Constants.RET_OK;
    }

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
				event.advice = "check your login and password";
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
	
	protected String readText(String a_locator) {
		Event event = startEventS("readText", a_locator);

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
	
	protected int pressDelete() {
		String tempLocator = Constants.DELETE_LOCATOR;

		Event event = startEvent("pressDelete", tempLocator);		
		
		seleniumInstance.chooseOkOnNextConfirmation();
		if (click(tempLocator)==Constants.RET_ERROR){
			closeEventError(event);
			return Constants.RET_ERROR;		
		}

//      if will be necessary to use next line, should use Exceptions 		
//		seleniumInstance.getConfirmation();
		
		waitForPageToLoad();	    
	    closeEventOk(event);
	   return Constants.RET_OK;
	}	
	
	protected int typeText(String a_locator, String a_text) {            
		Event event = startEventS("typeText", a_locator);
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
	
	protected int click(String a_locator) {   
		Event event = startEventS("click", a_locator);
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
	
	protected String getScreenshot(boolean isError) {            
//		if (!Settings.USE_SCREENSHOTS) return;
		String filename;
		filename = Utils.generateScreenshotName(isError);
		
		Event event = startEvent("getScreenshot", filename);
		
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
	
	public boolean waitForCondition(String condition, String timeout){
		Event event = startEventS("waitForCondition", condition);
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
	    			event.advice="may be record was present before test started";
	    			closeEventError(event);
	    			return Constants.RET_ERROR;
	   		}
	    }
	    else {
	    	closeEventError(event);
	    	return Constants.RET_ERROR;
	    }
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
	
	public Event startEvent(String eventName, String target)
	{
		Event tempEvent = new Event(eventName, target);
		events.add(tempEvent);
		tempEvent.beforeScreenshot = lastScreenshotFilename;
		logEvent (tempEvent);
		return tempEvent;
	}
	
	public Event startEventS(String eventName, String target)
	{
		getScreenshot();
		return startEvent(eventName, target);
	}
	
	public void closeEvent(Event a_event, int a_logLevel, String a_message){
		a_event.close(a_logLevel, a_message, lastScreenshotFilename);
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
	
	public void eventsToHtml(String a_name){
		eventProc.eventsToHtml(a_name);
	}

//**************            OTHER FUNCTIONS            *********************//	
	public void generateReport(){
		Event tempEvent;
		
		ut.warn("			Report:			");
		for (int i=0; i<events.size(); i++){
			tempEvent = events.get(i);
			if (tempEvent.logLevel>=Settings.LOG_LEVEL)
				logEvent(tempEvent);
		}
	}	

}
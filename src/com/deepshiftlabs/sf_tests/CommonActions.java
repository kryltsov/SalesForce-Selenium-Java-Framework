// TODO in low-level functions fill event field Target with locators

package com.deepshiftlabs.sf_tests;

import org.testng.Reporter;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import java.util.ArrayList;
import java.util.Date;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.*;



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
    String name = "actions";
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
        Event event = startEvent(name, "startSelenium");
        try {
        	seleniumInstance.start();
        } 
        catch (Exception e){
        	seleniumInstance = null;
        	fatal("Can't establish connection to Selenium Hub. Check if Hub or RC with requested environment is present.");
        	closeEventFatal(event, "Can't establish connection to Selenium Hub. Check if Hub or RC with requested environment is present.");
        	return Constants.RET_ERROR;
        };
        
    	seleniumInstance.setTimeout(Settings.TIMEOUT);
    	closeEventOk(event);
    	return Constants.RET_OK;
   }

    public void freeSelenium() {
    	Event event = startEvent(name, "freeSelenium");
    	if (seleniumInstance!=null){
	        seleniumInstance.stop();
	        seleniumInstance = null;
	        warn("--------SESSION ENDED--------");
    	}
    	closeEventOk(event);
    }
    
    public int open (String url){
    	Event event = startEvent(name, "open");
    	infoV("Opening url _"+url+"_");
        try {
        	seleniumInstance.open(url);
        } 
        catch (Exception e){
        	error("Error while opening url. Exception is: _"+e+"_");
        	closeEventError(event);
        	return Constants.RET_ERROR;
        };    	
        closeEventOk(event);
    	return Constants.RET_OK;
    }

	protected int login(String a_login, String a_password) {
			Event event = startEvent(name, "login");
			ArrayList <String> locatorsList = new ArrayList <String>();		
			String tempLocator = Constants.LOGIN_LOCATOR;
			String title = "";
	        locatorsList.add("username");
	        locatorsList.add("password");
			
	        info("Login started");

	        seleniumInstance.setTimeout(Constants.MIN_SELENIUM_TIMEOUT);
	        open("/");
	        seleniumInstance.setTimeout(Settings.TIMEOUT);
	        
	        if (waitForListOfElements(locatorsList, Settings.TIMEOUT)){
	        	infoV("Inputs for login found before page load.");
	        }
	        else
	        {
				fatal("Can't login - can't found inputs!");
				closeEventFatal(event, "cant found inputs");
				return Constants.RET_ERROR;
	        }
	        
	        typeText("username",a_login);
	        typeText("password",a_password);
	        
	        
			if (click(tempLocator)==Constants.RET_ERROR){
				fatal("Can't login - cant click on locator _"+tempLocator+"_ ");
				closeEventFatal(event, "cant click on locator _"+tempLocator+"_ ");
				return Constants.RET_ERROR;				
			}
			
	        waitForPageToLoad();
	        
			if (isTextPresent(Constants.LOGIN_FAILED_ERROR))
			{
				fatal("Can't login - may be your login or password are incorrect.");
				closeEventFatal(event, "cant click on locator _"+tempLocator+"_ ");
				return Constants.RET_ERROR;				
			}
			
			title = getTitle();
			
			if (!title.equals(Constants.HOME_PAGE_TITLE))
			{
				fatal("Can't login - invalid home page title. See screenshot for details.");
				closeEventFatal(event, "invalid home page title");
				return Constants.RET_ERROR;				
			}				
			
	        info("Login done: logged to "+ title);
	        closeEventOk(event);
	        return Constants.RET_OK;
	    }

	protected int waitForPageToLoad(){
		Event event = startEvent(name, "waitForPageToLoad");
		try {
			seleniumInstance.waitForPageToLoad(Settings.TIMEOUT);
		}
		catch (Exception e){
			error("ERROR while waiting for page to load!");
			getScreenshot(true);
			closeEventError(event);
			return Constants.RET_ERROR;
		}
		closeEventOk(event);
		return Constants.RET_OK;
	}
	
	protected String readText(String a_locator) {
		Event event = startEventS(name, "readText");
		event.setTargetName(a_locator);
		String tempString;
		
		try {
			tempString = seleniumInstance.getText(a_locator);
		}
		catch (Exception e){
			error("ERROR while reading text on locator _"+a_locator+"_, check if element is present.");
			closeEventError(event);
			return Constants.RET_ERROR_STRING;
		}
		closeEventOk(event);
		return tempString;
	}  	
	
	protected int pressDelete() {
		String tempLocator = Constants.DELETE_LOCATOR;

		Event event = startEvent(name, "pressDelete");		
		event.setTargetName(tempLocator);
		
	    infoV("Pressing delete started");
		seleniumInstance.chooseOkOnNextConfirmation();
		if (click(tempLocator)==Constants.RET_ERROR){
			error("Can't click on button delete, locator _"+tempLocator+"_ , check button presence");
			closeEventError(event);
			return Constants.RET_ERROR;		
		}
// TODO if will be necessary to use next line, should use Exceptions 		
//		seleniumInstance.getConfirmation();
		waitForPageToLoad();	    
	    info("Delete pressed OK");
	    closeEventOk(event);
	   return Constants.RET_OK;
	}	
	
	protected int typeText(String a_locator, String a_text) {            
		Event event = startEventS(name, "typeText");
		event.setTargetName(a_locator);
		event.setValue(a_text);
		
		try {
			seleniumInstance.type(a_locator, a_text);
		}
		catch (Exception e){
			error("ERROR while typing text on locator _"+a_locator+"_, check if element is present.");
			getScreenshot(true);
			closeEventError(event);			
			return Constants.RET_ERROR;
		}
		getScreenshot();
		closeEventOk(event);
		return Constants.RET_OK;
	}
	
	protected String readValue(String a_locator) {  
		Event event = startEvent(name, "readValue");
		event.setTargetName(a_locator);
		String tempString;
		
		try {
			tempString = seleniumInstance.getValue(a_locator);
		}
		catch (Exception e){
			error("ERROR while reading value on locator _"+a_locator+"_, check if element is present.");
			closeEventError(event);
			return Constants.RET_ERROR_STRING;
		}
		event.setRealValue(tempString);
		closeEventOk(event);
		return tempString;
	}  	
	
	protected boolean isElementPresent(String a_locator) {
		Event event = startEvent(name, "isElementPresent");
		event.setTargetName(a_locator);
		boolean result = false;
		
		try {
			result = seleniumInstance.isElementPresent(a_locator);
		}
		catch (Exception e){
			error("ERROR while doing isElementPresent on locator _"+a_locator+"_, check if element is present.");
			closeEventError(event);
		}
		closeEventOk(event);
		return result;
	}  
	
	protected boolean isTextPresent(String a_text) {
		Event event = startEvent(name, "isTextPresent");
		event.setTargetName(a_text);
		boolean result = false;
		
		try {
			result = seleniumInstance.isTextPresent(a_text);
		}
		catch (Exception e){
			closeEventError(event);
			error("ERROR while doing isTextPresent on text _"+a_text+"_, check if text is present.");
		}
		closeEventOk(event);
		return result;
	}  	
	
	protected int click(String a_locator) {   
		Event event = startEventS(name, "click");
		event.setTargetName(a_locator);
		try {
			if (!isElementPresent(a_locator)){
				error("ERROR while click on locator _"+a_locator+"_, element is not present.");
				closeEventError(event);
				return Constants.RET_ERROR;
			}
			
			seleniumInstance.click(a_locator);
		}
		catch (Exception e){
			error("ERROR while clicking on locator _"+a_locator+"_");
			closeEventError(event);
			return Constants.RET_ERROR;
		}
		getScreenshot();
		closeEventOk(event);
		return Constants.RET_OK;
	}  	
	
	protected String getScreenshot(boolean isError) {            
		Event event = startEvent(name, "getScreenshot");
//		if (!Settings.USE_SCREENSHOTS) return;
		String filename;
		
		filename = Utils.generateScreenshotName(isError);
	    info("Getting screenshot to file "+filename);
	    try{
	    	seleniumInstance.captureEntirePageScreenshot(screenshotsPath + filename,"args");
	    }
	    catch(Exception e){
	    	error("ERROR while getting screenshot to file _"+filename+"_, check if directory is present.");
	    	lastScreenshotFilename = "";
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
	
	protected String getTitle() {            
		Event event = startEvent(name, "getTitle");
		closeEventOk(event);
	    return seleniumInstance.getTitle();
	}	
	
	// TODO this function should be optimized
	public boolean isErrorPresent (String errorMessage){
		Event event = startEvent(name, "isErrorPresent");
		event.setTargetName(errorMessage);
		boolean isPresent = false; 
		String tempLocator;
		tempLocator = "//*[contains(text(),'"+Constants.GENERAL_PAGE_ERROR+"')]";
		if (isElementPresent(tempLocator)&&
				seleniumInstance.isVisible(tempLocator)){
					tempLocator = "//*[contains(text(),'"+errorMessage+"')]";
					if (isElementPresent(tempLocator)&&
							seleniumInstance.isVisible(tempLocator))
						isPresent = true;
		}
		closeEventOk(event);
		return isPresent;
	}
	
	public boolean waitForListOfElements(ArrayList <String> locators, String timeout){
		Event event = startEventS(name, "waitForListOfElements");
		
		String tempScript = "var result = false; ";
		infoV("Waiting for list of elements started");
		if (locators.size()>0) {
			tempScript = tempScript+ "result = selenium.isElementPresent('"+locators.get(0)+"');";
		}
		for (int i=1; i<locators.size(); i++){
			tempScript = tempScript+ "result = result && selenium.isElementPresent('"+locators.get(i)+"');";
		}
		
		event.setTargetName(tempScript);
		try {
			seleniumInstance.waitForCondition(tempScript, timeout);
		} catch (Exception E){
			getScreenshot(true);
			closeEventError(event);
			return false;
		}
		getScreenshot();
		closeEventOk(event);
		return true;
	}
	
	public boolean waitForCondition(String condition, String timeout){
		Event event = startEventS(name, "waitForCondition");
		event.setTargetName(condition);
		try {
			seleniumInstance.waitForCondition(condition, timeout);
		} catch (Exception E){
			closeEventError(event);
			return false;
		}
		getScreenshot();
		closeEventOk(event);
		return true;
	}		
	
//**************            FUNCTIONS WICH DO ACTIONS WITH BROWSER   *********************//	
	protected int logout() {
			Event event = startEvent(name, "logout");
			String tempLocator = Constants.LOGOUT_LOCATOR;
			
	        info("Logout started");
			if (click(tempLocator)==Constants.RET_ERROR){
				error("Can't logout - cant click on locator _"+tempLocator+"_ ");
				closeEventError(event);
				return Constants.RET_ERROR;				
			}
			waitForPageToLoad();
	        info("Logout done: logged to "+ getTitle());
	        closeEventOk(event);
	        return Constants.RET_OK;
	    }

	protected int openTab(String tabName) {            
			Event event = startEvent(name, "openTab");
			String tempLocator = "link="+tabName;
		
	        infoV("Tab opening started");
			if (click(tempLocator)==Constants.RET_ERROR){
				error("Can't click on tab locator _"+tempLocator+"_ ");
				closeEventError(event);
				return Constants.RET_ERROR;	
			}
			waitForPageToLoad();
	        infoV("Tab opened: title is "+ getTitle());
	        closeEventOk(event);
	        return Constants.RET_OK;
	    }    
	
	protected int pressButton(String a_locator) {            
		Event event = startEvent(name, "pressButton");
		event.setTargetName(a_locator);
		
		infoV("Pressing button with locator _"+a_locator+"_ started");
		if (click(a_locator)==Constants.RET_ERROR){
			error("Can't click on button locator _"+a_locator+"_ , check button presence");
			closeEventError(event);
			return Constants.RET_ERROR;
		}
		waitForPageToLoad();
	    info("Pressed button with locator _"+a_locator+"_ (OK)");
	    closeEventOk(event);
	    return Constants.RET_OK;
	}   
	
	protected int checkRecordPresence(String tabName, String recordId) {
		Event event = startEvent(name, "checkRecordPresence");
	    event.setTargetName(recordId);
	    String tempLocator;
	    
	    openTab(tabName);
	
	    tempLocator = "//a[contains(text(),'"+recordId+"')]";
	    
	    if (isElementPresent(tempLocator)){
	        infoV("--------RECORD _"+tabName+":"+recordId+"_ FOUND");    	
	        closeEventError(event);
	    	return Constants.RET_OK;
	    }
	    else {
	    	infoV("--------RECORD _"+tabName+":"+recordId+" NOT FOUND");
	    	closeEventOk(event);
	    	return Constants.RET_ERROR;
	    }
	}
	
	public int createNewEmptyRecord(String tabName){
		Event event = startEvent(name, "createNewEmptyRecord");
	    event.setTargetName(tabName);		
		String tempLocator =  "//input[@name='new']";
		
		openTab(tabName);

		if (click(tempLocator)==Constants.RET_ERROR){
			error("Can't createNewEmptyRecord - cant click on New, locator _"+tempLocator+"_ ");
			closeEventError(event);
			return Constants.RET_ERROR;				
		}	 		
		
		waitForPageToLoad() ;
	    
		info("New record on tab _"+tabName+"_ created, waiting for input.");
		closeEventOk(event);
	    return Constants.RET_OK;
	}
	
	public int createNewEmptyRecordFast(String tabName, String condition){
		Event event = startEvent(name, "createNewEmptyRecordFast");
		event.setTargetName(tabName);
		
		String tempLocator =  "//input[@name='new']";
		
		openTab(tabName);

		if (click(tempLocator)==Constants.RET_ERROR){
			error("Can't createNewEmptyRecord - cant click on New, locator _"+tempLocator+"_ ");
			closeEventError(event);
			return Constants.RET_ERROR;				
		}	 		
		
        if (waitForCondition(condition, Settings.TIMEOUT)){
        	infoV("All inputs on new record page found before page load.");
        }
        else
        {
			fatal("Can't createNewEmptyRecordFast  - cant found all locators!");
			closeEventError(event);
			return Constants.RET_ERROR;	
        }		
	    
		info("New record on tab _"+tabName+"_ created, waiting for input.");
		closeEventOk(event);
	    return Constants.RET_OK;
	}	

	protected int deleteRecord(String tabName, String recordId) {
		Event event = startEvent(name, "deleteRecord");
		event.setTargetName(recordId);
    	String tempLocator;

	    openTab(tabName);
	    
	    tempLocator = "//a[contains(text(),'"+recordId+"')]";
	    
	    if (isElementPresent(tempLocator)){
	        infoV("--------RECORD _"+tabName+":"+recordId+"_ FOUND");
			if (click(tempLocator)==Constants.RET_ERROR){
				error("Can't createNewEmptyRecord - cant click on record, locator _"+tempLocator+"_ ");
				closeEventError(event);
				return Constants.RET_ERROR;				
			}	 	        
	    	waitForPageToLoad();
	
	    	pressDelete();
	    	
	    	if (checkRecordPresence(tabName, recordId)==Constants.RET_ERROR){
	    		info("--------RECORD _"+tabName+":"+recordId+" DELETED");
	    		closeEventOk(event);
	    		return Constants.RET_OK;
	    	}else{
	    			error("--------RECORD _"+tabName+":"+recordId+" CAN'T BE DELETED (may be record was present before test started)");    		
	    			closeEventError(event);
	    			return Constants.RET_ERROR;
	   		}
	    }
	
	    else {
	    	error("--------RECORD _"+tabName+":"+recordId+" NOT FOUND FOR REMOVAL");
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
	
	public Event startEvent(String eventName, String elementName)
	{
		Event tempEvent = new Event(eventName, elementName);
		events.add(tempEvent);
		tempEvent.beforeScreenshot = lastScreenshotFilename;
		return tempEvent;
	}
	
	public Event startEventS(String eventName, String elementName)
	{
		getScreenshot();
		return startEvent(eventName, elementName);
	}
	
	public void closeEvent(Event a_event, int a_logLevel, String a_message){
		a_event.close(a_logLevel, a_message, lastScreenshotFilename);
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
		switch( a_event.type )
	    {
	      case 0: info(a_event.toString()); break;
	      case 1: info(a_event.toString()); break;
	      case 2: info(a_event.toString()); break;
	      case 3: warn(a_event.toString()); break;
	      case 4: error(a_event.toString()); break;
	      case 5: fatal(a_event.toString()); break;
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
			if (tempEvent.type>=Settings.LOG_LEVEL)
				logEvent(tempEvent);
		}
	}	

}
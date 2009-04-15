 
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



import com.thoughtworks.selenium.*;

/**
 *
 * @author Bear, Jan 15, 2009
 * TODO Now commonActions can have several instances in runtime. It's not optimal, may be it should be something like singleton * 
 */

public class commonActions {
  
    utils ut = new utils();

    protected DefaultSelenium getSelenium(String seleniumHost, int seleniumPort, String browser, String webSite){
        DefaultSelenium seleniumInstance = new DefaultSelenium(seleniumHost, seleniumPort, browser, webSite);
        
        try {
        	seleniumInstance.start();
        	warn("--------SESSION STARTED--------");        	
        } 
        catch (Exception e){
        	fatal("Can't establish connection to Selenium Hub. Check if Hub or RC with requested environment is present.");
        	return null;
        };
        
    	seleniumInstance.setTimeout(settings.TIMEOUT);        
        warn("--------BROWSER STARTED--------");
        return seleniumInstance;
   }

    public void freeSelenium(DefaultSelenium seleniumInstance) {
        seleniumInstance.stop();
        warn("--------BROWSER CLOSED--------");                
        seleniumInstance = null;
        warn("--------SESSION ENDED--------");        
    }
    
    public int open (DefaultSelenium seleniumInstance, String url){
    	infoV("Opening url _"+url+"_");
        try {
    	seleniumInstance.open(url);
        } 
        catch (Exception e){
        	error("Error while opening url. Exception is: _"+e+"_");
        	return constants.RET_ERROR;
        };    	
    	return constants.RET_OK;
    }

	protected int login(DefaultSelenium seleniumInstance, String a_login, String a_password) {
			ArrayList <String> locatorsList = new ArrayList <String>();		
			String tempLocator = constants.LOGIN_LOCATOR;
			String title = "";
	        locatorsList.add("username");
	        locatorsList.add("password");
			
	        info("Login started");

	        seleniumInstance.setTimeout(constants.MIN_SELENIUM_TIMEOUT);
	        open(seleniumInstance, "/");
	        seleniumInstance.setTimeout(settings.TIMEOUT);
	        
	        if (waitForListOfElements(seleniumInstance, locatorsList, settings.TIMEOUT)){
	        	infoV("Inputs for login found before page load.");
	        }
	        else
	        {
				fatal("Can't login - cant found inputs!");
				return constants.RET_ERROR;	
	        }
	        
	        typeText(seleniumInstance, "username",a_login);
	        typeText(seleniumInstance, "password",a_password);
	        
	        
			if (click(seleniumInstance, tempLocator)==constants.RET_ERROR){
				fatal("Can't login - cant click on locator _"+tempLocator+"_ ");
				return constants.RET_ERROR;				
			}
			
	        seleniumInstance.waitForPageToLoad(settings.TIMEOUT);
	        
			if (isTextPresent(seleniumInstance, constants.LOGIN_FAILED_ERROR))
			{
				fatal("Can't login - may be your login or password are incorrect.");
				getScreenshot(seleniumInstance, true);
				return constants.RET_ERROR;				
			}
			
			title = seleniumInstance.getTitle();
			
			if (!title.equals(constants.HOME_PAGE_TITLE))
			{
				fatal("Can't login - invalid home page title. See screenshot for details.");
				getScreenshot(seleniumInstance, true);
				return constants.RET_ERROR;				
			}				
			
	        info("Login done: logged to "+ title);
	        return constants.RET_OK;
	    }
	    
	protected int logout(DefaultSelenium seleniumInstance) {            
			String tempLocator = constants.LOGOUT_LOCATOR;
			
	        info("Logout started");
			if (click(seleniumInstance, tempLocator)==constants.RET_ERROR){
				error("Can't logout - cant click on locator _"+tempLocator+"_ ");
				return constants.RET_ERROR;				
			}
			seleniumInstance.waitForPageToLoad(settings.TIMEOUT);
	        info("Logout done: logged to "+ seleniumInstance.getTitle());
	        return constants.RET_OK;
	    }
	
	protected int openTab(DefaultSelenium seleniumInstance, String tabName) {            
			String tempLocator = "link="+tabName;
		
	        infoV("Tab opening started");
			if (click(seleniumInstance, tempLocator)==constants.RET_ERROR){
				error("Can't click on tab locator _"+tempLocator+"_ ");
				return constants.RET_ERROR;	
			}
			seleniumInstance.waitForPageToLoad(settings.TIMEOUT);
	        infoV("Tab opened: title is "+ seleniumInstance.getTitle());
	        return constants.RET_OK;
	    }    
	
	protected int pressButton(DefaultSelenium seleniumInstance, String a_locator) {            

		infoV("Pressing button with locator _"+a_locator+"_ started");
		if (click(seleniumInstance, a_locator)==constants.RET_ERROR){
			error("Can't click on button locator _"+a_locator+"_ , check button presence");
			getScreenshot(seleniumInstance, true);
			return constants.RET_ERROR;
		}
		seleniumInstance.waitForPageToLoad(settings.TIMEOUT);
	    info("Pressed button with locator _"+a_locator+"_ (OK)");
	    return constants.RET_OK;
	}   
	
	protected int pressDelete(DefaultSelenium seleniumInstance) {            
		String tempLocator = constants.DELETE_LOCATOR;
		
	    infoV("Pressing delete started");
		seleniumInstance.chooseOkOnNextConfirmation();
		if (click(seleniumInstance, tempLocator)==constants.RET_ERROR){
			error("Can't click on button delete, locator _"+tempLocator+"_ , check button presence");
			getScreenshot(seleniumInstance, true);
			return constants.RET_ERROR;		
		}
		seleniumInstance.getConfirmation();
		seleniumInstance.waitForPageToLoad(settings.TIMEOUT);	    
	    info("Delete pressed OK");
	   return constants.RET_OK;
	}	
	
	protected String readText(DefaultSelenium seleniumInstance, String a_locator) {            
		String tempString;
		
		try {
			tempString = seleniumInstance.getText(a_locator);
		}
		catch (Exception e){
			error("ERROR while reading text on locator _"+a_locator+"_, check if element is present.");
			getScreenshot(seleniumInstance, true);
			return constants.RET_ERROR_STRING;
		}
		return tempString;
	}  	
	
	protected int typeText(DefaultSelenium seleniumInstance, String a_locator, String a_text) {            
		
		try {
			seleniumInstance.type(a_locator, a_text);
		}
		catch (Exception e){
			error("ERROR while typing text on locator _"+a_locator+"_, check if element is present.");
			getScreenshot(seleniumInstance, true);
			return constants.RET_ERROR;
		}
		return constants.RET_OK;
	}
	
	protected String readValue(DefaultSelenium seleniumInstance, String a_locator) {            
		String tempString;
		
		try {
			tempString = seleniumInstance.getValue(a_locator);
		}
		catch (Exception e){
			error("ERROR while reading value on locator _"+a_locator+"_, check if element is present.");
			return constants.RET_ERROR_STRING;
		}
		return tempString;
	}  	
	
	protected boolean isElementPresent(DefaultSelenium seleniumInstance, String a_locator) {            
		boolean result = false;
		
		try {
			result = seleniumInstance.isElementPresent(a_locator);
		}
		catch (Exception e){
			error("ERROR while doing isElementPresent on locator _"+a_locator+"_, check if element is present.");
		}
		return result;
	}  
	
	protected boolean isTextPresent(DefaultSelenium seleniumInstance, String a_text) {            
		boolean result = false;
		
		try {
			result = seleniumInstance.isTextPresent(a_text);
		}
		catch (Exception e){
			error("ERROR while doing isTextPresent on text _"+a_text+"_, check if text is present.");
		}
		return result;
	}  	
	
	protected int click(DefaultSelenium seleniumInstance, String a_locator) {            
		
		try {
			if (!isElementPresent(seleniumInstance, a_locator)){
				error("ERROR while click on locator _"+a_locator+"_, element is not present.");
				getScreenshot(seleniumInstance, true);				
				return constants.RET_ERROR;
			}
			
			seleniumInstance.click(a_locator);
		}
		catch (Exception e){
			error("ERROR while clicking on locator _"+a_locator+"_");
			return constants.RET_ERROR;
		}
		return constants.RET_OK;
	}  	
	
	protected void getScreenshot(DefaultSelenium seleniumInstance, boolean error) {            
	
		if (!settings.USE_SCREENSHOTS) return;
		String filename;
		String status;
		Date d = new Date();
		DateFormat df = new SimpleDateFormat(settings.SCREENSHOTS_POSTFIX_FORMAT);
		
		if (error) status="(ERR)"; else {status="OK";}
		
		filename = (settings.SCREENSHOTS_PATH+settings.SCREENSHOTS_PREFIX+df.format(d)+status+".png");
	    info("Getting screenshot to file "+filename);
	    try{
	    	seleniumInstance.captureEntirePageScreenshot(filename,"args");
	    }
	    catch(Exception e){
	    	error("ERROR while getting screenshot to file _"+filename+"_, check if directory is present.");    	
	    };
	}
	
	protected String getTitle(DefaultSelenium seleniumInstance) {            
	    
	    return seleniumInstance.getTitle();
	}	

	protected int checkRecordPresence(DefaultSelenium seleniumInstance, String tabName, String recordId) {            
	    String tempLocator;
	    
	    openTab(seleniumInstance, tabName);
	
	    tempLocator = "//a[contains(text(),'"+recordId+"')]";
	    
	    if (isElementPresent(seleniumInstance, tempLocator)){
	        infoV("--------RECORD _"+tabName+":"+recordId+"_ FOUND");    	
	    	return constants.RET_OK;
	    }
	    else {
	    	infoV("--------RECORD _"+tabName+":"+recordId+" NOT FOUND");    	
	    	return constants.RET_ERROR;
	    }
	}
	
	public int createNewEmptyRecord(DefaultSelenium seleniumInstance, String tabName){
		String tempLocator =  "//input[@name='new']";
		
		openTab(seleniumInstance, tabName);

		if (click(seleniumInstance,tempLocator)==constants.RET_ERROR){
			error("Can't createNewEmptyRecord - cant click on New, locator _"+tempLocator+"_ ");
			getScreenshot(seleniumInstance, true);
			return constants.RET_ERROR;				
		}	 		
		
		seleniumInstance.waitForPageToLoad(settings.TIMEOUT) ;
	    
		info("New record on tab _"+tabName+"_ created, waiting for input.");
	    return constants.RET_OK;
	}
	
	public int createNewEmptyRecordFast(DefaultSelenium seleniumInstance, String tabName, String condition){
		String tempLocator =  "//input[@name='new']";
		
		openTab(seleniumInstance, tabName);

		if (click(seleniumInstance,tempLocator)==constants.RET_ERROR){
			error("Can't createNewEmptyRecord - cant click on New, locator _"+tempLocator+"_ ");
			getScreenshot(seleniumInstance, true);
			return constants.RET_ERROR;				
		}	 		
		
        if (waitForCondition(seleniumInstance, condition, settings.TIMEOUT)){
        	infoV("All inputs on new record page found before page load.");
        }
        else
        {
			fatal("Can't createNewEmptyRecordFast  - cant found all locators!");
			return constants.RET_ERROR;	
        }		
	    
		info("New record on tab _"+tabName+"_ created, waiting for input.");
	    return constants.RET_OK;
	}	

	protected int deleteRecord(DefaultSelenium seleniumInstance, String tabName, String recordId) {            
    	String tempLocator;

	    openTab(seleniumInstance, tabName);
	    
	    tempLocator = "//a[contains(text(),'"+recordId+"')]";
	    
	    if (isElementPresent(seleniumInstance, tempLocator)){
	        infoV("--------RECORD _"+tabName+":"+recordId+"_ FOUND");
			if (click(seleniumInstance,tempLocator)==constants.RET_ERROR){
				error("Can't createNewEmptyRecord - cant click on record, locator _"+tempLocator+"_ ");
				return constants.RET_ERROR;				
			}	 	        
	    	seleniumInstance.waitForPageToLoad(settings.TIMEOUT);
	
	    	pressDelete(seleniumInstance);
	    	
	    	if (checkRecordPresence(seleniumInstance, tabName, recordId)==constants.RET_ERROR){
	    		info("--------RECORD _"+tabName+":"+recordId+" DELETED");    		
	    		return constants.RET_OK;
	    	}else{
	    			error("--------RECORD _"+tabName+":"+recordId+" CAN'T BE DELETED (may be record was present before test started)");    		
	    			getScreenshot(seleniumInstance, true);
	    			return constants.RET_ERROR;
	   		}
	    }
	
	    else {
	    	error("--------RECORD _"+tabName+":"+recordId+" NOT FOUND FOR REMOVAL");    	
	    	return constants.RET_ERROR;
	    }
	}

// TODO this function should be optimized
	public boolean isErrorPresent (DefaultSelenium seleniumInstance, String errorMessage){
		boolean isPresent = false; 
		String tempLocator;
		tempLocator = "//*[contains(text(),'"+constants.GENERAL_PAGE_ERROR+"')]";
		if (isElementPresent(seleniumInstance, tempLocator)&&
				seleniumInstance.isVisible(tempLocator)){
					tempLocator = "//*[contains(text(),'"+errorMessage+"')]";
					if (isElementPresent(seleniumInstance, tempLocator)&&
							seleniumInstance.isVisible(tempLocator))
						isPresent = true;
		}
		return isPresent;
	}
	
	public boolean waitForListOfElements(DefaultSelenium seleniumInstance, ArrayList <String> locators, String timeout){
		String tempScript = "var result = false; ";
		infoV("Waiting for list of elements started");
		if (locators.size()>0) {
			tempScript = tempScript+ "result = selenium.isElementPresent('"+locators.get(0)+"');";
		}
		for (int i=1; i<locators.size(); i++){
			tempScript = tempScript+ "result = result && selenium.isElementPresent('"+locators.get(i)+"');";
		}
		
		try {
			seleniumInstance.waitForCondition(tempScript, timeout);
		} catch (Exception E){
			return false;
		}
		return true;
	}
	
	public boolean waitForCondition(DefaultSelenium seleniumInstance, String condition, String timeout){
		try {
			seleniumInstance.waitForCondition(condition, timeout);
		} catch (Exception E){
			return false;
		}
		return true;
	}	

	public void info (String message){
			if (!settings.LOG_INFOS) return;
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
		if (!settings.LOG_VERBOSE) return;
	        ut.info("(V)"+message);
	        Reporter.log("(V)"+message);
	}
}


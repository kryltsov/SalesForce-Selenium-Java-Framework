 
package com.deepshiftlabs.sf_tests;

import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
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
        warn("--------SESSION STARTED--------");
        seleniumInstance.start();
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

	protected void login(DefaultSelenium seleniumInstance, String a_login, String a_password) {            
	
	        info("Login started");
	        seleniumInstance.open("/");
	        seleniumInstance.waitForPageToLoad(settings.TIMEOUT);
	        seleniumInstance.type("username",a_login);
	        seleniumInstance.type("password",a_password);
	        seleniumInstance.click("//input[@id='Login']");
	        seleniumInstance.waitForPageToLoad(settings.TIMEOUT);
	        info("Login done: logged to "+ seleniumInstance.getTitle());
	    }
	    
	protected void logout(DefaultSelenium seleniumInstance) {            
	
	        info("Logout started");
			seleniumInstance.click("//a[contains(@href, '/secur/logout.jsp')]");
			seleniumInstance.waitForPageToLoad(settings.TIMEOUT);
	        info("Logout done: logged to "+ seleniumInstance.getTitle());
	    }
	
	protected void openTab(DefaultSelenium seleniumInstance, String tabName) {            
	
	        infoV("Tab opening started");
			seleniumInstance.click("link="+tabName);
			seleniumInstance.waitForPageToLoad(settings.TIMEOUT);
	        infoV("Tab opened: title is "+ seleniumInstance.getTitle());
	    }    
	protected void saveRecord(DefaultSelenium seleniumInstance) {            
	
	    infoV("Saving record started");
	    seleniumInstance.click(constants.SAVE_RECORD_LOCATOR);
		seleniumInstance.waitForPageToLoad(settings.TIMEOUT);
	    info("Save attempt done: title is "+ seleniumInstance.getTitle());
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

	protected int checkRecordPresence(DefaultSelenium seleniumInstance, String tabName, String recordId) {            
	    String tempLocator;
	    
	    openTab(seleniumInstance, tabName);
	
	    tempLocator = "//a[contains(text(),'"+recordId+"')]";
	    
	    if (seleniumInstance.isElementPresent(tempLocator)){
	        infoV("--------RECORD _"+tabName+":"+recordId+"_ FOUND");    	
	    	return constants.RET_OK;
	    }
	    else {
	    	infoV("--------RECORD _"+tabName+":"+recordId+" NOT FOUND");    	
	    	return constants.RET_ERROR;
	    }
	}

	public int createNewEmptyRecord(DefaultSelenium seleniumInstance, String tabName){
		openTab(seleniumInstance, tabName);
		seleniumInstance.click("//input[@name='new']");
		seleniumInstance.waitForPageToLoad("30000") ;
	    
		info("New record on tab _"+tabName+"_ created, waiting for input.");
	    return 0;
	}

	protected int deleteRecord(DefaultSelenium seleniumInstance, String tabName, String recordId) {            
    	String tempLocator;

	    openTab(seleniumInstance, tabName);
	    
	    tempLocator = "//a[contains(text(),'"+recordId+"')]";
	    
	    if (seleniumInstance.isElementPresent(tempLocator)){
	        infoV("--------RECORD _"+tabName+":"+recordId+"_ FOUND");
	        seleniumInstance.click(tempLocator);
	    	seleniumInstance.waitForPageToLoad(settings.TIMEOUT);
	
	    	seleniumInstance.chooseOkOnNextConfirmation();
	    	tempLocator = "del";
	    	seleniumInstance.click(tempLocator);
	    	seleniumInstance.getConfirmation();
	    	seleniumInstance.waitForPageToLoad(settings.TIMEOUT);    	
	    	
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
		if (seleniumInstance.isElementPresent(tempLocator)&&
				seleniumInstance.isVisible(tempLocator)){
					tempLocator = "//*[contains(text(),'"+errorMessage+"')]";
					if (seleniumInstance.isElementPresent(tempLocator)&&
							seleniumInstance.isVisible(tempLocator))
						isPresent = true;
		}
		return isPresent;
	}

	public void info (String message){
			if (!settings.LOG_INFOS) return;
	        ut.info(message);
	    }
	
	public void warn (String message){
	        ut.warn(message);
	    }
	    
	public void error (String message){
	        ut.error(message);
	    }    
	
	public void infoV (String message){
		if (!settings.LOG_VERBOSE) return;
	        ut.info("(V)"+message);
	}
}


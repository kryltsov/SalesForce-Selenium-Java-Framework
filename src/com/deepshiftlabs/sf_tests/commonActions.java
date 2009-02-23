package com.deepshiftlabs.sf_tests;

import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//import org.testng.annotations.*;

//com.thoughtworks.selenium.DefaultSelenium;
//import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.closeSeleniumSession;
//import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;
//import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.startSeleniumSession;


import com.thoughtworks.selenium.*;

/**
 *
 * @author Bear, Jan 15, 2009
 * 
 */

public class commonActions {
  
	// ---- constants  ---- //
    public static final String TIMEOUT = "40000";
    public static final Boolean USE_SCREENSHOTS = true;
    public static final Boolean LOG_INFOS = true;
    public static final String SCREENSHOTS_PATH = "C:\\trillium\\logs\\";
    public static final String SAVE_RECORD_LOCATOR = "save";
    public static final String SCREENSHOTS_PREFIX = "screen_";
    public static final String SCREENSHOTS_POSTFIX_FORMAT = "HHmmssSSS_ddMMyy";
	// ---- End of constants  ---- //
    
    
    utils ut = new utils();

    protected DefaultSelenium getSelenium(String seleniumHost, int seleniumPort, String browser, String webSite){
        DefaultSelenium seleniumInstance = new DefaultSelenium(seleniumHost, seleniumPort, browser, webSite);
        ut.info("--------SESSION STARTED--------");
        seleniumInstance.start();
        seleniumInstance.setTimeout(TIMEOUT);        
        ut.info("--------BROWSER STARTED--------");
        return seleniumInstance;
   }

    public void freeSelenium(DefaultSelenium seleniumInstance) {
        seleniumInstance.stop();
        ut.info("--------BROWSER CLOSED--------");                
        seleniumInstance = null;
        ut.info("--------SESSION ENDED--------");        
    }

protected void login(DefaultSelenium seleniumInstance, String a_login, String a_password) {            

        ut.info("--------LOGIN STARTED--------");
        seleniumInstance.open("/");
        seleniumInstance.waitForPageToLoad(TIMEOUT);
        seleniumInstance.type("username",a_login);
        seleniumInstance.type("password",a_password);
        seleniumInstance.click("//input[@id='Login']");
        seleniumInstance.waitForPageToLoad(TIMEOUT);
        ut.info("--------LOGIN DONE: logged to "+ seleniumInstance.getTitle());
    }
    
protected void logout(DefaultSelenium seleniumInstance) {            

        ut.info("--------LOGOUT STARTED-------");
		seleniumInstance.click("//a[contains(@href, '/secur/logout.jsp')]");
		seleniumInstance.waitForPageToLoad(TIMEOUT);
        ut.info("--------LOGOUT DONE: logged to "+ seleniumInstance.getTitle());
    }

protected void openTab(DefaultSelenium seleniumInstance, String tabName) {            

        ut.info("--------TAB OPENING STARTED-------");
		seleniumInstance.click("link="+tabName);
		seleniumInstance.waitForPageToLoad(TIMEOUT);
        ut.info("--------TAB OPENING DONE: title is "+ seleniumInstance.getTitle());
    }    
protected void saveRecord(DefaultSelenium seleniumInstance) {            

    ut.info("--------SAVING RECORD STARTED-------");
    seleniumInstance.click(SAVE_RECORD_LOCATOR);
	seleniumInstance.waitForPageToLoad(TIMEOUT);
    ut.info("--------RECORD SAVED: title is "+ seleniumInstance.getTitle());
}    

protected void getScreenshot(DefaultSelenium seleniumInstance, boolean error) {            

	if (!USE_SCREENSHOTS) return;
	String filename;
	String status;
	Date d = new Date();
	DateFormat df = new SimpleDateFormat(SCREENSHOTS_POSTFIX_FORMAT);
	
	if (error) status="(ERR)"; else {status="OK";}
	
	filename = (SCREENSHOTS_PATH+SCREENSHOTS_PREFIX+df.format(d)+status+".png");
    ut.info("--------GETTING SCREENSHOT to file "+filename+"--");
    seleniumInstance.captureEntirePageScreenshot(filename,"args");
}   
    
public void info (String message){
		if (!LOG_INFOS) return;
        ut.info(message);
    }

public void warn (String message){
        ut.warn(message);
    }
    
public void error (String message){
        ut.error(message);
    }    
    
  
//@Test(groups = { "sf_tests" }, enabled = true )    
/* public static void main(String args[])
 {
  commonActions mytests = new commonActions(); 
  mytests.runExample ();
  System.out.println("Hello, World!");
 }*/
}


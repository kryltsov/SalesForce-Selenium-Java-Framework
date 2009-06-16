package com.deepshiftlabs.sf_tests;

import org.testng.annotations.*;

import java.io.*;

/**
 * Represents Salesforce login-logout test. Can be executed in parallel with other tests.  
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
public class LoginLogoutTest {
	
	/**
	 * Each test has object of CommonActions. It's created here. 
	 * Don't forget initialize objects of GenericObject and inherited objects with link on CommonActions instance!  
	 */
	CommonActions action = new CommonActions();	
 
	/**
	 * Will contain all needed elements and common check sequences
	 */
	GenericObject loginObject;

	/**
	 * Will be used in Event generation. 
	 */
	String name = "LoginLogoutTest";

 /**
 * Adds to GenericObject all needed elements.
 */
public void addAllElements(){
	 loginObject.addElement(new GenericElement("User Name", Constants.RESERVED_PARAMETER, Constants.REQUIRED));	 
	 loginObject.addElement(new GenericElement("Password", Constants.RESERVED_PARAMETER, Constants.REQUIRED));
 }

 /**
  * Main method, it'll be executed in parallel by TestNG.
  * It get TestNG parameters. If seleniumHost parameter is not received from TestNG, parameters from Settings class are used.
  * @see Settings#SELENIUM_HOST
  * @see Settings#SELENIUM_PORT
  * @see Settings#BROWSER
  * @see Settings#WEB_SITE
 */
@Test(groups = {"default"}, description = "login_logout_test")
 @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite"})    
 public void runAllPageTests(@Optional("") String seleniumHost, @Optional("-1") int seleniumPort, @Optional("") String browser, @Optional("") String webSite){
	 action.init();
	 Event event = action.startEvent("runAllPageTests", name);
 
     if (seleniumHost.equals("")){
         seleniumHost = Settings.SELENIUM_HOST;
         seleniumPort = Settings.SELENIUM_PORT;
         browser = Settings.BROWSER;
         webSite = Settings.WEB_SITE;
     }
 	
     loginObject = new LoginLogout ("Loginlogout", "LoginlogoutObject", "Loginlogout Object");
     loginObject.init(action);
     addAllElements();
 	
 	try {
 		loginObject.prepareBrowser(seleniumHost, seleniumPort, browser, webSite);
 		loginObject.checkAll();
 		loginObject.freeBrowser();
	    action.closeEventOk(event);
 	}
 	catch (SftestException e)
 	{
 		loginObject.freeBrowser();
 		event.exceptionMessage = e.toString(); 		
 		action.closeEventFatal(event); 		
 	}

 	// HTML report generation
	action.eventsToHtml(name);
 }
}
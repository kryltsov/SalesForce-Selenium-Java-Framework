package com.deepshiftlabs.sf_tests;

import org.testng.annotations.*;

import java.io.*;

public class LoginLogoutTest {
	CommonActions action = new CommonActions();	
	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));      
 
	GenericObject loginObject;
	String name = "LoginLogoutTest";

 public int addAllElements(){
	 loginObject.addElement(new GenericElement("User Name", Constants.RESERVED_PARAMETER, Constants.RESERVED_PARAMETER, Constants.REQUIRED));	 
	 loginObject.addElement(new GenericElement("Password", Constants.RESERVED_PARAMETER, Constants.RESERVED_PARAMETER, Constants.REQUIRED));
    return 0;
 }

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

	action.eventsToHtml(name);
 }
}
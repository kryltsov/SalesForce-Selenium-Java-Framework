package com.deepshiftlabs.sf_tests;

import org.testng.annotations.*;

import java.io.*;

public class LoginLogoutTest {
	
	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));      
 
	GenericObject wwwObject = new LoginLogout ("Loginlogout", "smth", "smth2");

 public int addAllElements(){
 	    wwwObject.addElement(new GenericElement("User Name", Constants.RESERVED_PARAMETER, Constants.RESERVED_PARAMETER, Constants.REQUIRED));	 
 	    wwwObject.addElement(new GenericElement("Password", Constants.RESERVED_PARAMETER, Constants.RESERVED_PARAMETER, Constants.REQUIRED));
    return 0;
 }

 @Test(groups = {"default1"}, description = "login_logout_test")
 @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite"})    
 public void runAllPageTests(@Optional("") String seleniumHost, @Optional("-1") int seleniumPort, @Optional("") String browser, @Optional("") String webSite){
     if (seleniumHost.equals("")){
         seleniumHost = Settings.SELENIUM_HOST;
         seleniumPort = Settings.SELENIUM_PORT;
         browser = Settings.BROWSER;
         webSite = Settings.WEB_SITE;
     }
 	
 	addAllElements();
 	
 	try {
	    	wwwObject.prepareBrowser(seleniumHost, seleniumPort, browser, webSite);
	    	wwwObject.checkAll();
	    	wwwObject.freeBrowser();
 	}
 	catch (SftestException E)
 	{
 		wwwObject.action.fatal("Test stopped after fatal error. "+E);

 		if (wwwObject.sInstance!=null)
 			wwwObject.freeBrowser();
 	}
     
/*        try{
     	System.out.println("------------------WAITING FOR ENTER-------------------");
     	stdin.read();}
     catch(IOException e) {}; */
     
     
 };
}  


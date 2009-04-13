package com.deepshiftlabs.sf_tests;

import org.testng.annotations.*;

import java.io.*;

public class loginLogoutTest {
	
	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));      
 
	genericObject wwwObject = new loginLogout ("Loginlogout", "smth", "smth2");

 public int addAllElements(){
 	    wwwObject.addElement(new genericElement("User Name", constants.RESERVED_PARAMETER, constants.RESERVED_PARAMETER, constants.REQUIRED));	 
 	    wwwObject.addElement(new genericElement("Password", constants.RESERVED_PARAMETER, constants.RESERVED_PARAMETER, constants.REQUIRED));
    return 0;
 }

 @Test(groups = {"default"}, description = "login_logout_test")
 @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite"})    
 public void runAllPageTests(@Optional("") String seleniumHost, @Optional("-1") int seleniumPort, @Optional("") String browser, @Optional("") String webSite){
     if (seleniumHost.equals("")){
         seleniumHost = settings.SELENIUM_HOST;
         seleniumPort = settings.SELENIUM_PORT;
         browser = settings.BROWSER;
         webSite = settings.WEB_SITE;
     }
 	
 	addAllElements();
 	
 	try {
	    	wwwObject.prepareBrowser(seleniumHost, seleniumPort, browser, webSite);
	    	wwwObject.checkAll();
	    	wwwObject.freeBrowser();
 	}
 	catch (sftestException E)
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


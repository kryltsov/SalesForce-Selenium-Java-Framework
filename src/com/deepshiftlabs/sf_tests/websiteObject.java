// TODO  add comments
// TODO  do all TODO's
// TODO  do some whole statistics (now all results are only in runtime log)
// TODO  find errors after which there is not sense to continue current element test or all page test

package com.deepshiftlabs.sf_tests;

import org.testng.annotations.*;
import com.thoughtworks.selenium.*;

import java.util.*;
import java.io.*;

public class websiteObject {
	
	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));      
    
	genericObject wwwObject = new genericObject ("Employment Websites", "websitePage00001", "Employment Website");

    public int addAllElements(){
       
    	wwwObject.addElement(new textElement("Employment Website Name", constants.RESERVED_PARAMETER, wwwObject.parentTabID, constants.REQUIRED,80));
    	wwwObject.addElement(new urlElement("Web Address", constants.RESERVED_PARAMETER, wwwObject.parentTabID, constants.REQUIRED));
    	wwwObject.addElement(new currencyElement("Price Per Post", constants.RESERVED_PARAMETER, wwwObject.parentTabID, constants.REQUIRED,5,2));
    	wwwObject.addElement(new currencyElement("Maximum Budget", constants.RESERVED_PARAMETER, wwwObject.parentTabID, constants.REQUIRED,6,2));    
       
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
    	
    	wwwObject.setDeterminingRecordIdField("Employment Website Name");
    	
    	wwwObject.prepareBrowser(seleniumHost, seleniumPort, browser, webSite);
    	wwwObject.login();    	
    	
    	wwwObject.checkAll();
    	
    	wwwObject.logout();
    	wwwObject.freeBrowser();
        
        try{
        	System.out.println("------------------WAITING FOR ENTER-------------------");
        	stdin.read();}
        catch(IOException e) {};
        
    };
}  


package com.deepshiftlabs.sf_tests;

import org.testng.annotations.*;
import com.thoughtworks.selenium.*;
import org.testng.annotations.*;

import java.util.*;
import java.io.*;

public class websitePage {
	
    settings privateSettings;	
	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));      
    
    genericPage page = new genericPage ("Employment Website", "Employment Websites", "websitePage00001");

    public int addAllElements(){

       page.addElement(new textElement("Employment Website Name", "//input[@id='Name']","Employment Website", constants.REQUIRED,80));
       page.addElement(new urlElement("Web Address", "document.forms[3].elements[8]","Employment Website", constants.REQUIRED));
       page.addElement(new currencyElement("Price Per Post", "document.forms[3].elements[9]" ,"Employment Website", constants.REQUIRED,5,2));
       page.addElement(new currencyElement("Maximum Budget", "document.forms[3].elements[10]" ,"Employment Website", constants.REQUIRED,6,2));    

/*     page.addElement(new textElement("Employment Website Name", "Name","Employment Website", constants.REQUIRED,80));
       page.addElement(new urlElement("Web Address", "00N80000002suMr","Employment Website", constants.REQUIRED));
       page.addElement(new currencyElement("Price Per Post", "00N80000002sumz" ,"Employment Website", constants.REQUIRED,5,2));
       page.addElement(new currencyElement("Maximum Budget", "00N80000002sun3" ,"Employment Website", constants.REQUIRED,6,2));
*/       
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
    	page.setDeterminingRecordIdField("Employment Website Name");
    	
    	page.prepareBrowser(seleniumHost, seleniumPort, browser, webSite);
    	page.login();    	
    	page.createNewEmptyRecord();
        page.checkElementsPresence();
        page.fillElementsByValidValues();
        
        page.checkAllElements();

        page.logout();
        page.freeBrowser();
        
        try{
        	System.out.println("------------------WAITING FOR ENTER-------------------");
        	stdin.read();}
        catch(IOException e) {};
        
    };
}
      


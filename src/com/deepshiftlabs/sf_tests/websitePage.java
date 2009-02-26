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
       page.addElement(new textElement("Employment Website Name", "Name","Employment Website", "",true,80));
       page.addElement(new urlElement("Web Address", "00N80000002suMr","Employment Website", "",true));
       page.addElement(new numberElement("Price Per Post", "00N80000002sumz" ,"Employment Website", "",true));
       
//       page.addElement(new textElement("for_unique_check", "00N800000036pL8" ,"for_unique_check", "",false,20));
//       page.setUnique("for_unique_check",false);
       
       page.addElement(new numberElement("Maximum Budget", "00N80000002sun3" ,"Employment Website", "",true));
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
        
        try{
        	System.out.println("------------------WAITING FOR ENTER-------------------");
        	stdin.read();}
        catch(IOException e) {};
        
        page.logout();
        page.freeBrowser();
    };
}
      


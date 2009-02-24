package com.deepshiftlabs.sf_tests;

import org.testng.annotations.*;
import com.thoughtworks.selenium.*;
import org.testng.annotations.*;

import java.util.*;
import java.io.*;

public class websitePage {
	
	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));      
    
    genericPage page = new genericPage ("Employment Website", "Employment Websites");
    String myRecordId = "websitePage00001";
    
    public int addAllElements(){
       page.addElement(new textElement("Employment Website Name", "Name","Employment Website", myRecordId,"",true,80));
       page.addElement(new urlElement("Web Address", "00N80000002suMr","Employment Website", myRecordId,"",true));
       page.addElement(new numberElement("Price Per Post", "00N80000002sumz" ,"Employment Website", myRecordId,"",true));
       page.addElement(new numberElement("Maximum Budget", "00N80000002sun3" ,"Employment Website", myRecordId,"",true));       
       return 0;
    }

    @Test(groups = {"default"}, description = "login_logout_test")
//    @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite"})    
    public void runAllPageTests(){
    	page.prepareBrowser();
    	
    	addAllElements();
        
    	page.createNewEmptyRecord();
        page.checkElementsPresence();
        page.fillElementsByValidValues();
        page.checkAllElements();
        
        try{
        System.out.println("------------------WAITING FOR ENTER-------------------");
        stdin.read();}
        catch(IOException e) {};
        
        page.freeBrowser();
    };
    

/*    @Test(groups = {"default"}, description = "login_logout_test")
    @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite"})    
    public void do_login_logout(String seleniumHost, int seleniumPort, String browser, String webSite){
        commonActions actions = new commonActions();
        
        DefaultSelenium selenium = actions.getSelenium(seleniumHost, seleniumPort,  browser, webSite);
        actions.login(selenium, "bearoffl_dev@rambler.ru", "bear1212");
        actions.logout(selenium);
        actions.freeSelenium(selenium);
    }*/
}
      


package com.deepshiftlabs.sf_tests;

import com.thoughtworks.selenium.*;
import org.testng.annotations.*;

public class websitePage {
    
    genericPage page = new genericPage ("Employment Website", "Employment Websites");
    String myRecordId = "websitePage00001";
    
    public int addAllElements(){
       page.addElement(new textElement("Employment Website Name", "Name","Employment Website", myRecordId));
       page.addElement(new textElement("Web Address", "00N80000002suMr","Employment Website", myRecordId));
       page.addElement(new textElement("Price Per Post", "00N80000002sumz" ,"Employment Website", myRecordId));
       
       return 0;
    }    

    @Test(groups = {"default"}, description = "login_logout_test")
//    @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite"})    
    public void runAllPageTests(){
        addAllElements();
        page.createNewEmptyRecord();
        page.checkElementsPresence();
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
      


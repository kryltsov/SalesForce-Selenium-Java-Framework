package com.deepshiftlabs.sf_tests;

import org.testng.annotations.Configuration;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

//import org.testng.annotations.*;

import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.closeSeleniumSession;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.startSeleniumSession;


//import com.thoughtworks.selenium.*;

/**
 *
 * @author Bear, Jan 15, 2009
 * 
 */

//@Test(groups = { "sf_tests" }, enabled = true )
public class sf_tests_class {
  
    public static final String TIMEOUT = "40000";
    utils ut = new utils();

    @BeforeMethod(groups = {"default", "example", "sf_tests"}, alwaysRun = true)
    @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite"})
    protected void startSession(String seleniumHost, int seleniumPort, String browser, String webSite) throws Exception {
        startSeleniumSession(seleniumHost, seleniumPort, browser, webSite);
        ut.info("--------SESSION STARTED--------");
        session().setTimeout(TIMEOUT);
   }

    @AfterMethod(groups = {"default", "example", "sf_tests"}, alwaysRun = true)
    public void closeSession() throws Exception {
        closeSeleniumSession();
        ut.info("--------SESSION ENDED--------");
    }

protected void login() {            

        ut.info("--------LOGIN STARTED--------");
        session().open("/");
        session().waitForPageToLoad(TIMEOUT);
        session().click("xpath=//li[@id='tabnavlogin']/a/span");
        session().waitForPageToLoad(TIMEOUT);
        session().type("username","bearoffl@rambler.ru");
        session().type("password","bear1111");
        session().click("//input[@id='Login']");
        session().waitForPageToLoad(TIMEOUT);
        ut.info("--------LOGIN DONE: logged to "+ session().getTitle());
    }
    
protected void logout() {            

        ut.info("--------LOGOUT STARTED-------");
		session().click("//a[contains(@href, '/secur/logout.jsp')]");
		session().waitForPageToLoad(TIMEOUT);
        ut.info("--------LOGOUT DONE: logged to "+ session().getTitle());
    }
  
/*@Test(groups = { "sf_tests" }, enabled = true )    
 public static void main(String args[])
 {
  sf_tests_class mytests = new sf_tests_class(); 
  mytests.runExample ();
  System.out.println("Hello, World!");
 }*/
}


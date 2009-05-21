// TODO  add comments
// TODO when passing some parameters to selenium driver (like type()) we should check if parameters!=null
// TODO  do all TODO's
// TODO  do some whole statistics (now all results are only in runtime log)
// TODO  find errors after which there is not sense to continue current element test or all page test
// TODO  before starting of record creating we should check if there the same record  already

package com.deepshiftlabs.sf_tests;

import org.testng.annotations.*;

import java.util.*;
import java.io.*;

public class WebsiteObject {
	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
	CommonActions action = new CommonActions();
    
	GenericObject wwwObject;
	
	int actID = 0;
	String name = "Website object";

    public int addAllElements(){
       
    	wwwObject.addElement(new TextElement("Employment Website Name", Constants.RESERVED_PARAMETER, wwwObject.parentTabID, Constants.REQUIRED,80));
    	wwwObject.addElement(new UrlElement("Web Address", Constants.RESERVED_PARAMETER, wwwObject.parentTabID, Constants.REQUIRED));
    	wwwObject.addElement(new CurrencyElement("Price Per Post", Constants.RESERVED_PARAMETER, wwwObject.parentTabID, Constants.REQUIRED,5,2));
    	wwwObject.addElement(new CurrencyElement("Maximum Budget", Constants.RESERVED_PARAMETER, wwwObject.parentTabID, Constants.REQUIRED,6,2));
    	wwwObject.updateLocatorsLists();
       
       return 0;
    }

    @Test(groups = {"default"}, description = "Website object test")
    @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite"})    
    public void runAllPageTests(@Optional("") String seleniumHost, @Optional("-1") int seleniumPort, @Optional("") String browser, @Optional("") String webSite){
    	Event event = action.startEvent(name, "runAllPageTests");
    	action.init();
    	
        if (seleniumHost.equals("")){
            seleniumHost = Settings.SELENIUM_HOST;
            seleniumPort = Settings.SELENIUM_PORT;
            browser = Settings.BROWSER;
            webSite = Settings.WEB_SITE;
        }
        wwwObject = new GenericObject ("Employment Websites", "websitePage00001", "Employment Website");
        wwwObject.init(action);
    	addAllElements();
    
    	Utils.generateScreenshotName(false);
    	Utils.generateScreenshotName(false);
    	
    	try {
	    	wwwObject.setDeterminingRecordIdField("Employment Website Name");
	    	wwwObject.prepareBrowser(seleniumHost, seleniumPort, browser, webSite);
	    	wwwObject.login();    	
	    	wwwObject.checkAll();
	    	wwwObject.logout();
	    	wwwObject.freeBrowser();
	    	action.closeEventOk(event);
    	}
    	catch (SftestException E)
    	{
    		wwwObject.action.fatal("Test stopped after fatal error. "+E);
    		wwwObject.freeBrowser();
    		action.closeEventFatal(event, "Test stopped after fatal error. "+E);
    	}
    	catch (Exception E){
    		wwwObject.action.fatal("Test stopped after outer fatal error. "+E);
    		action.closeEventFatal(event, "Test stopped after fatal error. "+E);
    	}
    	
    	wwwObject.printErrorsSummary();
    	action.generateReport();
    	action.eventsToHtml(name);     	
        
/*        try{
        	System.out.println("------------------WAITING FOR ENTER-------------------");
        	stdin.read();}
        catch(IOException e) {}; 
        */
        
    };
}  


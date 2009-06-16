// TODO  add comments
// TODO when passing some parameters to selenium driver (like type()) we should check if parameters!=null
// TODO  do all TODO's
// TODO  do some whole statistics (now all results are only in runtime log)
// TODO  find errors after which there is not sense to continue current element test or all page test
// TODO  before starting of record creating we should check if there the same record  already

package com.deepshiftlabs.sf_tests;

import org.testng.annotations.*;

/**
 * Represents test of Website object. Can be executed in parallel with other tests.  
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
public class WebsiteObject {
	/**
	 * Each test has object of CommonActions. It's created here. 
	 * Don't forget initialize objects of GenericObject and inherited objects with link on CommonActions instance!  
	 */
	CommonActions action = new CommonActions();
	
	/**
	 * Will contain all needed elements and common check sequences.
	 */
	GenericObject wwwObject;
	
	/**
	 * Will be used in Event generation. 
	 */
	String name = "Website object";

	 /**
	 * Adds to GenericObject all needed elements.
	 */
    public void addAllElements(){
       
    	wwwObject.addElement(new TextElement("Employment Website Name", wwwObject.parentTabID, Constants.REQUIRED, 80));
    	wwwObject.addElement(new UrlElement("Web Address", wwwObject.parentTabID, Constants.REQUIRED));
    	wwwObject.addElement(new CurrencyElement("Price Per Post", wwwObject.parentTabID, Constants.REQUIRED, 5,2));
    	wwwObject.addElement(new CurrencyElement("Maximum Budget", wwwObject.parentTabID, Constants.REQUIRED, 6,2));
    	// we should call this method if FAST record creation will be used
    	wwwObject.updateLocatorsLists();
    }

    /**
     * Main method, it'll be executed in parallel by TestNG.
     * It get TestNG parameters. If seleniumHost parameter is not received from TestNG, parameters from Settings class are used.
     * @see Settings#SELENIUM_HOST
     * @see Settings#SELENIUM_PORT
     * @see Settings#BROWSER
     * @see Settings#WEB_SITE
    */    
    @Test(groups = {"default"}, description = "Website object test")
    @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite"})    
    public void runAllPageTests(@Optional("") String seleniumHost, @Optional("-1") int seleniumPort, @Optional("") String browser, @Optional("") String webSite){
    	Event event = action.startEvent("runAllPageTests", name);
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
    	try {
	    	wwwObject.setDeterminingRecordIdField("Employment Website Name");
	    	wwwObject.prepareBrowser(seleniumHost, seleniumPort, browser, webSite);
	    	wwwObject.login();    	
	    	wwwObject.checkAll();
	    	wwwObject.logout();
	    	wwwObject.freeBrowser();
	    	action.closeEventOk(event);
    	}
    	catch (SftestException e)
    	{
    		wwwObject.freeBrowser();
    		event.exceptionMessage = e.toString();
    		action.closeEventFatal(event);
    	}
    	catch (Exception e){
    		event.exceptionMessage = e.toString();
    		action.closeEventFatal(event);
    	}
    	
//    	wwwObject.printErrorsSummary();
//    	action.generateReport();
    	
     	// HTML report generation
    	action.eventsToHtml(name);     	
    };
}  


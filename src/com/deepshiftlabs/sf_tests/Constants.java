package com.deepshiftlabs.sf_tests;

/**
 * Represent a class which contains common constants.
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
public final class Constants {

// TODO when working with xpath we should not use ' in locators	
	
	
// return values
	public static final int RET_OK = 0;
	public static final int RET_SKIPPED = -100;
	public static final int RET_ERROR = -1;
	// for functions which returns strings:
	public static final String RET_ERROR_STRING = "e_rror_occured_";
	// for cases when after function execution we have opened View page, not Edit. 
	public static final int RET_PAGE_BROKEN_OK = -3;
	public static final int RET_PAGE_BROKEN_ERROR = -4;
	public static final int RET_SOMETHING_STRANGE = -50;
	
// standard parameters	
	public static final boolean REQUIRED = true;
	public static final boolean IT_IS_VALID_VALUE = true;
	public static final boolean IT_IS_INVALID_VALUE = false;
	public static final String ONLY_GENERAL_ERROR = "";
	public static final String STANDARD_INVALID_VALUE_ERROR = "";	
	public static final String RESERVED_PARAMETER = "reserved_parameter";
	public static final String MIN_SELENIUM_TIMEOUT = "1";
	
//statuses
	public static final int NOT_CHECKED = 0;
	public static final int CHECKED = 1;
	public static final boolean CHECK_OK = true;
	public static final boolean CHECK_ERROR = false;
	
// event levels	
	public static final int STARTED = 0;
	public static final int OK = 1;
	public static final int INFOV = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int FATAL = 6;	

// event constants 
    //number of highest level in HTML report hierarchy.
    public static final int TOP_IERARCHY_LEVEL = 0;

// SF Constants
	// i can't understand why "Review all error messages below to correct your data." is not working in xpath
	public static final String GENERAL_PAGE_ERROR = "Error: Invalid Data.";
    public static final String SAVE_RECORD_LOCATOR = "save";
    public static final String EDIT_RECORD_LOCATOR = "edit";
    public static final String SAVE_AND_NEW_LOCATOR = "save_new";
    public static final String CANCEL_LOCATOR = "cancel";
    public static final String CLONE_LOCATOR = "clone";
    public static final String DELETE_LOCATOR = "del";
    public static final String LOGIN_LOCATOR = "//input[@id='Login']";
    public static final String LOGOUT_LOCATOR = "//a[contains(@href, '/secur/logout.jsp')]";
    
    public static final String LOGIN_FAILED_ERROR = "Your login attempt has failed";
    public static final String BAD_IP_ERROR = "You are attempting to access salesforce.com from an unrecognized computer.";
    public static final String HOME_PAGE_TITLE = "Salesforce - Developer Edition";

    // this value is used to check Login sequence with invalid parameters.
    public static final String INVALID_LOGIN_VALUE = "IN-v(al!d login value";
    
    public static final String TITLE_LOGIN_PAGE = "Salesforce.com - Customer Secure Login Page";
    public static final String TITLE_LOGOUT_PAGE = "Salesforce - Developer Edition";

// some default messages
    // this value will be displayed in HTML report if Goal for event was not set.
    public static final String NO_EVENT_GOAL = "is not set";
    
// HTML constants
    public static final String HTML_HEADER_START = "<HTML><HEAD>\n";
    public static final String HTML_HEADER_END = "</HEAD>\n";
    public static final String HTML_BODY_START = "<BODY onLoad = \"getEventsByStartId('1')[0].style.display='block'\">\n";
    public static final String HTML_BODY_END = "</BODY>\n";
    public static final String TITLE = "<TITLE>Îò÷¸ò</TITLE>\n";
    public static final String HTML_FOOTER = "</HTML>";

    public static final String HTML_SCRIPT = "<SCRIPT LANGUAGE=\"JavaScript\" SRC=\"../../js/script.js\"></SCRIPT>\n\n";
    public static final String HTML_SELECTOR = "<select size=\"1\" name=\"levelSelector\" onChange='onSelector(this.value)'>\n    <option disabled>Select log level</option>\n    <option value=\"0\" \nselected>All events</option>\n    <option value=\"1\">Error&Fatal events</option> \n    <option value=\"2\">Fatal events only</option>\n<option value=\"-1\">Compress all</option>\n</select>";

    // parameters: eventId, linkTitle, linkStyle, imageString + eventName
    public static final String HTML_OPEN_CHILD_LINK = "<a href=\"javascript:toggleNode('%s')\" title=\"%s\" %s>%s</a>";

    // parameters: detailsHtmlId, linkStyle, imageString
    public static final String HTML_OPEN_DETAILS_LINK = "<a href=\"javascript:toggleDetails('%s')\" title=\"Click here to see details of event\" %s>%s</a>";

    // parameters: filename, imgString
    public static final String HTML_SCR_BEFORE = "<a href=\"..\\screenshots\\%s\" target=\"_blank\" title=\"Screenshot before event start\">%s</a>";
    public static final String HTML_SCR_AFTER = "<a href=\"..\\screenshots\\%s\" target=\"_blank\" title=\"Screenshot after event end\">%s</a>";    
    public static final String HTML_SCR_EQUAL = "<a href=\"..\\screenshots\\%s\" target=\"_blank\" title=\"Event did not act on browser\">%s</a>";

    // parameters: tabs, name, parent
    public static final String HTML_SUBBLOCK_NAMED_OPEN = "\n%s<DIV NAME=%s ID=%s >\n";

    // parameters: htmlId, StartId, style
    public static final String HTML_SUBBLOCK_OPEN = "\n<DIV ID=%s Name=%s %s>\n";

    // parameters: htmlId, parentHtmlId, logLevel, style
    public static final String HTML_EVENT_BLOCK_OPEN = "\n<DIV ID=%s name=%s class=%s %s>\n";

    // parameters: tabs
    public static final String HTML_SUBBLOCK_CLOSE = "%s</DIV>\n";

    public static final String HTML_DEFAULT_COLOR = "black";
    public static final String HTML_DETAILS_LINK_NAME = "...";

    // parameters: color
    public static final String HTML_STYLE_COLOR = "Style='color: %s'";
    
 // parameters: padddingLeft
    public static final String HTML_STYLE = "Style='display:none;padding-left: %dpx'";    
    public static final String HTML_STYLE_DETAILS = "Style='display:none;padding-left: 15px; margin: 0 60\u0025 0 20px;background: #F9F9A2;border: 2px solid blue;'";
    
    public static final String HTML_STYLE_OK = " <style type=\"text/css\">\nstyle_black{color: black}</style>";
    public static final String HTML_STYLE_ERROR = " <style type=\"text/css\">\nstyle_SaddleBrown{color: pink}</style>";
    public static final String HTML_STYLE_FATAL = " <style type=\"text/css\">\nstyle_red{color: red}</style>";

    // parameters: imgChildName
    public static final String HTML_IMG_CHILD_SHOW = "<img name=\"%s\" src=\"..\\..\\img\\p.gif\"alt=\"Child events\" hspace=\"5\">";
    // parameters: imgChildName
    public static final String HTML_IMG_CHILD_HIDE = "<img name=\"%s\" src=\"..\\..\\img\\p.gif\"alt=\"Child events\" hspace=\"5\">";
    
    // parameters: imgDetailsName
    public static final String HTML_IMG_DETAILS_SHOW = "<img name=\"%s\" src=\"..\\..\\img\\detailsShow.gif\"alt=\"Child events\" hspace=\"5\">";
    // parameters: imgDetailsName
    public static final String HTML_IMG_DETAILS_HIDE = "<img name=\"%s\" src=\"..\\..\\img\\detailsHide.gif\"alt=\"Child events\" hspace=\"5\">";
    
    // hard-coded images filenames 
    public static final String HTML_IMG_NO_CHILD = "<img src=\"..\\..\\img\\n.gif\"alt=\"No child events\" hspace=\"5\">";
    public static final String HTML_IMG_BEFORE = "<img src=\"..\\..\\img\\before.gif\"alt=\"Before screenshot\" hspace=\"3\">";
    public static final String HTML_IMG_AFTER = "<img src=\"..\\..\\img\\after.gif\"alt=\"After screenshot\" hspace=\"3\">";
    public static final String HTML_IMG_EQUAL = "<img src=\"..\\..\\img\\equal.gif\"alt=\"After screenshot\" hspace=\"3\">";
}
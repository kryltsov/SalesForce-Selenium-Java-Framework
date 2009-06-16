package com.deepshiftlabs.sf_tests;

// TODO Add timestamps to event details
// TODO FIX!! Null event id if 
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Represents methods which process event list and generate HTML report.
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
public class EventProcessor {
	ArrayList <Event> events;
	PrintWriter writer;
	
	public void init(ArrayList <Event> a_events){
		events = a_events;
	}
	
    public int prepareFile(String master){
    	
    	String filename = Settings.REPORT_PATH+"report_"+master+".html";
		try {
			writer = new PrintWriter(
			             		 new OutputStreamWriter(
					             new FileOutputStream(filename), "windows-1251"));
		} 
		catch (Exception E){
			return Constants.RET_ERROR;
		}        	
    	return Constants.RET_OK;
    }
    
	public void writeToFile (String a_string){
		try {    
			writer.write(a_string);
		} 
		catch (Exception E){
//			ut.fatal("Some exception when writing to file");
		}
	}
	
    public void closeFile(){
    	writer.close();
    }	
	
	/**
	 * @param startId id of event
	 * @return Link to Event if found, null otherwise.
	 */
	private Event getEventByStartId(int startId){
		for (int i=0; i<events.size(); i++){
			if ((events.get(i).getStartId()==startId)){
				return events.get(i);
			}
		}
		return null; 
	}
	
	/**
	 * @param startId id of event
	 * @return Index of event in list.
	 */
	public int eventIndexByStartId(int startId){
		for (int i=0; i<events.size();i++){
			if (events.get(i).getStartId()==startId){
				return i;
			}
		}
		return -1;
	}	
	
	/**
	 * @return Id of last event on list.
	 */
	private int getLastEventStartId(){
		return events.get(events.size()-1).getStartId(); 
	}	
	
	/**
	 * @return Name of color which corresponds this log level. Can be only one of colors in presented styles in class Constants.
	 */
	public String getHtmlColor(int a_logLevel){
		String color = "black";
		switch(a_logLevel)
	    {
	      case Constants.OK: color = "black"; break;
	      case Constants.INFOV: color = "black"; break;
	      case Constants.INFO: color = "black"; break;
	      case Constants.WARN: color = "blue"; break;
	      case Constants.ERROR: color = "SaddleBrown"; break;
	      case Constants.FATAL: color = "red"; break;
	    }		
		return color;
	}
	
	/**
	 * @return Given log level in string expression.
	 */
	public String getLogLevelString(int a_logLevel){
		String level = "black";
		switch(a_logLevel)
	    {
	      case Constants.OK: level = "OK"; break;
	      case Constants.INFOV: level = "INFOV"; break;
	      case Constants.INFO: level = "INFO"; break;
	      case Constants.WARN: level = "WARN"; break;
	      case Constants.ERROR: level = "ERROR"; break;
	      case Constants.FATAL: level = "FATAL"; break;
	    }		
		return level;
	}	
	
	/**
	 * This method is used to format source code of HTML report
	 * @param a_level level of event in hierarchy
	 * @return String which contains corresponding to level number of tabs.
	 */
	public String getTabs(int a_level){
		String tabs ="";
		for (int i = 0; i<a_level; i++){
			tabs = tabs+"\t";
		}
		return tabs;
	}
	
	public String getStyle (int a_level){
		String style = String.format(Constants.HTML_STYLE, a_level*30);
		return style;		
	}
	
	public String getStyleColor (String a_color){
		String style = String.format(Constants.HTML_STYLE_COLOR, a_color);
		return style;		
	}
	
	/**
	 * Used for event details block generation - adds open subblock string to output file   
	 * @param a_htmlId id of block
	 * @param a_myStartId name of block
	 * @param a_level level of event to format HTML source
	 * @param style style
	 */
	public void openSubBlock(String a_htmlId, int a_myStartId, int a_level, String style){
		String s = getTabs(a_level) +  String.format(Constants.HTML_SUBBLOCK_OPEN, a_htmlId, a_myStartId, style);
		writeToFile (s);
	}
	
	/**
	 * Used for event block generation
	 * @param a_htmlId id of block
	 * @param a_parentHtmlId name of block=Id of parent block
	 * @param a_logLevel class name of block
	 * @param a_level level of event to format HTML source
	 * @param style style
	 */
	public void openEventBlock(String a_htmlId, String a_parentHtmlId, String a_logLevel, int a_level, String style){
		String s = getTabs(a_level) + String.format(Constants.HTML_EVENT_BLOCK_OPEN,  a_htmlId, a_parentHtmlId, a_logLevel, style);
		writeToFile (s);
	}	
	
	public void closeSubBlock(int a_level){
		writeToFile (String.format(Constants.HTML_SUBBLOCK_CLOSE, getTabs(a_level)));
	}	
	
	/**
	 * If event has screenshots, generates links to them.
	 * @return String which contains images with links on screenshot(s).
	 */
	public String prepareScreenshotsLinks(Event a_event){
		String scrLinks = "";
		
		if (a_event.beforeScreenshot.equals("") && a_event.afterScreenshot.equals(""))
		{
			return scrLinks;
		}
		
		if (a_event.beforeScreenshot.equals(a_event.afterScreenshot))
		{
			scrLinks = scrLinks + String.format(Constants.HTML_SCR_EQUAL, a_event.beforeScreenshot, Constants.HTML_IMG_EQUAL);
			return scrLinks;
		}
		else {
			if (!a_event.beforeScreenshot.equals("") ){
				scrLinks = scrLinks + String.format(Constants.HTML_SCR_BEFORE, a_event.beforeScreenshot, Constants.HTML_IMG_BEFORE);
			}
			
			if (!a_event.afterScreenshot.equals("") ){
				scrLinks = scrLinks + String.format(Constants.HTML_SCR_AFTER, a_event.afterScreenshot, Constants.HTML_IMG_AFTER); 
			}
//			scrLinks = scrLinks+"<br>\n";
		}
		return scrLinks;
	}

	/**
	 * Prepares and writes to file a string, which contains HTML code which represents one event.
	 * @param a_event link to event
	 * @param a_level level of event in hierarchy
	 * @param a_parentStartId id of parent event
	 */
	public void eventToHtmlString(Event a_event, int a_level, int a_parentStartId){
		String color = getHtmlColor(a_event.logLevel);
		String htmlId = a_event.getStartId() + "_" + a_parentStartId+ "_" + a_event.logLevel+"_id";
		String parentStartId = ""+a_parentStartId;
		String levelHtmlId = getLogLevelString(a_event.logLevel);
		String imgChildName = a_event.getStartId() +"img";
		String detailsHtmlId = "details_"+a_event.getStartId();
		String imgDetailsName = detailsHtmlId+"img";
		String linkStyle = getStyleColor(color);
		String detailsLinkStyle = getStyleColor(Constants.HTML_DEFAULT_COLOR);
		String details = a_event.toHtmlDetail();
		String style = getStyle(a_level+1);
		String linkTitle = a_event.goal;
		
		String s;

		openEventBlock(htmlId, parentStartId, levelHtmlId, a_level, style);
		
		if (a_event.getStartId() == a_event.getEndId()){
			// if event has no subevents
			s = getTabs(a_level) + Constants.HTML_IMG_NO_CHILD;
			s = s + "<style_"+color+">"+a_event.eventName + "</style_"+color+">";
		}
		else {
			// if event has subevents
			String imageString = String.format(Constants.HTML_IMG_CHILD_SHOW, imgChildName);
			s = getTabs(a_level) + String.format(Constants.HTML_OPEN_CHILD_LINK, a_event.getStartId(), linkTitle, linkStyle, imageString + a_event.eventName);
		}

		s = s+"&nbsp&nbsp<i> Target:</i> "+a_event.targetName+"\n";
		s = s+ prepareScreenshotsLinks(a_event);
		
		if ( !details.equals("") ){
			// if event has details
			
			String imageDetailsString = String.format(Constants.HTML_IMG_DETAILS_SHOW, imgDetailsName);
			s = s + getTabs(a_level) + String.format(Constants.HTML_OPEN_DETAILS_LINK, detailsHtmlId, detailsLinkStyle, imageDetailsString);			
			writeToFile (s);
			writeToFile ("<br>\n");
			openSubBlock(detailsHtmlId, -2, a_level+1, Constants.HTML_STYLE_DETAILS);
			writeToFile (details);
			closeSubBlock(a_level+1);
		}
		else {
			// if event has no details
			writeToFile (s);
			writeToFile ("<br>\n");
		}
		
		// end of event block
		closeSubBlock(a_level);
	}	
	
	/**
	 * Recursive method which outputs to file event with all its subevents.
	 * @param a_startId id of event
	 * @param level level in hierarchy (used for formatting)
	 * @param a_parentStartId id of parent event (in it's eventToHtml method was called this iteration)
	 * @return EndId of just processed event.
	 */
	public int eventToHtml(int a_startId, int level, int a_parentStartId){
		Event tempEvent;
		int lastProcessed = a_startId;		
		tempEvent =  getEventByStartId(a_startId);
		if (tempEvent==null){
			writeToFile("Can't find this event: "+a_startId);			
			return a_startId;
		}
		
		if (0 == tempEvent.getEndId()){
// TODO should be prevented before
				writeToFile("Null event endID!");
				tempEvent.fixEndId();
		}

		// Event in HTML prints to file 
		eventToHtmlString(tempEvent, level, a_parentStartId);
		
		// if we have a subevents, we'll process each of them
		if (tempEvent.getStartId() != tempEvent.getEndId()){
			lastProcessed = tempEvent.getStartId();
			while (lastProcessed<tempEvent.getEndId()){
				lastProcessed = eventToHtml(lastProcessed+1, level+1, a_startId);			
			}
		}
		lastProcessed = tempEvent.getEndId();
		return lastProcessed;
	}
	
	/**
	 * @param masterName filename of HTML report 
	 * @return RET_ERROR, RET_OK.
	 */
	int eventsToHtml(String masterName){
		int lastProcessedEvent = 0;
		int nextEventToProcess = 0;
		int lastEventStartId = getLastEventStartId();

		if ( prepareFile(masterName) == Constants.RET_ERROR ){
			return Constants.RET_ERROR;
		}

		// writing HTML headers
		writeToFile(Constants.HTML_HEADER_START);
		writeToFile(Constants.HTML_STYLE_OK);
		writeToFile(Constants.HTML_STYLE_ERROR);
		writeToFile(Constants.HTML_STYLE_FATAL);
		writeToFile(Constants.HTML_HEADER_END);
		writeToFile(Constants.HTML_BODY_START);
		writeToFile(Constants.HTML_SCRIPT);
		writeToFile(Constants.HTML_SELECTOR);
		
		writeToFile("<hr>");

		// starting with first event in list
		nextEventToProcess = lastProcessedEvent+1;
		while (nextEventToProcess<=lastEventStartId){
			lastProcessedEvent = eventToHtml(nextEventToProcess, Constants.TOP_IERARCHY_LEVEL, 0);			
			nextEventToProcess = lastProcessedEvent+1;
		}		

		// writing HTML footers
		writeToFile(Constants.HTML_BODY_END);
		writeToFile(Constants.HTML_FOOTER);
		closeFile();
		return Constants.RET_OK;
	}
}

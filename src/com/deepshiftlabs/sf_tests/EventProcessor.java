package com.deepshiftlabs.sf_tests;


import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

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
	
	private Event getEventByStartId(int startId){
		for (int i=0; i<events.size(); i++){
			if ((events.get(i).getStartId()==startId)){
				return events.get(i);
			}
		}
		return null; 
	}
	
	public int eventIndexByStartId(int startId){
		for (int i=0; i<events.size();i++){
			if (events.get(i).getStartId()==startId){
				return i;
			}
		}
		return -1;
	}	
	
	private Event getUnclosedEvent(){
		for (int i=events.size()-1; i>=0; i--){
			if (!(events.get(i).isClosed)){
				return events.get(i);
			}
		}
		return null; 
	}

	private int getLastEventStartId(){
		return events.get(events.size()-1).getStartId(); 
	}	
	
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
	
	public String getTabs(int logLevel){
		String tabs ="";
		for (int i = 0; i<logLevel; i++){
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
	
	public void openSubBlock(String a_htmlId, int a_myStartId, int a_level, String style){
		String s = getTabs(a_level) +  String.format(Constants.HTML_SUBBLOCK_OPEN, a_htmlId, a_myStartId, style);
		writeToFile (s);
	}
	
	public void openEventBlock(String a_htmlId, String a_parentHtmlId, String a_logLevel, int a_level, String style){
		String s = getTabs(a_level) + String.format(Constants.HTML_EVENT_BLOCK_OPEN,  a_htmlId, a_parentHtmlId, a_logLevel, style);
		writeToFile (s);
	}	
	
/*	public void openNamedSubBlock(String a_name, int a_level, int a_parent){
		String s = String.format(Constants.HTML_SUBBLOCK_NAMED_OPEN, getTabs(a_level), a_name, "parent_"+a_parent);
		writeToFile (s);
	}	*/
	
	public void closeSubBlock(int a_level){
		writeToFile (String.format(Constants.HTML_SUBBLOCK_CLOSE, getTabs(a_level)));
	}	
	
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
	
// 204.14.234.33 = sfIP

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
			s = getTabs(a_level) + Constants.HTML_IMG_NO_CHILD;
			s = s + "<style_"+color+">"+a_event.eventName + "</style_"+color+">";
		}
		else {
			String imageString = String.format(Constants.HTML_IMG_CHILD_SHOW, imgChildName);
			s = getTabs(a_level) + String.format(Constants.HTML_OPEN_CHILD_LINK, a_event.getStartId(), linkTitle, linkStyle, imageString + a_event.eventName);
		}

		s = s+"&nbsp&nbsp<i> Target:</i> "+a_event.targetName+"\n";
		s = s+ prepareScreenshotsLinks(a_event);
		
		if ( !details.equals("") ){
			
			String imageDetailsString = String.format(Constants.HTML_IMG_DETAILS_SHOW, imgDetailsName);
			s = s + getTabs(a_level) + String.format(Constants.HTML_OPEN_DETAILS_LINK, detailsHtmlId, detailsLinkStyle, imageDetailsString);			
			writeToFile (s);
			writeToFile ("<br>\n");
			openSubBlock(detailsHtmlId, -2, a_level+1, Constants.HTML_STYLE_DETAILS);
			writeToFile (details);
			closeSubBlock(a_level+1);
		}
		else {
			writeToFile (s);
			writeToFile ("<br>\n");
		}
		
		closeSubBlock(a_level);
	}	
	
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
		
		eventToHtmlString(tempEvent, level, a_parentStartId);
		
		if (tempEvent.getStartId() != tempEvent.getEndId()){
			lastProcessed = tempEvent.getStartId();
			while (lastProcessed<tempEvent.getEndId()){
				lastProcessed = eventToHtml(lastProcessed+1, level+1, a_startId);			
			}
		}
		lastProcessed = tempEvent.getEndId();
		return lastProcessed;
	}
	
	int eventsToHtml(String masterName){
		int lastProcessedEvent = 0;
		int nextEventToProcess = 0;
		int lastEventStartId = getLastEventStartId();

		if ( prepareFile(masterName) == Constants.RET_ERROR ){
			return Constants.RET_ERROR;
		}

		writeToFile(Constants.HTML_HEADER_START);
		writeToFile(Constants.HTML_STYLE_OK);
		writeToFile(Constants.HTML_STYLE_ERROR);
		writeToFile(Constants.HTML_STYLE_FATAL);
		writeToFile(Constants.HTML_HEADER_END);
		writeToFile(Constants.HTML_BODY_START);
		writeToFile(Constants.HTML_SCRIPT);
		
/*		writeToFile(String.format(Constants.HTML_CHKBOX, "OK", "OK"));
		writeToFile(String.format(Constants.HTML_CHKBOX, "ERROR", "ERROR"));
		writeToFile(String.format(Constants.HTML_CHKBOX, "FATAL", "FATAL"));
		*/
		writeToFile(Constants.HTML_SELECTOR);
		
		writeToFile("<hr>");


		nextEventToProcess = lastProcessedEvent+1;
		while (nextEventToProcess<=lastEventStartId){
			lastProcessedEvent = eventToHtml(nextEventToProcess, Constants.TOP_IERARCHY_LEVEL, 0);			
			nextEventToProcess = lastProcessedEvent+1;
		}		

		writeToFile(Constants.HTML_BODY_END);
		writeToFile(Constants.HTML_FOOTER);
		closeFile();
		return Constants.RET_OK;
	}
}

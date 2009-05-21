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
	
	public String getHtmlColor(int logLevel){
		String color = "black";
		switch(logLevel)
	    {
	      case Constants.OK: color = "black"; break;
	      case Constants.INFOV: color = "black"; break;
	      case Constants.INFO: color = "black"; break;
	      case Constants.WARN: color = "blue"; break;
	      case Constants.ERROR: color = "red"; break;
	      case Constants.FATAL: color = "red"; break;
	    }		

		return color;
	}
	
	public String getTabs(int logLevel){
		String tabs ="";
		for (int i = 0; i<logLevel; i++){
			tabs = tabs+"\t";
		}
		return tabs;
	}
	
	public String getStyle (int a_level){
		String style = String.format(Constants.HTML_STYLE, a_level*20);
		return style;		
	}
	
	public String getStyleColor (String a_color){
		String style = String.format(Constants.HTML_STYLE_COLOR, a_color);
		return style;		
	}
	
	public String getStyleDetails (int a_level){
		String style = String.format(Constants.HTML_STYLE_DETAILS, a_level*20);
		return style;
	}	
	
	public void openSubBlock(String a_htmlId, int a_level, String style){
		String s = String.format(Constants.HTML_SUBBLOCK_OPEN, getTabs(a_level), a_htmlId, style);
		writeToFile (s);
	}	
	
	public void closeSubBlock(int a_level){
		writeToFile (String.format(Constants.HTML_SUBBLOCK_CLOSE, getTabs(a_level)));
	}	
	
	public void eventToHtmlString(Event a_event, int a_level){
		String color = getHtmlColor(a_event.logLevel);
		String htmlId = "id"+a_event.getStartId();
		String detailsHtmlId = "idDetails"+a_event.getStartId();
		String linkStyle = getStyleColor(color);
		String detailsLinkStyle = getStyleColor(Constants.HTML_DEFAULT_COLOR);
		String details = a_event.toHtmlDetail();
		
		String s = getTabs(a_level);
		
		if (a_event.getStartId() == a_event.getEndId()){
			s = s + "<"+color+">"+a_event.eventName + "</"+color+">";
		}
		else {
			s = s + String.format(Constants.HTML_OPEN_CHILD_LINK, htmlId, linkStyle, a_event.eventName);
		}

		s = s+" Target: "+a_event.targetName+"\n";

		if ( !details.equals("") ){
			s = s + String.format(Constants.HTML_OPEN_CHILD_LINK, detailsHtmlId, detailsLinkStyle, Constants.HTML_DETAILS_LINK_NAME);
		}
		s = s + "<br>\n"; 	
		writeToFile (s);

		if ( !details.equals("") ){
			openSubBlock(detailsHtmlId, a_level+1, getStyleDetails(a_level+2));
			writeToFile (details);
			closeSubBlock(a_level+1);
		}
	}	
	
	public int eventToHtml(int a_startId, int level){
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
		
		eventToHtmlString(tempEvent, level);
		
		if (tempEvent.getStartId() != tempEvent.getEndId()){
			String htmlId = "id"+tempEvent.getStartId();
			String style = getStyle(level+1);
			openSubBlock(htmlId, level+1, style);
			
			lastProcessed = tempEvent.getStartId();
			while (lastProcessed<tempEvent.getEndId()){
				lastProcessed = eventToHtml(lastProcessed+1, level+1);			
			}
			closeSubBlock(level+1);
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
		writeToFile(Constants.HTML_STYLE_BLACK);
		writeToFile(Constants.HTML_STYLE_RED);
		writeToFile(Constants.HTML_HEADER_END);
		writeToFile(Constants.HTML_BODY_START);
		writeToFile(Constants.HTML_SCRIPT);

		nextEventToProcess = lastProcessedEvent+1;
		while (nextEventToProcess<=lastEventStartId){
			lastProcessedEvent = eventToHtml(nextEventToProcess, Constants.TOP_IERARCHY_LEVEL);			
			nextEventToProcess = lastProcessedEvent+1;
		}		

		writeToFile(Constants.HTML_BODY_END);
		writeToFile(Constants.HTML_FOOTER);
		closeFile();
		return Constants.RET_OK;
	}
}

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
	
	public String eventToString(Event a_event, int a_level){
		String color = getHtmlColor(a_event.logLevel);		
		String s="";
		 	
		s = String.format(Constants.HTML_STRING_FORMAT, a_level*20, color, a_event.eventName, a_event.targetName);

		return s;
	}
	
	public String eventToLink(Event a_event, int a_level){
		String htmlId = "id"+a_event.getStartId();
		String color = getHtmlColor(a_event.logLevel);		
		String s="";
		 	
		s = String.format(Constants.HTML_LINK_FORMAT, htmlId, a_level*20, color, a_event.eventName, a_event.targetName);
		return s;
	}
	
	public String openSubBlock(Event a_event, int a_level){
		String htmlId = "id"+a_event.getStartId();
		String s="";
		 	
		s = String.format(Constants.HTML_SUBBLOCK_OPEN, htmlId, a_level*20);
		return s;
	}	
	
	public String closeSubBlock(){
		return Constants.HTML_SUBBLOCK_CLOSE;
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
		
		if (tempEvent.getStartId() == tempEvent.getEndId()){
			simpleEventToHtml(tempEvent, level);
		}
		else {
			writeToFile(eventToLink(tempEvent, level));
			writeToFile(openSubBlock(tempEvent, level));
			
			lastProcessed = tempEvent.getStartId();
			while (lastProcessed<tempEvent.getEndId()){
				lastProcessed = eventToHtml(lastProcessed+1, level+1);			
			}
			writeToFile(closeSubBlock());
		}

		lastProcessed = tempEvent.getEndId();
		return lastProcessed;
	}
	
	public void simpleEventToHtml(Event a_event, int level){
		writeToFile(eventToString(a_event, level));
	}
	
	int eventsToHtml(String masterName){
		int lastProcessedEvent = 0;
		int nextEventToProcess = 0;
		int lastEventStartId = getLastEventStartId();

		if ( prepareFile(masterName) == Constants.RET_ERROR ){
			return Constants.RET_ERROR;
		}

		writeToFile(Constants.HTML_HEADER);
		writeToFile(Constants.HTML_SCRIPT);

		nextEventToProcess = lastProcessedEvent+1;
		while (nextEventToProcess<=lastEventStartId){
			lastProcessedEvent = eventToHtml(nextEventToProcess, Constants.TOP_IERARCHY_LEVEL);			
			nextEventToProcess = lastProcessedEvent+1;
		}		

		writeToFile(Constants.HTML_FOOTER);
		closeFile();
		return Constants.RET_OK;
	}
}

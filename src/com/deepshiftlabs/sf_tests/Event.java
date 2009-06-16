package com.deepshiftlabs.sf_tests;

import java.util.*;

/**
 * Represents the Event, which contains all details of some operation, it's parameters and results. 
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
public class Event {
	
	private int startId=0;
	private int endId=0;
	private Date startTime = new Date();
	private Date endTime = new Date();

	boolean isClosed = false;
	String eventName = "";
	String checkLoginLogout = "";
	String resultMessage = "";
	String advice = "";
	String exceptionMessage = "";
	String beforeScreenshot = "";
	String afterScreenshot = "";
	String targetName = "";
	String value = "";
	String waitedValue = "";
	String realValue = "";
	int codeLevel = 0;
	int logLevel;
	String goal;
	
	// used for localization. 
	static ResourceBundle goals = ResourceBundle.getBundle("GoalsBundle", new Locale(Settings.MY_LOCALE));
	
	/**
	 * Creates new event. Tries to find Goal in resource file which is correspond to eventName.
	 * @param id id of new event
	 * @param a_eventName event name, which is usually chosen as method name in which event is created
	 * @param a_targetName definite object or element on which influence will be done due event, or name of method's class
	 */
	public Event(int id, String a_eventName, String a_targetName){
		eventName = a_eventName;
		targetName = a_targetName;
		logLevel = Constants.STARTED;
		startTime.getTime();
		startId = id;
		
		try {
			goal = "Goal: " + goals.getString(a_eventName) + '.';
		}
		catch (Exception e){
			goal = "Goal: " + Constants.NO_EVENT_GOAL;
		}
	}	
	
	// only formatting of two values
	public String prepareOneValue(String valueName, String value){
		return valueName+"\t"+value+"\n";
	}
	
	// prepares a very simple format of event output
	public String toString(){
		String tempString;
		tempString = "EVENT:\n";
		tempString = tempString+prepareOneValue("startId", new Integer(startId).toString());
		tempString = tempString+prepareOneValue("endId", new Integer(endId).toString());
		tempString = tempString+prepareOneValue("Start time", startTime.toString());
		tempString = tempString+prepareOneValue("End time", endTime.toString());
		tempString = tempString+prepareOneValue("isClosed", new Boolean(isClosed).toString());
		tempString = tempString+prepareOneValue("eventName", eventName);
		tempString = tempString+prepareOneValue("resultMessage", resultMessage);
		tempString = tempString+prepareOneValue("beforeScreenshot", beforeScreenshot);
		tempString = tempString+prepareOneValue("afterScreenshot", afterScreenshot);
		tempString = tempString+prepareOneValue("targetName", targetName);
		tempString = tempString+prepareOneValue("value", value);
		tempString = tempString+prepareOneValue("waitedValue", waitedValue);
		tempString = tempString+prepareOneValue("realValue", realValue);
		tempString = tempString+prepareOneValue("codeLevel", new Integer(codeLevel).toString());
		tempString = tempString+prepareOneValue("logLevel", new Integer(logLevel).toString());
		tempString = tempString+prepareOneValue("Advice", advice);
		tempString = tempString+prepareOneValue("exception", exceptionMessage);
		tempString = tempString+"\n";
		
		return tempString;
	}
	
	/**
	 * @return Given log level in string expression.
	 */
	private String logLevelToString(int logLevel){
		String tempString = "";
		switch( logLevel)
	    {
	    	case Constants.STARTED: tempString = "STARTED"; break;
	    	case Constants.OK: tempString = "OK"; break; 
	    	case Constants.INFOV: tempString = "INFOV"; break;
	    	case Constants.INFO: tempString = "INFO"; break;
	    	case Constants.WARN: tempString = "WARN"; break;
	    	case Constants.ERROR: tempString = "ERROR"; break;
	    	case Constants.FATAL: tempString = "FATAL"; break;
	    }
		return tempString;
	}
	
	/**
	 * @see Event#toHtmlDetail()
	 * @return  String which should be used to print out event using console or plain text file. 
	 */
	public String toConsole(){
		// this string will be prepared in accordance with presented values 
		String formatString = "";
		String returnString = "";

		if ( logLevel== Constants.STARTED){
			formatString = "EVENT (%s): %s, target: %s";
			formatString = formatString + "\n";
			returnString = String.format(formatString, logLevelToString(logLevel), eventName, targetName); 
		}
		else {
			formatString = "EVENT (%s): %s, target: %s";
			if (resultMessage.equals(""))
				formatString = formatString + "%s";
			else 
				formatString = formatString + "\nResult: %s. ";
			
			if (advice.equals(""))
				formatString = formatString + "%s";
			else 
				formatString = formatString + " %s";
			
			if (value.equals("") && waitedValue.equals("") && realValue.equals(""))
			{
				formatString = formatString + "%s%s%s";
			}
			else {
				formatString = formatString + "\nValues: \t";
				if (value.equals(""))
					formatString = formatString + "%s";
				else 
					formatString = formatString + "Value: %s; \t";
				
				if (waitedValue.equals(""))
					formatString = formatString + "%s";
				else 
					formatString = formatString + "Waited: %s; \t";
				
				if (realValue.equals(""))
					formatString = formatString + "%s";
				else 
					formatString = formatString + "Real: %s; \t";
			}
			
			if (beforeScreenshot.equals("") && afterScreenshot.equals(""))
			{
				formatString = formatString + "%s%s";
			}
			else {
				formatString = formatString + "\nScreenshots: ";
				if (beforeScreenshot.equals(""))
					formatString = formatString + "%s";
				else 
					formatString = formatString + "%s (before) \t";
				
				if (afterScreenshot.equals(""))
					formatString = formatString + "%s";
				else 
					formatString = formatString + "%s (after)";
			}

			if (exceptionMessage.equals(""))
				formatString = formatString + "%s";
			else 
				formatString = formatString + "\nException message: %s.";
			
			formatString = formatString + "\n";
			
			returnString = "(ID = " + startId +")  "+String.format(formatString, logLevelToString(logLevel), eventName, targetName, resultMessage, advice, 
											value, waitedValue, realValue,
											beforeScreenshot,  afterScreenshot,
											exceptionMessage);
		}
		// using format string to produce output string		
		return returnString;
	}
	
	/**
	 * @see Event#toConsole()
	 * @return String which contains HTML-formatted details of event. Should be used in HTML report generation.
	 */
	public String toHtmlDetail(){
		// this string will be prepared in accordance with presented values
		String formatString = "";
		String returnString = "";

		if (resultMessage.equals(""))
			formatString = formatString + "%s";
		else 
			formatString = formatString + "Result: %s.<br>\n";
		
		if (advice.equals(""))
			formatString = formatString + "%s";
		else 
			formatString = formatString + " %s<br>\n";
		
		if (value.equals("") && waitedValue.equals("") && realValue.equals(""))
		{
			formatString = formatString + "%s%s%s";
		}
		else {
			formatString = formatString + "Values: \t";
			if (value.equals(""))
				formatString = formatString + "%s";
			else 
				formatString = formatString + "Value: %s; \t";
			
			if (waitedValue.equals(""))
				formatString = formatString + "%s";
			else 
				formatString = formatString + "Waited: %s; \t";
			
			if (realValue.equals(""))
				formatString = formatString + "%s";
			else 
				formatString = formatString + "Real: %s; \t";
			
			formatString = formatString + "<br>\n";
		}
		if (exceptionMessage.equals(""))
			formatString = formatString + "%s";
		else 
			formatString = formatString + "\nException message: %s.<br>\n";
		
		// using format string to produce output string
		returnString = String.format(formatString, resultMessage, advice, 
										value, waitedValue, realValue,
										exceptionMessage);
		return returnString;
	}	
	
	/**
	 * Makes event closed.
	 * @param a_logLevel shows how good was event processed
	 * @param a_message description, can be empty
	 * @param a_screenshot filename of afterScreenshot, can be empty
	 * @param a_endId id of last event, which was started before this time. 
	 * If all is OK, those event has to be closed straight before this one.
	 */
	public void close(int a_logLevel, String a_message, String a_screenshot, int a_endId){
		logLevel = a_logLevel;		
		resultMessage = a_message;
		afterScreenshot = a_screenshot;
		endTime.getTime();
		endId = a_endId;
		isClosed = true;
	}
	
	public void setValue(String a_value){
		value = a_value;
	}
	
	public void setWaitedValue(String a_waitedValue){
		waitedValue = a_waitedValue;
	}
	
	public void setRealValue(String a_realValue){
		realValue = a_realValue;
	}	
	
	public void setTargetName(String a_targetName){
		targetName = a_targetName;
	}	
	
	public int getStartId(){
		return startId;
	}
	
	public void fixEndId(){
		endId = startId;
	}	

	public int getEndId(){
		return endId;
	}
}
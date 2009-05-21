package com.deepshiftlabs.sf_tests;

import java.util.*;

public class Event {
	
	private static int idGen=0;
	
	private int startId=0;
	private int endId=0;
	private Date startTime = new Date();
	private Date endTime = new Date();

	boolean isClosed = false;
	String eventName = "";
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
	int logLevel = Constants.TOP_IERARCHY_LEVEL;
	
	public Event(String a_eventName, String a_targetName){
		eventName = a_eventName;
		targetName = a_targetName;
		logLevel = Constants.STARTED;
		startTime.getTime();
		startId = ++idGen;
	}	
	
	public String prepareOneValue(String valueName, String value){
		return valueName+"\t"+value+"\n";
	}
	
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
	
	public String toConsole(){
		String formatString = "";
		String returnString = "";

		if ( logLevel== Constants.STARTED){
			formatString = "EVENT (%s): %s, target: %s";
			formatString = formatString + "\n";
			returnString = String.format(formatString, logLevelToString(logLevel), eventName, targetName); 
		}
		else {
			formatString = "EVENT (%s): %s, target: %s";
			if (resultMessage=="")
				formatString = formatString + "%s";
			else 
				formatString = formatString + "\nResult: %s. ";
			
			if (advice=="")
				formatString = formatString + "%s";
			else 
				formatString = formatString + " %s";
			
			if (value=="" && waitedValue=="" && realValue=="")
			{
				formatString = formatString + "%s%s%s";
			}
			else {
				formatString = formatString + "\nValues: \t";
				if (value=="")
					formatString = formatString + "%s";
				else 
					formatString = formatString + "Value: %s; \t";
				
				if (waitedValue=="")
					formatString = formatString + "%s";
				else 
					formatString = formatString + "Waited: %s; \t";
				
				if (realValue=="")
					formatString = formatString + "%s";
				else 
					formatString = formatString + "Real: %s; \t";
			}
			
			if (beforeScreenshot=="" && afterScreenshot=="")
			{
				formatString = formatString + "%s%s";
			}
			else {
				formatString = formatString + "\nScreenshots: ";
				if (beforeScreenshot=="")
					formatString = formatString + "%s";
				else 
					formatString = formatString + "%s (before) \t";
				
				if (afterScreenshot=="")
					formatString = formatString + "%s";
				else 
					formatString = formatString + "%s (after)";
			}

			if (exceptionMessage=="")
				formatString = formatString + "%s";
			else 
				formatString = formatString + "\nException message: %s.";
			
			formatString = formatString + "\n";
			
			returnString = String.format(formatString, logLevelToString(logLevel), eventName, targetName, resultMessage, advice, 
											value, waitedValue, realValue,
											beforeScreenshot,  afterScreenshot,
											exceptionMessage);
		}
		return returnString;
	}
	
	public String toHtmlDetail(){
		String formatString = "";
		String returnString = "";

		if (resultMessage=="")
			formatString = formatString + "%s";
		else 
			formatString = formatString + "Result: %s.<br>\n";
		
		if (advice=="")
			formatString = formatString + "%s";
		else 
			formatString = formatString + " %s.<br>\n";
		
		if (value=="" && waitedValue=="" && realValue=="")
		{
			formatString = formatString + "%s%s%s";
		}
		else {
			formatString = formatString + "Values: \t";
			if (value=="")
				formatString = formatString + "%s";
			else 
				formatString = formatString + "Value: %s; \t";
			
			if (waitedValue=="")
				formatString = formatString + "%s";
			else 
				formatString = formatString + "Waited: %s; \t";
			
			if (realValue=="")
				formatString = formatString + "%s";
			else 
				formatString = formatString + "Real: %s; \t";
			
			formatString = formatString + "<br>\n";
		}
		
		if (beforeScreenshot=="" && afterScreenshot=="")
		{
			formatString = formatString + "%s%s";
		}
		else {
			formatString = formatString + "Screenshots: ";
			if (beforeScreenshot=="")
				formatString = formatString + "%s";
			else 
				formatString = formatString + "%s (before) \t";
			
			if (afterScreenshot=="")
				formatString = formatString + "%s";
			else 
				formatString = formatString + "%s (after)";
			
			formatString = formatString + "<br>\n";
		}

		if (exceptionMessage=="")
			formatString = formatString + "%s";
		else 
			formatString = formatString + "\nException message: %s.";
		
		returnString = String.format(formatString, resultMessage, advice, 
										value, waitedValue, realValue,
										beforeScreenshot,  afterScreenshot,
										exceptionMessage);
		return returnString;
	}	
	

	public void close(int a_logLevel, String a_message, String a_screenshot){
		logLevel = a_logLevel;		
		resultMessage = a_message;
		afterScreenshot = a_screenshot;
		endTime.getTime();
		endId = getLastId();
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
	
	public static int getLastId(){
		return idGen;
	};
}
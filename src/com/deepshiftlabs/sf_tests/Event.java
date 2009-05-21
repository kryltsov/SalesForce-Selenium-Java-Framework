package com.deepshiftlabs.sf_tests;

import java.util.*;

public class Event {
	
	static int idGen=0;
	static Utils ut=new Utils();;
	
	CommonActions action;
	private int startId=0;
	private int endId=0;
	int type=0;
	Date time = new Date();

	boolean isClosed = false;
	String eventName="";
	String message="";
//	String exceptionMessage;
	String beforeScreenshot="";
	String afterScreenshot="";
//	String method;
	String targetName="";
	String value="";
	String waitedValue="";
	String realValue="";
	int codeLevel=0;
	int logLevel=0;
	
/*	public Event(){
		time.getTime();
		startId = ++idGen;
	}*/
	public Event(String a_targetName, String a_eventName){
		eventName = a_eventName;
		targetName = a_targetName;
		time.getTime();
		startId = ++idGen;
//		ut.info("startId=  "+startId+" name= "+a_eventName);
	}	
	
	
	public String prepareOneValue(String valueName, String value){
		return valueName+"\t"+value+"\n";
	}
	
	public String toString(){
		String tempString;
		tempString = "EVENT:\n";
		tempString = tempString+prepareOneValue("startId", new Integer(startId).toString());
		tempString = tempString+prepareOneValue("endId", new Integer(endId).toString());
		tempString = tempString+prepareOneValue("type", new Integer(type).toString());
		tempString = tempString+prepareOneValue("time", time.toString());
		tempString = tempString+prepareOneValue("isClosed", new Boolean(isClosed).toString());
		tempString = tempString+prepareOneValue("eventName", eventName);
		tempString = tempString+prepareOneValue("message", message);
		tempString = tempString+prepareOneValue("beforeScreenshot", beforeScreenshot);
		tempString = tempString+prepareOneValue("afterScreenshot", afterScreenshot);
		tempString = tempString+prepareOneValue("targetName", targetName);
		tempString = tempString+prepareOneValue("value", value);
		tempString = tempString+prepareOneValue("waitedValue", waitedValue);
		tempString = tempString+prepareOneValue("realValue", realValue);
		tempString = tempString+prepareOneValue("codeLevel", new Integer(codeLevel).toString());
		tempString = tempString+prepareOneValue("logLevel", new Integer(logLevel).toString());
		tempString = tempString+"\n";
		
		return tempString;
	}
	

	public void close(int a_logLevel, String a_message, String a_screenshot){
		logLevel = a_logLevel;		
		message = a_message;
		afterScreenshot = a_screenshot;
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
	
	public void badValue(String a_realValue){
		realValue = a_realValue;
		logLevel = Constants.ERROR;
		endId = getLastId();
		isClosed = true;		
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
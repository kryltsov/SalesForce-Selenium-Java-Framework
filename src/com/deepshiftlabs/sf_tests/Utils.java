package com.deepshiftlabs.sf_tests;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.*;

import org.apache.log4j.Logger;

/**
 * Represents class which contains small auxiliary functions.
 * It has a object of log4j Logger which is used for logging to console and files (exclude HTML reports).
 * logger is static member and as there may be several Utils objects at the same time (at least one for each parallel test
 * executed) all this objects are using ONLY ONE logger. 
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
 
public class Utils {
        private static Logger logger = null;

        static {
        	if (logger==null){
        		logger = Logger.getLogger(Utils.class);
        	}        	
        }
        
        public Utils(){
        }
        
        public void info (String message){
             logger.info(message);
        }
        public void warn (String message){
             logger.warn(message);
        }
        public void error (String message){
             logger.error(message);
        }
        
        public void fatal (String message){
            logger.fatal(message);
       }
        
        /**
         * @param tempString String that may contain symbols that should be preceded by escape symbols
         * @return Processed string.
         */
        static public String prepareForJavaScript(String tempString){
        	tempString = tempString.replace("'", "\\'");
        	return tempString;
        }
        
        /**
         * @param locators locators to elements which should be present on page
         * @return JavaScript which will determine if all needed elements on page loaded.
         * @see CommonActions#waitForCondition(String, String) 
         */
        static public String prepareCondition (ArrayList <String> locators){ 
			String tempScript = "var result = false; ";
			
			if (locators.size()>0) {
				tempScript = tempScript+ "result = selenium.isElementPresent('"+locators.get(0)+"');";
			}
			for (int i=1; i<locators.size(); i++){
				tempScript = tempScript+ "result = result && selenium.isElementPresent('"+locators.get(i)+"');";
			}
			return tempScript;
        }
        
        static public String generateScreenshotName(boolean isError){
        	String filename;
			Date d = new Date();
			String status;
			
			DateFormat df = new SimpleDateFormat(Settings.SCREENSHOTS_POSTFIX_FORMAT);
			
			if (isError) status="(ERR)"; else {status="";}
			
			filename = (Settings.SCREENSHOTS_PREFIX+df.format(d)+status+".png");
			return filename;
        }
        
        static public String generateScreenshotName(){
			return generateScreenshotName(false);
        }
        
        /**
         * If not exist creates directory and return absolute path to it. 
         * @param dirPath absolute or relative path to directory (e.g. "logs\\screenshots\\"). 
         * If relative, build.xml directory is assumed as parent. 
         * @return Absolute path to dirPath.
         */
        static public String prepareDir(String dirPath){
    		File f = new File(dirPath);
    		if (!f.exists()){
    			f.mkdirs();
    		}
    		return f.getAbsolutePath()+"\\";
        }
        
        static public void waitForEnterPressed(){
        	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        	try{
        		System.out.println("------------------PRESS ENTER-------------------");
        		stdin.read();}
        	catch(IOException e) {}; 
        }
}
package com.deepshiftlabs.sf_tests;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
//import org.apache.log4j.Level;
//import org.apache.log4j.SimpleLayout;
//import org.apache.log4j.FileAppender.*;

/**
  * @author bear
  * @date 05 Jan 2009
 */
 

public class Utils {
        private static Logger logger = null;

        static {
        	if (logger==null){
        		logger = Logger.getLogger(Utils.class);
        	}        	
        }
        
        public Utils(){
// TODO why this constructor is called 4 times in login-logout test        	
        	System.out.println("CONSTRUCTOR");
        }
        
        public void info (String message){
             logger.info(message);
        }
        public void warn (String message){
             logger.warn(message);
        }
        public void error (String message){
             logger.error("(!)"+message+"(!)");
        }
        
        public void fatal (String message){
            logger.fatal("(!!!)"+message+"(!!!)");
       }
        
        static public String prepareForJavaScript(String tempString){

        	tempString = tempString.replace("'", "\\'");
        	
        	return tempString;
        }
        
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
			
			if (isError) status="(ERR)"; else {status="OK";}
			
			filename = (Settings.SCREENSHOTS_PREFIX+df.format(d)+status+".png");
			return filename;
        }
        
        static public String generateScreenshotName(){
			return generateScreenshotName(false);
        }
        
// returns absolute path to dir        
        static public String prepareDir(String dirPath){
    		File f = new File(dirPath);
    		if (!f.exists()){
    			f.mkdirs();
    		}
    		return f.getAbsolutePath()+"\\";
        }
}
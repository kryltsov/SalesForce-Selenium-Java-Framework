package com.deepshiftlabs.sf_tests;


import java.util.ArrayList;

import org.apache.log4j.Logger;
//import org.apache.log4j.Level;
//import org.apache.log4j.SimpleLayout;
//import org.apache.log4j.FileAppender.*;

/**
  * @author bear
  * @date 05 Jan 2009
 */
 

public class Utils {
        public static Logger logger = null;

        protected Utils(){
            logger = Logger.getLogger(Utils.class);
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
}


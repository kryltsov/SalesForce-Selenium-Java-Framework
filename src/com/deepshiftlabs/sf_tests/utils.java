package com.deepshiftlabs.sf_tests;


import org.apache.log4j.Logger;
//import org.apache.log4j.Level;
//import org.apache.log4j.SimpleLayout;
//import org.apache.log4j.FileAppender.*;

/**
  * @author bear
  * @date 05 Jan 2009
 */
 

public class utils {
        public static Logger logger = null;

        protected utils(){
            logger = Logger.getLogger(utils.class);
        }
        public void info (String message){
             logger.info(message);
        }
        public void warn (String message){
             logger.warn(message);
        }
        public void error (String message){
             logger.error("!!!---  "+message+"  ---!!!");
        }
}


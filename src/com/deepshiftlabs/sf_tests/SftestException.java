package com.deepshiftlabs.sf_tests;

/**
 * Represent a exception class that can be throw by sf_tests.
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 *
 */
public class SftestException extends Exception {
	private String message; 

	/**
	 * @param a_message description of exception, that will be returned by toString() method
	 */
	SftestException(String a_message) { 
		message = a_message; 
	}
	
	public String toString() { 
		return message; 
	}
}

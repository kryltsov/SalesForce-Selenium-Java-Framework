package com.deepshiftlabs.sf_tests;

public class sftestException extends Exception {
	private String message; 

	sftestException(String a_message) { 
		message = a_message; 
	}
	
	public String toString() { 
		return "Exception message is: "+ message; 
	}
}

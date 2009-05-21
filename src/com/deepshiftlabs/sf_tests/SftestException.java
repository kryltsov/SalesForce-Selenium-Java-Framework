package com.deepshiftlabs.sf_tests;

public class SftestException extends Exception {
	private String message; 

	SftestException(String a_message) { 
		message = a_message; 
	}
	
	public String toString() { 
		return "Exception resultMessage is: "+ message; 
	}
}

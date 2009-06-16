package com.deepshiftlabs.sf_tests;

/**
 * Represents GenericObject for login-logout test. Stores needed elements and check sequences.   
 * @author Yakubovskiy Dima, bear@deepshiftlabs.com
 * @see GenericObject
 *
 */
public class LoginLogout extends GenericObject {

	public LoginLogout (String a_parentTabID, String a_myRecordId, String a_defaultTitleSingular ) {
		super(a_parentTabID, a_myRecordId, a_defaultTitleSingular);
    }

	/**
	 * As it's not usual Salesforce object like websites, we should provide self method for values init. 
	 * @return RET_OK, RET_ERROR if can't find in elements list one of needed elements ("User Name" or "Password").
	 */
	public int initValues(){
		GenericElement username;
		GenericElement password;
		
		int usernameId;
		int passwordId;

		usernameId = findElementIndexByName("User Name");
		passwordId = findElementIndexByName("Password");
		
		if (usernameId==Constants.RET_ERROR || 
				passwordId==Constants.RET_ERROR){
			return Constants.RET_ERROR;
		}

		// we should remember this is only links to elements in array		
		username = elements.get(usernameId);
		password = elements.get(passwordId);
		
		username.setValidValue(Settings.SF_LOGIN);
		password.setValidValue(Settings.SF_PASSWORD);
		
		username.setInvalidValue(Constants.INVALID_LOGIN_VALUE);
		password.setInvalidValue(Constants.INVALID_LOGIN_VALUE);
		
		return Constants.RET_OK;		
	}
	
	/**
	 * Check Salesforce reaction on login attempt with invalid values. 
	 * @return RET_OK, RET_ERROR.
	 */
	public int checkWrongValues(){
		Event event = action.startEvent("checkWrongValues", name);
		
		if (fillElementsByInvalidValues()== Constants.RET_ERROR){
			action.closeEventFatal(event);
			return Constants.RET_ERROR;
		}
		if (action.pressButton(Constants.LOGIN_LOCATOR)==Constants.RET_ERROR){
			action.closeEventFatal(event);
			return Constants.RET_ERROR;
		}
		if (!action.isTextPresent(Constants.LOGIN_FAILED_ERROR)){
			// TODO maybe it's fatal situation?
			action.closeEventError(event);
			return Constants.RET_OK;
		}
		action.closeEventOk(event);
		return Constants.RET_OK;
	}
	
	/**
	 * Check if we can login with right credentials. Checks titles.  
	 * @return RET_OK, RET_ERROR.
	 */
	public int checkLoginLogout(){
		Event event = action.startEvent("checkLoginLogout", name);
		fillElementsByValidValues();
		if (action.pressButton(Constants.LOGIN_LOCATOR)==Constants.RET_ERROR){
			action.closeEventFatal(event);
			return Constants.RET_ERROR;
		}
		if (action.isTextPresent(Constants.LOGIN_FAILED_ERROR)){
			action.closeEventFatal(event);
			return Constants.RET_ERROR;
		}
		if (action.isTextPresent(Constants.BAD_IP_ERROR)){
			action.closeEventFatal(event);
			return Constants.RET_ERROR;
		}
		if (checkTitle(Constants.HOME_PAGE_TITLE, "Home")==Constants.RET_ERROR){
			action.closeEventFatal(event);
			return Constants.RET_ERROR;
		}
		if (action.click(Constants.LOGOUT_LOCATOR)==Constants.RET_ERROR){
			action.closeEventFatal(event);
			return Constants.RET_ERROR;
		}
		if (checkTitle(Constants.TITLE_LOGOUT_PAGE, "Logged out")==Constants.RET_ERROR){
			action.closeEventFatal(event);
			return Constants.RET_ERROR;
		}		
		action.closeEventOk(event);
		return Constants.RET_OK;
	}
	
	/**
	 * Opens login page and checks if all needed elements are presented and title is right.
	 * @return RET_OK, RET_ERROR if elements missed or title is invalid.
	 */
	public int openWithElementsAndTitleCheck(){
		Event event = action.startEvent("openWithElementsAndTitleCheck", name);
// TODO see if I should move openUrl command from here		
    	action.openUrl("/");
    	if (missedElementsCount()>0){
    		action.closeEventFatal(event);
    		return Constants.RET_ERROR;
    	}
    	
    	if (checkTitle(Constants.TITLE_LOGIN_PAGE, "Login") == Constants.RET_ERROR){
    		action.closeEventFatal(event);
    		return Constants.RET_ERROR;
    	}
    	action.closeEventOk(event);
    	return Constants.RET_OK;    	
	}
	
    /* 
     * Do all needed checks and common sequences checks.
     * @see com.deepshiftlabs.sf_tests.GenericObject#checkAll()
     * @return RET_OK
     * @throws SftestException if any check is failed.
     */
    public int checkAll() throws SftestException{
    	Event event = action.startEvent("checkAll", name);
    	if (initValues() == Constants.RET_ERROR){
    		action.closeEventFatal(event, "init failed");
    		throw new SftestException("Wrong LoginLogout class configuration.");
    	}
    	
    	if (openWithElementsAndTitleCheck() == Constants.RET_ERROR){
    		action.closeEventFatal(event, "Wrong login page");
    		throw new SftestException("Wrong login page");
    	}
    	
    	if (checkWrongValues() == Constants.RET_ERROR){
    		action.closeEventFatal(event);
    		throw new SftestException("Wrong values check failed");
    	}
    	
    	if (checkWrongValues() == Constants.RET_ERROR){
    		action.closeEventFatal(event);
    		throw new SftestException("Wrong values check failed");
    	}

    	if (openWithElementsAndTitleCheck() == Constants.RET_ERROR){
    		action.closeEventFatal(event, "Wrong login page");
    		throw new SftestException("Wrong login page");
    	}
    	
    	if (checkLoginLogout() == Constants.RET_ERROR){
    		action.closeEventFatal(event, "can't login or logout");
    		throw new SftestException("can't login or logout");
    	}
    	
    	action.closeEventOk(event);
    	return Constants.RET_OK;
    }
}
package com.deepshiftlabs.sf_tests;

public class LoginLogout extends GenericObject {

	public LoginLogout (String a_parentTabID, String a_myRecordId, String a_defaultTitleSingular ) {
		super(a_parentTabID, a_myRecordId, a_defaultTitleSingular);
    }

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
	
	public int checkWrongValues(){
		Event event = action.startEvent(name, "checkWrongValues");
		
		if (fillElementsByInvalidValues()== Constants.RET_ERROR){
			action.fatal("Can't fill by invalid values");
			action.closeEventFatal(event, "Can't fill by invalid values");
			return Constants.RET_ERROR;
		}
		if (action.pressButton(Constants.LOGIN_LOCATOR)==Constants.RET_ERROR){
			action.fatal("Can't press on login button.");
			action.closeEventFatal(event, "Can't press on login button");
			return Constants.RET_ERROR;
		}
		if (!action.isTextPresent(Constants.LOGIN_FAILED_ERROR)){
			// TODO maybe it's fatal situation?
			action.error("Error when doing checkWrongValues - there is no error message on page!");
			action.closeEventError(event);
			return Constants.RET_OK;
		}
		action.info("Login with wrong values failed (OK)");
		action.closeEventOk(event);
		return Constants.RET_OK;
	}
	
	public int checkLoginLogout(){
		Event event = action.startEvent(name, "checkLoginLogout");
		fillElementsByValidValues();
		if (action.pressButton(Constants.LOGIN_LOCATOR)==Constants.RET_ERROR){
			action.fatal("Can't press on login button.");
			action.closeEventFatal(event, "Can't press on login button.");
			return Constants.RET_ERROR;
		}
		if (action.isElementPresent(Constants.LOGIN_FAILED_ERROR)){
			action.fatal("Error when logging in with valid values - there is error message on page!");
			action.closeEventFatal(event, "there is error message on page");
			return Constants.RET_ERROR;
		}
		if (action.isTextPresent(Constants.BAD_IP_ERROR)){
			action.fatal("Error when logging in with valid values - your IP is not approved.");
			action.closeEventFatal(event, "IP is not approved");
			return Constants.RET_ERROR;
		}
		if (checkTitle(Constants.HOME_PAGE_TITLE, "Home")==Constants.RET_ERROR){
			action.fatal("Error when logging in with valid values - title is wrong.");
			action.closeEventFatal(event, "title is wrong");
			return Constants.RET_ERROR;
		}
		if (action.click(Constants.LOGOUT_LOCATOR)==Constants.RET_ERROR){
			action.fatal("Can't press on logout.");
			action.closeEventFatal(event, "Can't press on logout");
			return Constants.RET_ERROR;
		}
		if (checkTitle(Constants.TITLE_LOGOUT_PAGE, "Logged out")==Constants.RET_ERROR){
			action.fatal("Error when logging out with valid values - title is wrong.");
			action.closeEventFatal(event, "title is wrong");
			return Constants.RET_ERROR;
		}		
		action.closeEventOk(event);
		return Constants.RET_OK;
	}
	
	public int openWithElementsAndTitleCheck(){
		Event event = action.startEvent(name, "openWithElementsAndTitleCheck");
    	action.open("/");
    	if (checkElementsPresence()>0){
    		action.fatal("Can't find all inputs to login!");
    		action.closeEventFatal(event, "Can't find all inputs to login");
    		return Constants.RET_ERROR;
    	}
    	
    	if (checkTitle(Constants.TITLE_LOGIN_PAGE, "Login") == Constants.RET_ERROR){
    		action.fatal("Bad title of login page!");
    		action.closeEventFatal(event, "title is wrong");
    		return Constants.RET_ERROR;
    	}
    	action.closeEventOk(event);
    	return Constants.OK;    	
	}
	
    public int checkAll() throws SftestException{
    	Event event = action.startEvent(name, "checkAll");
    	if (initValues() == Constants.RET_ERROR){
    		action.fatal("LoginLogout class has no appropriate username and password elements!");
    		action.closeEventFatal(event, "init failed");
    		throw new SftestException("Wrong LoginLogout class configuration.");
    	}
    	
    	if (openWithElementsAndTitleCheck() == Constants.RET_ERROR){
    		action.fatal("Wrong login page");
    		action.closeEventFatal(event, "Wrong login page");
    		throw new SftestException("Wrong login page");
    	}
    	
    	if (checkWrongValues() == Constants.RET_ERROR){
    		action.fatal("Wrong values check failed");
    		action.closeEventFatal(event, "Wrong values check failed");
    		throw new SftestException("Wrong values check failed");
    	}
    	
    	if (checkWrongValues() == Constants.RET_ERROR){
    		action.fatal("Wrong values check failed");
    		action.closeEventFatal(event, "Wrong values check failed");
    		throw new SftestException("Wrong values check failed");
    	}

    	if (openWithElementsAndTitleCheck() == Constants.RET_ERROR){
    		action.fatal("Wrong login page");
    		action.closeEventFatal(event, "Wrong login page");
    		throw new SftestException("Wrong login page");
    	}
    	
    	if (checkLoginLogout() == Constants.RET_ERROR){
    		action.fatal("can't login or logour");
    		action.closeEventFatal(event, "can't login or logour");
    		throw new SftestException("can't login or logour");
    	}
    	
    	action.closeEventOk(event);
    	return Constants.RET_OK;
    }
}

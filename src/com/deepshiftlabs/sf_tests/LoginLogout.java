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
	
	public int checkWrongValues()throws SftestException{
		
		fillElementsByInvalidValues();
		if (action.pressButton(Constants.LOGIN_LOCATOR)==Constants.RET_ERROR){
			action.fatal("Can't press on login button.");
    		throw new SftestException("Can't press login button.");
		}
		if (!action.isTextPresent(Constants.LOGIN_FAILED_ERROR)){
			// TODO maybe it's fatal situation?
			action.error("Error when doing checkWrongValues - there is no error message on page!");
			return Constants.RET_OK;
		}
		action.info("Login with wrong values failed (OK)");
		return Constants.RET_OK;
	}
	
	public int checkLoginLogout()throws SftestException{
		fillElementsByValidValues();
		if (action.pressButton(Constants.LOGIN_LOCATOR)==Constants.RET_ERROR){
			action.fatal("Can't press on login button.");
    		throw new SftestException("Can't press login button.");
		}
		if (action.isElementPresent(Constants.LOGIN_FAILED_ERROR)){
			action.fatal("Error when logging in with valid values - there is error message on page!");
			throw new SftestException("Unwaited error.");
		}
		if (action.isTextPresent(Constants.BAD_IP_ERROR)){
			action.fatal("Error when logging in with valid values - your IP is not approved.");
			throw new SftestException("Salesforce Bad IP error.");
		}
		if (checkTitle(Constants.HOME_PAGE_TITLE, "Home")==Constants.RET_ERROR){
			action.fatal("Error when logging in with valid values - title is wrong.");
			throw new SftestException("Bad login.");
		}
		if (action.click(Constants.LOGOUT_LOCATOR)==Constants.RET_ERROR){
			action.fatal("Can't press on logout.");
    		throw new SftestException("Can't pres logout link.");
		}
		if (checkTitle(Constants.TITLE_LOGOUT_PAGE, "Logged out")==Constants.RET_ERROR){
			action.fatal("Error when logging out with valid values - title is wrong.");
			throw new SftestException("Bad logout.");
		}		
		return Constants.RET_OK;
	}
	
	public void openWithElementsAndTitleCheck() throws SftestException{
    	action.open("/");
    	if (checkElementsPresence()>0){
    		action.fatal("Can't find all inputs to login!");
    		throw new SftestException("Can't find inputs.");
    	}
    	
    	if (checkTitle(Constants.TITLE_LOGIN_PAGE, "Login") == Constants.RET_ERROR){
    		action.fatal("Bad title of login page!");
    		throw new SftestException("Bad title of login page.");
    	}		
	}
	
    public int checkAll() throws SftestException{
   
    	if (initValues() == Constants.RET_ERROR){
    		action.fatal("LoginLogout class has no appropriate username and password elements!");
    		throw new SftestException("Wrong LoginLogout class configuration.");
    	}
    	openWithElementsAndTitleCheck();
    	checkWrongValues();

    	openWithElementsAndTitleCheck();    	
    	checkLoginLogout();
    	
    	return Constants.RET_OK;
    }
}

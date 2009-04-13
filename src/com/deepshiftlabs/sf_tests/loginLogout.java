package com.deepshiftlabs.sf_tests;

public class loginLogout extends genericObject {

	public loginLogout (String a_parentTabID, String a_myRecordId, String a_defaultTitleSingular ) {
		super(a_parentTabID, a_myRecordId, a_defaultTitleSingular);
    }

	public int initValues(){
		genericElement username;
		genericElement password;
		
		int usernameId;
		int passwordId;

		usernameId = findElementIndexByName("User Name");
		passwordId = findElementIndexByName("Password");
		
		if (usernameId==constants.RET_ERROR || 
				passwordId==constants.RET_ERROR){
			return constants.RET_ERROR;
		}

		// we should remember this is only links to elements in array		
		username = elements.get(usernameId);
		password = elements.get(passwordId);
		
		username.setValidValue(settings.SF_LOGIN);
		password.setValidValue(settings.SF_PASSWORD);
		
		username.setInvalidValue(constants.INVALID_LOGIN_VALUE);
		password.setInvalidValue(constants.INVALID_LOGIN_VALUE);
		
		return constants.RET_OK;		
	}
	
	public int checkWrongValues()throws sftestException{
		
		fillElementsByInvalidValues();
		if (action.pressButton(sInstance, constants.LOGIN_LOCATOR)==constants.RET_ERROR){
			action.fatal("Can't press on login button.");
    		throw new sftestException("Can't press login button.");
		}
		if (!action.isTextPresent(sInstance, constants.LOGIN_FAILED_ERROR)){
			// TODO maybe it's fatal situation?
			action.error("Error when doing checkWrongValues - there is no error message on page!");
			return constants.RET_OK;
		}
		action.info("Login with wrong values failed (OK)");
		return constants.RET_OK;
	}
	
	public int checkLoginLogout()throws sftestException{
		fillElementsByValidValues();
		if (action.pressButton(sInstance, constants.LOGIN_LOCATOR)==constants.RET_ERROR){
			action.fatal("Can't press on login button.");
    		throw new sftestException("Can't press login button.");
		}
		if (action.isElementPresent(sInstance, constants.LOGIN_FAILED_ERROR)){
			action.fatal("Error when logging in with valid values - there is error message on page!");
			throw new sftestException("Unwaited error.");
		}
		if (action.isTextPresent(sInstance, constants.BAD_IP_ERROR)){
			action.fatal("Error when logging in with valid values - your IP is not approved.");
			throw new sftestException("Salesforce Bad IP error.");
		}
		if (checkTitle(constants.HOME_PAGE_TITLE, "Home")==constants.RET_ERROR){
			action.fatal("Error when logging in with valid values - title is wrong.");
			throw new sftestException("Bad login.");
		}
		if (action.click(sInstance, constants.LOGOUT_LOCATOR)==constants.RET_ERROR){
			action.fatal("Can't press on logout.");
    		throw new sftestException("Can't pres logout link.");
		}
		if (checkTitle(constants.TITLE_LOGOUT_PAGE, "Logged out")==constants.RET_ERROR){
			action.fatal("Error when logging out with valid values - title is wrong.");
			throw new sftestException("Bad logout.");
		}		
		return constants.RET_OK;
	}
	
	public void openWithElementsAndTitleCheck() throws sftestException{
    	action.open(sInstance, "/");
    	if (checkElementsPresence()>0){
    		action.fatal("Can't find all inputs to login!");
    		throw new sftestException("Can't find inputs.");
    	}
    	
    	if (checkTitle(constants.TITLE_LOGIN_PAGE, "Login") == constants.RET_ERROR){
    		action.fatal("Bad title of login page!");
    		throw new sftestException("Bad title of login page.");
    	}		
	}
	
    public int checkAll() throws sftestException{
   
    	if (initValues() == constants.RET_ERROR){
    		action.fatal("loginLogout class has no appropriate username and password elements!");
    		throw new sftestException("Wrong loginLogout class configuration.");
    	}
    	openWithElementsAndTitleCheck();
    	checkWrongValues();

    	openWithElementsAndTitleCheck();    	
    	checkLoginLogout();
    	
    	return constants.RET_OK;
    }
}

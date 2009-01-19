package com.deepshiftlabs.sf_tests;

import org.testng.annotations.Test;

/**
 */
 
public class doLoginLogout extends sf_tests_class {

    @Test(groups = {"sf_tests"}, description = "login_logout_test")
    public void do_login_logout() throws Throwable {
        login();
        logout();
    }
    
    @Test(groups = {"sf_tests"}, description = "login_logout_test2")
    public void do_login_logout_2() throws Throwable {
        login();
        logout();
    }    
    
        @Test(groups = {"sf_tests"}, description = "login_logout_test3")
    public void do_login_logout_3() throws Throwable {
        login();
        logout();
    }    

}
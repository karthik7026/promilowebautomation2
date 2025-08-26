package com.promilo.automation.signupandlogin;

import org.testng.annotations.Test;
import com.aventstack.extentreports.*;
import com.microsoft.playwright.*;
import com.promilo.automation.pageobjects.signuplogin.*;
import com.promilo.automation.resources.*;


public class BlankFieldLoginValidation extends Baseclass {

    @Test
    public void blankFieldSignupValidation() throws Exception { 
    	
    	Page page = initializePlaywright();
    	page.setViewportSize(1366, 768);


        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("Promilo Staging - Blank Field Signup Validation");

        test.info("🚀 Test started: Blank Field Signup Validation");

        page.navigate(prop.getProperty("url"));
        test.info("🌐 Navigated to URL: " + prop.getProperty("url"));

LandingPage landingPage = new LandingPage(page);
Thread.sleep(3000);

landingPage.dismissPopup();

        landingPage.clickLoginButton();
        test.info("✅ Signup button clicked.");

LoginPage loginpage = new LoginPage(page);
Locator loginButton = loginpage.loginButton();

        if (loginButton.isDisabled()) {
            test.pass("login  button is disabled when fields are empty, as expected.");
        } else {
            test.fail("❌ login button should be disabled when fields are empty.");
        }

        page.close();
        test.info("🧹 Browser closed.");
        extent.flush();
    }
}

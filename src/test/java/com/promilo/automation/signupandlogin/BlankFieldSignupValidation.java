package com.promilo.automation.signupandlogin;

import org.testng.annotations.Test;
import com.aventstack.extentreports.*;
import com.microsoft.playwright.*;
import com.promilo.automation.pageobjects.signuplogin.*;
import com.promilo.automation.resources.*;


public class BlankFieldSignupValidation extends Baseclass {

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
        try {
            Locator popup = landingPage.getPopup();
            if (popup.isVisible()) {
                popup.click();
                test.info("✅ Popup closed.");
            } else {
                test.info("ℹ️ No popup detected, continuing...");
            }
        } catch (Exception e) {
            test.info("ℹ️ No popup detected, continuing...");
        }

        landingPage.clickSignup();
        test.info("✅ Signup button clicked.");

        CreateAccountpage accountPage = new CreateAccountpage(page);
        Locator sendCodeButton = accountPage.getSendCodeButton();

        if (sendCodeButton.isDisabled()) {
            test.pass("✅ Send Code button is disabled when fields are empty, as expected.");
        } else {
            test.fail("❌ Send Code button should be disabled when fields are empty.");
        }

        page.close();
        test.info("🧹 Browser closed.");
        extent.flush();
    }
}

package com.promilo.automation.registereduser.jobs;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.promilo.automation.pageobjects.signuplogin.JobListingPage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExtentManager;
import com.promilo.automation.resources.SignupWithMailosaurUI;

public class AskUsWithInvalidData extends Baseclass {

    private static String registeredEmail = null;
    private static String registeredPassword = null;

    @BeforeSuite
    public void performSignupOnce() throws Exception {
        System.out.println("⚙️ Performing signup ONCE before suite using Mailosaur UI signup...");

        SignupWithMailosaurUI signupWithMailosaur = new SignupWithMailosaurUI();
        String[] creds = signupWithMailosaur.performSignupAndReturnCredentials();

        registeredEmail = creds[0];
        registeredPassword = creds[1];

        System.out.println("✅ Signup completed. Registered user: " + registeredEmail);
    }

    @DataProvider(name = "jobApplicationData")
    public Object[][] jobApplicationData() {
        return new Object[][] {
            // TestCaseID , Keyword , Name , OTP , Mobile , TextArea
            { "TC01", "Askus?", "123 User", "9999", "", "Feedback from automation" }
        };
    }

    @Test(dataProvider = "jobApplicationData")
    public void applyForJobAsRegisteredUser(
            String testCaseId, String keyword, String name, String otp,
            String mailphone, String textArea) throws Exception {

        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("🚀 Apply for Job (AskUs) | " + testCaseId);

        // Only run Askus? keyword
        if (!keyword.equalsIgnoreCase("Askus?")) {
            test.info("⏭️ Skipping TestCaseID: " + testCaseId + " due to keyword: " + keyword);
            throw new SkipException("Skipping as keyword is " + keyword);
        }

        if (registeredEmail == null || registeredPassword == null) {
            test.fail("❌ Signup credentials not found.");
            Assert.fail("Signup not completed.");
            return;
        }

        // 🔑 Use dynamic signup creds
        String email = registeredEmail;
        String password = registeredPassword;

        Page page = initializePlaywright();
        page.navigate(prop.getProperty("url"));
        page.setViewportSize(1280, 800);

        // --- Login Flow ---
        LandingPage landingPage = new LandingPage(page);
        try {
            landingPage.getPopup().click();
        } catch (Exception ignored) {}
        landingPage.clickLoginButton();

        LoginPage loginPage = new LoginPage(page);
        loginPage.loginMailPhone().fill(email);
        loginPage.passwordField().fill(password);
        loginPage.loginButton().click();

        // --- Apply Job Flow (AskUs Form) ---
        JobListingPage homePage = new JobListingPage(page);
        page.waitForTimeout(3000);
        homePage.homepageJobs().click();
        
        page.locator("//p[text()='Crime Scene Cleaner (Trauma & Biohazard Technician']").first().click();
        page.waitForTimeout(2000);
        page.locator("//button[text()='Ask us?']").first().click();

        page.locator("//input[@name='userName']").fill(name);
        
     System.out.println(page.locator("//div[text()='Invalid User Name, only letters and spaces are allowed, and it cannot start with a space']").textContent());   

    
        // Fill the number
        page.locator("//input[@placeholder='Mobile*']").fill("1233");
        System.out.println(page.locator("//div[@class='text-danger']").nth(1).textContent());  

        
        
        page.locator("//textarea[@id='feedbackDetails']").fill(textArea);

        homePage.askUsSubmitButton().click();

      
       

        extent.flush();
    }
}

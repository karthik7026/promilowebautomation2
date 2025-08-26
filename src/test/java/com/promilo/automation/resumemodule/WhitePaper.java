package com.promilo.automation.resumemodule;

import com.microsoft.playwright.*;
import com.aventstack.extentreports.*;
import com.promilo.automation.pageobjects.myresume.AccomplishmentsPage;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.promilo.automation.resources.SignupWithMailosaurUI;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class WhitePaper extends Baseclass {

    private static String registeredEmail = null;
    private static String registeredPassword = null;

    @BeforeSuite
    public void performSignupOnce() throws Exception {
        System.out.println("⚙️ Performing signup ONCE before suite using Mailosaur...");

        SignupWithMailosaurUI signupWithMailosaur = new SignupWithMailosaurUI();
        String[] creds = signupWithMailosaur.performSignupAndReturnCredentials();

        registeredEmail = creds[0];
        registeredPassword = creds[1];

        System.out.println("✅ Signup completed. Registered user: " + registeredEmail);
    }

    @Test
    public void whitePaperTest() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("📄 White Paper Submission | Excel Keyword Driven");

        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata",
                "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

        String testCaseId = excel.getCellData(2, 0);    // TestCaseID
        String whitePaperTitle = excel.getCellData(2, 11);
        String url = excel.getCellData(2, 12);
        String year = excel.getCellData(2, 13);
        String month = excel.getCellData(2, 14);
        String description = excel.getCellData(2, 15);

        Page page = initializePlaywright();

        try {
            page.navigate(prop.getProperty("url"));
            page.setViewportSize(1000, 768);

            // Landing Page
            LandingPage landingPage = new LandingPage(page);
            try {
                landingPage.getPopup().click(new Locator.ClickOptions().setTimeout(5000));
                test.info("✅ Closed popup.");
            } catch (PlaywrightException e) {
                test.info("ℹ️ No popup to close.");
            }
            landingPage.clickLoginButton();

            // Login with Mailosaur creds
            if (registeredEmail == null || registeredPassword == null) {
                test.fail("❌ Signup credentials not available. Aborting test.");
                return;
            }

            LoginPage loginPage = new LoginPage(page);
            loginPage.loginMailPhone().fill(registeredEmail);
            loginPage.passwordField().fill(registeredPassword);
            loginPage.loginButton().click();
            test.info("🔐 Logged in as " + registeredEmail);

            // Navigate to Resume > WhitePaper
            MyResumePage resumePage = new MyResumePage(page);
            resumePage.Mystuff().click();
            resumePage.MyAccount().click();
            resumePage.MyResume().click();
            resumePage.AddWhitePaper().click();

            // Fill Whitepaper form
            AccomplishmentsPage whitePaper = new AccomplishmentsPage(page);
            whitePaper.whitepaperTitleTextField().fill(whitePaperTitle);
            whitePaper.whitepaperUrlField().fill(url);
            whitePaper.whitepaperDescriptionBox().fill(description);

            Locator yearDrop = whitePaper.whitepaperPublishedOnYear();
            yearDrop.click();
            yearDrop.fill(year);
            page.keyboard().press("Enter");

            Locator monthDrop = whitePaper.whitepaperPublishedOnMonth();
            monthDrop.click();
            page.keyboard().type(month);
            page.keyboard().press("Enter");

            whitePaper.whitepaperSaveButton().click();
            test.pass("✅ Submitted whitepaper for TestCaseID: " + testCaseId);

            // Screenshot
            String screenshotPath = System.getProperty("user.dir") + "/screenshots/" + testCaseId + "_whitepaper.png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)).setFullPage(true));
            test.addScreenCaptureFromPath(screenshotPath, "📸 WhitePaper Screenshot");

        } catch (Exception e) {
            test.fail("❌ TestCaseID: " + testCaseId + " failed: " + e.getMessage());
            throw e;
        } finally {
            closePlaywright();
            test.info("🧹 Closed browser for TestCaseID: " + testCaseId);
        }
    }
}

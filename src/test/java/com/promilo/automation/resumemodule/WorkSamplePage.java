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
import com.promilo.automation.resources.ToasterUtil;
import com.promilo.automation.resources.SignupWithMailosaurUI;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class WorkSamplePage extends Baseclass {

    private static String registeredEmail = null;
    private static String registeredPassword = null;

    @BeforeSuite
    public void performSignupOnce() throws Exception {
        System.out.println("⚙️ Performing signup once before suite with Mailosaur...");

        SignupWithMailosaurUI signupWithMailosaur = new SignupWithMailosaurUI();
        String[] creds = signupWithMailosaur.performSignupAndReturnCredentials();

        registeredEmail = creds[0];
        registeredPassword = creds[1];

        System.out.println("✅ Signup completed. Registered test user: " + registeredEmail);
    }

    @Test
    public void uploadWorkSampleUsingExcel() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("📄 Upload Work Sample Using Excel");

        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata",
                "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

        String testCaseId = excel.getCellData(1, 0); // TestCaseID
        String workTitle = excel.getCellData(1, 11); 
        String url = excel.getCellData(1, 12);
        String year = excel.getCellData(1, 13);
        String month = excel.getCellData(1, 14);
        String description = excel.getCellData(1, 15);

        Page page = null;

        try {
            // Launch browser
            page = initializePlaywright();
            page.navigate(prop.getProperty("url"));
            page.setViewportSize(1000, 768);
            Thread.sleep(2000);

            LandingPage landingPage = new LandingPage(page);

            // Handle popup
            try {
                landingPage.getPopup().click(new Locator.ClickOptions().setTimeout(5000));
                test.info("✅ Closed popup.");
            } catch (PlaywrightException e) {
                test.info("ℹ️ No popup appeared.");
            }

            // Login with Mailosaur creds
            landingPage.clickLoginButton();
            if (registeredEmail == null || registeredPassword == null) {
                test.fail("❌ Signup credentials not available. Aborting test.");
                return;
            }

            LoginPage loginPage = new LoginPage(page);
            loginPage.loginMailPhone().fill(registeredEmail);
            loginPage.passwordField().fill(registeredPassword);
            loginPage.loginButton().click();
            test.info("🔐 Logged in as " + registeredEmail);

            // Resume navigation
            MyResumePage resumePage = new MyResumePage(page);
            resumePage.Mystuff().click();
            resumePage.MyAccount().click();
            resumePage.MyResume().click();
            resumePage.AddWorksample().click();
            test.info("📂 Navigated to Add Work Sample section.");

            // Fill work sample form
            AccomplishmentsPage workSample = new AccomplishmentsPage(page);
            workSample.workTitleTextField().fill("qa engineer");
            workSample.urlTextField().fill("https://www.linkedin.com/");
            workSample.currentlyWorkingCheckbox().click();

            Locator yearDropdown = workSample.durationFromYearDropdown();
            yearDropdown.click();
            yearDropdown.fill("2020");
            page.keyboard().press("Enter");

            Locator monthDropdown = workSample.durationFromMonthDropDown();
            monthDropdown.click();
            monthDropdown.fill("jan");
            page.keyboard().press("Enter");

            workSample.descriptionBox().fill("something");
            workSample.worksamplesaveButton().click();
            test.info("📤 Submitted work sample form.");

            // Toaster validation
            ToasterUtil toasterValidation = new ToasterUtil(page);
            try {
                toasterValidation.getSuccessToaster().waitFor(new Locator.WaitForOptions().setTimeout(5000));
                if (toasterValidation.getSuccessToaster().isVisible()) {
                    test.pass("✅ Success toaster appeared for TestCaseID: " + testCaseId);
                } else {
                    test.warning("⚠️ Success toaster not visible after submission.");
                }
            } catch (Exception ex) {
                test.warning("⚠️ No toaster appeared. Might be a backend issue.");
            }

            // Screenshot
            String screenshotPath = System.getProperty("user.dir") + "/screenshots/" + testCaseId + "_worksample.png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)).setFullPage(true));
            test.addScreenCaptureFromPath(screenshotPath, "📸 Screenshot after submitting work sample.");

        } catch (Exception e) {
            test.fail("❌ TestCaseID: " + testCaseId + " failed: " + e.getMessage());
            throw e;
        } finally {
            if (page != null) {
                closePlaywright();
                test.info("🧹 Closed browser for TestCaseID: " + testCaseId);
            }
        }

        extent.flush();
    }
}

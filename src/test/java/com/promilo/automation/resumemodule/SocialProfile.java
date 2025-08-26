package com.promilo.automation.resumemodule;

import com.aventstack.extentreports.*;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.promilo.automation.pageobjects.myresume.AccomplishmentsPage;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.promilo.automation.resources.ToasterUtil;

import org.testng.annotations.Test;

import java.nio.file.Paths;

public class SocialProfile extends Baseclass {

    @Test
    public void addSocialProfileTest() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("📎 Add Social Profile | TC_AddSocialProfile");

        Page page = null;
        String testCaseId = null;

        try {
            // Load Excel test data
            String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
            ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

            // Row 4 (index starts from 0, this is the 5th row in Excel)
            testCaseId = excel.getCellData(4, 0);  // TC_AddSocialProfile
            String keyword = excel.getCellData(4, 1);     
            String email = excel.getCellData(4, 3);       // Testautomation@gmail.com
            String password = excel.getCellData(4, 6);    // Karthik@88
            String description = excel.getCellData(4, 10); // This is linkedIn account
            String profileType = excel.getCellData(4, 11); // LinkedIn
            String profileUrl = excel.getCellData(4, 12);  // https://www.linkedin.com

            // Playwright Setup
            page = initializePlaywright();
            page.navigate(prop.getProperty("url"));
            page.setViewportSize(1000, 768);

            LandingPage landingPage = new LandingPage(page);
            try {
                landingPage.getPopup().click(new Locator.ClickOptions().setTimeout(5000));
                test.info("✅ Closed popup.");
            } catch (PlaywrightException e) {
                test.info("ℹ️ No popup to close.");
            }

            landingPage.clickLoginButton();

            LoginPage loginPage = new LoginPage(page);
            loginPage.loginMailPhone().fill(email);
            loginPage.passwordField().fill(password);
            loginPage.loginButton().click();
            test.info("🔐 Logged in with " + email);

            MyResumePage resumePage = new MyResumePage(page);
            resumePage.Mystuff().click();
            resumePage.MyAccount().click();
            resumePage.MyResume().click();
            resumePage.AddSocialProfile().click();

            AccomplishmentsPage socialPage = new AccomplishmentsPage(page);
            socialPage.addSocialProfileField().fill("LinkedIn");
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Paste profile link")).fill("https://www.linkedin.com");
            socialPage.socialDescriptionBox().fill("This is a social profile.");

            socialPage.saveButton().click();

            // Toast verification
            ToasterUtil toaster = new ToasterUtil(page);
            Locator toast = toaster.getSuccessToaster();
            toast.waitFor(new Locator.WaitForOptions().setTimeout(5000));

            if (!toast.isVisible()) {
                throw new AssertionError("❌ Toast was not displayed.");
            }

            test.pass("✅ Social profile added successfully for " + testCaseId);

            String screenshotPath = System.getProperty("user.dir") + "/screenshots/" + testCaseId + "_socialprofile_pass.png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)).setFullPage(true));
            test.addScreenCaptureFromPath(screenshotPath, "📸 Screenshot");

        } catch (Exception e) {
            test.fail("❌ " + (testCaseId != null ? testCaseId : "Test") + " failed: " + e.getMessage());
            throw e;
        } finally {
            closePlaywright();
            test.info("🧹 Closed browser for " + (testCaseId != null ? testCaseId : "Test"));
            extent.flush();
        }
    }
}

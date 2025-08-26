package com.promilo.automation.resumemodule;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.aventstack.extentreports.*;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;

import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.*;

public class UploadProfilePictureUsingLink extends Baseclass {

    @Test
    public void resumeUploadTest() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("🖼️ Upload Profile Picture Using Link | Excel Keyword Driven");

        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

        String testCaseId = excel.getCellData(03, 0); // TestCaseID
        String keyword = excel.getCellData(03, 1);    // Keyword
        String email = excel.getCellData(03, 03);     // MailPhone (Email)
        String password = excel.getCellData(03, 6);   // Password

        String imageUrl = excel.getCellData(03, 16);       // URL
        

            Page page = initializePlaywright();
            page.navigate(prop.getProperty("url"));
            page.setViewportSize(1000, 768);
            Thread.sleep(3000);

            try {
                // Landing Page
                LandingPage landingPage = new LandingPage(page);
                try {
                    landingPage.getPopup().click(new Locator.ClickOptions().setTimeout(5000));
                    test.info("✅ Closed popup.");
                } catch (PlaywrightException e) {
                    test.info("ℹ️ No popup to close.");
                }
                landingPage.clickLoginButton();

                // Login
                LoginPage loginPage = new LoginPage(page);
                loginPage.loginMailPhone().fill(email);
                loginPage.passwordField().fill(password);
                loginPage.loginButton().click();
                test.info("🔐 Logged in as " + email);

                // Navigate to Resume
                MyResumePage resumePage = new MyResumePage(page);
                resumePage.Mystuff().click();
                resumePage.MyAccount().click();
                resumePage.MyResume().click();

                Thread.sleep(3000);
                resumePage.EditProfileIcon().click();
                Thread.sleep(3000);
                resumePage.LinkTab().click();
                resumePage.ProfilePictureUrlInput().fill("https://fastly.picsum.photos/id/256/200/300.jpg?hmac=6-SQmUqIECHQ4QadM7mAYY3sHPH5r_8e2pCBs7V67Sc");
                resumePage.SaveButton().click();
                Thread.sleep(3000);
                resumePage.CropButton().click();

                test.pass("✅ Profile picture uploaded successfully for TestCaseID: " + testCaseId);

                // Screenshot
                String screenshotPath = System.getProperty("user.dir") + "/screenshots/" + testCaseId + "_profilepicture.png";
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)).setFullPage(true));
                test.addScreenCaptureFromPath(screenshotPath);

            } catch (Exception e) {
                test.fail("❌ TestCaseID: " + testCaseId + " failed: " + e.getMessage());
                throw e;
            } finally {
                closePlaywright();
                test.info("🧹 Closed browser for TestCaseID: " + testCaseId);
            }

        }

    }


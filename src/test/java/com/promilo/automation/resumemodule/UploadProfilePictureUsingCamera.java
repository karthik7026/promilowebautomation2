package com.promilo.automation.resumemodule;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.aventstack.extentreports.*;

import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.*;

public class UploadProfilePictureUsingCamera extends Baseclass {

    @Test
    public void resumeUploadWithCamera() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("📸 Upload Profile Picture Using Camera | Excel Keyword Driven");

        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

        String testCaseId = excel.getCellData(4, 0);
        String keyword = excel.getCellData(4, 1);
        String email = excel.getCellData(4, 3);
        String password = excel.getCellData(4, 6);

        String workTitle = excel.getCellData(4, 11);
        String url = excel.getCellData(4, 12);
        String year = excel.getCellData(4, 13);
        String month = excel.getCellData(4, 14);
        String description = excel.getCellData(4, 15);

        Page page = initializePlaywright();
        BrowserContext context = getContext();

        // ✅ Grant camera and location permissions for Promilo
        context.grantPermissions(
            List.of("camera", "geolocation"),
            new BrowserContext.GrantPermissionsOptions().setOrigin("https://promilo.com")
        );

        // ✅ Handle browser permission dialogs (for older browser flows)
        page.onDialog(dialog -> {
            test.info("📢 Permission dialog detected: " + dialog.message());
            dialog.accept(); // auto-accept
        });

        try {
            page.setViewportSize(1100, 780);
            page.navigate(prop.getProperty("url"));
            Thread.sleep(3000);

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

            // Resume
            MyResumePage resumePage = new MyResumePage(page);
            resumePage.Mystuff().click();
            resumePage.MyAccount().click();
            resumePage.MyResume().click();
            Thread.sleep(3000);

            // Camera Upload
            resumePage.EditProfileIcon().click();
            Thread.sleep(3000);
            resumePage.cameraOption().click();
            
            
            
            Thread.sleep(10000);
            
         // Grant camera permission for your site
            context.grantPermissions(
                List.of("camera"),
                new BrowserContext.GrantPermissionsOptions().setOrigin("https://stage.promilo.com/my-resume")
            );
         // ✅ Safe default timeout (avoids NPE)
            double defaultNavigationTimeout = 30000; // 30 seconds
            page.setDefaultNavigationTimeout(defaultNavigationTimeout);
            page.setDefaultTimeout(defaultNavigationTimeout);

            // ✅ Grant camera permission BEFORE visiting the site
            context.grantPermissions(
                List.of("camera"),
                new BrowserContext.GrantPermissionsOptions().setOrigin("https://promilo.com")
            );

        
                // Focus the button
            page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Camera").setExact(true)).click();
                
                Thread.sleep(5000);
                

             


            
            
           page.locator("//button[text()='Crop']").click();


            test.pass("✅ Camera resume upload successful for TestCaseID: " + testCaseId);

            // Screenshot
            String screenshotPath = System.getProperty("user.dir") + "/screenshots/" + testCaseId + "_camera_upload.png";
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

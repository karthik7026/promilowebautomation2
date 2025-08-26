package com.promilo.automation.resumemodule;

import com.aventstack.extentreports.*;
import com.microsoft.playwright.*;
import com.promilo.automation.pageobjects.myresume.AccomplishmentsPage;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.promilo.automation.resources.ToasterUtil;
import com.promilo.automation.resources.SignupWithMailosaurUI;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class Presentation extends Baseclass {

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
    public void addPresentationTest() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("📊 Add Presentation | Data-Driven Test");

        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", 
                "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

        int rowCount = 0;
        for (int i = 1; i <= 1000; i++) {
            String testCaseId = excel.getCellData(i, 0);
            if (testCaseId == null || testCaseId.trim().isEmpty()) break;
            rowCount++;
        }
        test.info("📘 Loaded " + rowCount + " rows from Excel.");

        for (int i = 1; i < rowCount; i++) {
            String testCaseId = excel.getCellData(i, 0);
            String keyword = excel.getCellData(i, 1);
            String title = excel.getCellData(i, 7);
            String url = excel.getCellData(i, 8);
            String description = excel.getCellData(i, 9);

            if (!"Presentation".equalsIgnoreCase(keyword)) {
                continue;
            }

            Page page = initializePlaywright();
            try {
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

                // 🔑 Use Mailosaur signup credentials
                if (registeredEmail == null || registeredPassword == null) {
                    test.fail("❌ Signup credentials not available. Aborting test.");
                    return;
                }

                LoginPage loginPage = new LoginPage(page);
                loginPage.loginMailPhone().fill(registeredEmail);
                loginPage.passwordField().fill(registeredPassword);
                loginPage.loginButton().click();
                test.info("🔐 Logged in with " + registeredEmail);

                MyResumePage resumePage = new MyResumePage(page);
                resumePage.Mystuff().click();
                Assert.assertTrue(resumePage.Mystuff().isVisible(), "'MyStuff' should be visible.");
                resumePage.MyAccount().click();
                resumePage.MyResume().click();
                resumePage.AddPresentation().click();

                AccomplishmentsPage presentationPage = new AccomplishmentsPage(page);
                presentationPage.presentationTitleTextField().fill(title != null ? title : "Default Title");
                presentationPage.presentationUrlTextField().fill(url != null ? url : "https://example.com");
                presentationPage.presentationDescriptionBox().fill(description != null ? description : "Default presentation description.");

                presentationPage.presentationSaveButton().click();

                

                test.pass("✅ Presentation added successfully for " + testCaseId);

                String screenshotPath = System.getProperty("user.dir") + "/screenshots/" + testCaseId + "_presentation_pass.png";
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)).setFullPage(true));
                test.addScreenCaptureFromPath(screenshotPath, "📸 Screenshot");
                break;

            } catch (Exception e) {
                test.fail("❌ " + testCaseId + " failed: " + e.getMessage());
                throw e;
            } finally {
                closePlaywright();
                test.info("🧹 Closed browser for " + testCaseId);
            }
        }

        extent.flush();
    }
}

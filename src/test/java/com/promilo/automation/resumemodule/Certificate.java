package com.promilo.automation.resumemodule;

import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.promilo.automation.pageobjects.myresume.AccomplishmentsPage;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;

public class Certificate extends Baseclass {

    @Test
    public void basicDetailsLoginTest() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("🏅 Certificate Functionality | Data Driven Test");

        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
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
            String email = excel.getCellData(i, 7);
            String password = excel.getCellData(i, 6);
            String certName = excel.getCellData(i, 8);
            String certUrl = excel.getCellData(i, 9);
            String certId = excel.getCellData(i, 10);

            if (!"Certificate".equalsIgnoreCase(keyword)) {
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

                LoginPage loginPage = new LoginPage(page);
                loginPage.loginMailPhone().fill("testautomation@gmail.com");
                loginPage.passwordField().fill("Karthik@88");
                loginPage.loginButton().click();

                MyResumePage resumePage = new MyResumePage(page);
                resumePage.Mystuff().click();
                Assert.assertTrue(resumePage.Mystuff().isVisible(), "Mystuff menu should be visible after login");
                resumePage.MyAccount().click();
                resumePage.MyResume().click();
                resumePage.AddCertificate().click();

                AccomplishmentsPage certificate = new AccomplishmentsPage(page);
                certificate.certificationsNameTextField().fill("devops");
                certificate.certificationsCompletionIdField().fill("devops123");
                certificate.certificationsUrlField().fill("https://promilo.com/");

                certificate.certificationsValidityFromYear().first().click();
                page.keyboard().type("2020");
                page.keyboard().press("Enter");


                certificate.durationFromMonthDropDown().first().click();
                page.keyboard().type("jan");
                page.keyboard().press("Enter");
                
                page.locator("(//div[contains(@class,'react-select__control')])[3]").click();
                
                //certificate.certificationsValidityToYear().click();
                System.out.println();
                page.keyboard().type("2022");
                page.keyboard().press("Enter");


                //certificate.certificationsValidityToMonth().click();
                page.locator("(//div[contains(@class,'col-sm-3')])[4]//div[contains(@class,'react-select__control')]").click();
                page.keyboard().type("feb");
                page.keyboard().press("Enter");

                page.locator("//button[text()='Save']").click();

                Thread.sleep(2000);
                Locator toast = page.locator("//div[@role='status']");
                toast.waitFor(new Locator.WaitForOptions().setTimeout(5000));

                if (!toast.isVisible()) {
                    throw new AssertionError("❌ Toast with role='status' was not displayed.");
                }

                System.out.println("✅ Toast displayed successfully.");
                test.pass("✅ Certificate added and toast verified.");

            } catch (Exception e) {
                test.fail("❌ Test failed with exception: " + e.getMessage());
                throw e;
            } finally {
                page.context().close();
            }
        }
    }
}

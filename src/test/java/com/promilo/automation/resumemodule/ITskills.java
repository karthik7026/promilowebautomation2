package com.promilo.automation.resumemodule;

import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;

public class ITskills extends Baseclass {

    @Test
    public void addITSkillsDataDrivenTest() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("🛠️ IT Skills Functionality Test");

        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

        int rowCount = 0;
        for (int i = 1; i <= 1000; i++) {
            String testCaseId = excel.getCellData(i, 0);
            if (testCaseId == null || testCaseId.trim().isEmpty()) break;
            rowCount++;
        }
        test.info("📗 Loaded " + rowCount + " rows from Excel.");

        for (int i = 1; i < rowCount; i++) {
            String keyword = excel.getCellData(i, 1);
            String email = excel.getCellData(i, 3);
            String password = excel.getCellData(i, 6);
            String skillName = excel.getCellData(i, 7); // e.g., Selenium
            String version = excel.getCellData(i, 8); // e.g., version-1
            String yearUsed = excel.getCellData(i, 10); // e.g., 2022
            String experienceYear = excel.getCellData(i, 8); // e.g., 2
            String experienceMonth = excel.getCellData(i, 9); // e.g., 6

           if (!"ITskills".equalsIgnoreCase(keyword)) continue;

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
                loginPage.loginMailPhone().fill("testuser1110@gmail.com");
                loginPage.passwordField().fill("Karthik@88");
                loginPage.loginButton().click();

                MyResumePage resumePage = new MyResumePage(page);
                resumePage.Mystuff().click();
                resumePage.MyAccount().click();
                resumePage.MyResume().click();
                resumePage.AddITskills().click();

                page.locator("//input[@id='experience' and @placeholder='Enter skill/software name']").fill("devops");
                page.locator("//input[@placeholder='Enter software version ']").fill("version1");

                Locator yearDropdown = page.locator("#react-select-2-input");
                yearDropdown.click();
                page.keyboard().type("2022");
                page.keyboard().press("Enter");

                Locator experYear = page.locator("(//div[contains(@class,'react-select__control')])[2]");
                experYear.click();
                
                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("2 years").setExact(true)).click();


                page.locator(".label-month > .custom-select > .react-select__control > .react-select__value-container > .react-select__input-container").click();
                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("1 month").setExact(true)).click();
              
                

                page.locator("//button[@class='save-resume-btn']").click();
                
                Locator statusDiv = page.locator("(//div[@role='status'])[1]");

             // Wait for visibility (up to 10 seconds)
             statusDiv.waitFor(new Locator.WaitForOptions()
                 .setTimeout(10000)
                 .setState(WaitForSelectorState.VISIBLE));

             // TestNG assertion
             Assert.assertTrue(statusDiv.isVisible(), "❌ Status message is NOT visible");

             // If you want ExtentReports log as well
             if (statusDiv.isVisible()) {
                 test.pass("✅ Status message is visible");
             } else {
                 test.fail("❌ Status message is not visible");
             }

            } catch (Exception e) {
                test.fail("❌ Test failed due to: " + e.getMessage());
                throw e;
            } finally {
                page.context().browser().close();
            }
        }
    }
}

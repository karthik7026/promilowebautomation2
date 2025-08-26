package com.promilo.automation.resumemodule;

import java.io.IOException;
import java.nio.file.Paths;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.promilo.automation.pageobjects.myresume.CareerProfilePage;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;

public class CareerProfilePositiveTest extends Baseclass {

    @Test
    public void fillCareerProfileSuccessfully() throws IOException, InterruptedException {
        Page page = initializePlaywright();
        page.navigate(prop.getProperty("url"));
        page.setViewportSize(1000, 768);

        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

        int rowCount = 0;
        for (int i = 1; i <= 1000; i++) {
            String testCaseId = excel.getCellData(i, 0);
            if (testCaseId == null || testCaseId.trim().isEmpty()) break;
            rowCount++;
        }

        for (int i = 1; i < rowCount; i++) {
            String testCaseId = excel.getCellData(i, 0);
            String keyword = excel.getCellData(i, 1);
            String password = excel.getCellData(i, 6);
            String email = excel.getCellData(i, 7);
            String industry = excel.getCellData(i, 8);
            String department = excel.getCellData(i, 9);
            String role = excel.getCellData(i, 10);
            String preferredLocation = excel.getCellData(i, 11);
            String salary = excel.getCellData(i, 12);

            if (!"AddCareerProfile".equalsIgnoreCase(keyword)) continue;

            page.navigate(prop.getProperty("url"));

            // Handle potential popup
            LandingPage landingPage = new LandingPage(page);
            try {
                landingPage.getPopup().click(new Locator.ClickOptions().setTimeout(5000));
            } catch (PlaywrightException e) {
                // Ignore if popup not present
            }

            landingPage.clickLoginButton();

            LoginPage loginPage = new LoginPage(page);
            loginPage.loginMailPhone().fill("testautomation@gmail.com");
            loginPage.passwordField().fill("Karthik@88");
            loginPage.loginButton().click();

            // Navigate to Resume > Career Profile
            MyResumePage myResumePage = new MyResumePage(page);
            myResumePage.Mystuff().click();
            myResumePage.MyAccount().click();
            myResumePage.MyResume().click();
            myResumePage.AddCareerProfile().click();

            CareerProfilePage careerProfilePage = new CareerProfilePage(page);
            Thread.sleep(2000);

            // Fill Career Profile Fields
            careerProfilePage.careerProfileCurrentIndustryDropdown().click();
            careerProfilePage.careerProfileCurrentIndustryDropdown().fill(industry);
            page.keyboard().press("ArrowDown");
            page.keyboard().press("Enter");

            careerProfilePage.careerProfileDepartmentDropdown().click();
            careerProfilePage.careerProfileDepartmentDropdown().fill(department);
            page.keyboard().press("ArrowDown");
            page.keyboard().press("Enter");

            careerProfilePage.careerProfileJobRoleDropdown().click();
            careerProfilePage.careerProfileJobRoleDropdown().fill(role);
            page.keyboard().press("ArrowDown");
            page.keyboard().press("Enter");

            careerProfilePage.careerProfilePermanentCheckbox().click();
            careerProfilePage.careerProfileFullTimeCheckbox().click();

            careerProfilePage.careerProfileShiftPreferredDay().check();

            careerProfilePage.careerProfilePreferredLocationsDropdown().click();
            page.keyboard().type(preferredLocation);
            page.keyboard().press("Enter");

            careerProfilePage.careerProfileSelectSalaryDropdown().click();
            page.keyboard().type(salary);
            page.keyboard().press("Enter");

            // Save
            careerProfilePage.careerProfileSaveButton().click();
            Thread.sleep(3000);

            // Assertion
            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(
                page.locator("text=Career Profile added successfully").isVisible(),
                "❌ Toast not visible for successful career profile addition"
            );
            softAssert.assertAll();
        }
        
        
    }
}

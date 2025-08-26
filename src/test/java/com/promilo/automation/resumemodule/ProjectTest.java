package com.promilo.automation.resumemodule;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Page;
import com.promilo.automation.pageobjects.myresume.AddProjectPage;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;

public class ProjectTest extends Baseclass {

    @Test
    public void addProjectTest() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("📁 Add Project - Data Driven");

        Page page = initializePlaywright();
        page.navigate(prop.getProperty("url"));
        page.setViewportSize(1280, 800);

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
            String keyword = excel.getCellData(i, 1);
            if (!"ProjectTest".equalsIgnoreCase(keyword)) {
                continue;
            }

            Map<String, String> data = new HashMap<>();
            data.put("TestCaseID", excel.getCellData(i, 0));       // A
            data.put("Keyword", excel.getCellData(i, 1));          // B
            data.put("InputValue", excel.getCellData(i, 3));       // C - Email
            data.put("OTP", excel.getCellData(i, 3));              // D
            data.put("Password", excel.getCellData(i, 6));         // F (E is blank)
            data.put("ProjectTitle", excel.getCellData(i, 7));  // G
            data.put("Client", excel.getCellData(i, 8));           // H
            data.put("FromYear", excel.getCellData(i, 9));         // I
            data.put("FromMonth", excel.getCellData(i, 10));        // J
            data.put("Description", excel.getCellData(i, 11));     // K
            data.put("Location", excel.getCellData(i, 12));        // L
            data.put("TeamSize", excel.getCellData(i, 13));        // M
            data.put("Role", excel.getCellData(i, 14));            // N
            data.put("RoleDescription", excel.getCellData(i, 15)); // O
            data.put("Skills", excel.getCellData(i, 16));          // P

            // Login
            LandingPage landingPage = new LandingPage(page);
            try {
                landingPage.getPopup().click();
            } catch (Exception e) {
                test.info("ℹ️ No popup appeared.");
            }
            landingPage.clickLoginButton();

            LoginPage loginPage = new LoginPage(page);
            loginPage.loginMailPhone().fill(data.get("InputValue"));
            loginPage.passwordField().fill(data.get("Password"));
            loginPage.loginButton().click();

            // Navigate to Resume -> Add Project
            MyResumePage myResumePage = new MyResumePage(page);
            myResumePage.Mystuff().click();
            myResumePage.MyAccount().click();
            myResumePage.MyResume().click();
            myResumePage.AddProject().click();

            Thread.sleep(4000);

            // Fill Project form
            AddProjectPage addProjectPage = new AddProjectPage(page);
            addProjectPage.ProjectTitle().fill(data.get("ProjectTitle"));
            addProjectPage.ClientTextField().fill(data.get("Client"));
            addProjectPage.InProgressStatus().click();

            Thread.sleep(2000);

            addProjectPage.WorkedFromYear().click();
            page.keyboard().type(data.get("FromYear"));
            page.keyboard().press("Enter");

            Thread.sleep(2000);

            addProjectPage.WorkedFromMonth().click();
            page.keyboard().type(data.get("FromMonth"));
            page.keyboard().press("Enter");

            Thread.sleep(2000);

            addProjectPage.ProjectDescription().fill(data.get("Description"));
            addProjectPage.AddMoreDetailsButton().click();

            Thread.sleep(2000);

            addProjectPage.ProjectLocationInput().click();
            page.keyboard().type(data.get("Location"));
            page.keyboard().press("Enter");

            Thread.sleep(2000);

            addProjectPage.OnsiteRadio().click();
            addProjectPage.FullTimeRadio().click();

            addProjectPage.TeamSizeInput().click();
            page.keyboard().type(data.get("TeamSize"));
            page.keyboard().press("Enter");

            Thread.sleep(2000);

            addProjectPage.RoleInput().click();
            page.keyboard().type(data.get("Role"));
            page.keyboard().press("Enter");

            Thread.sleep(2000);

            addProjectPage.RoleDescription().fill(data.get("RoleDescription"));
            addProjectPage.SkillsUsedTextarea().fill(data.get("Skills"));
            addProjectPage.SaveButton().click();

            Thread.sleep(4000);

            test.pass("✅ Project added successfully for: " + data.get("InputValue"));
        }

        extent.flush();
    }
}

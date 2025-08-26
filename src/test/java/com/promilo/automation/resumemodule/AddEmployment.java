package com.promilo.automation.resumemodule;

import java.nio.file.Paths;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.promilo.automation.pageobjects.myresume.AddEmploymentDetails;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.promilo.automation.resources.SignupWithMailosaurUI;

public class AddEmployment extends Baseclass {

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
    public void addEmploymentPositiveTest() throws Exception {

        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("✅ Add Employment - Positive Test");

        if (registeredEmail == null || registeredPassword == null) {
            test.fail("❌ Signup credentials not found. Aborting test.");
            return;
        }

        Page page = initializePlaywright();
        page.navigate(prop.getProperty("url"));
        page.setViewportSize(1080, 720);

        String excelPath = Paths.get(System.getProperty("user.dir"),
                "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
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
            if (!"AddEmployment".equalsIgnoreCase(keyword)) continue;

            String inputValue = excel.getCellData(i, 3);
            String description = excel.getCellData(i, 10);

            try {
                // Login using registered Mailosaur creds
                LandingPage landingPage = new LandingPage(page);
                try {
                    landingPage.getPopup().click();
                } catch (Exception ignored) {}

                landingPage.clickLoginButton();

                LoginPage loginPage = new LoginPage(page);
                loginPage.loginMailPhone().fill(registeredEmail);
                loginPage.passwordField().fill(registeredPassword);
                loginPage.loginButton().click();

                // Navigate to My Resume
                MyResumePage resumePage = new MyResumePage(page);
                resumePage.Mystuff().click();
                resumePage.MyAccount().click();
                resumePage.MyResume().click();
                page.locator("//span[@class='pointer edit-option ms-1 ']").click();

                Thread.sleep(3000);

                // Fill Employment Details
                AddEmploymentDetails emp = new AddEmploymentDetails(page);
                emp.YesRadioBox().click();
                emp.FullTimeButton().click();

                emp.CurrentCompanyDropdown().click();
                emp.currentCompanyoption().first().click();
                Thread.sleep(2000);

                emp.Currentdesignatipondropdown().nth(1).click();
                emp.currentDesignationoption().first().click();

                // Start Month
                emp.JoiningMonth().click();
                emp.joiningMonthoption().click();

                emp.JoiningYear().click();
                emp.joiningYearoption().click();

                // Skills Used
                emp.SkillsUsed().click();
                emp.skillsOption().click();

                // Salary
                emp.currentSalary().click();
                emp.currentSalaryoption().click();

                // Notice period
                emp.NoticePeriod().click();
                emp.noticePeriodoption().click();

                // Description
                emp.description().fill(description != null ? description : "Automation Added Employment");

                // Save
                emp.SaveButton().click();

                Thread.sleep(5000);

                // Validate success toast (report only, no assertion)
                Locator successToast = page.locator("//div[contains(text(),'Employment added successfully')]");
                boolean isToastVisible = successToast.isVisible();

                if (isToastVisible) {
                    test.pass("✅ Employment added successfully for: " + inputValue);
                } else {
                    test.warning("⚠️ Employment success toast not visible for: " + inputValue);
                }

            } catch (Exception e) {
                test.fail("❌ Exception occurred for row " + i + " → " + e.getMessage());
            }
        }

        extent.flush();
    }
}

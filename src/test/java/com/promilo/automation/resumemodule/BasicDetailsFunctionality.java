package com.promilo.automation.resumemodule;

import java.nio.file.Paths;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.AriaRole;
import com.promilo.automation.pageobjects.myresume.*;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.promilo.automation.resources.SignupWithMailosaurUI;

public class BasicDetailsFunctionality extends Baseclass {

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
    public void basicDetailsLoginTest() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("🚀 Basic Details Functionality | Data Driven");

        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

        int row = 40; // fixed row index
        String inputValue = excel.getCellData(row, 3);
        String password = excel.getCellData(row, 6);
        String testCaseId = excel.getCellData(row, 0);     // TestCaseID
        String keyword = excel.getCellData(row, 1);        // Keyword
        String whitePaperTitle = excel.getCellData(row, 11); // Work Title
        String url = excel.getCellData(row, 12);           // URL
        String year = excel.getCellData(row, 13);          // Year
        String month = excel.getCellData(row, 14);         // Month
        String description = excel.getCellData(row, 15);   // Description

        Page page = initializePlaywright();
        page.navigate(prop.getProperty("url"));
        page.setViewportSize(1000, 768);

        LandingPage landingPage = new LandingPage(page);
        try {
            landingPage.getPopup().click(new Locator.ClickOptions().setTimeout(5000));
        } catch (PlaywrightException e) {
            // No popup
        }

        landingPage.clickLoginButton();

        // 🔑 Login using Mailosaur-generated creds
        if (registeredEmail == null || registeredPassword == null) {
            test.fail("❌ Signup credentials not available. Aborting test.");
            return;
        }

        LoginPage loginPage = new LoginPage(page);
        loginPage.loginMailPhone().fill(registeredEmail);
        loginPage.passwordField().fill(registeredPassword);
        loginPage.loginButton().click();

        // Resume Navigation
        MyResumePage resumePage = new MyResumePage(page);
        resumePage.Mystuff().click();
        resumePage.MyAccount().click();
        resumePage.MyResume().click();
        resumePage.EditOption().click();

        // Basic Details
        BasicDetailsPage basicDetails = new BasicDetailsPage(page);

        basicDetails.BasicDetailsFirstName().clear();
        basicDetails.BasicDetailsFirstName().fill("Karthik");

        basicDetails.BasicDetailsLastName().clear();
        basicDetails.BasicDetailsLastName().fill("U");

        basicDetails.locationDropdDrown().fill("Bangalore");
        basicDetails.locationDropdDrown().press("Enter");

        basicDetails.MobileNumber().fill("9000047723");

        page.locator("//button[text()='Send OTP']").click();
        page.locator("//input[@placeholder='Enter OTP']").fill("9999");
        page.locator("//button[text()='Verify']").click();

        Locator verifiedSpan = page.locator("//span[text()='Verified']");
        if (verifiedSpan.isVisible()) {
            test.pass("✅ OTP verified successfully.");
        } else {
            test.warning("⚠️ OTP verification label not visible.");
        }

        basicDetails.AddEmployment().click();

        // Employment Details
        AddEmploymentDetails employmentDetails = new AddEmploymentDetails(page);
        employmentDetails.YesRadioBox().click();
        employmentDetails.FullTimeButton().click();
        
page.locator(".my-2 > div > div > .custom-select > .react-select__control > .react-select__value-container > .react-select__input-container").first().click();
page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("PRO TECH 1234").setExact(true)).click();


page.locator(".my-2 > div > .custom-select > .react-select__control > .react-select__value-container > .react-select__input-container").click();
page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Software Development - ALL")).click();


        employmentDetails.currentSalary().click();
        employmentDetails.currentSalaryoption().click();

        employmentDetails.NoticePeriod().click();
        employmentDetails.noticePeriodoption().click();

        employmentDetails.description().fill(description != null ? description : "Automation Employment Description");

        employmentDetails.JoiningYear().click();
        employmentDetails.joiningYearoption().click();

        employmentDetails.JoiningMonth().click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("January")).click();

        employmentDetails.SkillsUsed().click();
        employmentDetails.skillsOption().click();

        employmentDetails.SaveButton().click();

        test.pass("✅ Basic details & employment submitted successfully for: " + inputValue);

        extent.flush();
    }
}

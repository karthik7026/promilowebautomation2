package com.promilo.automation.resumemodule;

import com.aventstack.extentreports.*;
import com.microsoft.playwright.*;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.myresume.PersonalDetailsPage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.promilo.automation.resources.SignupWithMailosaurUI;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class PersonalDetailsTest extends Baseclass {

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
    public void fillPersonalDetailsFormDataDriven() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("📝 Personal Details | Data-Driven Test");

        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", 
                "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
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

            String dob = excel.getCellData(i, 11);
            String address = excel.getCellData(i, 7);
            String pincode = excel.getCellData(i, 8);
            String language = excel.getCellData(i, 9);
            String proficiency = excel.getCellData(i, 15);
            String breakYear = excel.getCellData(i, 16);
            String breakMonth = excel.getCellData(i, 17);
            String workPermitUSA = excel.getCellData(i, 18);
            String workPermitOther = excel.getCellData(i, 19);

            if (!"PersonalDetailsTest".equalsIgnoreCase(keyword)) continue;

            Page page = initializePlaywright();
            try {
                page.navigate(prop.getProperty("url"));
                page.setViewportSize(1000, 760);

                LandingPage landingPage = new LandingPage(page);
                try {
                    landingPage.getPopup().click(new Locator.ClickOptions().setTimeout(5000));
                } catch (Exception e) {
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
                resumePage.AddPersonalDetails().click();

                PersonalDetailsPage personalDetails = new PersonalDetailsPage(page);
                personalDetails.personalDetailsGenderMaleButton().click();
                personalDetails.personalDetailsSingleParentButton().click();
                personalDetails.personalDetailsMaritalStatusSingleButton().click();
                personalDetails.personalDetailsDobField().fill(dob != null ? dob : "1997-01-05");

                personalDetails.personalDetailsCategoryGeneralButton().click();
                personalDetails.personalDetailsDifferentlyAbledNo().check();
                personalDetails.personalDetailsCareerBreakYes().check();
                personalDetails.personalDetailsCareerBreakEducationButton().click();
                personalDetails.personalDetailsCurrentlyOnBreakCheckbox().check();

                Locator selectYear = personalDetails.personalDetailsBreakStartedYearDropdown();
                selectYear.click();
                page.keyboard().type(breakYear != null ? breakYear : "2020");
                page.keyboard().press("Enter");

                Locator selectMonth = personalDetails.personalDetailsBreakStartedMonthDropdown();
                selectMonth.click();
                page.keyboard().type(breakMonth != null ? breakMonth : "Jan");
                page.keyboard().press("Enter");

                Locator workPermit = personalDetails.personalDetailsWorkPermitUSA();
                workPermit.click();
                workPermit.fill(workPermitUSA != null ? workPermitUSA : "citizen");
                page.keyboard().press("Enter");

                Locator otherCountries = personalDetails.personalDetailsWorkPermitOtherCountries();
                otherCountries.click();
                otherCountries.fill(workPermitOther != null ? workPermitOther : "afg");
                page.keyboard().press("Enter");

                personalDetails.personalDetailsAddressField().fill(address != null ? address : "123 Test Street, Bangalore");
                personalDetails.personalDetailsPincodeField().fill(pincode != null ? pincode : "560001");

                personalDetails.personalDetailsEnterLanguageField().fill(language != null ? language : "English");
                Locator proficiencyDropdown = personalDetails.personalDetailsLanguageProficiencyDropdown();
                proficiencyDropdown.click();
                proficiencyDropdown.fill(proficiency != null ? proficiency : "Expert");
                page.keyboard().press("Enter");

                personalDetails.personalDetailsLanguageReadCheckbox().check();
                personalDetails.personalDetailsLanguageWriteCheckbox().check();
                personalDetails.personalDetailsLanguageSpeakCheckbox().check();

                personalDetails.personalDetailsSaveButton().click();

                test.pass("✅ Personal details saved successfully for " + testCaseId);

                String screenshotPath = System.getProperty("user.dir") + "/screenshots/" + testCaseId + "_personaldetails_pass.png";
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)).setFullPage(true));
                test.addScreenCaptureFromPath(screenshotPath);

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

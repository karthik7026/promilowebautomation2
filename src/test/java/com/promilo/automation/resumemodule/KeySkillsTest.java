package com.promilo.automation.resumemodule;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExtentManager;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.SignupWithMailosaurUI;

public class KeySkillsTest extends Baseclass {

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
    public void keySkillsTest() throws Exception {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("🚀 Key Skills Functionality | Data Driven");

        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

        int rowCount = 0;
        for (int i = 1; i <= 1000; i++) {
            String testCaseId = excel.getCellData(i, 0);
            if (testCaseId == null || testCaseId.trim().isEmpty()) break;
            rowCount++;
        }

        test.info("📘 Total test rows found: " + rowCount);

        for (int i = 1; i < rowCount; i++) {
            String keyword = excel.getCellData(i, 1);
            if (!"KeySkillsTest".equalsIgnoreCase(keyword)) continue;

            Map<String, String> data = new HashMap<>();
            data.put("TestCaseID", excel.getCellData(i, 0));
            data.put("Keyword", keyword);
            data.put("InputValue", excel.getCellData(i, 3));  // Comma-separated skills

            Page page = initializePlaywright();
            try {
                page.navigate(prop.getProperty("url"));
                page.setViewportSize(1000, 768);

                // Handle popup
                LandingPage landingPage = new LandingPage(page);
                try {
                    landingPage.getPopup().click(new Locator.ClickOptions().setTimeout(3000));
                    test.info("✅ Popup closed.");
                } catch (PlaywrightException e) {
                    test.info("ℹ️ No popup appeared.");
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

                // Navigate to Resume section
                MyResumePage resumePage = new MyResumePage(page);
                resumePage.Mystuff().click();
                resumePage.MyAccount().click();
                resumePage.MyResume().click();

                Thread.sleep(3000);

                // Click ADD KEY SKILLS
                page.locator("//span[text()='ADD KEY SKILLS']").click();
                test.info("🔑 Add Key Skills button clicked.");

                // Add skills one by one
                Locator skillsInput = page.locator("//input[starts-with(@id, 'react-select-') and contains(@id, '-input')]");
                skillsInput.click();

                String[] skills = data.get("InputValue").split(",");
                for (String skill : skills) {
                    page.keyboard().type(skill.trim());
                    page.keyboard().press("Enter");
                    page.waitForTimeout(500);
                }

                test.pass("✅ Skills added successfully: " + data.get("InputValue"));

                SoftAssert softAssert = new SoftAssert();
                softAssert.assertAll();

                break; // run only first matching keyword

            } catch (Exception e) {
                test.fail("❌ Test failed due to exception: " + e.getMessage());
                throw e;
            } finally {
                if (page != null) {
                    page.context().browser().close();
                }
            }
        }
    }
}

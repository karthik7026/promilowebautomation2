package com.promilo.automation.guestuser.jobs;


import java.nio.file.Paths;
import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.promilo.automation.pageobjects.signuplogin.JobListingPage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;


public class GuestUserShortListWithInvalidOTP extends Baseclass{

	
	@DataProvider(name = "jobApplicationData")
    public Object[][] jobApplicationData() throws Exception {
        // Load test data from Excel
        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");
        int rowCount = 0;
        for (int i = 1; i <= 1000; i++) {
            String testCaseId = excel.getCellData(i, 0);
            if (testCaseId == null || testCaseId.trim().isEmpty()) break;
            rowCount++;
        }
        Object[][] data = new Object[rowCount - 1][8]; // Exclude header

        for (int i = 1; i < rowCount; i++) {
            data[i - 1][0] = excel.getCellData(i, 0); // TestCaseID
            data[i - 1][1] = excel.getCellData(i, 1); // Keyword
            data[i - 1][2] = excel.getCellData(i, 3); // Email
            data[i - 1][3] = excel.getCellData(i, 6); // Password
            data[i - 1][4] = excel.getCellData(i, 7); // Name
            data[i - 1][5] = excel.getCellData(i, 5); // OTP
            data[i - 1][6] = excel.getCellData(i, 8); // MailPhone
        }
        return data;
    }

    @Test(dataProvider = "jobApplicationData")
    public void applyForJobAsRegisteredUser(String testCaseId, String keyword, String email, String password, String name, String otp, String mailphone, String expectedResult) throws Exception {

        // Initialize Extent Report
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("🚀 Apply for Job as Registered User | " + testCaseId);

        // Skip if keyword does not match
        if (!keyword.equalsIgnoreCase("JobShortList")) {
            test.info("⏭️ Skipping TestCaseID: " + testCaseId + " due to keyword: " + keyword);
            return;
        }

        // Initialize Playwright and navigate to URL
        Page page = initializePlaywright();
        page.navigate(prop.getProperty("url"));
        page.setViewportSize(1280, 800);

        LandingPage landingPage = new LandingPage(page);
        landingPage.getPopup().click();

        // Navigate to Job Listing and perform shortlist flow
        JobListingPage homePage = new JobListingPage(page);
        homePage.homepageJobs().click();
        Thread.sleep(5000);
        homePage.jobShortlist1().click();
        Thread.sleep(4000);

        // Fill in valid name and phone
        page.locator("//input[@name='userName']").fill("karthik U");
        page.locator("//input[@placeholder='Mobile*']").fill("9000090780");

        // Click on Shortlist
        homePage.jobShortList().click();

        if (otp == null || otp.length() < 4) {
            throw new IllegalArgumentException("OTP provided is less than 4 characters: " + otp);
        }

        for (int i = 0; i < 4; i++) {
            String otpChar = Character.toString(otp.charAt(i));
            Locator otpField = page.locator("//input[@aria-label='Please enter OTP character " + (i + 1) + "']");

            otpField.waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));

            boolean filled = false;
            int attempts = 0;

            while (!filled && attempts < 3) {
                attempts++;
                otpField.click(); // force focus
                otpField.fill(""); // clear previous
                otpField.fill(otpChar);

                // Validate the field actually has the entered digit
                String currentValue = otpField.evaluate("el => el.value").toString().trim();
                if (currentValue.equals(otpChar)) {
                    filled = true;
                } else {
                    page.waitForTimeout(500); // wait before retry
                }
            }

            if (!filled) {
                throw new RuntimeException("Failed to enter OTP digit " + (i + 1) + " correctly after retries.");
            }
        }



        Locator verifyButton = page.locator("//button[text()='Verify & Proceed']");
        verifyButton.waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
        verifyButton.click();

        try {
            // Attempt to capture toaster message
            Locator errorLocator = page.locator("//div[@role='status' and @aria-live='polite']");
            errorLocator.waitFor(new Locator.WaitForOptions().setTimeout(5000));

            String actualErrorText = errorLocator.innerText().trim();
            test.info("⚡ Actual Error Text captured: " + actualErrorText);

            // Pass if toaster is displayed, regardless of text
            test.pass("✅ Error toaster displayed as expected for " + testCaseId + ". Message: '" + actualErrorText + "'");

        } catch (Exception e) {
            // Fail test if toaster not displayed
            test.fail("❌ Error toaster was not displayed for " + testCaseId + ". Expected: '" + Optional.ofNullable(expectedResult).orElse("").trim() + "'");
            e.printStackTrace();
            Assert.fail("Error toaster was not displayed for " + testCaseId);
        }

        // Capture screenshot for reporting
        String screenshotPath = System.getProperty("user.dir") + "/screenshots/" + testCaseId + "_signup_pass.png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)).setFullPage(true));
        test.addScreenCaptureFromPath(screenshotPath, "🖼️ Screenshot after signup");

        // Close page and log completion
        page.close();
        test.info("Browser closed for TestCaseID: " + testCaseId);
    }
}

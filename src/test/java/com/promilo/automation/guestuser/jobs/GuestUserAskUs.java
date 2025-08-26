package com.promilo.automation.guestuser.jobs;

import java.nio.file.Paths;

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
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;

public class GuestUserAskUs extends Baseclass {

    /**
     * DataProvider still loads Excel, but not used to drive test execution.
     * (kept only for framework consistency)
     */
    @DataProvider(name = "jobApplicationData")
    public Object[][] jobApplicationData() throws Exception {
        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata",
                "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

        int rowCount = 0;
        for (int i = 1; i <= 1000; i++) {
            String testCaseId = excel.getCellData(i, 0);
            if (testCaseId == null || testCaseId.trim().isEmpty())
                break;
            rowCount++;
        }

        // Just return dummy values to satisfy TestNG DataProvider
        Object[][] data = new Object[1][8];
        data[0][0] = "DUMMY"; // TestCaseID
        data[0][1] = "Askus?"; // Keyword
        data[0][2] = "dummy@example.com"; // InputValue
        data[0][3] = "password"; // Password
        data[0][4] = "Dummy Name"; // Name
        data[0][5] = "0000"; // OTP
        data[0][6] = "9999999999"; // MailPhone
        data[0][7] = "Dummy text"; // TextArea
        return data;
    }

    /**
     * Guest user flow for 'Ask Us' section.
     * Excel values ignored, using hardcoded test data.
     */
    @Test(dataProvider = "jobApplicationData")
    public void applyForJobAsGuestUser(String testCaseId, String keyword, String email, String password,
            String name, String otp, String mailphone, String textArea) throws Exception {

        // 🚨 OVERRIDE: Ignore DataProvider values, use hardcoded ones
        testCaseId = "TC_ASKUS_001";
        keyword = "Askus?";
        password = "dummyPass123";
        name = "Karthik U";
        otp = "9999"; 
      

        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("🚀 Guest User AskUs Flow | " + testCaseId);

        if (!keyword.equalsIgnoreCase("Askus?")) {
            test.info("⏭️ Skipping TestCaseID: " + testCaseId + " (keyword mismatch)");
            return;
        }

        // Launch browser
        Page page = initializePlaywright();
        page.navigate(prop.getProperty("url"));
        page.setViewportSize(1280, 800);

        // Landing page
        LandingPage landingPage = new LandingPage(page);
        landingPage.getPopup().click();

        // Navigate Ask Us job
        JobListingPage homePage = new JobListingPage(page);
        homePage.homepageJobs().click();
        Thread.sleep(2000);
        
        page.locator("//p[text()='python developer']").nth(1).click();
        Thread.sleep(3000);
page.locator("//button[@class='pointer border-1 p-50 border-chip ']").first().click();
Thread.sleep(4000);

        // Fill details
        page.locator("input[name='userName']").nth(1).fill("karthik");
        page.locator("//input[@placeholder='Mobile*']").nth(1).fill("9000089271");
        page.locator("//textarea[@id='feedbackDetails']").first().fill("something");
        page.locator("input[placeholder='Email*']").nth(1).fill("testuser110112@gmail.com");
        homePage.askUsSubmitButton().click();

        // Enter OTP
        for (int i = 0; i < otp.length(); i++) {
            String otpChar = String.valueOf(otp.charAt(i));
            Locator otpField = page.locator("//input[@aria-label='Please enter OTP character " + (i + 1) + "']");
            otpField.waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
            otpField.fill(otpChar);
        }

        // Verify & Proceed
        Locator verifyButton = page.locator("//button[text()='Verify & Proceed']");
        verifyButton.waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
        verifyButton.click();

        // Validate Thank You popup
        Locator thankYouPopup = page.locator(
                "//div[translate(normalize-space(text()), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'thank you!']");
        thankYouPopup.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        String popupText = thankYouPopup.innerText().trim();
        Assert.assertTrue(popupText.equalsIgnoreCase("Thank You!"),
                "Expected 'Thank You!' popup, but found: " + popupText);

        test.pass("✅ Flow completed successfully for TestCaseID: " + testCaseId);

        // Screenshot
        String screenshotPath = System.getProperty("user.dir") + "/screenshots/" + testCaseId + "_guest_pass.png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)).setFullPage(true));
        test.addScreenCaptureFromPath(screenshotPath, "🖼️ Screenshot after AskUs flow");

        // Close browser
        page.context().close();
        page.close();

        extent.flush();
    }
}

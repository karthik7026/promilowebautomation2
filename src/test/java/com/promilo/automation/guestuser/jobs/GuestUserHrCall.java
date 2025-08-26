package com.promilo.automation.guestuser.jobs;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.promilo.automation.pageobjects.signuplogin.JobListingPage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;

public class GuestUserHrCall extends Baseclass {

    // Keep Excel logic for reference but don't use it for launching
    private void readExcelForReference() throws Exception {
        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata",
                "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");
        // Example: read first row only
        String testCaseId = excel.getCellData(1, 0);
        String keyword = excel.getCellData(1, 1);
        String email = excel.getCellData(1, 3);
        String password = excel.getCellData(1, 6);
        String name = excel.getCellData(1, 7);
        String otp = excel.getCellData(1, 5);
        String mailphone = excel.getCellData(1, 8);
        String expectedResult = excel.getCellData(1, 4);

        System.out.println("Excel read for reference: " + testCaseId + ", " + keyword);
    }

    @Test
    public void applyForJobAsGuestUser() throws Exception {

        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("🚀 Apply for Job as Guest User | Single Run");

        // Example hardcoded test data
        String name = "Test User";
        String mailphone = "9999999999";
        String otp = "1234"; // 4-digit OTP

        // Initialize Playwright and navigate
        Page page = initializePlaywright();
        page.navigate(prop.getProperty("url"));
        page.setViewportSize(1280, 800);

        // Close popup
        LandingPage landingPage = new LandingPage(page);
        landingPage.getPopup().click();

        // Navigate to fintech job
        JobListingPage homePage = new JobListingPage(page);
        homePage.homepageJobs().click();
        homePage.fintech();

        Locator fintechJobCard = page.locator(
                "//span[@class='font-12 additional-tags-text additional-cards-text-truncate jobs-brand-additional-title']");
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        fintechJobCard.scrollIntoViewIfNeeded();
        fintechJobCard.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
        fintechJobCard.click();

        // Fill Get HR Call form
page.locator("//button[@class='functional_btn-Get-call']").click();
homePage.applyNameField().nth(1).fill("karthik");
        homePage.applyNowMobileTextField().nth(1).fill("9000019642");
        page.locator("//input[@placeholder='Email*']").nth(1).fill("testuser0000113@gmail.com");

      
        
        homePage.selectIndustryDropdown().click();
        test.info("Opened Industry dropdown");
        Thread.sleep(1000);

        List<String> industries = Arrays.asList("Telecom / ISP", "Advertising & Marketing", "Animation & VFX", "Healthcare", "Education");
        Locator options = page.locator("//div[@class='sub-sub-option d-flex justify-content-between pointer']");
        for (String industry : industries) {
            for (int i = 0; i < options.count(); i++) {
                String optionText = options.nth(i).innerText().trim();
                if (optionText.equalsIgnoreCase(industry)) {
                    options.nth(i).click();
                    test.info("✅ Selected industry: " + industry);
                    break;
                }
            }
        }
        homePage.applyNameField().nth(1).click();
        Thread.sleep(2000);

        homePage.getAnHrCallButton().click();

        if (otp == null || otp.length() < 4) {
            throw new IllegalArgumentException("OTP must be 4 characters: " + otp);
        }

        for (int i = 0; i < 4; i++) {
            String otpChar = String.valueOf(otp.charAt(i));
            Locator otpField = page.locator("//input[@aria-label='Please enter OTP character " + (i + 1) + "']");
            otpField.waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));

            int attempts = 0;
            boolean filled = false;
            while (!filled && attempts < 3) {
                attempts++;
                otpField.click();
                otpField.fill("");
                otpField.fill(otpChar);

                String currentValue = otpField.evaluate("el => el.value").toString().trim();
                if (currentValue.equals(otpChar)) {
                    filled = true;
                } else {
                    page.waitForTimeout(500);
                }
            }

            if (!filled) {
                throw new RuntimeException("Failed to enter OTP digit " + (i + 1));
            }
            test.info("Entered OTP digit: " + otpChar);
        }

        page.locator("//button[text()='Verify & Proceed']").click();
        Thread.sleep(3000);

        homePage.getHrCallSubmitButton().click();

        Locator thankYouPopup = homePage.getHrCallThankYouPopup();
        thankYouPopup.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        String popupText = thankYouPopup.innerText().trim();

        Assert.assertTrue(popupText.toLowerCase().contains("thank you"),
                "Expected popup to contain 'Thank You', but found: " + popupText);

        test.pass("✅ 'Thank You' popup validated successfully");
        extent.flush();
    }
}

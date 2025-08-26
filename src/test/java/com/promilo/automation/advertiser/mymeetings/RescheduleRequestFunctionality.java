package com.promilo.automation.advertiser.mymeetings;

import java.io.IOException;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.microsoft.playwright.Page;
import com.promilo.automation.advertiser.AdverstiserMyaccount;
import com.promilo.automation.advertiser.AdvertiserHomepage;
import com.promilo.automation.advertiser.AdvertiserLoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class RescheduleRequestFunctionality extends Baseclass {

    @Test
    public void verifyRescheduleRequestFunctionalityHardcoded() throws InterruptedException, IOException {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("📅 Reschedule Advertiser Meeting | Hardcoded Login");

        // ✅ Initialize Excel (kept but not used for fetching data)
        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");
        test.info("📘 Excel Utility Initialized (Using hardcoded login credentials instead)");

        // ✅ Hardcoded login credentials
        String email = "adv@yopmail.com";
        String password = "devuttan2023";

        // ✅ Launch browser and navigate
        Page page = initializePlaywright();
        page.navigate(prop.getProperty("stageUrl"));
        page.setViewportSize(1000, 768);
        Thread.sleep(5000);

        // ✅ Login flow
        AdvertiserLoginPage login = new AdvertiserLoginPage(page);
        Assert.assertTrue(login.signInContent().isVisible(), "❌ Sign-in content is not visible.");
        Assert.assertTrue(login.talkToAnExpert().isVisible(), "❌ Talk To expert content should be visible");

        login.loginMailField().fill(email);
        login.loginPasswordField().fill(password);
        login.signInButton().click();

        // ✅ Navigate to My Account → My Meeting
        AdvertiserHomepage home = new AdvertiserHomepage(page);
        home.myAccount().click();

        AdverstiserMyaccount account = new AdverstiserMyaccount(page);
        account.myMeeting().click();

        com.promilo.automation.advertiser.AdvertiserMymeetingPage myMeetingPage = new com.promilo.automation.advertiser.AdvertiserMymeetingPage(page);
        myMeetingPage.jobs().click();
        myMeetingPage.reschedule().first().click();
        
        // Select today's date dynamically
        page.locator("span.flatpickr-day[aria-current='date']").click();

        // Select the first available time slot dynamically
        page.locator("li.time-slot-box.list-group-item").first().click();
        
        
        

        test.pass("✅ Reschedule request successfully processed for " + email);

        page.close();
    }
}

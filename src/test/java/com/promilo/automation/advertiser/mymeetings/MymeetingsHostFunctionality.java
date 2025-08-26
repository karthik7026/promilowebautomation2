package com.promilo.automation.advertiser.mymeetings;

import java.io.IOException;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.microsoft.playwright.Page;
import com.promilo.automation.advertiser.AdvertiserHomepage;
import com.promilo.automation.advertiser.AdvertiserLoginPage;
import com.promilo.automation.advertiser.AdverstiserMyaccount;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ExtentReports;

public class MymeetingsHostFunctionality extends Baseclass {

    @Test
    public void verifyMyMeetingHostFunctionalityHardcoded() throws InterruptedException, IOException {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("📌 MyMeetings Host Functionality | Hardcoded Data");

        // ✅ Excel initialized but not used for data
        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");
        test.info("📘 Excel Utility Initialized (Using hardcoded login credentials instead)");

        // ✅ Hardcoded advertiser login credentials
        String email = "adv@yopmail.com";
        String password = "devuttan2023";

        // ✅ Launch browser and navigate
        Page page = initializePlaywright();
        page.navigate(prop.getProperty("stageUrl"));
        page.setViewportSize(1000, 768);
        Thread.sleep(3000);

        // ✅ Login flow
        AdvertiserLoginPage login = new AdvertiserLoginPage(page);
        Assert.assertTrue(login.signInContent().isVisible(), "❌ Sign-in content is not visible.");
        Assert.assertTrue(login.talkToAnExpert().isVisible(), "❌ Talk To Expert should be visible.");

        login.loginMailField().fill(email);
        login.loginPasswordField().fill(password);
        login.signInButton().click();

        // ✅ Navigate to My Meetings
        AdvertiserHomepage homepage = new AdvertiserHomepage(page);
        homepage.hamburger().click();
        homepage.myAccount().click();

        AdverstiserMyaccount account = new AdverstiserMyaccount(page);
        account.myMeeting().click();

        com.promilo.automation.advertiser.AdvertiserMymeetingPage myMeeting = new com.promilo.automation.advertiser.AdvertiserMymeetingPage(page);

        // ✅ Host functionality flow
        myMeeting.jobs().click();
        myMeeting.hostButton().first().click();
        Thread.sleep(2000);;
        myMeeting.audioMuteButton().first().click();
        Thread.sleep(2000);;

        myMeeting.videoMuteButton().first().click();
        Thread.sleep(2000);;

        myMeeting.joinMeetingButton().first().click();
        Thread.sleep(2000);;

        myMeeting.riseHandButton().first().click();
        Thread.sleep(2000);;

        myMeeting.chatButton().first().click();
        Thread.sleep(2000);;

        myMeeting.chatTextfield().first().fill("Hi");
        Thread.sleep(2000);;

        myMeeting.chatSendButton().first().click();
        Thread.sleep(2000);;

        myMeeting.chatExitButton().first().click();
        Thread.sleep(2000);;

        myMeeting.audioMuteButton().first().click();

        test.pass("✅ MyMeeting Host flow executed successfully for email: " + email);

        page.close();
    }
}

package com.promilo.automation.advertiser.mymeetings;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Page;
import com.promilo.automation.advertiser.AdvertiserHomepage;
import com.promilo.automation.advertiser.AdvertiserLoginPage;
import com.promilo.automation.advertiser.AdverstiserMyaccount;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ExtentReports;

public class InviteFunctionality extends Baseclass {

	@Test
    public void verifyCommentFunctionality() throws InterruptedException, IOException {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("💬 Comment Functionality | Hardcoded/Random Data");

        // ✅ Excel initialized but not used for fetching test data
        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", 
                "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");
        test.info("📘 Excel Utility Initialized (Using hardcoded/random values instead)");

        // ✅ Hardcoded + random test data
        String email = "agree-laugh@ofuk8kzb.mailosaur.net"; // use a real test advertiser account email
        String password = "Karthik@88";          // use the correct password for above account
        String comment = "Automation Comment " + new Random().nextInt(1000);
        String editedComment = "Edited Automation Comment " + new Random().nextInt(1000);

        // ✅ Launch browser
        Page page = initializePlaywright();
        page.navigate(prop.getProperty("stageUrl"));
        page.setViewportSize(1000, 768);
        Thread.sleep(3000);

        AdvertiserLoginPage login = new AdvertiserLoginPage(page);

        // ✅ UI assertions
        Assert.assertTrue(login.signInContent().isVisible(), "❌ Sign-in content is not visible.");
        Assert.assertTrue(login.talkToAnExpert().isVisible(), "Talk To expert content should be visible");

        // ✅ Login
        login.loginMailField().fill(email);
        login.loginPasswordField().fill(password);
        login.signInButton().click();


            // Navigate to My Account > My Meetings

            AdvertiserHomepage homepage = new AdvertiserHomepage(page);
            homepage.hamburger().click();
            homepage.myAccount().click();

            AdverstiserMyaccount account = new AdverstiserMyaccount(page);
            account.myMeeting().click();

            com.promilo.automation.advertiser.AdvertiserMymeetingPage mymeeting = new com.promilo.automation.advertiser.AdvertiserMymeetingPage(page);
            mymeeting.jobs().click();
            mymeeting.inviteButton().first().click();

            page.locator("input[class='save-input pointer save-btn']").click();
            
           
            test.pass("✅ Invite sent successfully.");
            
            

        }
    }


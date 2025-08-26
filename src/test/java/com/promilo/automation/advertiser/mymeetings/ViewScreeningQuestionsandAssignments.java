package com.promilo.automation.advertiser.mymeetings;

import java.io.IOException;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.promilo.automation.advertiser.AdverstiserMyaccount;
import com.promilo.automation.advertiser.AdvertiserHomepage;
import com.promilo.automation.advertiser.AdvertiserLoginPage;
import com.promilo.automation.advertiser.AdvertiserMymeetingPage;
import com.promilo.automation.advertiser.AdvertiserProspects;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ExtentReports;

public class ViewScreeningQuestionsandAssignments extends Baseclass {

    @Test
    public void verifyBasicDetailsFunctionalityHardcoded() throws InterruptedException, IOException {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("📄 View Screening Questions and Assignments | Hardcoded Login");

        // ✅ Initialize Excel (kept but not used for login)
        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");
        test.info("📘 Excel Utility Initialized (Using hardcoded login credentials instead)");

        // ✅ Hardcoded login credentials
        String email = "agree-laugh@ofuk8kzb.mailosaur.net";
        String password = "Karthik@88";

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

        // ✅ Navigate to My Account → My Meetings
        page.locator("//a[@class='nav-menu-main menu-toggle hidden-xs is-active nav-link']").click();
        AdvertiserHomepage myaccount = new AdvertiserHomepage(page);
        myaccount.myAccount().click();

        AdverstiserMyaccount prospect = new AdverstiserMyaccount(page);
        prospect.myMeeting().click();

        AdvertiserProspects prospect1 = new AdvertiserProspects(page);
        AdvertiserMymeetingPage Mymeeting = new AdvertiserMymeetingPage(page);

        Thread.sleep(3000);
        Mymeeting.jobs().click();

        // View Screening Questions
        prospect1.ThreedDotOption().first().click();
        Mymeeting.screeningQuestions().click();
        page.locator("//img[@alt='Add New']").click();
        Thread.sleep(2000);

        // View Assignments
        prospect1.ThreedDotOption().first().click();
        Mymeeting.assignment().click();
        Thread.sleep(5000);

      
        test.pass("✅ Test passed for user: " + email);

        page.close();
    }
}

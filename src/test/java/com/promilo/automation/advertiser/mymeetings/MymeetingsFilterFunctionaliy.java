package com.promilo.automation.advertiser.mymeetings;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.promilo.automation.advertiser.AdverstiserMyaccount;
import com.promilo.automation.advertiser.AdvertiserHomepage;
import com.promilo.automation.advertiser.AdvertiserLoginPage;
import com.promilo.automation.advertiser.AdvertiserProspects;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ExtentReports;

public class MymeetingsFilterFunctionaliy extends Baseclass {

    @Test
    public void verifyMyMeetingsFiltersWithoutExcel() throws InterruptedException, IOException {
        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("📊 MyMeetings Filter Functionality | Hardcoded Data");

        // ✅ Excel initialized but not used
        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata",
                "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");
        test.info("📘 Excel Utility Initialized (Using hardcoded values instead)");

        // ✅ Hardcoded login credentials (use valid advertiser account)
        String email = "agree-laugh@ofuk8kzb.mailosaur.net";
        String password = "Karthik@88";

        // ✅ Filters to validate (replace or extend if needed)
        List<String> filters = Arrays.asList(
                "all", "approved", "expired", "new leads", "rejected",
                "completed", "cancelled", "reschedule requests", "pendings"
        );

        // ✅ Launch browser
        Page page = initializePlaywright();
        page.navigate(prop.getProperty("stageUrl"));
        page.setViewportSize(1000, 768);
        Thread.sleep(3000);

        AdvertiserLoginPage login = new AdvertiserLoginPage(page);

        // ✅ UI checks
        Assert.assertTrue(login.signInContent().isVisible(), "❌ Sign-in content not visible.");
        Assert.assertTrue(login.talkToAnExpert().isVisible(), "❌ Talk to expert not visible.");

        // ✅ Login
        login.loginMailField().fill(email);
        login.loginPasswordField().fill(password);
        login.signInButton().click();

        // ✅ Navigate to My Meetings
        page.locator("//a[@class='nav-menu-main menu-toggle hidden-xs is-active nav-link']").click();

        AdvertiserHomepage myaccount = new AdvertiserHomepage(page);
        myaccount.myAccount().click();

        AdverstiserMyaccount prospect = new AdverstiserMyaccount(page);
        prospect.myProspect().click();

        AdvertiserProspects approveFunctionality = new AdvertiserProspects(page);
        approveFunctionality.Jobs().click();

        // ✅ Loop through each filter and test it
        for (String filter : filters) {
            test.info("🔍 Applying filter: " + filter);

            approveFunctionality.AllDropdown().click();

            switch (filter.toLowerCase()) {
                case "all":
                    approveFunctionality.All().click();
                    break;
                case "approved":
                    approveFunctionality.Approved().click();
                    break;
                case "expired":
                    approveFunctionality.Expired().click();
                    break;
                case "new leads":
                    approveFunctionality.NewLeads().click();
                    break;
                case "rejected":
                    approveFunctionality.Rejected().click();
                    break;
                case "completed":
                    approveFunctionality.Completed().click();
                    break;
                case "cancelled":
                    approveFunctionality.Cancelled().click();
                    break;
                case "reschedule requests":
                    approveFunctionality.ResceduleRequests().click();
                    break;
                case "pendings":
                    approveFunctionality.Pendings().click();
                    break;
                default:
                    test.warning("⚠️ Unknown filter type: " + filter);
                    continue;
            }

            waitForFilterUpdate(approveFunctionality, filter);
        }

        page.close();
    }

    private void waitForFilterUpdate(AdvertiserProspects approveFunctionality, String filterName) {
        Locator filterResult = approveFunctionality.FilterResult();
        Assert.assertTrue(filterResult.isVisible(), "❌ Filter result not visible for: " + filterName);
        String filterText = filterResult.textContent().trim();
        System.out.println("✅ Filter Result (" + filterName + "): " + filterText);
    }
}

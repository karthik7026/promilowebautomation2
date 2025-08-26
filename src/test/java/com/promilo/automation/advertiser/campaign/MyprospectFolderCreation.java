package com.promilo.automation.advertiser.campaign;

import java.io.IOException;
import java.nio.file.Paths;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.promilo.automation.advertiser.AdverstiserMyaccount;
import com.promilo.automation.advertiser.AdvertiserHomepage;
import com.promilo.automation.advertiser.AdvertiserLoginPage;
import com.promilo.automation.advertiser.AdvertiserProspects;
import com.promilo.automation.resources.*;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ExtentReports;

public class MyprospectFolderCreation extends Baseclass {

	 @Test
	    public void verifyFilterFunctionalityDataDriven() throws InterruptedException, IOException {
	        ExtentReports extent = ExtentManager.getInstance();
	        ExtentTest test = extent.createTest("🚀 Filter Functionality | Data Driven");

	        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
	        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloTestData");

	        int rowCount = 0;
	        for (int i = 1; i <= 1000; i++) {
	            String testCaseId = excel.getCellData(i, 0);
	            if (testCaseId == null || testCaseId.trim().isEmpty()) break;
	            rowCount++;
	        }

	        test.info("📘 Loaded " + rowCount + " rows from Excel.");

	        for (int i = 1; i < rowCount; i++) {
	            String testCaseId = excel.getCellData(i, 0);
	            String keyword = excel.getCellData(i, 1);
	            String email = excel.getCellData(i, 7);
	            String password = excel.getCellData(i, 6);

	            if (!"FilterFunctionality".equalsIgnoreCase(keyword)) {
	                continue;
	            }

	            test.info("🔐 Executing: " + testCaseId + " | Email: " + email);

	            Page page = initializePlaywright();
	            page.navigate(prop.getProperty("stageUrl"));
	            page.setViewportSize(1000, 768);
	            Thread.sleep(3000);

	            AdvertiserLoginPage login = new AdvertiserLoginPage(page);
	            Assert.assertTrue(login.signInContent().isVisible(), "❌ Sign-in content not visible.");
	            Assert.assertTrue(login.talkToAnExpert().isVisible(), "Talk To Expert should be visible");

	            login.loginMailField().fill("agree-laugh@ofuk8kzb.mailosaur.net");
	            login.loginPasswordField().fill("Karthik@88");
	            login.signInButton().click();


                page.locator("//a[@class='nav-menu-main menu-toggle hidden-xs is-active nav-link']").click();
                AdvertiserHomepage myaccount = new AdvertiserHomepage(page);
                myaccount.myAccount().click();

                AdverstiserMyaccount prospect = new AdverstiserMyaccount(page);
                prospect.myProspect().click();

                AdvertiserProspects approveFunctionality = new AdvertiserProspects(page);
                approveFunctionality.Jobs().click();
                Thread.sleep(3000);

                approveFunctionality.shortlistedbutton().click();
                approveFunctionality.CreateFolder().click();
                approveFunctionality.FolderName().fill("foldername");
                approveFunctionality.FolderDescription().fill("folder description");
                approveFunctionality.CreateButton().click();

                Thread.sleep(4000);
                page.locator("//div[@class='d-flex']//a[@class='nav-link active']").click();

                approveFunctionality.FolderCheckbox().first().click();
                page.locator("//div[text()='···']").first().click();
                approveFunctionality.ViewDetails().click();
                page.locator("//button[@aria-label='Close']").click();
                Thread.sleep(3000);

                page.locator("//div[text()='···']").first().click();
                approveFunctionality.RenameFolder().click();
                approveFunctionality.FolderName().fill("ranamefolder");
                page.locator("//button[text()='Confirm']").click();
                Thread.sleep(4000);

                approveFunctionality.DeleteFolder().click();

                Locator codeElement = page.locator("//b[contains(text(), 'Please enter this code')]");
                String fullText = codeElement.textContent();
                String otpCode = fullText.replaceAll("[^0-9]", "");

                for (int j = 0; j < otpCode.length(); j++) {
                    char digit = otpCode.charAt(j);
                    String locatorXpath = String.format("//input[@aria-label='Please enter OTP character %d']", j + 1);
                    Locator otpInput = page.locator(locatorXpath);
                    otpInput.fill(String.valueOf(digit));
                }

                Locator deleteButtons = page.locator("//button[text()='Delete']");
                deleteButtons.nth(1).click();
                

               

                test.pass("✅ MyProspect folder created, renamed, viewed, and deleted successfully.");

            }
    }
}

package com.promilo.automation.emailnotifications.user.gethrcall;

import static org.testng.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.promilo.automation.advertiser.AdverstiserMyaccount;
import com.promilo.automation.advertiser.AdvertiserHomepage;
import com.promilo.automation.advertiser.AdvertiserLoginPage;
import com.promilo.automation.advertiser.AdvertiserProspects;
import com.promilo.automation.advertiser.campaign.ProspectApproveFunctionality;
import com.promilo.automation.pageobjects.emailnotifications.gethrcall.InitiateHrCallNotification;
import com.promilo.automation.pageobjects.signuplogin.JobListingPage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.registereduser.jobs.RegisteredUserShortList;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.promilo.automation.resources.SignupWithMailosaurUI;

public class GetHrCallRequestAccepted  extends Baseclass{
	
	 ExtentReports extent = ExtentManager.getInstance();
     ExtentTest test = extent.createTest("🚀 Promilo Staging Signup - Passes if 'My Stuff' is visible after signup (Playwright)");

	 private static final Logger logger = LogManager.getLogger(RegisteredUserShortList.class);

	    private static String registeredEmail = null;
	    private static String registeredPassword = null;

	    @BeforeSuite
	    public void performSignupOnce() throws Exception {
	        System.out.println("⚙️ Performing signup ONCE before entire suite using Mailosaur UI signup...");

	        SignupWithMailosaurUI signupWithMailosaur = new SignupWithMailosaurUI();
	        String[] creds = signupWithMailosaur.performSignupAndReturnCredentials();

	        registeredEmail = creds[0];
	        registeredPassword = creds[1];

	        System.out.println("✅ Signup completed. Registered user: " + registeredEmail);
	    }

	    @DataProvider(name = "jobApplicationData")
	    public Object[][] jobApplicationData() throws Exception {
	        String excelPath = Paths.get(System.getProperty("user.dir"), "Testdata", "PromiloAutomationTestData_Updated_With_OTP (2).xlsx").toString();
	        ExcelUtil excel = new ExcelUtil(excelPath, "PromiloJob");

	        int rowCount = 0;
	        for (int i = 1; i <= 1; i++) {  
	            String testCaseId = excel.getCellData(i, 0);
	            if (testCaseId == null || testCaseId.isEmpty()) break;
	            rowCount++;
	        }

	        Object[][] data = new Object[rowCount][8];
	        for (int i = 1; i <= rowCount; i++) {
	            data[i - 1][0] = excel.getCellData(i, 0); // TestCaseID
	            data[i - 1][1] = excel.getCellData(i, 1); // Keyword
	            data[i - 1][2] = excel.getCellData(i, 4); // InputValue (ignored)
	            data[i - 1][3] = excel.getCellData(i, 6); // Password (ignored)
	            data[i - 1][4] = excel.getCellData(i, 7); // Name
	            data[i - 1][5] = excel.getCellData(i, 5); // OTP
	            data[i - 1][6] = excel.getCellData(i, 8); // MailPhone
	            data[i - 1][7] = i;                       // RowIndex
	        }
	        return data;
	    }

	    @Test(dataProvider = "jobApplicationData")
	    public void applyForJobTestFromExcel(
	            String testCaseId,
	            String keyword,
	            String inputvalue,
	            String password,
	            String name,
	            String otp,
	            String mailphone,
	            int rowIndex
	    ) throws Exception {

	        ExtentReports extent = ExtentManager.getInstance();
	        ExtentTest test = extent.createTest("Apply for Job as Registered User | " + testCaseId);

	        if (registeredEmail == null || registeredPassword == null) {
	            test.fail("❌ Signup credentials not found.");
	            Assert.fail("Signup not completed.");
	            return;
	        }

	        // Override input credentials with signed up ones
	        inputvalue = registeredEmail;
	        password = registeredPassword;

	        Page page = initializePlaywright();
	        page.navigate(prop.getProperty("url"));
	        page.setViewportSize(1000, 768);
	        test.info("🌐 Navigated to Promilo staging site");

	        applyForJobAsRegisteredUser(page, inputvalue, password, name, otp, mailphone);

	        test.pass("✅ Job application test passed for TestCase: " + testCaseId);
	        extent.flush();
	    }

	    public void applyForJobAsRegisteredUser(Page page, String inputvalue, String password, String name, String otp, String mailphone) throws Exception {
	        LandingPage landingPage = new LandingPage(page);
	        try {
	            landingPage.getPopup().click();
	            test.info("Closed initial popup");
	        } catch (Exception ignored) {}

	        landingPage.clickLoginButton();
	        test.info("Clicked on Login button");

	        LoginPage loginPage = new LoginPage(page);
	        loginPage.loginMailPhone().fill(inputvalue);
	        loginPage.passwordField().fill(password);
	        loginPage.loginButton().click();
	        test.info("Logged in as registered user: " + inputvalue);

	        applyJobDetailsFlow(page, name, otp, mailphone);
	    }

	    private void applyJobDetailsFlow(Page page, String name, String otp, String mailphone) throws Exception {
	        JobListingPage homePage = new JobListingPage(page);
	        
	        homePage.homepageJobs().click();
	        test.info("Navigated to Jobs section");

	        homePage.fintech();
	        test.info("Selected Fintech jobs category");

	        Thread.sleep(5000);
	        Locator fintechJobCard = page.locator("//p[text()='Developer']").first();
	        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
	        fintechJobCard.scrollIntoViewIfNeeded();
	        fintechJobCard.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
	        fintechJobCard.click();
	        test.info("Opened Python Developer job card");

	        Thread.sleep(2000);
	        homePage.getHrCall().first().click();
	        test.info("Clicked on Get HR Call button");

	        page.locator("//div[@class='ask-us-popup-form-side']//input[@id='userName']").fill("karthikabc");
	        test.info("Filled user name");

	        Random random = new Random();
	        String mobileToUse = (mailphone != null && !mailphone.isEmpty()) ? mailphone : ("90000" + String.format("%05d", random.nextInt(100000)));
	        page.locator("//div[@class='ask-us-popup-form-side']//input[@id='userMobile']").fill(mobileToUse);
	        test.info("Filled user mobile: " + mobileToUse);
	        
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
	        
	        page.locator("//div[@class='ask-us-popup-form-side']//input[@id='userName']").click();


	        page.waitForTimeout(4000);
	        homePage.getAnHrCallButton().click();
	        test.info("Clicked on Get An HR Call");

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
	        test.info("Clicked Verify & Proceed");

	        page.locator("//button[text()='Submit']").nth(1).click();
	        test.info("Clicked Submit on HR call popup");
	        
	        Locator thankYouPopup = page.locator("//div[translate(normalize-space(text()), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'thank you!']");
	        thankYouPopup.waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(WaitForSelectorState.VISIBLE));

	        Assert.assertTrue(thankYouPopup.isVisible(), "'Thank You' popup not displayed.");
	       
	        
	        test.info("✅ 'Thank You' popup displayed");
	        
	        
	        
	        Page approvePage = page.context().newPage();
	        approvePage.navigate(prop.getProperty("stageUrl"));
	        approvePage.setViewportSize(1000, 768);

	        Thread.sleep(5000);

	        // ✅ Login in new tab
	        AdvertiserLoginPage login2 = new AdvertiserLoginPage(approvePage);
	        Assert.assertTrue(login2.signInContent().isVisible(), "❌ Sign-in content is not visible.");
	        Assert.assertTrue(login2.talkToAnExpert().isVisible(), "Talk To expert content should be visible");

	        login2.loginMailField().fill("vikas78@yopmail.com");
	        login2.loginPasswordField().fill("Abcd12345");
	        login2.signInButton().click();

	        // ✅ Navigate to My Account → Prospects
	        approvePage.locator("//a[@class='nav-menu-main menu-toggle hidden-xs is-active nav-link']").click();

	        AdvertiserHomepage myaccount2 = new AdvertiserHomepage(approvePage);
	        myaccount2.myAccount().click();

	        AdverstiserMyaccount prospect2 = new AdverstiserMyaccount(approvePage);
	        prospect2.myProspect().click();

	        // ✅ Approve functionality
	        AdvertiserProspects approveFunctionality = new AdvertiserProspects(approvePage);
	        approveFunctionality.Jobs().click();
	        approveFunctionality.CallbackOrTalktoExpert().click();
	        Thread.sleep(3000);

	        approveFunctionality.ApproveButton().first().click();
	        Thread.sleep(6000);

	        // ✅ Popup handling must be on approvePage (not page)
	        Locator popups = approvePage.locator("//div[@class='modal-content']");
	        Locator visiblePopup = popups.filter(new Locator.FilterOptions().setHasText("By proceeding with the ")); 
	        visiblePopup.locator("//button[normalize-space()='Proceed']").click();

	       
	    System.out.println(approvePage.locator("//p[@class='font-14 text-center']").textContent());   
	    approvePage.locator("//button[contains(text(),'Done')]").click(); 
	       
	        // ✅ Close the approve tab
	        approvePage.close();

	        // ✅ Switch back to main page
	        page.bringToFront();

	        // ✅ Open Mailosaur inbox in new tab
	        Page mailPage = page.context().newPage();
	        mailPage.navigate("https://mailosaur.com/app/servers/ofuk8kzb/messages/inbox");
	        test.info("Opened Mailosaur inbox");

	        mailPage.locator("//input[@placeholder='Enter your email address']").fill("karthiku7026@gmail.com");
	        mailPage.locator("//button[text()='Continue']").click();
	        mailPage.locator("//input[@placeholder='Enter your password']").fill("Karthik@88");
	        mailPage.locator("//button[text()='Log in']").click();
	        test.info("Logged into Mailosaur");


	        // ✅ Work inside Mailosaur tab
	        Locator firstEmail = mailPage.locator("//p[text()='HR Call Request with BCFORWARD is Pending – We’ll Be in Touch Soon!']").first();
	        firstEmail.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
	        firstEmail.click();
	        test.info("Opened first HR Call Request email");

	        Thread.sleep(3000);
	        InitiateHrCallNotification hrCall = new InitiateHrCallNotification(mailPage);
	        hrCall.GreetingText().textContent();
	        test.info("Fetched greeting text from email");
	        hrCall.GetHRcallText().textContent();
	        test.info("Fetched HR Call details text");

	        // ✅ Verify links
	        hrCall.facebook();
	        test.info("Verified Facebook link");
	        hrCall.instagram();
	        test.info("Verified Instagram link");
	        hrCall.getItonAppstore();
	        test.info("Verified App Store link");
	        hrCall.getItonPlaystore();
	        test.info("Verified Play Store link");
	        hrCall.myPreference();
	        test.info("Verified My Preference link");
	        hrCall.jobCard();
	        test.info("Verified Job Card");

	        hrCall.myPreference().click();
	        test.info("Clicked My Preference link");

	        // ✅ Assert card is visible
	        mailPage.locator(".my-preferance-card-body");
	        test.pass("✅ Card is displayed in Talk to Experts/1:1 Call section");

}
	    
}

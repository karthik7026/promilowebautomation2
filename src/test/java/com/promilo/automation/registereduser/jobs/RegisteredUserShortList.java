package com.promilo.automation.registereduser.jobs;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Paths;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.promilo.automation.pageobjects.emailnotifications.EmailnotificationsShortlisted;
import com.promilo.automation.pageobjects.signuplogin.JobListingPage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExcelUtil;
import com.promilo.automation.resources.ExtentManager;
import com.promilo.automation.resources.SignupWithMailosaurUI;

public class RegisteredUserShortList extends Baseclass {
	
	
	 ExtentReports extent = ExtentManager.getInstance();
     //Test Description
     ExtentTest test = extent.createTest("🚀 promilo shortlist functionality with email notification validation");


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

        applyForJobAsRegisteredUser(page, inputvalue, password, name, otp, mailphone);

        test.pass("✅ Job application test passed for TestCase: " + testCaseId);
        extent.flush();
    }

    public void applyForJobAsRegisteredUser(Page page, String inputvalue, String password, String name, String otp, String mailphone) throws Exception {
        LandingPage landingPage = new LandingPage(page);
        try {
            landingPage.getPopup().click();
        } catch (Exception ignored) {}

        landingPage.clickLoginButton();

        LoginPage loginPage = new LoginPage(page);
        loginPage.loginMailPhone().fill(inputvalue);
        loginPage.passwordField().fill(password);
        loginPage.loginButton().click();

        applyJobDetailsFlow(page, name, otp, mailphone);
    }

    private void applyJobDetailsFlow(Page page, String name, String otp, String mailphone) throws Exception {
        JobListingPage homePage = new JobListingPage(page);
        
        Thread.sleep(5000);
        homePage.homepageJobs().click();
        page.waitForTimeout(5000);
        homePage.jobShortlist1().first().click();
        page.waitForTimeout(5000);

        // Use the 'name' and 'mailphone' params in the form instead of hardcoded values
        page.locator("//div[@class='ask-us-popup-form-side']//input[@id='userName']").fill("karthik");

        Random random = new Random();
        // Use mailphone if provided, else generate random mobile
        String mobileToUse = (mailphone != null && !mailphone.isEmpty()) ? mailphone : ("90000" + String.format("%05d", random.nextInt(100000)));
        page.locator("//div[@class='ask-us-popup-form-side']//input[@id='userMobile']").fill(mobileToUse);

        page.waitForTimeout(4000);
        homePage.jobShortList().click();

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
        }

        page.locator("//button[text()='Verify & Proceed']").click();

        Locator thankYouPopup = page.locator("//div[translate(normalize-space(text()), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'thank you!']");
        thankYouPopup.waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(WaitForSelectorState.VISIBLE));

        Assert.assertTrue(thankYouPopup.isVisible(), "'Thank You' popup not displayed.");
        
        

        Page page1 = page.context().newPage();

        page1.navigate("https://mailosaur.com/app/servers/ofuk8kzb/messages/inbox");

        page1.locator("//input[@placeholder='Enter your email address']").fill("karthiku7026@gmail.com");
        page1.locator("//button[text()='Continue']").click();

        page1.locator("//input[@placeholder='Enter your password']").fill("Karthik@88");
        page1.locator("//button[text()='Log in']").click();
        
        Thread.sleep(3000);
        
        page.reload();

        Locator firstEmail = page1.locator("//p[contains(text(),'Great Choice! You Shortlisted')]").first();
        
        firstEmail.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        firstEmail.click();
        System.out.println("2nd mail oepned");
        
        Thread.sleep(3000);
        
        EmailnotificationsShortlisted notifications = new EmailnotificationsShortlisted(page1);
       
       System.out.println(notifications.greetings().textContent()); 
       System.out.println(page1.getByText("You’ve successfully").textContent()); 
       System.out.println(page1.getByText("Keep up the momentum by").textContent()); 
       System.out.println(page1.getByText("Apply Now to Kickstart Your").textContent());
       
       


     
       page1.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Get HR Call")).click();
       
       
       Thread.sleep(5000);
       
       page1.getByText("react devreact devBengaluru/BangaloreshbhjbhExperience2-6 YrsSalary2L -").click();
       
     
       

        
        // Close the context/browser after test to avoid resource leaks
    }
}

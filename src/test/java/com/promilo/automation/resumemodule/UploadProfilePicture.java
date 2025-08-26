package com.promilo.automation.resumemodule;

import java.nio.file.Paths;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.promilo.automation.pageobjects.myresume.MyResumePage;
import com.promilo.automation.pageobjects.signuplogin.LandingPage;
import com.promilo.automation.pageobjects.signuplogin.LoginPage;
import com.promilo.automation.resources.Baseclass;
import com.promilo.automation.resources.ExtentManager;

public class UploadProfilePicture extends Baseclass{
	
	@Test
    public void ResumeUpload() throws Exception {

        ExtentReports extent = ExtentManager.getInstance();
        ExtentTest test = extent.createTest("🚀 Apply for Job as Registered User | Hardcoded");

        Page page = initializePlaywright();
        page.navigate(prop.getProperty("url"));
        page.setViewportSize(1000, 768);

        LandingPage landingPage = new LandingPage(page);
        landingPage.getPopup().click();
        landingPage.clickLoginButton();

        LoginPage loginPage = new LoginPage(page);
        loginPage.loginMailPhone().fill("testuserppp4@gmail.com");           // hardcoded email/phone
        loginPage.passwordField().fill("Karthik@88");            // hardcoded password
        loginPage.loginButton().click();

        MyResumePage resumePage = new MyResumePage(page);
        resumePage.Mystuff().click();
        System.out.println("my stuff clicked");
        resumePage.MyAccount().click();
        System.out.println("my account clicked");
        resumePage.MyResume().click();
        
        Thread.sleep(3000);
        
     // Step 1: Click edit icon
        page.locator("//img[@alt='Edit']").click();
        Thread.sleep(3000);

        
        
     // Click on 'prolet' image to open upload dialog (modal)
        Locator setInputFiles = page.locator("//img[@alt='prolet']");
        setInputFiles.first().click();

        Thread.sleep(5000); // Wait for modal/input to appear

        // Upload the file to the input[type=file] element (e.g., <input id="upload-img" type="file">)
        page.locator("#upload-img").setInputFiles(Paths.get("C:\\Users\\Admin\\Downloads\\capture.png"));

        
        Thread.sleep(5000);
        // Click Crop button
        page.locator("//button[text()='Crop']").click();



        
	}
}

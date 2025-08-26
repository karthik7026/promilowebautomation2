package com.promilo.automation.pageobjects.signuplogin;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class DashboardPage {

    private final Page page;

    // ✅ Profile image after login
    private final Locator profileImage;

    // ✅ Login validation image/icon
    private final Locator loginValidation;

    //My stuff element
    private final Locator mystuff;
    
    
    //locators of the Dashboard page
    public DashboardPage(Page page) {
        this.page = page;
        this.profileImage = page.locator("//div[@role='status']");
        this.loginValidation = page.locator("//div[@id='navbarScroll']//img[@class='me-50 menu-header-user-img rounded-circle']");
        this.mystuff = page.locator("//img[@alt='my stuff']");
    }

    
    public Locator getProfileImage() {
        return profileImage;
    }

   
    public Locator loginValidation() {
        return loginValidation;
    }
    
    public Locator mystuff() {
        return mystuff;
    }
}

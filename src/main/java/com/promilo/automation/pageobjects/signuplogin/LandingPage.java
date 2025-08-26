package com.promilo.automation.pageobjects.signuplogin;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LandingPage {

    private Page page;

    // Locators
    private Locator popup;
    private Locator signup;
    private Locator loginButton;

    // Constructor
    public LandingPage(Page page) {
        this.page = page;
        this.popup = page.locator("//button[text()='May be later!']");
        this.signup = page.locator("//div[@class='sign-up-button']");
        this.loginButton = page.locator("//div[@class='Login-button']");
        
    }

    // Actions or Getters

    public void dismissPopup() {
        if (popup.isVisible()) {
            popup.click();
        }
    }

    public void clickSignup() {
        signup.click();
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    // Exposing Locators if you want them (optional)
    public Locator getPopup() {
        return popup;
    }

    public Locator getSignup() {
        return signup;
    }

    public Locator getLoginButton() {
        return loginButton;
    }
}

package com.automation.pages.SauceDemo;

import com.automation.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {
    
    // Priority 1: ID selectors (most stable and reliable)
    @FindBy(id = "user-name")
    private WebElement usernameInput;
    
    @FindBy(id = "password")
    private WebElement passwordInput;
    
    @FindBy(id = "login-button")
    private WebElement loginButton;
    
    // Priority 3: CSS selector for error message (no ID available)
    @FindBy(css = ".error-message-container")
    private WebElement errorMessage;
    
    @FindBy(css = ".login-box")
    private WebElement loginBox;
    
    // Constructor
    public LoginPage(org.openqa.selenium.WebDriver driver) {
        super(driver);
    }
    
    // Interaction methods using BasePage utilities
    public void login(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(loginButton);
    }
    
    // Validation methods using BasePage utilities
    public boolean isLoginSuccessful() {
        return getCurrentUrl().contains("/inventory.html");
    }
    
    public String getErrorMessage() {
        return getText(errorMessage);
    }
    
    public boolean isPageLoaded() {
        try {
            String currentUrl = getCurrentUrl();
            System.out.println("Current URL: " + currentUrl);
            
            // Use longer timeout for parallel execution - elements may take longer to initialize
            waitForElementVisible(usernameInput, 15);
            
            boolean usernameDisplayed = isDisplayed(usernameInput);
            boolean passwordDisplayed = isDisplayed(passwordInput);
            boolean buttonDisplayed = isDisplayed(loginButton);
            
            System.out.println("Username displayed: " + usernameDisplayed);
            System.out.println("Password displayed: " + passwordDisplayed);
            System.out.println("Login button displayed: " + buttonDisplayed);
            
            return usernameDisplayed && passwordDisplayed && buttonDisplayed;
        } catch (Exception e) {
            System.out.println("Login page not loaded: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }
}

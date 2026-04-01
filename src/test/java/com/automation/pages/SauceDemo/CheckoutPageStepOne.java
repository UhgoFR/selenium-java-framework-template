package com.automation.pages.SauceDemo;

import com.automation.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutPageStepOne extends BasePage {
    
    // Page title
    @FindBy(css = ".title")
    private WebElement pageTitle;
    
    // Form fields
    @FindBy(id = "first-name")
    private WebElement firstNameInput;
    
    @FindBy(id = "last-name")
    private WebElement lastNameInput;
    
    @FindBy(id = "postal-code")
    private WebElement postalCodeInput;
    
    // Action buttons
    @FindBy(id = "cancel")
    private WebElement cancelButton;
    
    @FindBy(id = "continue")
    private WebElement continueButton;
    
    // Error message
    @FindBy(css = ".error-message-container")
    private WebElement errorMessage;
    
    @FindBy(css = ".error-button")
    private WebElement errorButton;
    
    // Constructor
    public CheckoutPageStepOne(org.openqa.selenium.WebDriver driver) {
        super(driver);
    }
    
    // Page validation methods
    public boolean isPageLoaded() {
        return getCurrentUrl().contains("/checkout-step-one.html") && isDisplayed(pageTitle);
    }
    
    public String getPageTitle() {
        return getText(pageTitle);
    }
    
    // Form interaction methods
    public void fillCheckoutInformation(String firstName, String lastName, String postalCode) {
        
        System.out.println("CheckoutPageStepOne: Filling checkout information");
        System.out.println("First name input displayed: " + isDisplayed(firstNameInput));
        System.out.println("Last name input displayed: " + isDisplayed(lastNameInput));
        System.out.println("Postal code input displayed: " + isDisplayed(postalCodeInput));
        
        type(firstNameInput, firstName);
        System.out.println("First name entered: " + firstName);
        
        type(lastNameInput, lastName);
        System.out.println("Last name entered: " + lastName);
        
        type(postalCodeInput, postalCode);
        System.out.println("Postal code entered: " + postalCode);
        
    }
    
    
    public void setFirstName(String firstName) {
        type(firstNameInput, firstName);
    }
    
    public void setLastName(String lastName) {
        type(lastNameInput, lastName);
    }
    
    public void setPostalCode(String postalCode) {
        type(postalCodeInput, postalCode);
    }
    
    // Form submission methods
    public void continueCheckout() {
        System.out.println("CheckoutPageStepOne: Attempting to continue checkout");
        System.out.println("Continue button displayed: " + isDisplayed(continueButton));
        System.out.println("Current URL before continue: " + getCurrentUrl());
        
        // Try regular click first
        click(continueButton);
        
        // Wait for navigation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Current URL after regular click: " + getCurrentUrl());
        
        // If URL hasn't changed, try JavaScript click
        if (!getCurrentUrl().contains("checkout-step-two")) {
            System.out.println("CheckoutPageStepOne: Regular click didn't work, trying JavaScript click");
            clickWithJS(continueButton);
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            System.out.println("Current URL after JS click: " + getCurrentUrl());
        }
        
        System.out.println("CheckoutPageStepOne: Continue checkout completed");
    }
    
    public void cancelCheckout() {
        click(cancelButton);
    }
    
    // Form validation methods
    public String getFirstName() {
        return getValue(firstNameInput);
    }
    
    public String getLastName() {
        return getValue(lastNameInput);
    }
    
    public String getPostalCode() {
        return getValue(postalCodeInput);
    }
    
    public boolean isFormEmpty() {
        return getFirstName().isEmpty() && getLastName().isEmpty() && getPostalCode().isEmpty();
    }
    
    public boolean isFormValid() {
        return !getFirstName().isEmpty() && !getLastName().isEmpty() && !getPostalCode().isEmpty();
    }
    
    // Error handling methods
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }
    
    public String getErrorMessage() {
        return getText(errorMessage);
    }
    
    public void dismissError() {
        if (isDisplayed(errorButton)) {
            click(errorButton);
        }
    }
    
    // Utility methods
    public void clearForm() {
        firstNameInput.clear();
        lastNameInput.clear();
        postalCodeInput.clear();
    }
    
    public boolean areAllFieldsDisplayed() {
        return isDisplayed(firstNameInput) && isDisplayed(lastNameInput) && isDisplayed(postalCodeInput);
    }
    
    public boolean areButtonsDisplayed() {
        return isDisplayed(continueButton) && isDisplayed(cancelButton);
    }
}

package com.automation.pages.SauceDemo;

import com.automation.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class CheckoutCompletePage extends BasePage {
    
    // Page title
    @FindBy(css = ".title")
    private WebElement pageTitle;
    
    // Completion message
    @FindBy(css = ".complete-header")
    private WebElement completeHeader;
    
    @FindBy(css = ".complete-text")
    private WebElement completeText;
    
    // Pony Express image
    @FindBy(css = ".pony_express")
    private WebElement ponyExpressImage;
    
    // Action button
    @FindBy(id = "back-to-products")
    private WebElement backToProductsButton;
    
    // Constructor
    public CheckoutCompletePage(org.openqa.selenium.WebDriver driver) {
        super(driver);
    }
    
    // Page validation methods
    public boolean isPageLoaded() {
        return getCurrentUrl().contains("/checkout-complete.html") && isDisplayed(pageTitle);
    }
    
    public String getPageTitle() {
        return getText(pageTitle);
    }
    
    // Completion message methods
    public String getCompleteHeader() {
        return getText(completeHeader);
    }
    
    public String getCompleteText() {
        return getText(completeText);
    }
    
    public boolean isOrderCompleteMessageDisplayed() {
        return isDisplayed(completeHeader) && isDisplayed(completeText);
    }
    
    public boolean isThankYouMessageDisplayed() {
        String header = getCompleteHeader();
        return header != null && header.contains("Thank you for your order");
    }
    
    // Image validation
    public boolean isPonyExpressImageDisplayed() {
        return isDisplayed(ponyExpressImage);
    }
    
    // Action methods
    public void backToProducts() {
        click(backToProductsButton);
    }
    
    // Utility methods
    public boolean areAllElementsDisplayed() {
        return isPageLoaded() && 
               isOrderCompleteMessageDisplayed() && 
               isPonyExpressImageDisplayed() && 
               isDisplayed(backToProductsButton);
    }
    
    // Navigation validation
    public boolean canNavigateBackToProducts() {
        return isDisplayed(backToProductsButton);
    }
}

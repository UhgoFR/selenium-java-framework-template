package com.automation.pages.SauceDemo;

import com.automation.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CheckoutPageStepTwo extends BasePage {
    
    // Page title
    @FindBy(css = ".title")
    private WebElement pageTitle;
    
    // Summary items
    @FindBy(css = ".cart_item")
    private List<WebElement> summaryItems;
    
    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;
    
    @FindBy(css = ".inventory_item_desc")
    private List<WebElement> productDescriptions;
    
    @FindBy(css = ".inventory_item_price")
    private List<WebElement> productPrices;
    
    @FindBy(css = ".cart_quantity")
    private List<WebElement> productQuantities;
    
    // Payment information
    @FindBy(css = ".summary_info")
    private WebElement summaryInfo;
    
    @FindBy(css = ".summary_info div:nth-child(2)")
    private WebElement paymentInfoLabel;
    
    @FindBy(css = ".summary_info div:nth-child(3)")
    private WebElement paymentInfoValue;
    
    // Shipping information
    @FindBy(css = ".summary_info div:nth-child(4)")
    private WebElement shippingInfoLabel;
    
    @FindBy(css = ".summary_info div:nth-child(5)")
    private WebElement shippingInfoValue;
    
    // Price totals
    @FindBy(css = ".summary_info div:nth-child(6)")
    private WebElement subtotalLabel;
    
    @FindBy(css = ".summary_info div:nth-child(7)")
    private WebElement taxLabel;
    
    @FindBy(css = ".summary_info div:nth-child(8)")
    private WebElement totalLabel;
    
    // Action buttons
    @FindBy(id = "cancel")
    private WebElement cancelButton;
    
    @FindBy(id = "finish")
    private WebElement finishButton;
    
    // Constructor
    public CheckoutPageStepTwo(org.openqa.selenium.WebDriver driver) {
        super(driver);
    }
    
    // Page validation methods
    public boolean isPageLoaded() {
        return getCurrentUrl().contains("/checkout-step-two.html") && isDisplayed(pageTitle);
    }
    
    public String getPageTitle() {
        return getText(pageTitle);
    }
    
    // Summary item methods
    public int getSummaryItemCount() {
        return summaryItems.size();
    }
    
    public boolean isProductInSummary(String productName) {
        for (WebElement name : productNames) {
            if (getText(name).equals(productName)) {
                return true;
            }
        }
        return false;
    }
    
    public String getProductQuantity(String productName) {
        for (int i = 0; i < productNames.size(); i++) {
            if (getText(productNames.get(i)).equals(productName)) {
                return getText(productQuantities.get(i));
            }
        }
        return null;
    }
    
    public String getProductPrice(String productName) {
        for (int i = 0; i < productNames.size(); i++) {
            if (getText(productNames.get(i)).equals(productName)) {
                return getText(productPrices.get(i));
            }
        }
        return null;
    }
    
    // Payment information methods
    public String getPaymentInformation() {
        return getText(paymentInfoValue);
    }
    
    public String getShippingInformation() {
        return getText(shippingInfoValue);
    }
    
    // Price calculation methods
    public String getSubtotal() {
        String text = getText(subtotalLabel);
        return text.replace("Item total: $", "");
    }
    
    public String getTax() {
        String text = getText(taxLabel);
        return text.replace("Tax: $", "");
    }
    
    public String getTotal() {
        String text = getText(totalLabel);
        return text.replace("Total: $", "");
    }
    
    public double getSubtotalAsDouble() {
        try {
            return Double.parseDouble(getSubtotal());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public double getTaxAsDouble() {
        try {
            return Double.parseDouble(getTax());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public double getTotalAsDouble() {
        try {
            return Double.parseDouble(getTotal());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    // Validation methods
    public boolean isPriceCalculationCorrect() {
        double expectedTotal = getSubtotalAsDouble() + getTaxAsDouble();
        double actualTotal = getTotalAsDouble();
        return Math.abs(expectedTotal - actualTotal) < 0.01; // Allow for rounding differences
    }
    
    public boolean isPaymentInfoDisplayed() {
        return isDisplayed(paymentInfoLabel) && isDisplayed(paymentInfoValue);
    }
    
    public boolean isShippingInfoDisplayed() {
        return isDisplayed(shippingInfoLabel) && isDisplayed(shippingInfoValue);
    }
    
    public boolean arePriceTotalsDisplayed() {
        return isDisplayed(subtotalLabel) && isDisplayed(taxLabel) && isDisplayed(totalLabel);
    }
    
    // Action methods
    public void finishCheckout() {
        click(finishButton);
    }
    
    public void cancelCheckout() {
        click(cancelButton);
    }
    
    // Utility methods
    public boolean areAllElementsDisplayed() {
        return isPageLoaded() && 
               isPaymentInfoDisplayed() && 
               isShippingInfoDisplayed() && 
               arePriceTotalsDisplayed() &&
               isDisplayed(finishButton) && 
               isDisplayed(cancelButton);
    }
}

package com.automation.pages.SauceDemo;

import com.automation.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CartPage extends BasePage {
    
    // Page title
    @FindBy(css = ".title")
    private WebElement pageTitle;
    
    // Cart items
    @FindBy(css = ".cart_item")
    private List<WebElement> cartItems;
    
    @FindBy(css = ".cart_item_label")
    private List<WebElement> cartItemLabels;
    
    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;
    
    @FindBy(css = ".inventory_item_desc")
    private List<WebElement> productDescriptions;
    
    @FindBy(css = ".inventory_item_price")
    private List<WebElement> productPrices;
    
    @FindBy(css = ".cart_quantity")
    private List<WebElement> productQuantities;
    
    @FindBy(css = ".cart_item_label .btn_secondary")
    private List<WebElement> removeButtons;
    
    // Action buttons
    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButton;
    
    @FindBy(id = "checkout")
    private WebElement checkoutButton;
    
    // Cart badge
    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;
    
    // Constructor
    public CartPage(org.openqa.selenium.WebDriver driver) {
        super(driver);
    }
    
    // Page validation methods
    public boolean isPageLoaded() {
        return getCurrentUrl().contains("/cart.html") && isDisplayed(pageTitle);
    }
    
    public String getPageTitle() {
        return getText(pageTitle);
    }
    
    // Cart item methods
    public int getCartItemCount() {
        return cartItems.size();
    }
    
    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }
    
    public boolean isProductInCart(String productName) {
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
    
    public String getProductDescription(String productName) {
        for (int i = 0; i < productNames.size(); i++) {
            if (getText(productNames.get(i)).equals(productName)) {
                return getText(productDescriptions.get(i));
            }
        }
        return null;
    }
    
    // Cart modification methods
    public void removeProduct(String productName) {
        for (int i = 0; i < productNames.size(); i++) {
            if (getText(productNames.get(i)).equals(productName)) {
                click(removeButtons.get(i));
                break;
            }
        }
    }
    
    public void removeProductByIndex(int index) {
        if (index >= 0 && index < removeButtons.size()) {
            click(removeButtons.get(index));
        }
    }
    
    // Navigation methods
    public void continueShopping() {
        click(continueShoppingButton);
    }
    
    public void proceedToCheckout() {
        System.out.println("CartPage: Attempting to click checkout button");
        System.out.println("Checkout button displayed: " + isDisplayed(checkoutButton));
        System.out.println("Current URL before click: " + getCurrentUrl());
        
        // Try regular click first
        click(checkoutButton);
        
        // Wait for navigation with longer timeout
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Current URL after regular click: " + getCurrentUrl());
        
        // If URL hasn't changed, try JavaScript click
        if (!getCurrentUrl().contains("checkout-step-one")) {
            System.out.println("CartPage: Regular click didn't work, trying JavaScript click");
            clickWithJS(checkoutButton);
            
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            System.out.println("Current URL after JS click: " + getCurrentUrl());
        }
        
        System.out.println("CartPage: Checkout button clicked");
    }
    
    // Cart badge methods
    public String getCartBadgeCount() {
        if (isDisplayed(cartBadge)) {
            return getText(cartBadge);
        }
        return "0";
    }
    
    public boolean isCartBadgeVisible() {
        return isDisplayed(cartBadge);
    }
    
    // Utility methods
    public double calculateTotalPrice() {
        double total = 0.0;
        for (WebElement price : productPrices) {
            String priceText = getText(price).replace("$", "");
            try {
                total += Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                // Ignore invalid price formats
            }
        }
        return total;
    }
    
    public boolean isCartItemCountValid() {
        int itemCount = getCartItemCount();
        String badgeCount = getCartBadgeCount();
        try {
            return itemCount == Integer.parseInt(badgeCount);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

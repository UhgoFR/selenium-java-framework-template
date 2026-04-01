package com.automation.pages.SauceDemo;

import com.automation.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class InventoryPage extends BasePage {
    
    // Page header and navigation
    @FindBy(css = ".app_logo")
    private WebElement pageTitle;
    
    @FindBy(css = ".title")
    private WebElement productsTitle;
    
    @FindBy(id = "shopping-cart-container")
    private WebElement cartContainer;
    
    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;
    
    @FindBy(css = ".shopping_cart_link")
    private WebElement cartLink;
    
    // Sort dropdown
    @FindBy(css = ".product_sort_container")
    private WebElement sortDropdown;
    
    // Product items (using generic selectors for dynamic products)
    @FindBy(css = ".inventory_item")
    private List<WebElement> productItems;
    
    @FindBy(css = ".inventory_item_name")
    private List<WebElement> productNames;
    
    @FindBy(css = ".inventory_item_desc")
    private List<WebElement> productDescriptions;
    
    @FindBy(css = ".inventory_item_price")
    private List<WebElement> productPrices;
    
    @FindBy(css = ".btn_inventory")
    private List<WebElement> addToCartButtons;
    
    @FindBy(css = ".btn_secondary.btn_inventory")
    private List<WebElement> removeButtons;
    
    // Menu
    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButton;
    
    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLink;
    
    @FindBy(css = ".bm-menu-wrap")
    private WebElement menuContainer;
    
    // Constructor
    public InventoryPage(org.openqa.selenium.WebDriver driver) {
        super(driver);
    }
    
    // Page validation methods
    public boolean isPageLoaded() {
        return isDisplayed(productsTitle) && isDisplayed(pageTitle);
    }
    
    public boolean isProductListVisible() {
        return !productItems.isEmpty() && isDisplayed(productItems.get(0));
    }
    
    public int getProductCount() {
        return productItems.size();
    }
    
    // Product interaction methods
    public void addProductToCart(String productName) {
        WebElement product = findProductByName(productName);
        if (product != null) {
            WebElement addToCartButton = product.findElement(org.openqa.selenium.By.cssSelector(".btn_inventory"));
            click(addToCartButton);
        }
    }
    
    public void addProductToCartByIndex(int index) {
        if (index >= 0 && index < addToCartButtons.size()) {
            click(addToCartButtons.get(index));
        }
    }
    
    public void removeProductFromCart(String productName) {
        WebElement product = findProductByName(productName);
        if (product != null) {
            WebElement removeButton = product.findElement(org.openqa.selenium.By.cssSelector(".btn_secondary.btn_inventory"));
            click(removeButton);
        }
    }
    
    // Cart methods
    public void navigateToCart() {
        System.out.println("InventoryPage: Attempting to navigate to cart");
        System.out.println("Cart link displayed: " + isDisplayed(cartLink));
        System.out.println("Current URL before click: " + getCurrentUrl());
        
        // Try regular click first
        click(cartLink);
        
        // Wait for navigation
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Current URL after regular click: " + getCurrentUrl());
        
        // If URL hasn't changed, try JavaScript click
        if (!getCurrentUrl().contains("cart")) {
            System.out.println("InventoryPage: Regular click didn't work, trying JavaScript click");
            clickWithJS(cartLink);
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            System.out.println("Current URL after JS click: " + getCurrentUrl());
        }
        
        System.out.println("InventoryPage: Cart navigation completed");
    }
    
    public String getCartBadgeCount() {
        if (isDisplayed(cartBadge)) {
            return getText(cartBadge);
        }
        return "0";
    }
    
    public boolean isCartBadgeVisible() {
        return isDisplayed(cartBadge);
    }
    
    // Sort methods
    public void sortProducts(String sortOption) {
        selectByVisibleText(sortDropdown, sortOption);
    }
    
    // Menu methods
    public void openMenu() {
        click(menuButton);
        // Wait for menu to be visible
        waitForElementVisible(menuContainer);
    }
    
    public void logout() {
        openMenu();
        click(logoutLink);
    }
    
    // Utility methods
    public String getProductTitle() {
        return getText(productsTitle);
    }
    
    public String getPageTitle() {
        return getText(pageTitle);
    }
    
    private WebElement findProductByName(String productName) {
        for (WebElement product : productItems) {
            WebElement nameElement = product.findElement(org.openqa.selenium.By.cssSelector(".inventory_item_name"));
            if (getText(nameElement).equals(productName)) {
                return product;
            }
        }
        return null;
    }
    
    public boolean isProductInInventory(String productName) {
        return findProductByName(productName) != null;
    }
    
    public String getProductPrice(String productName) {
        WebElement product = findProductByName(productName);
        if (product != null) {
            WebElement priceElement = product.findElement(org.openqa.selenium.By.cssSelector(".inventory_item_price"));
            return getText(priceElement);
        }
        return null;
    }
}

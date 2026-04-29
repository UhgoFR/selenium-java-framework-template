package com.automation.tests.web.SauceDemo;

import com.automation.base.BaseTest;
import com.automation.pages.SauceDemo.LoginPage;
import com.automation.pages.SauceDemo.InventoryPage;
import com.automation.pages.SauceDemo.CartPage;
import com.automation.pages.SauceDemo.CheckoutPageStepOne;
import com.automation.pages.SauceDemo.CheckoutPageStepTwo;
import com.automation.pages.SauceDemo.CheckoutCompletePage;
import com.automation.utils.DataLoader;
import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CheckoutTests extends BaseTest {
    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutPageStepOne checkoutPageStepOne;
    private CheckoutPageStepTwo checkoutPageStepTwo;
    private CheckoutCompletePage checkoutCompletePage;

    public CheckoutTests() {
        super();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpCheckout() {
        // El driver se inicializa automáticamente en BaseTest @BeforeMethod
        WebDriver driver = getDriver();
        
        // Inicializar todas las páginas
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        cartPage = new CartPage(driver);
        checkoutPageStepOne = new CheckoutPageStepOne(driver);
        checkoutPageStepTwo = new CheckoutPageStepTwo(driver);
        checkoutCompletePage = new CheckoutCompletePage(driver);
        
        // Clean up session and start fresh
        driver.manage().deleteAllCookies();
        
        // Navegar a la URL base
        navigateToBaseUrl();
        
        // Login para preparar el entorno
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page should be loaded after login");
    }

    @Test(dataProvider = "validCheckoutData", groups = {"smoke", "regression", "SauceDemo"})
    public void testCompleteCheckoutFlow(JsonNode checkoutData) {
        // Add product to cart
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        Assert.assertTrue(inventoryPage.isCartBadgeVisible(), "Cart badge should be visible");
        
        // Navigate to cart
        inventoryPage.navigateToCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart should have 1 item");
        
        // Proceed to checkout
        cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPageStepOne.isPageLoaded(), "Checkout step one page should be loaded");
        
        // Fill checkout information
        String firstName = checkoutData.get("firstName").asText();
        String lastName = checkoutData.get("lastName").asText();
        String postalCode = checkoutData.get("postalCode").asText();
        
        checkoutPageStepOne.fillCheckoutInformation(firstName, lastName, postalCode);
        checkoutPageStepOne.continueCheckout();
        
        // Verify checkout step two
        Assert.assertTrue(checkoutPageStepTwo.isPageLoaded(), "Checkout step two page should be loaded");
        Assert.assertTrue(checkoutPageStepTwo.isPriceCalculationCorrect(), "Price calculation should be correct");
        
        // Complete checkout
        checkoutPageStepTwo.finishCheckout();
        
        // Verify checkout completion
        Assert.assertTrue(checkoutCompletePage.isPageLoaded(), "Checkout complete page should be loaded");
        Assert.assertTrue(checkoutCompletePage.isThankYouMessageDisplayed(), "Thank you message should be displayed");
        Assert.assertTrue(checkoutCompletePage.isPonyExpressImageDisplayed(), "Pony Express image should be displayed");
    }
        

    @Test(dataProvider = "invalidCheckoutData", groups = {"negative", "regression", "SauceDemo"})
    public void testInvalidCheckoutInformation(JsonNode checkoutData) {
        // Add product to cart and proceed to checkout
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        inventoryPage.navigateToCart();
        cartPage.proceedToCheckout();
        
        Assert.assertTrue(checkoutPageStepOne.isPageLoaded(), "Checkout step one page should be loaded");
        
        // Fill invalid checkout information
        String firstName = checkoutData.get("firstName").asText();
        String lastName = checkoutData.get("lastName").asText();
        String postalCode = checkoutData.get("postalCode").asText();
        String expectedError = checkoutData.get("expectedError").asText();
        
        checkoutPageStepOne.fillCheckoutInformation(firstName, lastName, postalCode);
        checkoutPageStepOne.continueCheckout();
        
        // Verify error message
        Assert.assertTrue(checkoutPageStepOne.isErrorMessageDisplayed(), "Error message should be displayed");
        String actualError = checkoutPageStepOne.getErrorMessage();
        Assert.assertTrue(actualError.contains(expectedError), 
                         "Error message should contain: " + expectedError + ", but got: " + actualError);
        
        // Verify still on checkout step one page
        Assert.assertTrue(checkoutPageStepOne.isPageLoaded(), "Should remain on checkout step one page");
    }
  

    @Test(groups = {"regression", "SauceDemo"})
    public void testCheckoutWithEmptyCart() {
        // Navigate to cart without adding products
        inventoryPage.navigateToCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty");
        
        // Try to proceed to checkout
        cartPage.proceedToCheckout();
        
        // Should still be able to proceed to checkout even with empty cart
        Assert.assertTrue(checkoutPageStepOne.isPageLoaded(), "Should be able to proceed to checkout");
    }

    @Test(groups = {"regression", "SauceDemo"})
    public void testCheckoutCancelFlow() {
        // Add product to cart and proceed to checkout
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        inventoryPage.navigateToCart();
        cartPage.proceedToCheckout();
        
        Assert.assertTrue(checkoutPageStepOne.isPageLoaded(), "Checkout step one page should be loaded");
        
        // Fill some information and then cancel
        checkoutPageStepOne.setFirstName("John");
        checkoutPageStepOne.cancelCheckout();
        
        // Should return to cart page
        Assert.assertTrue(cartPage.isPageLoaded(), "Should return to cart page after cancel");
    }

    @Test(groups = {"regression", "SauceDemo"})
    public void testCheckoutStepTwoCancelFlow() {
        // Complete first step of checkout
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        inventoryPage.navigateToCart();
        cartPage.proceedToCheckout();
        
        checkoutPageStepOne.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPageStepOne.continueCheckout();
        
        Assert.assertTrue(checkoutPageStepTwo.isPageLoaded(), "Checkout step two page should be loaded");
        
        // Cancel from step two
        checkoutPageStepTwo.cancelCheckout();
        
        // Should return to inventory page
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Should return to inventory page after cancel");
    }

    @Test(groups = {"regression", "SauceDemo"})
    public void testBackToProductsAfterComplete() {
        // Complete checkout flow
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        inventoryPage.navigateToCart();
        cartPage.proceedToCheckout();
        
        checkoutPageStepOne.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPageStepOne.continueCheckout();
        checkoutPageStepTwo.finishCheckout();
        
        Assert.assertTrue(checkoutCompletePage.isPageLoaded(), "Checkout complete page should be loaded");
        
        // Navigate back to products
        checkoutCompletePage.backToProducts();
        
        // Should return to inventory page
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Should return to inventory page");
    }

    @Test(groups = {"regression", "SauceDemo"})
    public void testCheckoutPriceCalculations() {
        // Add multiple products
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        
        inventoryPage.navigateToCart();
        cartPage.proceedToCheckout();
        
        checkoutPageStepOne.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPageStepOne.continueCheckout();
        
        Assert.assertTrue(checkoutPageStepTwo.isPageLoaded(), "Checkout step two page should be loaded");
        
        // Verify price calculations
        double subtotal = checkoutPageStepTwo.getSubtotalAsDouble();
        double tax = checkoutPageStepTwo.getTaxAsDouble();
        double total = checkoutPageStepTwo.getTotalAsDouble();
        
        Assert.assertTrue(subtotal > 0, "Subtotal should be greater than 0");
        Assert.assertTrue(tax >= 0, "Tax should be non-negative");
        Assert.assertTrue(total > subtotal, "Total should be greater than subtotal");
        Assert.assertTrue(checkoutPageStepTwo.isPriceCalculationCorrect(), "Price calculation should be correct");
    }

    @DataProvider(name = "validCheckoutData")
    public Object[][] getValidCheckoutData() {
        JsonNode rootNode = DataLoader.loadJsonAsNode("data/SauceDemo/checkout-data.json");
        JsonNode validCheckoutInfo = rootNode.get("validCheckoutInfo");
        
        Object[][] data = new Object[validCheckoutInfo.size()][1];
        for (int i = 0; i < validCheckoutInfo.size(); i++) {
            data[i][0] = validCheckoutInfo.get(i);
        }
        return data;
    }

    @DataProvider(name = "invalidCheckoutData")
    public Object[][] getInvalidCheckoutData() {
        JsonNode rootNode = DataLoader.loadJsonAsNode("data/SauceDemo/checkout-data.json");
        JsonNode invalidCheckoutInfo = rootNode.get("invalidCheckoutInfo");
        
        Object[][] data = new Object[invalidCheckoutInfo.size()][1];
        for (int i = 0; i < invalidCheckoutInfo.size(); i++) {
            data[i][0] = invalidCheckoutInfo.get(i);
        }
        return data;
    }
}

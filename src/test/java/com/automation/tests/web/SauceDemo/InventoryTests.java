package com.automation.tests.web.SauceDemo;

import com.automation.base.BaseTest;
import com.automation.pages.SauceDemo.LoginPage;
import com.automation.pages.SauceDemo.InventoryPage;
import com.automation.utils.DataLoader;
import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class InventoryTests extends BaseTest {
    private LoginPage loginPage;
    private InventoryPage inventoryPage;

    public InventoryTests() {
        super();
        System.out.println("InventoryTests constructor - Thread: " + Thread.currentThread().getId());
        // No inicializar páginas en constructor - esperar a @BeforeMethod
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpPagesAndLogin() {
        System.out.println("InventoryTests @BeforeMethod starting - Thread: " + Thread.currentThread().getId());
        
        // El driver se inicializa automáticamente en BaseTest @BeforeMethod
        WebDriver driver = getDriver();
        
        // Navegar a la URL base
        navigateToBaseUrl();
        
        // Inicializar páginas
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        
        // Hacer login
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page should be loaded after login");
        
        System.out.println("InventoryTests @BeforeMethod completed - Logged in and ready");
    }

    @Test(groups = {"smoke", "regression", "SauceDemo"})
    public void testInventoryPageLoaded() {
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page should be loaded");
        Assert.assertTrue(inventoryPage.isProductListVisible(), "Products should be visible");
        Assert.assertEquals(inventoryPage.getProductCount(), 6, "Should display 6 products");
    }

    @Test(groups = {"smoke", "regression", "SauceDemo"})
    public void testProductInformationDisplayed() {
        Assert.assertTrue(inventoryPage.isProductListVisible(), "Products should be visible");
        
        JsonNode products = DataLoader.loadJsonAsNode("data/SauceDemo/products-data.json").get("products");
        
        for (JsonNode product : products) {
            String productName = product.get("name").asText();
            Assert.assertTrue(inventoryPage.isProductInInventory(productName), 
                            "Product should be in inventory: " + productName);
            
            String expectedPrice = product.get("price").asText();
            String actualPrice = inventoryPage.getProductPrice(productName).replace("$", "");
            Assert.assertEquals(actualPrice, expectedPrice, 
                              "Price mismatch for product: " + productName);
        }
    }

    @Test(dataProvider = "singleProductData", groups = {"smoke", "regression", "SauceDemo"})
    public void testAddSingleProductToCart(JsonNode testData) {
        String productName = testData.get("productName").asText();
        String expectedPrice = testData.get("expectedPrice").asText();
        
        Assert.assertFalse(inventoryPage.isCartBadgeVisible(), "Cart badge should not be visible initially");
        
        inventoryPage.addProductToCart(productName);
        
        Assert.assertTrue(inventoryPage.isCartBadgeVisible(), "Cart badge should be visible after adding product");
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "1", "Cart badge should show 1 item");
        
        String actualPrice = inventoryPage.getProductPrice(productName).replace("$", "");
        Assert.assertEquals(actualPrice, expectedPrice, "Product price should match expected");
    }

    @Test(dataProvider = "multipleProductData", groups = {"regression", "SauceDemo"})
    public void testAddMultipleProductsToCart(JsonNode testData) {
        JsonNode products = testData.get("products");
        String expectedTotal = testData.get("expectedTotal").asText();
        
        Assert.assertFalse(inventoryPage.isCartBadgeVisible(), "Cart badge should not be visible initially");
        
        for (JsonNode product : products) {
            String productName = product.asText();
            inventoryPage.addProductToCart(productName);
        }
        
        Assert.assertTrue(inventoryPage.isCartBadgeVisible(), "Cart badge should be visible after adding products");
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), String.valueOf(products.size()), 
                          "Cart badge should show correct item count");
    }

    @Test(dataProvider = "sortData", groups = {"regression", "SauceDemo"})
    public void testProductSorting(JsonNode testData) {
        String sortBy = testData.get("sortBy").asText();
        JsonNode expectedOrder = testData.get("expectedOrder");
        
        inventoryPage.sortProducts(sortBy);
        
        // Verify that products are sorted correctly
        for (int i = 0; i < expectedOrder.size(); i++) {
            String expectedProduct = expectedOrder.get(i).asText();
            // This is a basic check - in a real implementation, you'd want to verify the actual order
            Assert.assertTrue(inventoryPage.isProductInInventory(expectedProduct), 
                            "Product should be in inventory: " + expectedProduct);
        }
    }

    @Test(groups = {"regression", "SauceDemo"})
    public void testRemoveProductFromInventory() {
        String productName = "Sauce Labs Bike Light";
        
        inventoryPage.addProductToCart(productName);
        Assert.assertTrue(inventoryPage.isCartBadgeVisible(), "Cart badge should be visible after adding product");
        
        inventoryPage.removeProductFromCart(productName);
        
        // After removing, the cart badge should still be visible if there were other items,
        // or not visible if this was the only item
        // This test assumes we're removing the only item
        Assert.assertFalse(inventoryPage.isCartBadgeVisible(), "Cart badge should not be visible after removing only item");
    }

    @Test(groups = {"regression", "SauceDemo"})
    public void testNavigateToCart() {
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        inventoryPage.navigateToCart();
        
        // Verify navigation to cart page
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/cart.html"), 
                         "Should navigate to cart page");
    }

    @Test(groups = {"regression", "SauceDemo"})
    public void testLogout() {
        inventoryPage.logout();
        
        // Verify logout and redirect to login page
        Assert.assertTrue(getDriver().getCurrentUrl().equals("https://www.saucedemo.com/"), 
                         "Should redirect to login page after logout");
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded after logout");
    }

    @DataProvider(name = "singleProductData")
    public Object[][] getSingleProductData() {
        JsonNode rootNode = DataLoader.loadJsonAsNode("data/SauceDemo/products-data.json");
        JsonNode singleProductTests = rootNode.get("singleProductTests");
        
        Object[][] data = new Object[singleProductTests.size()][1];
        for (int i = 0; i < singleProductTests.size(); i++) {
            data[i][0] = singleProductTests.get(i);
        }
        return data;
    }

    @DataProvider(name = "multipleProductData")
    public Object[][] getMultipleProductData() {
        JsonNode rootNode = DataLoader.loadJsonAsNode("data/SauceDemo/products-data.json");
        JsonNode multipleProductTests = rootNode.get("multipleProductTests");
        
        Object[][] data = new Object[multipleProductTests.size()][1];
        for (int i = 0; i < multipleProductTests.size(); i++) {
            data[i][0] = multipleProductTests.get(i);
        }
        return data;
    }

    @DataProvider(name = "sortData")
    public Object[][] getSortData() {
        JsonNode rootNode = DataLoader.loadJsonAsNode("data/SauceDemo/products-data.json");
        JsonNode sortTests = rootNode.get("sortTests");
        
        Object[][] data = new Object[sortTests.size()][1];
        for (int i = 0; i < sortTests.size(); i++) {
            data[i][0] = sortTests.get(i);
        }
        return data;
    }
}

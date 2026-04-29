package com.automation.tests.web.SauceDemo;

import com.automation.base.BaseTest;
import com.automation.pages.SauceDemo.LoginPage;
import com.automation.pages.SauceDemo.InventoryPage;
import com.automation.pages.SauceDemo.CartPage;
import com.automation.pages.SauceDemo.CheckoutPageStepOne;
import com.automation.pages.SauceDemo.CheckoutPageStepTwo;
import com.automation.pages.SauceDemo.CheckoutCompletePage;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SauceDemoTestSuite extends BaseTest {
    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutPageStepOne checkoutPageStepOne;
    private CheckoutPageStepTwo checkoutPageStepTwo;
    private CheckoutCompletePage checkoutCompletePage;

    public SauceDemoTestSuite() {
        super();
        System.out.println("SauceDemoTestSuite constructor - Thread: " + Thread.currentThread().getId());
        // No inicializar páginas en constructor - esperar a @BeforeMethod
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpPages() {
        System.out.println("SauceDemoTestSuite @BeforeMethod starting - Thread: " + Thread.currentThread().getId());
        
        // El driver se inicializa automáticamente en BaseTest @BeforeMethod
        WebDriver driver = getDriver();
        
        // Navegar a la URL base
        navigateToBaseUrl();
        
        // Inicializar todas las páginas
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        cartPage = new CartPage(driver);
        checkoutPageStepOne = new CheckoutPageStepOne(driver);
        checkoutPageStepTwo = new CheckoutPageStepTwo(driver);
        checkoutCompletePage = new CheckoutCompletePage(driver);
        
        System.out.println("SauceDemoTestSuite @BeforeMethod completed - All pages initialized");
    }

    /**
     * Test Case 1: Login con credenciales válidas
     * Verificar que los usuarios pueden iniciar sesión con credenciales válidas
     */
    @Test(groups = {"smoke", "critical-path", "SauceDemo"}, description = "Login con credenciales válidas")
    public void testValidLogin() {
        navigateToBaseUrl();
        
        // Steps: Login con credenciales válidas
        loginPage.login("standard_user", "secret_sauce");
        
        // Expected Result: Usuario redirigido a la página de inventario
        Assert.assertTrue(loginPage.isLoginSuccessful(), "Login should redirect to inventory page");
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page should be loaded");
        Assert.assertTrue(inventoryPage.isProductListVisible(), "Products should be visible after login");
    }

    /**
     * Test Case 2: Login con credenciales inválidas
     * Verificar que el sistema rechaza credenciales inválidas
     */
    @Test(groups = {"negative", "critical-path", "SauceDemo"}, description = "Login con credenciales inválidas")
    public void testInvalidLogin() {
        navigateToBaseUrl();
        
        // Steps: Login con credenciales inválidas
        loginPage.login("invalid_user", "wrong_password");
        
        // Expected Result: Mensaje de error displayed
        Assert.assertFalse(loginPage.isLoginSuccessful(), "Login should fail with invalid credentials");
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Username and password do not match"), 
                         "Should display appropriate error message");
    }

    /**
     * Test Case 3: Visualización del catálogo de productos
     * Verificar que los productos se muestran correctamente en el inventario
     */
    @Test(groups = {"smoke", "critical-path", "SauceDemo"}, description = "Visualización del catálogo de productos")
    public void testProductCatalogDisplay() {
        // Step 1: Login con credenciales válidas
        navigateToBaseUrl();
        loginPage.login("standard_user", "secret_sauce");
        
        // Step 2: Verificar que los productos se muestran
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page should be loaded");
        Assert.assertTrue(inventoryPage.isProductListVisible(), "Products should be visible");
        
        // Step 3: Verificar nombre, descripción y precio de los productos
        Assert.assertEquals(inventoryPage.getProductCount(), 6, "Should display 6 products");
        
        // Verify specific products
        Assert.assertTrue(inventoryPage.isProductInInventory("Sauce Labs Backpack"), 
                         "Sauce Labs Backpack should be in inventory");
        Assert.assertTrue(inventoryPage.isProductInInventory("Sauce Labs Bike Light"), 
                         "Sauce Labs Bike Light should be in inventory");
        
        // Verify prices
        Assert.assertEquals(inventoryPage.getProductPrice("Sauce Labs Backpack"), "$29.99", 
                          "Backpack price should be $29.99");
        Assert.assertEquals(inventoryPage.getProductPrice("Sauce Labs Bike Light"), "$9.99", 
                          "Bike Light price should be $9.99");
    }

    /**
     * Test Case 4: Adición de productos al carrito
     * Verificar que los productos pueden ser agregados al carrito
     */
    @Test(groups = {"smoke", "critical-path", "SauceDemo"}, description = "Adición de productos al carrito")
    public void testAddProductsToCart() {
        // Step 1: Login con credenciales válidas
        navigateToBaseUrl();
        loginPage.login("standard_user", "secret_sauce");
        
        // Step 2: Click en "Add to cart" de un producto
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        
        // Step 3: Verificar que el contador del carrito se actualiza
        Assert.assertTrue(inventoryPage.isCartBadgeVisible(), "Cart badge should be visible");
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "1", "Cart should show 1 item");
        
        // Step 4: Navegar al carrito
        inventoryPage.navigateToCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        
        // Step 5: Verificar que el producto está en el carrito
        Assert.assertTrue(cartPage.isProductInCart("Sauce Labs Bike Light"), 
                         "Product should be in cart");
        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart should have 1 item");
        Assert.assertEquals(cartPage.getProductPrice("Sauce Labs Bike Light"), "$9.99", 
                          "Product price should match");
    }

    /**
     * Test Case 5: Proceso de checkout completo
     * Verificar el flujo completo de checkout
     */
    @Test(groups = {"smoke", "critical-path", "SauceDemo"}, description = "Proceso de checkout completo")
    public void testCompleteCheckoutProcess() {
        // Step 1: Login con credenciales válidas
        navigateToBaseUrl();
        loginPage.login("standard_user", "secret_sauce");
        
        // Step 2: Agregar producto al carrito
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        Assert.assertTrue(inventoryPage.isCartBadgeVisible(), "Cart badge should be visible");
        
        // Step 3: Navegar al carrito
        inventoryPage.navigateToCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Cart page should be loaded");
        
        // Step 4: Click en "Checkout"
        cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPageStepOne.isPageLoaded(), "Checkout step one should be loaded");
        
        // Step 5: Completar información de envío
        checkoutPageStepOne.fillCheckoutInformation("John", "Doe", "12345");
        
        // Step 6: Click en "Continue"
        checkoutPageStepOne.continueCheckout();
        Assert.assertTrue(checkoutPageStepTwo.isPageLoaded(), "Checkout step two should be loaded");
        
        // Step 7: Verificar resumen del pedido
        Assert.assertTrue(checkoutPageStepTwo.isProductInSummary("Sauce Labs Backpack"), 
                         "Product should be in summary");
        Assert.assertTrue(checkoutPageStepTwo.isPriceCalculationCorrect(), 
                         "Price calculation should be correct");
        
        // Step 8: Click en "Finish"
        checkoutPageStepTwo.finishCheckout();
        
        // Expected Result: Pedido completado exitosamente
        Assert.assertTrue(checkoutCompletePage.isPageLoaded(), "Checkout complete page should be loaded");
        Assert.assertTrue(checkoutCompletePage.isThankYouMessageDisplayed(), 
                         "Thank you message should be displayed");
        Assert.assertTrue(checkoutCompletePage.isPonyExpressImageDisplayed(), 
                         "Pony Express image should be displayed");
    }

    /**
     * Test Case 6: Logout del sistema
     * Verificar que los usuarios pueden cerrar sesión
     */
    @Test(groups = {"smoke", "critical-path", "SauceDemo"}, description = "Logout del sistema")
    public void testSystemLogout() {
        // Step 1: Login con credenciales válidas
        navigateToBaseUrl();
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Inventory page should be loaded");
        
        // Step 2: Cerrar sesión (abre menú internamente)
        inventoryPage.logout();
        
        // Expected Result: Usuario redirigido a la página de login
        Assert.assertTrue(getDriver().getCurrentUrl().equals("https://www.saucedemo.com/"), 
                         "Should redirect to login page");
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page should be loaded after logout");
    }

    /**
     * Additional test: Complete user journey from login to logout
     * This test covers the entire critical path in one flow
     */
    @Test(groups = {"end-to-end", "critical-path", "SauceDemo"}, description = "Complete user journey")
    public void testCompleteUserJourney() {
        // Login
        navigateToBaseUrl();
        loginPage.login("standard_user", "secret_sauce");
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Should be on inventory page");
        
        // Browse products
        Assert.assertTrue(inventoryPage.isProductListVisible(), "Products should be visible");
        Assert.assertEquals(inventoryPage.getProductCount(), 6, "Should have 6 products");
        
        // Add multiple products
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "2", "Cart should show 2 items");
        
        // View cart
        inventoryPage.navigateToCart();
        Assert.assertTrue(cartPage.isPageLoaded(), "Should be on cart page");
        Assert.assertEquals(cartPage.getCartItemCount(), 2, "Cart should have 2 items");
        
        // Checkout
        cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPageStepOne.isPageLoaded(), "Should be on checkout step one");
        
        checkoutPageStepOne.fillCheckoutInformation("Test", "User", "54321");
        checkoutPageStepOne.continueCheckout();
        
        Assert.assertTrue(checkoutPageStepTwo.isPageLoaded(), "Should be on checkout step two");
        Assert.assertTrue(checkoutPageStepTwo.isPriceCalculationCorrect(), "Prices should be correct");
        
        checkoutPageStepTwo.finishCheckout();
        Assert.assertTrue(checkoutCompletePage.isPageLoaded(), "Should be on checkout complete page");
        
        // Back to products
        checkoutCompletePage.backToProducts();
        Assert.assertTrue(inventoryPage.isPageLoaded(), "Should be back on inventory page");
        
        // Logout
        inventoryPage.logout();
        Assert.assertTrue(loginPage.isPageLoaded(), "Should be logged out and on login page");
    }
}

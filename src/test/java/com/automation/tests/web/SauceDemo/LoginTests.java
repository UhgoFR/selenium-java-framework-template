package com.automation.tests.web.SauceDemo;

import com.automation.base.BaseTest;
import com.automation.pages.PageInitializer;
import com.automation.utils.DataLoader;
import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginTests extends BaseTest {
    protected PageInitializer.SauceDemoPages pages;

    public LoginTests() {
        super();
        System.out.println("LoginTests constructor - Thread: " + Thread.currentThread().getId());
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpPages(java.lang.reflect.Method method) {
        System.out.println("LoginTests @BeforeMethod - Thread: " + Thread.currentThread().getId() + " - Method: " + method.getName());
        WebDriver driver = getDriver();
        if (driver == null) {
            System.err.println("ERROR: Driver is null in LoginTests @BeforeMethod!");
            throw new IllegalStateException("WebDriver is null - BaseTest.setUp() may not have executed");
        }
        
        // Navigate to base URL
        navigateToBaseUrl();
        System.out.println("Current URL after navigation: " + driver.getCurrentUrl());
        
        pages = PageInitializer.initSauceDemoPages(driver);
        System.out.println("Pages initialized successfully for " + method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void resetPages() {
        System.out.println("Resetting pages after test");
        pages = null;
    }

    @Test(dataProvider = "validLoginData", groups = {"smoke", "regression", "SauceDemo"})
    public void testValidLogin(JsonNode loginData) {
        System.out.println("testValidLogin starting - Thread: " + Thread.currentThread().getId());
        
        Assert.assertNotNull(pages.loginPage, "LoginPage should be initialized");
        Assert.assertNotNull(pages.inventoryPage, "InventoryPage should be initialized");
        
        Assert.assertTrue(pages.loginPage.isPageLoaded(), "Login page should be loaded");
        
        pages.loginPage.login(loginData.get("username").asText(), loginData.get("password").asText());
        
        Assert.assertTrue(pages.loginPage.isLoginSuccessful(), "Login should redirect to inventory page");
        Assert.assertTrue(pages.inventoryPage.isPageLoaded(), "Inventory page should be loaded");
        Assert.assertTrue(pages.inventoryPage.isProductListVisible(), "Products should be visible after login");
    }

    @Test(dataProvider = "invalidLoginData", groups = {"negative", "regression", "SauceDemo"})
    public void testInvalidLogin(JsonNode loginData) {
        Assert.assertTrue(pages.loginPage.isPageLoaded(), "Login page should be loaded");
        
        pages.loginPage.login(loginData.get("username").asText(), loginData.get("password").asText());
        
        Assert.assertFalse(pages.loginPage.isLoginSuccessful(), "Login should fail with invalid credentials");
        Assert.assertTrue(pages.loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        
        String actualError = pages.loginPage.getErrorMessage();
        String expectedError = loginData.get("expectedError").asText();
        Assert.assertTrue(actualError.contains(expectedError), 
                         "Error message should contain: " + expectedError + ", but got: " + actualError);
    }

    @Test(dataProvider = "lockedUserData", groups = {"negative", "regression", "SauceDemo"})
    public void testLockedUserLogin(JsonNode loginData) {
        Assert.assertTrue(pages.loginPage.isPageLoaded(), "Login page should be loaded");
        
        pages.loginPage.login(loginData.get("username").asText(), loginData.get("password").asText());
        
        Assert.assertFalse(pages.loginPage.isLoginSuccessful(), "Login should fail for locked user");
        Assert.assertTrue(pages.loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
        
        String actualError = pages.loginPage.getErrorMessage();
        String expectedError = loginData.get("expectedError").asText();
        Assert.assertTrue(actualError.contains(expectedError), 
                         "Error message should contain: " + expectedError + ", but got: " + actualError);
    }

    @Test(groups = {"smoke", "regression"})
    public void testLoginPageElements() {
        Assert.assertTrue(pages.loginPage.isPageLoaded(), "Login page should be loaded");
        Assert.assertTrue(pages.loginPage.isPageLoaded(), "Login page should contain all required elements");
    }

    @Test(groups = {"negative", "regression"})
    public void testEmptyCredentials() {
        Assert.assertTrue(pages.loginPage.isPageLoaded(), "Login page should be loaded");
        
        pages.loginPage.login("", "");
        
        Assert.assertFalse(pages.loginPage.isLoginSuccessful(), "Login should fail with empty credentials");
        Assert.assertTrue(pages.loginPage.isErrorMessageDisplayed(), "Error message should be displayed");
    }

    @DataProvider(name = "validLoginData")
    public Object[][] getValidLoginData() {
        JsonNode rootNode = DataLoader.loadJsonAsNode("data/SauceDemo/login-data.json");
        JsonNode validCredentials = rootNode.get("validCredentials");
        
        Object[][] data = new Object[validCredentials.size()][1];
        for (int i = 0; i < validCredentials.size(); i++) {
            data[i][0] = validCredentials.get(i);
        }
        return data;
    }

    @DataProvider(name = "invalidLoginData")
    public Object[][] getInvalidLoginData() {
        JsonNode rootNode = DataLoader.loadJsonAsNode("data/SauceDemo/login-data.json");
        JsonNode invalidCredentials = rootNode.get("invalidCredentials");
        
        Object[][] data = new Object[invalidCredentials.size()][1];
        for (int i = 0; i < invalidCredentials.size(); i++) {
            data[i][0] = invalidCredentials.get(i);
        }
        return data;
    }

    @DataProvider(name = "lockedUserData")
    public Object[][] getLockedUserData() {
        JsonNode rootNode = DataLoader.loadJsonAsNode("data/SauceDemo/login-data.json");
        JsonNode lockedUser = rootNode.get("lockedUser");
        
        Object[][] data = new Object[lockedUser.size()][1];
        for (int i = 0; i < lockedUser.size(); i++) {
            data[i][0] = lockedUser.get(i);
        }
        return data;
    }
}

package com.automation.tests;

import com.automation.base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class WebTests extends BaseTest {

    @Test(description = "Ejemplo de prueba web básica", groups = "WebTest")
    public void basicWebTest() {
        navigateToBaseUrl();
        
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        
        String expectedTitle = "Example Domain";
        String actualTitle = getDriver().getTitle();
        
        Assert.assertEquals(actualTitle, expectedTitle, "El título de la página no coincide");
        
        System.out.println("Prueba web completada exitosamente");
    }

    @Test(description = "Ejemplo de búsqueda en Google", groups = "WebTest")
    public void googleSearchTest() {
        getDriver().get("https://www.google.com");
        
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
        
        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("q")));
        searchBox.sendKeys("Selenium WebDriver");
        searchBox.submit();
        
        wait.until(ExpectedConditions.titleContains("Selenium WebDriver"));
        
        String pageTitle = getDriver().getTitle();
        Assert.assertTrue(pageTitle.contains("Selenium WebDriver"), 
                        "La búsqueda no se completó correctamente");
        
        System.out.println("Búsqueda en Google completada exitosamente");
    }
}

package com.automation.pages;

import com.automation.pages.SauceDemo.LoginPage;
import com.automation.pages.SauceDemo.InventoryPage;
import com.automation.pages.SauceDemo.CartPage;
import com.automation.pages.SauceDemo.CheckoutCompletePage;
import org.openqa.selenium.WebDriver;

/**
 * Clase responsable de inicializar todos los Page Objects del framework.
 * 
 * <p>Esta clase centraliza la creación y configuración de instancias de páginas,
 * proporcionando un punto único de gestión para todos los Page Objects.</p>
 */
public class PageInitializer {
    
    /**
     * Inicializa las páginas específicas para las pruebas de SauceDemo.
     * 
     * @param driver Instancia de WebDriver para inicializar las páginas
     * @return SauceDemoPages Objeto que contiene todas las páginas de SauceDemo
     */
    public static SauceDemoPages initSauceDemoPages(WebDriver driver) {
        return new SauceDemoPages(driver);
    }
    
    /**
     * Contenedor para todas las páginas de SauceDemo.
     */
    public static class SauceDemoPages {
        public final LoginPage loginPage;
        public final InventoryPage inventoryPage;
        public final CartPage cartPage;
        public final CheckoutCompletePage checkoutCompletePage;
        
        public SauceDemoPages(WebDriver driver) {
            this.loginPage = new LoginPage(driver);
            this.inventoryPage = new InventoryPage(driver);
            this.cartPage = new CartPage(driver);
            this.checkoutCompletePage = new CheckoutCompletePage(driver);
        }
    }
}

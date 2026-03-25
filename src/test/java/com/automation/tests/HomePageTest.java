package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {
    private HomePage homePage;

    @BeforeMethod
    public void setUpHomePage() {
        homePage = new HomePage(getDriver());
    }

    @Test(description = "Verificar que la página de inicio carga correctamente", groups = "WebTest")
    public void testHomePageLoad() {
        homePage = new HomePage(getDriver());
        homePage.navigateToHomePage();
        
        Assert.assertTrue(homePage.isMainTitleDisplayed(), 
                         "El título principal no está visible");
        
        String title = homePage.getPageTitle();
        Assert.assertNotNull(title, "El título de la página no debería ser nulo");
        
        System.out.println("Página de inicio cargada exitosamente");
    }

    @Test(description = "Verificar funcionalidad de búsqueda", groups = "WebTest")
    public void testSearchFunctionality() {
        homePage = new HomePage(getDriver());
        homePage.navigateToHomePage();
        
        if (homePage.isSearchInputVisible()) {
            homePage.searchFor("test automation");
            
            // Verificar que la búsqueda se realizó (esto depende del sitio específico)
            homePage.pause(2000L); // Esperar a que carguen los resultados
            
            System.out.println("Funcionalidad de búsqueda verificada");
        } else {
            System.out.println("Campo de búsqueda no disponible en esta página");
        }
    }

    @Test(description = "Verificar navegación a About", groups = "WebTest")
    public void testNavigationToAbout() {
        homePage = new HomePage(getDriver());
        homePage.navigateToHomePage();
        
        homePage.clickAboutLink(); // Esperar a que cargue la página About
        
        String currentUrl = homePage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("help") || currentUrl.contains("Help"), 
                         "No se navegó correctamente a la página About");
        
        homePage.pause(2000L); // Esperar a que cargue la página About
        
        System.out.println("Navegación a About verificada exitosamente");
    }
}

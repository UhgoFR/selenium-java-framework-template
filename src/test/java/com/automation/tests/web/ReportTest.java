package com.automation.tests.web;

import com.automation.base.BaseTest;
import com.automation.listeners.ExtentReportListener;
import com.automation.pages.HomePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ReportTest extends BaseTest {
    private static final Logger logger = LogManager.getLogger(ReportTest.class);

    @Test(description = "Prueba exitosa para demostrar reportes")
    public void testSuccessfulExecution() {
        logger.info("Iniciando prueba exitosa");
        ExtentReportListener.logInfo("Iniciando prueba exitosa");
        
        HomePage homePage = new HomePage(getDriver());
        homePage.navigateToHomePage();
        
        // Simular alguna verificación
        String title = homePage.getPageTitle();
        Assert.assertNotNull(title, "El título no debería ser nulo");
        
        ExtentReportListener.logPass("Verificación de título completada exitosamente");
        logger.info("Prueba exitosa completada");
        ExtentReportListener.logInfo("Prueba exitosa completada");
    }

    @Test(description = "Prueba fallida para demostrar capturas de pantalla")
    public void testFailedExecution() {
        logger.info("Iniciando prueba que fallará intencionalmente");
        ExtentReportListener.logInfo("Iniciando prueba que fallará intencionalmente");
        
        HomePage homePage = new HomePage(getDriver());
        homePage.navigateToHomePage();
        
        ExtentReportListener.logInfo("Navegación completada, ejecutando assertion fallida");
        
        // Esta assertion fallará intencionalmente para demostrar el reporte
        Assert.fail("Esta prueba falla intencionalmente para demostrar la generación de reportes con screenshots");
    }

    @Test(description = "Prueba omitida para demostrar diferentes estados")
    public void testSkippedExecution() {
        logger.info("Esta prueba será omitida");
        ExtentReportListener.logInfo("Esta prueba será omitida");
        
        // Simular que se omite la prueba
        throw new org.testng.SkipException("Prueba omitida para demostración");
    }

    @Test(description = "Prueba con logs detallados")
    public void testDetailedLogging() {
        logger.debug("Iniciando prueba con logs detallados");
        ExtentReportListener.logInfo("Iniciando prueba con logs detallados");
        
        ExtentReportListener.logInfo("Paso 1: Navegando a la página");
        HomePage homePage = new HomePage(getDriver());
        homePage.navigateToHomePage();
        
        ExtentReportListener.logInfo("Paso 2: Verificando título de página");
        String title = homePage.getPageTitle();
        ExtentReportListener.logInfo("Título obtenido: " + title);
        
        ExtentReportListener.logInfo("Paso 3: Validando que el título no sea nulo");
        Assert.assertNotNull(title, "El título no debería ser nulo");
        
        ExtentReportListener.logPass("Prueba con logs detallados completada exitosamente");
        logger.info("Prueba con logs detallados completada");
        
        logger.info("Paso 2: Verificando elementos");
        boolean isTitleDisplayed = homePage.isMainTitleDisplayed();
        logger.info("Título principal visible: " + isTitleDisplayed);
        
        logger.info("Paso 3: Realizando acciones");
        if (homePage.isSearchInputVisible()) {
            logger.debug("Campo de búsqueda encontrado, realizando búsqueda");
            homePage.searchFor("automation test");
        } else {
            logger.warn("Campo de búsqueda no encontrado");
        }
        
        logger.info("Prueba con logs detallados completada exitosamente");
    }
}

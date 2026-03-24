package com.automation.base;

import com.automation.config.ConfigManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestContext;
import org.testng.annotations.*;

public class BaseTest {
    protected WebDriver driver;

    /**
     * Configura el entorno de drivers de Selenium WebDriver antes de ejecutar la suite de pruebas.
     * 
     * <p>Este método se ejecuta una sola vez al inicio de todas las pruebas y se encarga de:
     * <ul>
     *   <li>Descargar automáticamente los drivers de Chrome y Firefox compatibles</li>
     *   <li>Configurar las variables de entorno necesarias</li>
     *   <li>Preparar el sistema para múltiples navegadores</li>
     * </ul>
     * 
     * @param context Contexto de TestNG que permite compartir información entre pruebas
     */
    @BeforeSuite
    public void setUpSuite(ITestContext context) {
        WebDriverManager.chromedriver().setup();
        WebDriverManager.firefoxdriver().setup();
    }

    /**
     * Inicializa una nueva instancia del navegador para cada método de prueba.
     * 
     * <p>Este método se ejecuta antes de cada prueba y configura el navegador según:
     * <ul>
     *   <li>Parámetros proporcionados desde TestNG (máxima prioridad)</li>
     *   <li>Configuración del archivo config.properties (prioridad media)</li>
     *   <li>Valores por defecto del código (prioridad baja)</li>
     * </ul>
     * 
     * <p>Soporta los siguientes navegadores:
     * <ul>
     *   <li>Chrome - con opciones para headless y estabilidad</li>
     *   <li>Firefox - con opciones para headless y rendimiento</li>
     * </ul>
     * 
     * @param browser Navegador a utilizar ("chrome" o "firefox"). 
     *                Si es vacío, usa la configuración de ConfigManager.
     * @param headless Indica si ejecutar el navegador en modo sin interfaz gráfica.
     *                Se combina con la configuración de ConfigManager.
     * @throws IllegalArgumentException Si el navegador especificado no está soportado
     */
    @BeforeMethod
    @Parameters({"browser", "headless"})
    public void setUp(@Optional("chrome") String browser, @Optional("false") boolean headless) {
        String browserConfig = ConfigManager.getBrowser();
        String selectedBrowser = browser.isEmpty() ? browserConfig : browser;
        boolean isHeadless = headless || ConfigManager.isHeadless();

        switch (selectedBrowser.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                if (isHeadless) {
                    chromeOptions.addArguments("--headless");
                }
                chromeOptions.addArguments("--no-sandbox", "--disable-dev-shm-usage");
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (isHeadless) {
                    firefoxOptions.addArguments("--headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;
            default:
                throw new IllegalArgumentException("Browser no soportado: " + selectedBrowser);
        }

        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
    }

    /**
     * Limpia los recursos después de cada método de prueba.
     * 
     * <p>Este método se ejecuta después de cada prueba y se encarga de:
     * <ul>
     *   <li>Cerrar completamente el navegador y todos sus procesos</li>
     *   <li>Liberar memoria del sistema</li>
     *   <li>Prevenir contaminación entre pruebas</li>
     *   <li>Asegurar un estado limpio para la siguiente prueba</li>
     * </ul>
     * 
     * <p>Realiza una verificación de nulidad antes de cerrar para evitar
     * excepciones en caso de que el driver no se haya inicializado correctamente.
     */
    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Proporciona acceso a la instancia actual del WebDriver.
     * 
     * <p>Este método es fundamental para:
     * <ul>
     *   <li>Inicializar Page Objects con la instancia del navegador</li>
     *   <li>Permitir que los listeners accedan al driver para screenshots</li>
     *   <li>Proporcionar acceso directo al WebDriver cuando sea necesario</li>
     * </ul>
     * 
     * <p>La visibilidad es pública para que pueda ser accedido desde:
     * <ul>
     *   <li>Clases de prueba que heredan de BaseTest</li>
     *   <li>TestListener para captura de screenshots</li>
     *   <li>Page Objects durante su inicialización</li>
     * </ul>
     * 
     * @return Instancia actual de WebDriver configurada para la prueba actual
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Navega a una URL específica utilizando el WebDriver actual.
     * 
     * <p>Este método proporciona una forma conveniente de navegar a cualquier
     * dirección web sin necesidad de acceder directamente al WebDriver.
     * 
     * <p>Características:
     * <ul>
     *   <li>Espera a que la página comience a cargar</li>
     *   <li>Utiliza el método get() de Selenium WebDriver</li>
     *   <li>Es un wrapper que facilita la navegación en las pruebas</li>
     * </ul>
     * 
     * @param url URL completa a la que se desea navegar. 
     *            Debe incluir el protocolo (http:// o https://)
     */
    protected void navigateTo(String url) {
        driver.get(url);
    }

    /**
     * Navega a la URL base configurada en el sistema.
     * 
     * <p>Este método es un wrapper conveniente que:
     * <ul>
     *   <li>Obtiene la URL base desde ConfigManager</li>
     *   <li>Utiliza el método navigateTo() internamente</li>
     *   <li>Facilita la navegación consistente en todas las pruebas</li>
     * </ul>
     * 
     * <p>La URL base se configura en el archivo config.properties:
     * <pre>
     * base.url=https://example.com
     * </pre>
     * 
     * <p>Uso típico al inicio de cada prueba:
     * <pre>
     * navigateToBaseUrl(); // Navega a la página principal
     * </pre>
     * 
     * @see ConfigManager#getBaseUrl()
     * @see #navigateTo(String)
     */
    protected void navigateToBaseUrl() {
        navigateTo(ConfigManager.getBaseUrl());
    }
}

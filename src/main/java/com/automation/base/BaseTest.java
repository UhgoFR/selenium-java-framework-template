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

import java.time.Duration;

public class BaseTest {
    // Thread-safe para ejecución en paralelo
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<Boolean> isInitializing = new ThreadLocal<>();

    public BaseTest() {
        System.out.println("BaseTest constructor called for thread: " + Thread.currentThread().getId());
        addShutdownHook();
    }

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

    @BeforeClass
    public void setUpClass() {
        System.out.println("BaseTest @BeforeClass called for thread: " + Thread.currentThread().getId());
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
        System.out.println("BaseTest @BeforeMethod called - creating new driver instance for thread: " + Thread.currentThread().getId());
        String browserConfig = ConfigManager.getBrowser();
        String selectedBrowser = browser.isEmpty() ? browserConfig : browser;
        boolean isHeadless = headless || ConfigManager.isHeadless();
        
        initializeDriver(selectedBrowser, isHeadless);
        System.out.println("BaseTest: Driver initialized for thread: " + Thread.currentThread().getId());
        // Navigation removed - each test class will navigate when needed
    }
    
    /**
     * Waits for the page to load completely after navigation.
     * Uses document.readyState to ensure DOM is ready.
     */
    private void waitForPageToLoad() {
        WebDriver currentDriver = driver.get();
        if (currentDriver != null) {
            try {
                org.openqa.selenium.support.ui.WebDriverWait wait = 
                    new org.openqa.selenium.support.ui.WebDriverWait(currentDriver, Duration.ofSeconds(30));
                wait.until(webDriver -> 
                    ((org.openqa.selenium.JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
            } catch (Exception e) {
                System.err.println("Error waiting for page load: " + e.getMessage());
            }
        }
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
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        System.out.println("BaseTest @AfterMethod called for thread: " + Thread.currentThread().getId());
        // Cerrar driver solo si no estamos en modo global compartido
        if (driver.get() != null) {
            try {
                driver.get().quit();
                System.out.println("Driver closed in @AfterMethod for thread: " + Thread.currentThread().getId());
            } catch (Exception e) {
                System.err.println("Error closing driver in @AfterMethod: " + e.getMessage());
            } finally {
                driver.remove();
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        System.out.println("BaseTest @AfterClass called for thread: " + Thread.currentThread().getId());
        closeDriver("@AfterClass");
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        killBrowserProcesses();
    }

    /**
     * Inicializa el WebDriver con el navegador y opciones especificadas.
     * 
     * @param browser Navegador a utilizar ("chrome" o "firefox")
     * @param headless Indica si ejecutar en modo sin interfaz gráfica
     */
    protected void initializeDriver(String browser, boolean headless) {
        if (isInitializing.get() != null && isInitializing.get()) {
            throw new IllegalStateException("Driver initialization already in progress - circular dependency detected");
        }
        
        isInitializing.set(true);
        try {
            WebDriver webDriver = createWebDriver(browser, headless);
            configureDriver(webDriver);
            driver.set(webDriver);
            
            String caller = Thread.currentThread().getStackTrace()[3].getMethodName();
            System.out.println("Driver initialized in " + caller + " for thread " + 
                             Thread.currentThread().getId() + ": " + webDriver);
        } finally {
            isInitializing.set(false);
        }
    }

    /**
     * Crea una instancia de WebDriver según el navegador y opciones especificadas.
     * 
     * @param browser Navegador a utilizar
     * @param headless Indica si ejecutar en modo headless
     * @return Instancia de WebDriver configurada
     */
    private WebDriver createWebDriver(String browser, boolean headless) {
        switch (browser.toLowerCase()) {
            case "chrome":
                return createChromeDriver(headless);
            case "firefox":
                return createFirefoxDriver(headless);
            default:
                throw new IllegalArgumentException("Browser no soportado: " + browser);
        }
    }

    /**
     * Crea una instancia de Chrome Driver con las opciones especificadas.
     * 
     * @param headless Indica si ejecutar en modo headless
     * @return ChromeDriver configurado
     */
    private WebDriver createChromeDriver(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        // Disable Chrome native notifications and infobars to prevent password prompts
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-password-manager-reauthentication");
        
        // Configurar preferencias para desactivar el guardado de contraseñas
        java.util.Map<String, Object> prefs = new java.util.HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("password_manager.leak_detection", false); // Desactiva la detección de fugas
        
        options.setExperimentalOption("prefs", prefs);
        
        // Desactivar específicamente la comprobación de Safe Browsing para contraseñas
        options.addArguments("--disable-features=PasswordLeakDetection,SafeBrowsingPasswordCheck");
        options.addArguments("--incognito");
        
        return new ChromeDriver(options);
    }

    /**
     * Crea una instancia de Firefox Driver con las opciones especificadas.
     * 
     * @param headless Indica si ejecutar en modo headless
     * @return FirefoxDriver configurado
     */
    private WebDriver createFirefoxDriver(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("--headless");
        }
        return new FirefoxDriver(options);
    }

    /**
     * Configura las opciones básicas del WebDriver (maximizar ventana, limpiar cookies, timeouts).
     * 
     * @param webDriver WebDriver a configurar
     */
    private void configureDriver(WebDriver webDriver) {
        webDriver.manage().window().maximize();
        webDriver.manage().deleteAllCookies();
        
        // Configurar timeouts para mejor manejo de esperas
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(20));
    }

    /**
     * Cierra el WebDriver actual y lo elimina del ThreadLocal.
     * 
     * @param context Contexto desde donde se llama el método (para logging)
     */
    private void closeDriver(String context) {
        if (driver.get() != null) {
            try {
                driver.get().quit();
                System.out.println("Driver closed and removed from thread: " + 
                                 Thread.currentThread().getId() + " (" + context + ")");
            } catch (Exception e) {
                System.err.println("Error closing driver in " + context + ": " + e.getMessage());
            } finally {
                driver.remove();
            }
        }
    }

    /**
     * Agrega un Shutdown Hook para garantizar cierre de browsers si la JVM termina abruptamente.
     */
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown Hook: Closing all WebDriver instances...");
            closeDriver("Shutdown Hook");
            killBrowserProcesses();
        }));
    }

    /**
     * Mata procesos de browsers y drivers residuales según el sistema operativo.
     */
    private void killBrowserProcesses() {
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            
            if (osName.contains("mac")) {
                killMacProcesses();
            } else if (osName.contains("windows")) {
                killWindowsProcesses();
            } else {
                killLinuxProcesses();
            }
        } catch (Exception e) {
            System.err.println("Error killing browser processes: " + e.getMessage());
        }
    }

    /**
     * Mata procesos de Chrome y ChromeDriver en macOS.
     */
    private void killMacProcesses() throws Exception {
        Runtime.getRuntime().exec("pkill -f chromedriver");
        Thread.sleep(200);
        Runtime.getRuntime().exec("pkill -f 'Google Chrome'");
        Thread.sleep(200);
        Runtime.getRuntime().exec("pkill -f 'Chrome'");
        System.out.println("Chrome and ChromeDriver processes killed (macOS)");
    }

    /**
     * Mata procesos de Chrome y ChromeDriver en Windows.
     */
    private void killWindowsProcesses() throws Exception {
        Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
        Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
        System.out.println("Chrome and ChromeDriver processes killed (Windows)");
    }

    /**
     * Mata procesos de Chrome y ChromeDriver en Linux.
     */
    private void killLinuxProcesses() throws Exception {
        Runtime.getRuntime().exec("pkill -f chromedriver");
        Runtime.getRuntime().exec("pkill -f chrome");
        System.out.println("Chrome and ChromeDriver processes killed (Linux)");
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
        System.out.println("getDriver() called for thread " + Thread.currentThread().getId() + ", driver: " + driver.get());
        
        if (driver.get() == null) {
            if (isInitializing.get() != null && isInitializing.get()) {
                throw new IllegalStateException("Driver initialization in progress - circular dependency detected");
            }
            
            System.out.println("Driver is null in thread " + Thread.currentThread().getId() + 
                             ", initializing as fallback...");
            initializeDriver(ConfigManager.getBrowser(), ConfigManager.isHeadless());
            System.out.println("Driver initialized in getDriver() for thread " + Thread.currentThread().getId() + 
                             ": " + driver.get());
        }
        
        return driver.get();
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
        getDriver().get(url);
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

package com.automation.listeners;

import com.automation.base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * VERSIÓN CORREGIDA - THREAD-SAFE PARA EJECUCIÓN PARALELA
 * 
 * Problemas en versión original:
 * - extent es static sin sincronización
 * - onStart() se ejecuta múltiples veces en paralelo (uno por cada <test> en testng.xml)
 * - setupExtentReports() crea múltiples instancias sin control de acceso
 * - En testng.xml con parallel="tests" + thread-count="4", hay 4 threads simultáneos
 * - Resultado: Reportes corruptos, datos perdidos, excepciones de concurrencia
 * 
 * Solución:
 * - Usar synchronized block en onStart() para garantizar una sola inicialización
 * - Flag setupComplete evita reinicializaciones innecesarias
 * - Double-checked locking para mejor rendimiento
 * - Bloqueo solo durante construcción inicial del reporte
 */
public class ExtentReportListener implements ITestListener, ISuiteListener {
    private static final Logger logger = LogManager.getLogger(ExtentReportListener.class);
    
    // ✅ Instancia estática del reporte (compartida entre threads)
    private static ExtentReports extent;
    
    // ✅ ThreadLocal para cada test en su thread (thread-safe)
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    
    // ✅ Flag volatile para indicar si el setup ya se ejecutó
    private static volatile boolean setupComplete = false;
    
    // ✅ Lock para sincronización de inicialización
    private static final Object LOCK = new Object();
    
    private static final String REPORT_PATH = "target/extent-reports/";

    /**
     * Se ejecuta al iniciar la suite de pruebas.
     * 
     * EN EJECUCIÓN PARALELA:
     * - Con parallel="tests", se ejecuta UNA VEZ por cada <test> en testng.xml
     * - Con thread-count="4" y 4 <test> tags, onStart() se llama 4 veces simultáneamente
     * - Necesitamos sincronización para evitar crear ExtentReports múltiples veces
     */
    @Override
    public void onStart(ISuite suite) {
        String threadId = String.valueOf(Thread.currentThread().getId());
        logger.info(String.format("[Thread %s] onStart() invoked para suite: %s", threadId, suite.getName()));
        
        // ✅ Sincronización: solo un thread ejecuta setupExtentReports()
        if (!setupComplete) {
            synchronized (LOCK) {
                // ✅ Double-checked locking para mejor rendimiento
                // Otra situación: otro thread ya completó setup mientras esperaba el lock
                if (!setupComplete) {
                    logger.info(String.format(
                        "[Thread %s] INICIANDO setupExtentReports() - primer thread en acceder",
                        threadId
                    ));
                    setupExtentReports();
                    setupComplete = true;
                    logger.info("[Thread " + threadId + "] setupExtentReports() COMPLETADO");
                } else {
                    logger.info(String.format(
                        "[Thread %s] setupExtentReports() YA completado por otro thread",
                        threadId
                    ));
                }
            }
        } else {
            logger.info(String.format(
                "[Thread %s] setupExtentReports() YA completado - saltando inicialización",
                threadId
            ));
        }
    }

    /**
     * Se ejecuta al finalizar la suite de pruebas.
     * 
     * IMPORTANTE: El flush debe ser thread-safe y solo ejecutarse UNA VEZ.
     */
    @Override
    public void onFinish(ISuite suite) {
        String threadId = String.valueOf(Thread.currentThread().getId());
        logger.info(String.format("[Thread %s] onFinish() invoked", threadId));
        
        // ✅ Sincronización: solo un thread ejecuta flush()
        synchronized (LOCK) {
            if (extent != null) {
                try {
                    logger.info(String.format("[Thread %s] Flushing ExtentReports...", threadId));
                    extent.flush();
                    logger.info(String.format("[Thread %s] ExtentReports flushed successfully", threadId));
                } catch (Exception e) {
                    logger.error(String.format(
                        "[Thread %s] Error flushing ExtentReports: %s",
                        threadId, e.getMessage()
                    ), e);
                }
            }
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String threadId = String.valueOf(Thread.currentThread().getId());
        String testName = result.getMethod().getMethodName();
        
        logger.info(String.format("[Thread %s] onTestStart(): %s", threadId, testName));
        
        // ✅ Acceso sincronizado a extent para crear test
        synchronized (LOCK) {
            if (extent != null) {
                try {
                    ExtentTest extentTest = extent.createTest(
                        testName,
                        result.getMethod().getDescription()
                    );
                    test.set(extentTest);
                    logger.debug(String.format(
                        "[Thread %s] ExtentTest creado para: %s",
                        threadId, testName
                    ));
                } catch (Exception e) {
                    logger.error(String.format(
                        "[Thread %s] Error creando ExtentTest para %s: %s",
                        threadId, testName, e.getMessage()
                    ), e);
                }
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String threadId = String.valueOf(Thread.currentThread().getId());
        String testName = result.getMethod().getMethodName();
        
        logger.info(String.format("[Thread %s] onTestSuccess(): %s", threadId, testName));
        
        if (test.get() != null) {
            long duration = result.getEndMillis() - result.getStartMillis();
            
            // ✅ ThreadLocal access es thread-safe (sin sincronización necesaria)
            test.get().log(Status.PASS, "Prueba ejecutada exitosamente");
            test.get().log(Status.INFO, "Tiempo de ejecución: " + duration + " ms");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String threadId = String.valueOf(Thread.currentThread().getId());
        String testName = result.getMethod().getMethodName();
        
        logger.error(String.format("[Thread %s] onTestFailure(): %s", threadId, testName));
        
        if (test.get() != null) {
            test.get().log(Status.FAIL, "Prueba fallida: " + result.getThrowable().getMessage());
            
            // Capturar screenshot en caso de fallo
            try {
                WebDriver driver = getDriverFromResult(result);
                if (driver != null) {
                    String screenshotPath = captureScreenshot(driver, testName);
                    if (screenshotPath != null) {
                        test.get().log(Status.FAIL, "Screenshot capturado",
                                MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                        test.get().log(Status.INFO, "Screenshot guardado en: " + screenshotPath);
                    }
                }
            } catch (Exception e) {
                logger.error(String.format(
                    "[Thread %s] Error al capturar screenshot: %s",
                    threadId, e.getMessage()
                ), e);
                
                test.get().log(Status.WARNING, "No se pudo capturar screenshot: " + e.getMessage());
            }
            
            // Agregar stack trace
            test.get().log(Status.FAIL, result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String threadId = String.valueOf(Thread.currentThread().getId());
        String testName = result.getMethod().getMethodName();
        
        logger.info(String.format("[Thread %s] onTestSkipped(): %s", threadId, testName));
        
        if (test.get() != null) {
            test.get().log(Status.SKIP, "Prueba omitida: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String threadId = String.valueOf(Thread.currentThread().getId());
        String testName = result.getMethod().getMethodName();
        
        logger.warn(String.format("[Thread %s] onTestFailedButWithinSuccessPercentage(): %s",
            threadId, testName));
        
        if (test.get() != null) {
            test.get().log(Status.WARNING, "Prueba parcialmente exitosa");
        }
    }

    /**
     * Configura ExtentReports - DEBE ser sincronizado y ejecutarse una sola vez.
     * 
     * IMPORTANTE: Este método solo debe ejecutarse desde dentro de un bloqueque sincronizado
     * (ver onStart() que usa synchronized LOCK)
     */
    private void setupExtentReports() {
        try {
            String threadId = String.valueOf(Thread.currentThread().getId());
            logger.info(String.format("[Thread %s] Iniciando setupExtentReports()", threadId));
            
            // Crear directorio de reportes si no existe
            File reportDir = new File(REPORT_PATH);
            if (!reportDir.exists()) {
                reportDir.mkdirs();
                logger.info("[" + threadId + "] Directorio de reportes creado: " + REPORT_PATH);
            }

            // Configurar el reporte
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportFileName = "ExtentReport_" + timestamp + ".html";
            
            logger.info("[" + threadId + "] Creando ExtentSparkReporter: " + reportFileName);
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH + reportFileName);
            sparkReporter.config().setDocumentTitle("Reporte de Automatización");
            sparkReporter.config().setReportName("Test Automation Report");
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setEncoding("UTF-8");
            sparkReporter.config().setTimelineEnabled(true);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            // Agregar información del sistema
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Selenium Version", "4.15.0");
            extent.setSystemInfo("TestNG Version", "7.8.0");
            extent.setSystemInfo("Ejecutor", System.getProperty("user.name"));
            extent.setSystemInfo("Parallel Mode", "tests con 4 threads");

            logger.info(String.format(
                "[%s] Reporte Extent configurado exitosamente en: %s%s",
                threadId, REPORT_PATH, reportFileName
            ));
        } catch (Exception e) {
            logger.error("Error al configurar Extent Reports: " + e.getMessage(), e);
        }
    }

    /**
     * Captura screenshot del driver actual.
     * Este método es thread-safe ya que cada thread tiene su propio WebDriver (ThreadLocal en BaseTest)
     */
    private String captureScreenshot(WebDriver driver, String testName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String screenshotName = testName + "_" + timestamp + ".png";
            String screenshotPath = REPORT_PATH + "screenshots/" + screenshotName;
            
            // Crear directorio de screenshots si no existe
            File screenshotDir = new File(REPORT_PATH + "screenshots/");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            FileUtils.copyFile(screenshot, new File(screenshotPath));
            logger.info(String.format("[Thread %s] Screenshot guardado: %s",
                Thread.currentThread().getId(), screenshotPath));
            
            return "screenshots/" + screenshotName;
        } catch (IOException e) {
            logger.error(String.format("[Thread %s] Error al guardar screenshot: %s",
                Thread.currentThread().getId(), e.getMessage()), e);
            return null;
        }
    }

    /**
     * Obtiene el WebDriver del resultado de la prueba.
     * Thread-safe porque usa BaseTest.getDriver() que internamente usa ThreadLocal.
     */
    private WebDriver getDriverFromResult(ITestResult result) {
        try {
            Object testInstance = result.getInstance();
            if (testInstance instanceof BaseTest) {
                return ((BaseTest) testInstance).getDriver();
            }
        } catch (Exception e) {
            logger.error(String.format("[Thread %s] Error al obtener WebDriver: %s",
                Thread.currentThread().getId(), e.getMessage()), e);
        }
        return null;
    }

    /**
     * Retorna el ExtentTest del thread actual.
     * ThreadLocal es thread-safe.
     */
    public static ExtentTest getTest() {
        return test.get();
    }

    /**
     * Log INFO en Extent y Logger.
     * ThreadLocal es thread-safe.
     */
    public static void logInfo(String message) {
        if (test.get() != null) {
            test.get().log(Status.INFO, message);
        }
        logger.info(message);
    }

    /**
     * Log PASS en Extent y Logger.
     * ThreadLocal es thread-safe.
     */
    public static void logPass(String message) {
        if (test.get() != null) {
            test.get().log(Status.PASS, message);
        }
        logger.info(message);
    }

    /**
     * Log FAIL en Extent y Logger.
     * ThreadLocal es thread-safe.
     */
    public static void logFail(String message) {
        if (test.get() != null) {
            test.get().log(Status.FAIL, message);
        }
        logger.error(message);
    }

    /**
     * Log WARNING en Extent y Logger.
     * ThreadLocal es thread-safe.
     */
    public static void logWarning(String message) {
        if (test.get() != null) {
            test.get().log(Status.WARNING, message);
        }
        logger.warn(message);
    }
}

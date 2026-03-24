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

public class ExtentReportListener implements ITestListener, ISuiteListener {
    private static final Logger logger = LogManager.getLogger(ExtentReportListener.class);
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static final String REPORT_PATH = "target/extent-reports/";

    @Override
    public void onStart(ISuite suite) {
        logger.info("Iniciando generación de reporte Extent");
        setupExtentReports();
    }

    @Override
    public void onFinish(ISuite suite) {
        logger.info("Finalizando generación de reporte Extent");
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("Iniciando prueba: " + result.getMethod().getMethodName());
        if (extent != null) {
            ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName(), 
                    result.getMethod().getDescription());
            test.set(extentTest);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("Prueba exitosa: " + result.getMethod().getMethodName());
        if (test.get() != null) {
            test.get().log(Status.PASS, "Prueba ejecutada exitosamente");
            test.get().log(Status.INFO, "Tiempo de ejecución: " + 
                    (result.getEndMillis() - result.getStartMillis()) + " ms");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("Prueba fallida: " + result.getMethod().getMethodName());
        if (test.get() != null) {
            test.get().log(Status.FAIL, "Prueba fallida: " + result.getThrowable().getMessage());
            
            // Capturar screenshot en caso de fallo
            try {
                WebDriver driver = getDriverFromResult(result);
                if (driver != null) {
                    String screenshotPath = captureScreenshot(driver, result.getMethod().getMethodName());
                    if (screenshotPath != null) {
                        test.get().log(Status.FAIL, "Screenshot capturado", 
                                MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                        test.get().log(Status.INFO, "Screenshot guardado en: " + screenshotPath);
                    }
                }
            } catch (Exception e) {
                logger.error("Error al capturar screenshot: " + e.getMessage());
                test.get().log(Status.WARNING, "No se pudo capturar screenshot: " + e.getMessage());
            }
            
            // Agregar stack trace
            test.get().log(Status.FAIL, result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.info("Prueba omitida: " + result.getMethod().getMethodName());
        if (test.get() != null) {
            test.get().log(Status.SKIP, "Prueba omitida: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("Prueba parcialmente exitosa: " + result.getMethod().getMethodName());
        if (test.get() != null) {
            test.get().log(Status.WARNING, "Prueba parcialmente exitosa");
        }
    }

    private void setupExtentReports() {
        try {
            // Crear directorio de reportes si no existe
            File reportDir = new File(REPORT_PATH);
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }

            // Configurar el reporte
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportFileName = "ExtentReport_" + timestamp + ".html";
            
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

            logger.info("Reporte Extent configurado en: " + REPORT_PATH + reportFileName);
        } catch (Exception e) {
            logger.error("Error al configurar Extent Reports: " + e.getMessage());
        }
    }

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
            logger.info("Screenshot guardado: " + screenshotPath);
            return "screenshots/" + screenshotName;
        } catch (IOException e) {
            logger.error("Error al guardar screenshot: " + e.getMessage());
            return null;
        }
    }

    private WebDriver getDriverFromResult(ITestResult result) {
        try {
            Object testInstance = result.getInstance();
            if (testInstance instanceof BaseTest) {
                return ((BaseTest) testInstance).getDriver();
            }
        } catch (Exception e) {
            logger.error("Error al obtener WebDriver: " + e.getMessage());
        }
        return null;
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void logInfo(String message) {
        if (test.get() != null) {
            test.get().log(Status.INFO, message);
        }
        logger.info(message);
    }

    public static void logPass(String message) {
        if (test.get() != null) {
            test.get().log(Status.PASS, message);
        }
        logger.info(message);
    }

    public static void logFail(String message) {
        if (test.get() != null) {
            test.get().log(Status.FAIL, message);
        }
        logger.error(message);
    }

    public static void logWarning(String message) {
        if (test.get() != null) {
            test.get().log(Status.WARNING, message);
        }
        logger.warn(message);
    }
}

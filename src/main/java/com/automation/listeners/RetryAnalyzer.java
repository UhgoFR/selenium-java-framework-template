package com.automation.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    
    private int retryCount = 0;
    private static final int maxRetryCount = 2; // Número máximo de reintentos

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            logger.warn("Reintentando prueba " + result.getMethod().getMethodName() + 
                       " - Intento #" + retryCount + " de " + maxRetryCount);
            
            // Esperar antes de reintentar
            try {
                Thread.sleep(2000); // Esperar 2 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return true;
        }
        
        logger.error("Se alcanzó el número máximo de reintentos para la prueba: " + 
                    result.getMethod().getMethodName());
        return false;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void resetRetryCount() {
        retryCount = 0;
    }
}

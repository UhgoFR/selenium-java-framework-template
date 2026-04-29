package com.automation.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * VERSIÓN CORREGIDA - THREAD-SAFE PARA EJECUCIÓN PARALELA
 * 
 * Problema en versión original:
 * - retryCount era variable de instancia (no thread-safe)
 * - En ejecución paralela, múltiples threads compartían el mismo contador
 * - Resultado: Race conditions, reintentos incorrectos
 * 
 * Solución:
 * - Usar ThreadLocal<Integer> para asegurar cada thread tenga su propio contador
 * - Sincronización correcta de incremento y lectura
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    
    // ✅ ThreadLocal para asegurar que cada thread tenga su propio contador
    private static ThreadLocal<Integer> retryCount = new ThreadLocal<>();
    private static final int maxRetryCount = 2; // Número máximo de reintentos

    @Override
    public boolean retry(ITestResult result) {
        // Obtener contador del thread actual (null si es la primera vez)
        Integer currentCount = retryCount.get();
        int count = (currentCount == null) ? 0 : currentCount;
        
        String testName = result.getMethod().getMethodName();
        String threadId = String.valueOf(Thread.currentThread().getId());
        
        if (count < maxRetryCount) {
            count++;
            retryCount.set(count);
            
            logger.warn(String.format(
                "[Thread %s] Reintentando prueba '%s' - Intento #%d de %d",
                threadId, testName, count, maxRetryCount
            ));
            
            // Esperar antes de reintentar para permitir que se limpien recursos
            try {
                Thread.sleep(2000); // Esperar 2 segundos
            } catch (InterruptedException e) {
                logger.warn("Interrupción durante espera de reintento: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
            
            return true;
        }
        
        logger.error(String.format(
            "[Thread %s] Se alcanzó el número máximo de reintentos para la prueba: '%s'",
            threadId, testName
        ));
        
        // ✅ IMPORTANTE: Limpiar ThreadLocal al finalizar para evitar memory leaks
        retryCount.remove();
        return false;
    }
    
    /**
     * Retorna el contador de reintentos del thread actual.
     * 
     * @return Contador de reintentos (0 si no hay reintentos)
     */
    public int getRetryCount() {
        Integer count = retryCount.get();
        return (count == null) ? 0 : count;
    }
    
    /**
     * Reinicia el contador de reintentos del thread actual.
     * Se ejecuta automáticamente al finalizar cada reintento.
     */
    public void resetRetryCount() {
        retryCount.set(0);
    }
}

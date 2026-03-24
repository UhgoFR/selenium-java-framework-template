package com.automation.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class AnnotationTransformer implements IAnnotationTransformer {
    private static final Logger logger = LogManager.getLogger(AnnotationTransformer.class);

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Configurar timeout por defecto si no está especificado
        if (annotation.getTimeOut() == 0) {
            annotation.setTimeOut(300000); // 5 minutos por defecto
        }
        
        // Log de configuración
        if (testMethod != null) {
            logger.debug("Configurando anotación para prueba: " + testMethod.getName());
            logger.debug("Timeout: " + annotation.getTimeOut() + "ms");
        }
    }
}

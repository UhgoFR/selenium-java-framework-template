# 🚀 Selenium + Rest Assured Automation Framework

Framework de automatización de pruebas enterprise-grade con ejecución en paralelo, gestión robusta de recursos, y configuración avanzada para testing web y API.

## 🎯 Características Principales

- **🔄 Ejecución en Paralelo Thread-Safe** - Soporte para múltiples runners concurrentes
- **🛡️ Gestión Robusta de WebDriver** - Cierre garantizado de browsers y procesos
- **⏱️ Timeouts Configurados** - Implicit wait, page load y script timeouts
- **📊 Reportes Avanzados** - Extent Reports con screenshots automáticos
- **🔧 Configuración Optimizada** - Sin duplicidad de código, mantenible
- **🌐 Multiplataforma** - Compatible con macOS, Windows y Linux

## 🛠️ Tecnologías Utilizadas

- **Selenium WebDriver 4.15.0** - Automatización de pruebas web
- **Rest Assured 5.3.2** - Automatización de pruebas API REST  
- **TestNG 7.8.0** - Framework de testing con ejecución paralela
- **Maven 3.6+** - Gestión de dependencias y build
- **Extent Reports 5.1.1** - Reportes HTML interactivos
- **Log4j 2.20.0** - Sistema de logging avanzado
- **Lombok 1.18.30** - Reducción de código boilerplate
- **WebDriverManager 5.5.3** - Gestión automática de drivers

## 📁 Estructura del Proyecto

```
src/
├── main/java/com/automation/
│   ├── config/          # ConfigManager - Gestión centralizada de propiedades
│   ├── base/           # Clases base optimizadas (BaseTest, BaseAPITest)
│   ├── pages/          # BasePage y Page Objects específicos
│   ├── listeners/      # Listeners para reportes y transformación
│   ├── models/         # POJOs para datos API con Lombok
│   └── utils/          # Utilidades varias (DataLoader)
└── test/java/com/automation/
    ├── tests/          # Clases de prueba (WebTests, APITests)
    ├── pages/          # Page Objects para pruebas web
    └── resources/
        ├── data/       # Archivos JSON para datos de prueba API
        └── config.properties
```

## 🏗️ Arquitectura del Framework

### BaseTest - Clase Base Thread-Safe para Automatización Web

#### **Características de Ejecución en Paralelo**
- **ThreadLocal<WebDriver>** - Aislamiento completo por thread
- **Gestión Multi-Capa de Recursos** - 4 niveles de limpieza garantizada
- **Shutdown Hook** - Protección contra terminación abrupta
- **Lazy Initialization** - Creación de drivers bajo demanda

#### **Ciclo de Vida Completo**
```java
@BeforeSuite     → Descarga automática de drivers
@BeforeClass     → Inicialización backup por clase  
@BeforeMethod    → Creación de driver por método
@Test            → Ejecución de prueba
@AfterMethod     → Cierre de driver (alwaysRun=true)
@AfterClass      → Cierre adicional (backup)
@AfterSuite      → Limpieza de procesos del SO
Shutdown Hook    → Limpieza de emergencia
```

#### **Configuración de Timeouts**
```java
// Configurados automáticamente en configureDriver()
webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(20));
```

#### **Métodos de Limpieza Multiplataforma**
- **macOS**: `pkill -f chromedriver` y `pkill -f 'Google Chrome'`
- **Windows**: `taskkill /F /IM chromedriver.exe` y `taskkill /F /IM chrome.exe`
- **Linux**: `pkill -f chromedriver` y `pkill -f chrome`

### BaseAPITest - Clase Base Thread-Safe para API

#### **Características de Ejecución en Paralelo**
- **ThreadLocal<RequestSpecification>** - Especificaciones aisladas por thread
- **ThreadLocal<ResponseSpecification>** - Validaciones independientes
- **Sin Estados Compartidos** - Cero race conditions
- **Lazy Initialization** - Creación bajo demanda por thread

#### **Configuración Automática**
```java
// Por cada thread se crea:
RequestSpecification threadRequestSpec = new RequestSpecBuilder()
    .setBaseUri(ConfigManager.getApiBaseUrl())
    .setAccept("application/json")
    .setContentType("application/json")
    .addFilter(RequestLoggingFilter.logRequestTo(System.out))

### testng.xml - Configuración Principal
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Automation Test Suite" parallel="classes" thread-count="3">
    
    <listeners>
        <listener class-name="com.automation.listeners.ExtentReportListener"/>
        <listener class-name="com.automation.listeners.AnnotationTransformer"/>
    </listeners>
    
    <parameter name="browser" value="chrome"/>
    <parameter name="environment" value="test"/>
    <parameter name="headless" value="false"/>
    
    <!-- WEB TESTS - All web automation tests -->
    <test name="Web Tests">
        <classes>
            <class name="com.automation.tests.web.YourWebTestClass"/>
            <!-- Agregar más clases de tests Web aquí -->
        </classes>
    </test>
    
    <!-- API TESTS - All API automation tests -->
    <test name="API Tests">
        <classes>
            <class name="com.automation.tests.api.YourAPITestClass"/>
            <!-- Agregar más clases de tests API aquí -->
        </classes>
    </test>
    
</suite>
```

### **Configuración de Paralelismo (Thread-Safe)**

> **Importante:** Todos los archivos usan `parallel="classes"` para garantizar ejecución thread-safe.

| Configuración | Descripción | Uso Recomendado |
|---------------|-------------|----------------|
| `parallel="classes"` ✅ | Ejecuta diferentes clases en paralelo | **Actual - Thread-safe** |
| `parallel="methods"` ❌ | Ejecuta métodos en paralelo | No recomendado - causa race conditions |
| `parallel="tests"` | Ejecuta diferentes `<test>` en paralelo | Alternativa válida |
| `parallel="false"` | Ejecución secuencial | Solo para debugging |

## 🚀 Comandos de Ejecución

### Instalación y Configuración
```bash
# 1. Instalar dependencias y compilar
mvn clean install

# 2. Verificar configuración
mvn dependency:tree
```

### Ejecución por Tipo de Suite

#### **Smoke Tests (Validación rápida)**
```bash
mvn clean test -DsuiteXmlFile=testng-smoke.xml -Dbrowser=chrome -Dheadless=false
```

#### **Regression Tests (Suite completa)**
```bash
mvn clean test -DsuiteXmlFile=testng-regression.xml -Dbrowser=chrome -Dheadless=false
```

#### **Negative Tests (Manejo de errores)**
```bash
mvn clean test -DsuiteXmlFile=testng-negative.xml -Dbrowser=chrome -Dheadless=false
```

#### **Solo Tests Web**
```bash
mvn clean test -DsuiteXmlFile=testng-web.xml -Dbrowser=chrome -Dheadless=false
```

#### **Solo Tests API**
```bash
mvn clean test -DsuiteXmlFile=testng-api.xml
```

#### **Suite Completa (Web + API)**
```bash
mvn clean test -DsuiteXmlFile=testng.xml -Dbrowser=chrome -Dheadless=false
```

### Parámetros de Ejecución Disponibles

| Parámetro | Valores | Descripción | Ejemplo |
|-----------|---------|-------------|----------|
| `-DsuiteXmlFile` | `testng.xml`, `testng-smoke.xml`, etc. | Archivo de suite TestNG a ejecutar | `-DsuiteXmlFile=testng-smoke.xml` |
| `-Dbrowser` | `chrome`, `firefox`, `edge` | Navegador para tests Web | `-Dbrowser=firefox` |
| `-Dheadless` | `true`, `false` | Modo headless (sin UI) | `-Dheadless=true` |
| `-Dtest` | Nombre de clase | Ejecutar clase específica | `-Dtest=YourTestClass` |
| `-Dgroups` | `smoke`, `regression`, `negative` | Filtrar por grupos TestNG | `-Dgroups=smoke` |

### Ejemplos de Uso

```bash
# Modo headless para CI/CD
mvn clean test -DsuiteXmlFile=testng-smoke.xml -Dbrowser=chrome -Dheadless=true

# Firefox en modo visible
mvn clean test -DsuiteXmlFile=testng-web.xml -Dbrowser=firefox -Dheadless=false

# Test específico
mvn clean test -Dtest=YourTestClass -Dbrowser=chrome -Dheadless=false

# Método específico
mvn clean test -Dtest=YourTestClass#testMethodName -Dbrowser=chrome -Dheadless=false

# Por grupos TestNG
mvn clean test -Dgroups=smoke -Dbrowser=chrome -Dheadless=false
```

### Verificación de Paralelismo

#### **Logs de Thread IDs**
```bash
# Ejecutar y observar diferentes thread IDs
mvn test -Dgroups=WebTest -Dsurefire.suiteXmlFiles=testng.xml | grep "thread"
```

Salida esperada:
```
BaseTest constructor called for thread: 1
BaseTest constructor called for thread: 26
BaseAPITest @BeforeClass for thread: 27
Initializing specifications for thread: 27
```

#### **Verificación de Procesos**
```bash
# Verificar que no quedan procesos abiertos
ps aux | grep -i chromedriver | grep -v grep | wc -l
# Debería mostrar: 0

# Verificar procesos durante ejecución
ps aux | grep -i chrome
```

## 📊 Reportes y Logs

### Reportes Extent
```bash
# Generar reportes y abrir automáticamente
mvn clean test -Dmaven.test.failure.ignore=true && \
open target/extent-reports/ExtentReport_$(date +%Y%m%d)_*.html

# Ver screenshots capturados
ls -la target/extent-reports/screenshots/
open target/extent-reports/screenshots/
```

### Logs en Tiempo Real
```bash
# Ver logs generales con thread IDs
tail -f target/logs/automation.log | grep "thread"

# Ver solo errores
tail -f target/logs/error.log

# Ver logs de pruebas
tail -f target/logs/tests.log
```

## ⚙️ Configuración

### Archivo de Configuración: `src/test/resources/config.properties`
```properties
# Configuración Selenium WebDriver
browser=chrome
headless=false
timeout=10

# URLs de prueba
base.url=https://example.com
api.base.url=https://jsonplaceholder.typicode.com

# Configuración de logging
log.level=INFO
```

## � Cómo Extender el Framework

### Agregar Tests Web

#### **1. Crear Clase de Test**
```java
package com.automation.tests.web;

import com.automation.base.BaseTest;
import org.testng.annotations.*;

public class YourWebTests extends BaseTest {
    
    private YourPage yourPage;
    
    @BeforeMethod(alwaysRun = true)
    public void setUpPages() {
        // Obtener driver thread-safe
        WebDriver driver = getDriver();
        
        // Navegar a la URL base
        navigateToBaseUrl();
        
        // Inicializar page objects
        yourPage = new YourPage(driver);
    }
    
    @Test(description = "Test example", groups = {"smoke", "regression"})
    public void testExample() {
        // Tu lógica de test aquí
        yourPage.performAction();
        Assert.assertTrue(yourPage.isElementVisible(), "Element should be visible");
    }
}
```

#### **2. Crear Page Object**
```java
package com.automation.pages;

import com.automation.pages.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class YourPage extends BasePage {
    
    @FindBy(id = "elementId")
    private WebElement element;
    
    public YourPage(WebDriver driver) {
        super(driver);
    }
    
    public void performAction() {
        // Usa métodos de BasePage (thread-safe con waits automáticos)
        click(element);
        type(element, "text");
    }
    
    public boolean isElementVisible() {
        return isDisplayed(element);
    }
}
```

#### **3. Agregar a testng.xml**
```xml
<test name="Web Tests">
    <classes>
        <class name="com.automation.tests.web.YourWebTests"/>
    </classes>
</test>
```

---

### Agregar Tests API

#### **1. Crear Clase de Test**
```java
package com.automation.tests.api;

import com.automation.base.BaseAPITest;
import io.restassured.response.Response;
import org.testng.annotations.*;
import static io.restassured.RestAssured.given;

public class YourAPITests extends BaseAPITest {
    
    @Test(description = "GET request example", groups = {"smoke", "api"})
    public void testGetEndpoint() {
        Response response = given()
                .spec(getRequestSpecification())
                .when()
                .get("/endpoint")
                .then()
                .spec(getResponseSpecification())
                .extract().response();
        
        Assert.assertEquals(response.getStatusCode(), 200);
        // Validaciones adicionales
    }
    
    @Test(description = "POST request example", groups = {"regression", "api"})
    public void testPostEndpoint() {
        String requestBody = "{ \"key\": \"value\" }";
        
        Response response = given()
                .spec(getRequestSpecification())
                .body(requestBody)
                .when()
                .post("/endpoint")
                .then()
                .extract().response();
        
        Assert.assertEquals(response.getStatusCode(), 201);
    }
}
```

#### **2. Crear POJO con Lombok (Opcional)**
```java
package com.automation.models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YourModel {
    private String id;
    private String name;
    private String description;
}
```

#### **3. Agregar a testng.xml**
```xml
<test name="API Tests">
    <classes>
        <class name="com.automation.tests.api.YourAPITests"/>
    </classes>
</test>
```

---

### Configurar Data Providers

#### **Crear archivo JSON de datos**
`src/test/resources/data/your-test-data.json`
```json
[
  {
    "testCase": "Valid scenario",
    "input": "value1",
    "expected": "result1"
  },
  {
    "testCase": "Invalid scenario",
    "input": "value2",
    "expected": "result2"
  }
]
```

#### **Usar DataProvider en tests**
```java
@DataProvider(name = "testData")
public Object[][] getTestData() {
    return DataLoader.loadJsonData("data/your-test-data.json");
}

@Test(dataProvider = "testData", groups = {"regression"})
public void testWithData(JsonNode data) {
    String input = data.get("input").asText();
    String expected = data.get("expected").asText();
    
    // Tu lógica de test
    Assert.assertEquals(actualResult, expected);
}
```

## 📋 Archivos Generados

```
target/
├── extent-reports/          # Reportes Extent HTML interactivos
│   ├── ExtentReport_YYYYMMDD_HHMMSS.html
│   └── screenshots/         # Screenshots automáticos de fallos
│       └── testFailedExecution_YYYYMMDD_HHMMSS.png
├── logs/
│   ├── automation.log       # Log general con thread IDs
│   ├── error.log           # Solo errores y excepciones
│   ├── tests.log           # Logs específicos de pruebas
│   └── automation.json     # JSON para integración externa
├── surefire-reports/       # Reportes TestNG tradicionales
│   ├── index.html          # Reporte HTML básico
│   └── emailable-report.html
└── test-classes/           # Clases compiladas de pruebas
```

## ✅ Características Implementadas

### **Core Framework**
- ✅ **Ejecución en Paralelo Thread-Safe** - `parallel="classes"` con 3 threads
- ✅ **Gestión Robusta de WebDriver** - Cierre garantizado multi-capa
- ✅ **ThreadSafe API Testing** - BaseAPITest con ThreadLocal
- ✅ **Timeouts Configurados** - Implicit (10s), Page Load (30s), Script (20s)
- ✅ **Limpieza Multiplataforma** - macOS, Windows, Linux
- ✅ **Navegación Controlada** - `navigateToBaseUrl()` en `@BeforeMethod` de clases hijas

### **Optimización y Mantenimiento**
- ✅ **Código DRY Eliminado** - Métodos reutilizables en BaseTest
- ✅ **Factory Pattern** - Creación optimizada de drivers
- ✅ **Lazy Initialization** - Creación bajo demanda
- ✅ **Shutdown Hooks** - Protección contra terminación abrupta

### **Reportes y Logging**
- ✅ **Extent Reports Avanzados** - Dashboard interactivo
- ✅ **Screenshots Automáticos** - Captura en fallos
- ✅ **Logging Dual** - Log4j2 + Extent Reports
- ✅ **Thread-Aware Logging** - Logs con IDs de thread

### **Datos y Configuración**
- ✅ **POJOs con Lombok** - Modelos de datos para API
- ✅ **DataLoader** - Carga flexible de JSON
- ✅ **Configuración Centralizada** - Properties + TestNG
- ✅ **Parámetros Flexibles** - Browser, headless, environment

## 📦 Configuración de Maven (pom.xml)

### Dependencias Principales

```xml
<dependencies>
    <!-- Selenium WebDriver -->
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.15.0</version>
    </dependency>
    
    <!-- Rest Assured -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>5.3.2</version>
    </dependency>
    
    <!-- TestNG -->
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.8.0</version>
    </dependency>
    
    <!-- Extent Reports -->
    <dependency>
        <groupId>com.aventstack</groupId>
        <artifactId>extentreports</artifactId>
        <version>5.1.1</version>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- WebDriverManager -->
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>5.5.3</version>
    </dependency>
</dependencies>
```

### Configuración de Maven Surefire Plugin

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <suiteXmlFiles>
                    <suiteXmlFile>${suiteXmlFile}</suiteXmlFile>
                </suiteXmlFiles>
                <systemPropertyVariables>
                    <browser>${browser}</browser>
                    <headless>${headless}</headless>
                </systemPropertyVariables>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Properties Configurables

```xml
<properties>
    <!-- Versión de Java -->
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    
    <!-- Encoding -->
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    
    <!-- Suite TestNG por defecto -->
    <suiteXmlFile>testng.xml</suiteXmlFile>
    
    <!-- Configuración de ejecución -->
    <browser>chrome</browser>
    <headless>false</headless>
</properties>
```

---

## 🎯 Inicio Rápido

```bash
# 1. Instalar dependencias
mvn clean install

# 2. Ejecutar smoke tests (validación rápida)
mvn clean test -DsuiteXmlFile=testng-smoke.xml -Dbrowser=chrome -Dheadless=false

# 3. Ver reportes generados
open target/extent-reports/ExtentReport_*.html

# 4. Ejecutar suite completa
mvn clean test -DsuiteXmlFile=testng.xml -Dbrowser=chrome -Dheadless=false

# 5. Verificar que no quedan procesos
ps aux | grep -i chromedriver | grep -v grep | wc -l
```

## 🔧 Troubleshooting

### **Problemas Comunes**

#### **Problema: Tests fallan por timeout**
```bash
# Solución: Aumentar implicit wait en BaseTest.configureDriver()
webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
```

#### **Problema: Proyectos Chrome abiertos**
```bash
# Verificar procesos abiertos
ps aux | grep -i chrome

# Limpiar manualmente si es necesario
pkill -f chromedriver
pkill -f "Google Chrome"
```

#### **Problema: Race conditions en paralelo**
```bash
# Verificar thread IDs en logs
mvn test -Dgroups=WebTest | grep "thread"

# Asegurar configuración correcta en testng.xml
parallel="classes" thread-count="4"
```

## 🚀 Mejores Prácticas

### **Para Desarrollo**
- Usar `getDriver()` - Siempre thread-safe con lazy initialization
- No crear drivers manualmente - Usar la infraestructura de BaseTest
- Verificar limpieza de procesos después de cada ejecución

### **Para Ejecución en Paralelo**
- Configurar `thread-count` según recursos disponibles
- Usar grupos TestNG para ejecutar tipos específicos de tests
- Monitorear logs para verificar thread-safety

### **Para CI/CD**
- Usar siempre `-Dheadless=true`
- Configurar `-Dmaven.test.failure.ignore=true`
- Verificar `thread-count` adecuado para el entorno

## 🎨 Patrones de Diseño Implementados

### **Page Object Model (POM)**
- Separación de lógica de UI y lógica de test
- Reutilización de código
- Mantenibilidad mejorada
- Encapsulación de elementos y acciones

### **Factory Pattern**
- Creación centralizada de WebDriver
- Soporte para múltiples navegadores
- Configuración dinámica basada en parámetros

### **Singleton Pattern**
- ConfigManager para gestión de propiedades
- Una sola instancia de configuración por ejecución

### **ThreadLocal Pattern**
- Aislamiento de WebDriver por thread
- Aislamiento de RequestSpecification por thread
- Ejecución paralela thread-safe

### **Builder Pattern**
- RequestSpecBuilder para configuración de API
- ResponseSpecBuilder para validaciones

---

## 📈 Métricas de Rendimiento

### **Ejecución Típica (parallel="classes", thread-count="3")**
- **Smoke Tests**: ~15-20 segundos
- **Regression Tests**: ~3-5 minutos (depende del número de tests)
- **Negative Tests**: ~1-2 minutos
- **API Tests**: ~30 segundos - 1 minuto
- **Limpieza**: 0 procesos residuales

### **Uso de Recursos**
- **Memoria**: ~200-400MB por thread
- **CPU**: 1-2 cores por thread activo
- **Browsers**: Máximo 3 instancias simultáneas (thread-count=3)
- **Thread Safety**: Garantizado con `ThreadLocal<WebDriver>`

### **Escalabilidad**
- Ajustar `thread-count` según recursos disponibles
- Recomendado: 3-4 threads en máquina local
- CI/CD: 5-8 threads con recursos adecuados

---

## 🔄 Cambios Recientes (2026-03-29)

### **Mejoras en Paralelización:**
- ✅ Cambiado de `parallel="methods"` a `parallel="classes"` en todos los archivos TestNG
- ✅ Navegación movida de `BaseTest.setUp()` a `@BeforeMethod` de clases hijas
- ✅ Eliminados problemas de `NullPointerException` y `TimeoutException` en ejecución paralela
- ✅ Thread-safety garantizado con `ThreadLocal<WebDriver>`

### **Nuevos Archivos TestNG:**
- ✅ `testng-smoke.xml` - 6 tests críticos específicos
- ✅ `testng-regression.xml` - Suite de regresión completa
- ✅ `testng-negative.xml` - Tests de manejo de errores
- ✅ `testng-web.xml` - Solo tests Web
- ✅ `testng-api.xml` - Solo tests API

### **Configuración:**
- ✅ `pom.xml` usa `${suiteXmlFile}` dinámicamente
- ✅ Todos los archivos usan `thread-count="3"` con `parallel="classes"`
- ✅ Documentación actualizada en README.md, TEST-EXECUTION-GUIDE.md y SauceDemo/README.md

---

**🎉 Framework enterprise-ready para automatización web y API con ejecución en paralelo thread-safe, gestión robusta de recursos y configuración avanzada.**

**Última actualización:** 2026-03-29

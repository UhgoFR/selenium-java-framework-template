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
    .addFilter(ResponseLoggingFilter.logResponseTo(System.out))
    .build();
```

## ⚙️ Configuración de Ejecución en Paralelo

### testng.xml - Configuración Principal
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Automation Test Suite" parallel="classes" thread-count="4">
    
    <listeners>
        <listener class-name="com.automation.listeners.ExtentReportListener"/>
        <listener class-name="com.automation.listeners.AnnotationTransformer"/>
    </listeners>
    
    <parameter name="browser" value="chrome"/>
    <parameter name="environment" value="test"/>
    <parameter name="headless" value="false"/>
    
    <test name="Web Tests">
        <classes>
            <class name="com.automation.tests.WebTests"/>
            <class name="com.automation.tests.HomePageTest"/>
        </classes>
    </test>
    
    <test name="API Tests">
        <classes>
            <class name="com.automation.tests.APITests"/>
        </classes>
    </test>
    
</suite>
```

### **Opciones de Paralelismo**

| Configuración | Descripción | Runners Simultáneos |
|---------------|-------------|-------------------|
| `parallel="tests"` | Ejecuta diferentes `<test>` en paralelo | Hasta 4 tests |
| `parallel="classes"` | Ejecuta diferentes clases en paralelo | Hasta 4 clases |
| `parallel="methods"` | Ejecuta métodos en paralelo | Hasta 4 métodos |
| `parallel="false"` | Ejecución secuencial | 1 runner |

## 🚀 Comandos de Ejecución

### Instalación y Configuración
```bash
# 1. Instalar dependencias y compilar
mvn clean install

# 2. Verificar configuración
mvn dependency:tree
```

### Ejecución en Paralelo

#### **Suite Completa en Paralelo**
```bash
# Ejecutar todos los tests con 4 runners simultáneos
mvn clean test -Dsurefire.suiteXmlFiles=testng.xml

# Ejecutar ignorando fallos (para CI/CD)
mvn clean test -Dmaven.test.failure.ignore=true
```

#### **Pruebas Web en Paralelo**
```bash
# Ejecutar solo pruebas web con múltiples threads
mvn test -Dgroups=WebTest -Dsurefire.suiteXmlFiles=testng.xml

# Prueba específica
mvn test -Dtest=HomePageTest -Dsurefire.suiteXmlFiles=testng.xml
```

#### **Pruebas API en Paralelo**
```bash
# Ejecutar solo pruebas API con thread-safe
mvn test -Dgroups=ApiTest -Dsurefire.suiteXmlFiles=testng.xml

# Prueba específica
mvn test -Dtest=APITests -Dsurefire.suiteXmlFiles=testng.xml
```

#### **Ejecución con Parámetros**
```bash
# Diferentes navegadores en paralelo
mvn test -Dbrowser=chrome -Dsurefire.suiteXmlFiles=testng.xml
mvn test -Dbrowser=firefox -Dsurefire.suiteXmlFiles=testng.xml

# Modo headless para CI/CD
mvn test -Dheadless=true -Dsurefire.suiteXmlFiles=testng.xml

# Combinación de parámetros
mvn test -Dbrowser=chrome -Dheadless=true -Dsurefire.suiteXmlFiles=testng.xml
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

## 💡 Ejemplos de Uso

### Prueba Web con Thread-Safety
```java
public class WebTests extends BaseTest {
    
    @Test(description = "Prueba de búsqueda thread-safe", groups = "WebTest")
    public void testSearchFunctionality() {
        // getDriver() es thread-safe y maneja lazy initialization
        HomePage homePage = new HomePage(getDriver());
        navigateToBaseUrl();
        
        // Implicit wait de 10 segundos configurado automáticamente
        homePage.search("Selenium WebDriver");
        
        // Verificación con WebDriverWait explícito si es necesario
        Assert.assertTrue(homePage.isSearchResultsVisible(), 
                        "Los resultados de búsqueda deberían ser visibles");
    }
}
```

### Prueba API Thread-Safe
```java
public class APITests extends BaseAPITest {
    
    @Test(description = "Prueba API thread-safe", groups = "ApiTest")
    public void basicGetTest() {
        // getRequestSpecification() es thread-safe
        Response response = given()
                .spec(getRequestSpecification())
                .when()
                .get("/users/1")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, 
                          "El código de estado no es 200");
        
        System.out.println("Response: " + response.asString());
    }
}
```

### Page Object con BasePage Optimizada
```java
public class HomePage extends BasePage {
    @FindBy(name = "q")
    private WebElement searchInput;
    
    @FindBy(name = "btnK")
    private WebElement searchButton;
    
    public HomePage(WebDriver driver) {
        super(driver);
    }
    
    public void search(String term) {
        // Usa métodos thread-safe de BasePage con waits automáticos
        type(searchInput, term);
        click(searchButton);
    }
    
    public boolean isSearchResultsVisible() {
        // Implicit wait configurado automáticamente
        return isDisplayed(By.id("search"));
    }
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
- ✅ **Ejecución en Paralelo Thread-Safe** - 4 runners simultáneos
- ✅ **Gestión Robusta de WebDriver** - Cierre garantizado multi-capa
- ✅ **ThreadSafe API Testing** - BaseAPITest con ThreadLocal
- ✅ **Timeouts Configurados** - Implicit (10s), Page Load (30s), Script (20s)
- ✅ **Limpieza Multiplataforma** - macOS, Windows, Linux

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

## 🎯 Inicio Rápido

```bash
# 1. Instalar dependencias
mvn clean install

# 2. Ejecutar pruebas web en paralelo
mvn test -Dgroups=WebTest -Dsurefire.suiteXmlFiles=testng.xml

# 3. Ejecutar pruebas API en paralelo
mvn test -Dgroups=ApiTest -Dsurefire.suiteXmlFiles=testng.xml

# 4. Ver reportes generados
open target/extent-reports/ExtentReport_$(date +%Y%m%d)_*.html

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

## 📈 Métricas de Rendimiento

### **Ejecución Típica**
- **Web Tests (5 tests)**: ~25-30 segundos en paralelo
- **API Tests (7 tests)**: ~3-5 segundos en paralelo
- **Suite Completa**: ~30-35 segundos con 4 runners
- **Limpieza**: 0 procesos residuales

### **Uso de Recursos**
- **Memoria**: ~200-400MB por runner
- **CPU**: 1-2 cores por runner activo
- **Browsers**: Máximo 4 instancias simultáneas

---

**🎉 Framework enterprise-ready para automatización web y API con ejecución en paralelo, gestión robusta de recursos y configuración avanzada.**

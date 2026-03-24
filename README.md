# Selenium + Rest Assured Automation Framework

Framework de automatización de pruebas completo para testing web y API con Selenium WebDriver, Rest Assured, reportes avanzados y sistema de logging integral.

## 🚀 Tecnologías Utilizadas

- **Selenium WebDriver 4.15.0** - Automatización de pruebas web
- **Rest Assured 5.3.2** - Automatización de pruebas API REST
- **TestNG 7.8.0** - Framework de testing con listeners
- **Maven 3.6+** - Gestión de dependencias y build
- **Extent Reports 5.1.1** - Reportes HTML interactivos y visuales
- **Log4j 2.20.0** - Sistema de logging avanzado
- **Lombok 1.18.30** - Reducción de código boilerplate
- **WebDriverManager 5.5.3** - Gestión automática de drivers
- **Commons IO 2.11.0** - Operaciones de archivos

## 📁 Estructura del Proyecto

```
src/
├── main/java/com/automation/
│   ├── config/          # ConfigManager - Gestión centralizada de propiedades
│   ├── base/           # Clases base (BaseTest, BaseAPITest)
│   ├── pages/          # BasePage y Page Objects específicos
│   ├── listeners/      # Listeners para reportes y reintentos
│   ├── models/         # POJOs para datos API con Lombok
│   └── utils/          # Utilidades varias (DataLoader)
└── test/java/com/automation/
    ├── tests/          # Clases de prueba (WebTests, APITests, ReportTest)
    ├── pages/          # Page Objects para pruebas web
    └── resources/
        ├── data/       # Archivos JSON para datos de prueba API
        └── config.properties
```

## 🎯 Componentes Principales

### BasePage - Clase Base para Automatización Web
Clase base completa con 40+ métodos comunes de interacción web:

#### **Espera y Sincronización**
- `waitForElementVisible()`, `waitForElementClickable()`, `waitForElementInvisible()`
- `waitForElementPresent()`, `waitForTextPresent()`, `waitForPageLoad()`, `waitForAjaxComplete()`

#### **Interacción con Elementos**
- `click()`, `clickWithJS()`, `doubleClick()`, `rightClick()`
- `type()`, `typeAndEnter()`, `typeWithAction()`
- `selectByVisibleText()`, `selectByValue()`, `selectByIndex()`

#### **Verificación y Obtención de Datos**
- `isDisplayed()`, `isEnabled()`, `isSelected()`, `isElementPresent()`
- `getText()`, `getAttribute()`, `getValue()`

#### **Navegación y Scroll**
- `navigateTo()`, `refresh()`, `goBack()`, `goForward()`
- `scrollToElement()`, `scrollToBottom()`, `scrollToTop()`, `scrollBy()`

#### **JavaScript y Utilidades Avanzadas**
- `executeJS()`, `highlightElement()`, `clickWithJS()`
- `pause()`, `getCurrentUrl()`, `hoverOverElement()`

#### **Manejo de Ventanas y Alerts**
- `switchToWindow()`, `switchToWindowByTitle()`, `closeCurrentWindow()`
- `getAlertText()`, `acceptAlert()`, `dismissAlert()`, `typeInAlert()`

#### **Interacciones Complejas**
- `dragAndDrop()`, `dragAndDropBy()`, `findElementByText()`

### ExtentReportListener - Sistema de Reportes Avanzado

#### **Características Principales**
- **📊 Reportes HTML interactivos** con dashboard completo
- **📸 Captura automática de screenshots** en pruebas fallidas
- **� Logging dual** (Log4j2 + Extent Reports)
- **📈 Métricas y estadísticas** visuales
- **🔍 Timeline de ejecución** con detalles por prueba
- **� Información del sistema** incluida automáticamente

#### **Reportes Generados**
```
target/extent-reports/
├── ExtentReport_YYYYMMDD_HHMMSS.html  # Reporte principal con timestamp
└── screenshots/                        # Screenshots de fallos
    ├── testFailedExecution_20260324_155724.png
    └── ...
```

#### **Métodos de Logging**
```java
// Logging dual automático
ExtentReportListener.logInfo("Mensaje informativo");
ExtentReportListener.logPass("Verificación exitosa");
ExtentReportListener.logFail("Error o fallo");
ExtentReportListener.logWarning("Advertencia");
```

### Sistema de POJOs para API con Lombok

#### **Modelos de Datos**
- **User.java** - Modelo completo de usuario con Address y Company
- **Address.java** - Modelo de dirección con Geo
- **Post.java** - Modelo de posts para pruebas API
- **Company.java** - Modelo de compañía
- **Geo.java** - Modelo de coordenadas

#### **Características**
- **@Data** - Getters, setters, toString, equals, hashCode automáticos
- **@Builder** - Construcción fluida de objetos
- **@NoArgsConstructor/@AllArgsConstructor** - Constructores completos
- **@JsonProperty** - Mapeo JSON a Java

### DataLoader - Utilidad para Datos de Prueba

#### **Métodos Principales**
```java
// Cargar JSON como objeto tipado
User user = DataLoader.loadJson("data/user.json", User.class);

// Cargar JSON como string
String json = DataLoader.loadJsonAsString("data/post.json");

// Cargar JSON como JsonNode para acceso dinámico
JsonNode node = DataLoader.loadJsonAsNode("data/user.json");
```

### Sistema de Logging Log4j2

Configuración completa con múltiples appenders:

#### **Tipos de Logs**
- **🖥️ Consola**: Salida en tiempo real durante ejecución
- **📄 Archivo general**: `target/logs/automation.log`
- **❌ Archivo de errores**: `target/logs/error.log`
- **🧪 Logs de pruebas**: `target/logs/tests.log`
- **📋 JSON**: Para integración con herramientas externas

#### **Características**
- **🔄 Rotación automática** (10MB, máximo 10 archivos)
- **📊 Niveles configurados** por componente (DEBUG, INFO, WARN, ERROR)
- **📅 Logs con timestamp** y contexto completo

## 🛠️ Comandos de Ejecución

### Instalación y Configuración
```bash
# 1. Instalar dependencias y compilar
mvn clean install

# 2. Verificar configuración
mvn dependency:tree
```

### Ejecución de Pruebas

#### **Suite Completa**
```bash
# Ejecutar todas las pruebas con reportes
mvn clean test -Dmaven.test.failure.ignore=true

# Ejecutar suite completa sin reportes (más rápido)
mvn clean test -DskipTests=false -Dmaven.test.failure.ignore=true
```

#### **Pruebas Web**
```bash
# Ejecutar solo pruebas web
mvn test -Dtest="*WebTests,*HomePageTest" -Dmaven.test.failure.ignore=true

# Ejecutar pruebas web específicas
mvn test -Dtest=WebTests -Dmaven.test.failure.ignore=true
mvn test -Dtest=HomePageTest -Dmaven.test.failure.ignore=true
```

#### **Pruebas API**
```bash
# Ejecutar solo pruebas API
mvn test -Dtest=APITests -Dmaven.test.failure.ignore=true

# Ejecutar pruebas API con datos de modelos
mvn test -Dtest=APITests -Dmaven.test.failure.ignore=true
```

#### **Pruebas de Reportes**
```bash
# Ejecutar pruebas de demostración de reportes
mvn test -Dtest=ReportTest -Dmaven.test.failure.ignore=true

# Ejecutar prueba específica
mvn test -Dtest=ReportTest#testSuccessfulExecution -Dmaven.test.failure.ignore=true
```

#### **Ejecución con Parámetros**
```bash
# Ejecutar con navegador específico
mvn test -Dbrowser=chrome -Dmaven.test.failure.ignore=true
mvn test -Dbrowser=firefox -Dmaven.test.failure.ignore=true

# Ejecutar en modo headless (ideal para CI/CD)
mvn test -Dheadless=true -Dmaven.test.failure.ignore=true

# Combinación de parámetros
mvn test -Dbrowser=chrome -Dheadless=true -Dmaven.test.failure.ignore=true
```

### Reportes y Logs

#### **Ver Reportes Extent**
```bash
# Generar reportes y abrir automáticamente
mvn clean test -Dmaven.test.failure.ignore=true && open target/extent-reports/ExtentReport_$(date +%Y%m%d)_*.html

# Buscar y abrir el reporte más reciente
open target/extent-reports/ExtentReport_$(ls -t target/extent-reports/ExtentReport_*.html | head -1 | cut -d'_' -f2-)

# Ver screenshots capturados
ls -la target/extent-reports/screenshots/
open target/extent-reports/screenshots/
```

#### **Logs en Tiempo Real**
```bash
# Ver logs generales
tail -f target/logs/automation.log

# Ver solo errores
tail -f target/logs/error.log

# Ver logs de pruebas
tail -f target/logs/tests.log

# Ver todos los logs disponibles
ls -la target/logs/
```

#### **Ejecución sin Reportes (Rápida)**
```bash
# Para desarrollo rápido - sin screenshots ni reportes
mvn test -DskipReports=true -Dmaven.test.failure.ignore=true

# Solo para verificar compilación y pruebas básicas
mvn test-compile
mvn surefire:test -Dmaven.test.failure.ignore=true
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

### Configuración TestNG: `testng.xml`
```xml
<suite name="Automation Test Suite" parallel="tests" thread-count="2">
    <listeners>
        <listener class-name="com.automation.listeners.ExtentReportListener"/>
        <listener class-name="com.automation.listeners.AnnotationTransformer"/>
    </listeners>
    
    <parameter name="browser" value="chrome"/>
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
    
    <test name="Report Tests">
        <classes>
            <class name="com.automation.tests.ReportTest"/>
        </classes>
    </test>
</suite>
```

## 💡 Ejemplos de Uso

### Page Object con BasePage
```java
public class HomePage extends BasePage {
    @FindBy(name = "search")
    private WebElement searchInput;
    
    @FindBy(id = "searchBtn")
    private WebElement searchButton;
    
    public HomePage(WebDriver driver) {
        super(driver);
    }
    
    public void search(String term) {
        type(searchInput, term);
        click(searchButton);
    }
    
    public boolean isSearchVisible() {
        return isDisplayed(searchInput);
    }
}
```

### Prueba Web con Logging Dual
```java
public class WebTests extends BaseTest {
    private static final Logger logger = LogManager.getLogger(WebTests.class);
    
    @Test(description = "Prueba de búsqueda con logging completo")
    public void testSearch() {
        logger.info("Iniciando prueba de búsqueda");
        ExtentReportListener.logInfo("Iniciando prueba de búsqueda");
        
        HomePage homePage = new HomePage(getDriver());
        navigateToBaseUrl();
        
        ExtentReportListener.logInfo("Verificando visibilidad del campo de búsqueda");
        Assert.assertTrue(homePage.isSearchVisible());
        
        ExtentReportListener.logPass("Campo de búsqueda verificado exitosamente");
        logger.info("Realizando búsqueda: Selenium");
        homePage.search("Selenium");
        
        waitForPageLoad();
        ExtentReportListener.logPass("Prueba de búsqueda completada exitosamente");
        logger.info("Prueba de búsqueda completada");
    }
}
```

### Prueba API con Modelos POJO
```java
public class APITests extends BaseAPITest {
    @Test(description = "Prueba POST usando modelo User")
    public void createUserTest() {
        // Cargar usuario desde JSON usando modelo POJO
        User user = DataLoader.loadJson("data/user.json", User.class);
        
        ExtentReportListener.logInfo("Usuario cargado: " + user.getName());
        
        // Modificar usuario usando Builder pattern
        User modifiedUser = User.builder()
                .name("John Modified")
                .email("modified@example.com")
                .build();
        
        ExtentReportListener.logInfo("Usuario modificado: " + modifiedUser.getName());
        
        Response response = given()
                .body(modifiedUser)  // Serialización automática a JSON
                .when()
                .post("/users")
                .then()
                .extract().response();
        
        Assert.assertEquals(response.getStatusCode(), 201);
        ExtentReportListener.logPass("Usuario creado exitosamente");
    }
}
```

### Prueba con Reportes Detallados
```java
public class ReportTest extends BaseTest {
    @Test(description = "Prueba exitosa para demostrar reportes")
    public void testSuccessfulExecution() {
        ExtentReportListener.logInfo("Iniciando prueba exitosa");
        
        HomePage homePage = new HomePage(getDriver());
        homePage.navigateToHomePage();
        
        String title = homePage.getPageTitle();
        Assert.assertNotNull(title, "El título no debería ser nulo");
        
        ExtentReportListener.logPass("Verificación de título completada exitosamente");
    }
    
    @Test(description = "Prueba fallida para demostrar capturas de pantalla")
    public void testFailedExecution() {
        ExtentReportListener.logInfo("Iniciando prueba que fallará intencionalmente");
        
        HomePage homePage = new HomePage(getDriver());
        homePage.navigateToHomePage();
        
        ExtentReportListener.logInfo("Ejecutando assertion fallida");
        Assert.fail("Esta prueba falla intencionalmente para demostrar la generación de reportes con screenshots");
    }
}
```

## 📋 Archivos Generados

Después de ejecutar las pruebas, se generarán los siguientes archivos:

```
target/
├── extent-reports/          # Reportes Extent HTML interactivos
│   ├── ExtentReport_20260324_155724.html
│   └── screenshots/         # Screenshots automáticos de fallos
│       ├── testFailedExecution_20260324_155724.png
│       └── ...
├── logs/
│   ├── automation.log       # Log general de ejecución
│   ├── error.log           # Solo errores y excepciones
│   ├── tests.log           # Logs específicos de pruebas
│   └── automation.json     # JSON para integración externa
├── surefire-reports/       # Reportes TestNG tradicionales
│   ├── index.html          # Reporte HTML básico
│   └── emailable-report.html
└── test-classes/           # Clases compiladas de pruebas
```

## ✅ Características Implementadas

1. ✅ **BasePage completa** - 40+ métodos para automatización web
2. ✅ **Sistema de Page Objects** - Estructura escalable y mantenible
3. ✅ **Extent Reports** - Reportes HTML interactivos avanzados
4. ✅ **Sistema de logging dual** - Log4j2 + Extent Reports
5. ✅ **POJOs con Lombok** - Modelos de datos para API
6. ✅ **Screenshots automáticos** - Captura en fallos con timestamps
7. ✅ **DataLoader** - Carga flexible de datos JSON
8. ✅ **Configuración centralizada** - Properties y TestNG
9. ✅ **Ejecución flexible** - Múltiples opciones de comandos
10. ✅ **Ejemplos completos** - Pruebas web, API y reportes

## 🚀 Próximos Pasos

1. Agregar más Page Objects para diferentes secciones
2. Implementar clases de servicio para API endpoints específicos
3. Configurar integración continua (CI/CD)
4. Agregar data-driven testing con Excel/JSON
5. Configurar ejecución paralela avanzada
6. Implementar pruebas de carga con Gatling
7. Agregar integración con JIRA para gestión de defectos

## 📋 Requisitos Previos

- **Java 11+** - JDK instalado y configurado
- **Maven 3.6+** - Build tool y gestión de dependencias
- **Chrome/Firefox** - Navegadores para pruebas web
- **IDE** - IntelliJ IDEA o Eclipse recomendado

## 🎯 Inicio Rápido

```bash
# 1. Instalar dependencias
mvn clean install

# 2. Ejecutar pruebas de demostración
mvn test -Dtest=ReportTest -Dmaven.test.failure.ignore=true

# 3. Ver reportes generados
open target/extent-reports/ExtentReport_$(date +%Y%m%d)_*.html

# 4. Ver logs en tiempo real
tail -f target/logs/automation.log
```

## 📖 Guía de Ejecución Completa

### **Para Desarrollo Rápido**
```bash
# Ejecutar pruebas sin reportes (más rápido)
mvn test -DskipReports=true -Dmaven.test.failure.ignore=true
```

### **Para Ejecución Completa con Reportes**
```bash
# Suite completa con todo habilitado
mvn clean test -Dmaven.test.failure.ignore=true && open target/extent-reports/ExtentReport_$(date +%Y%m%d)_*.html
```

### **Para CI/CD**
```bash
# Ejecución en modo headless para pipelines
mvn test -Dheadless=true -Dmaven.test.failure.ignore=true
```

### **Para Debugging**
```bash
# Ejecutar prueba específica con logs detallados
mvn test -Dtest=ReportTest -Dmaven.test.failure.ignore=true -X
tail -f target/logs/automation.log
```

El framework está completamente funcional y listo para uso en proyectos de automatización web y API con reportes avanzados y logging integral.

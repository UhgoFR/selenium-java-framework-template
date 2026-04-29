# SauceDemo Automation Framework

## Overview

This directory contains the complete automation test suite for SauceDemo e-commerce website using Selenium WebDriver with TestNG framework.

## Test Structure

### Test Files

| File | Description | Test Count |
|------|-------------|------------|
| `LoginTests.java` | Authentication and login functionality | 3 tests |
| `InventoryTests.java` | Product catalog, cart operations, sorting | 9 tests |
| `CheckoutTests.java` | Complete checkout flow and validation | 8 tests |
| `SauceDemoTestSuite.java` | End-to-end critical path scenarios | 7 tests |

### Page Objects

| File | Description |
|------|-------------|
| `LoginPage.java` | Login page functionality |
| `InventoryPage.java` | Product catalog and cart operations |
| `CartPage.java` | Shopping cart management |
| `CheckoutPageStepOne.java` | Checkout information form |
| `CheckoutPageStepTwo.java` | Order summary and confirmation |
| `CheckoutCompletePage.java` | Order completion page |

## Test Categories and Tags

### Available Tags

- **`SauceDemo`** - All SauceDemo tests (27 tests total)
- **`smoke`** - Critical functionality tests (8 tests)
- **`regression`** - Full regression suite (19 tests)
- **`negative`** - Error handling and validation (6 tests)
- **`critical-path`** - Core user journey tests (7 tests)
- **`end-to-end`** - Complete user flows (1 test)

### Test Distribution

```
SauceDemo (27 total)
├── smoke (8)
├── regression (19)
├── negative (6)
├── critical-path (7)
└── end-to-end (1)
```

## Running Tests

### Prerequisites

1. Java 11+ installed
2. Maven configured
3. Chrome browser installed

### Execution Methods

#### Method 1: TestNG XML Files (Recommended)

**Archivos TestNG Disponibles:**

| Archivo | Descripción | Tests | Tiempo Estimado |
|---------|-------------|-------|----------------|
| `testng-smoke.xml` | Tests críticos | 6 tests específicos | ~15-20 segundos |
| `testng-regression.xml` | Suite completa | Todos con grupo `regression` | ~3 minutos |
| `testng-negative.xml` | Manejo de errores | Todos con grupo `negative` | ~1-2 minutos |
| `testng-web.xml` | Todos los tests Web | 5 clases (LoginTests, InventoryTests, etc.) | ~3-4 minutos |
| `testng.xml` | Suite completa | Web + API | ~4-5 minutos |

**Ejecutar Tests por Archivo:**

```bash
# Smoke Tests (6 tests críticos)
mvn clean test -DsuiteXmlFile=testng-smoke.xml -Dbrowser=chrome -Dheadless=false

# Regression Tests (suite completa)
mvn clean test -DsuiteXmlFile=testng-regression.xml -Dbrowser=chrome -Dheadless=false

# Negative Tests (manejo de errores)
mvn clean test -DsuiteXmlFile=testng-negative.xml -Dbrowser=chrome -Dheadless=false

# Todos los tests Web
mvn clean test -DsuiteXmlFile=testng-web.xml -Dbrowser=chrome -Dheadless=false

# Suite completa (Web + API)
mvn clean test -DsuiteXmlFile=testng.xml -Dbrowser=chrome -Dheadless=false
```

#### Method 2: Maven Command Line with Groups (Alternativa)

> **Nota:** Usar archivos XML (Method 1) es recomendado para mejor control y reportes completos.

**Run by Test Type:**

```bash
# Smoke Tests (Critical functionality)
mvn clean test -Dgroups=smoke -Dbrowser=chrome -Dheadless=false

# Regression Tests (Full suite)
mvn clean test -Dgroups=regression -Dbrowser=chrome -Dheadless=false

# Negative Tests (Error scenarios)
mvn clean test -Dgroups=negative -Dbrowser=chrome -Dheadless=false

# All SauceDemo Tests
mvn clean test -Dgroups=SauceDemo -Dbrowser=chrome -Dheadless=false
```


#### Method 3: Run Specific Test Classes

```bash
# Login Tests
mvn clean test -Dtest=LoginTests -Dbrowser=chrome -Dheadless=false

# Inventory Tests
mvn clean test -Dtest=InventoryTests -Dbrowser=chrome -Dheadless=false

# Checkout Tests
mvn clean test -Dtest=CheckoutTests -Dbrowser=chrome -Dheadless=false

# Complete Test Suite
mvn clean test -Dtest=SauceDemoTestSuite -Dbrowser=chrome -Dheadless=false
```

#### Method 4: Run Individual Test Methods

```bash
# Specific test method
mvn clean test -Dtest=SauceDemoTestSuite#testValidLogin -Dbrowser=chrome -Dheadless=false

# Multiple specific methods
mvn clean test -Dtest="SauceDemoTestSuite#testValidLogin,SauceDemoTestSuite#testProductCatalogDisplay" -Dbrowser=chrome -Dheadless=false
```

### Parallel Execution

**Configuración Actual (Thread-Safe):**

Todos los archivos TestNG están configurados con:
- `parallel="classes"` - Las clases se ejecutan en paralelo
- `thread-count="3"` - 3 threads simultáneos
- Thread-safe con `ThreadLocal<WebDriver>`

```xml
<suite name="Smoke Test Suite" parallel="classes" thread-count="3">
```

**Por qué `parallel="classes"`:**
- ✅ Garantiza que `BaseTest.setUp()` se ejecute antes que `@BeforeMethod` de clases hijas
- ✅ Evita `NullPointerException` en inicialización de page objects
- ✅ Mantiene paralelización efectiva
- ✅ Thread-safe con `ThreadLocal<WebDriver>`

**Ajustar Threads:**
Edita el archivo XML y modifica `thread-count`:
```xml
<suite name="..." parallel="classes" thread-count="5">
```

## Test Data

### Data Files Location
- **Login Data:** `src/test/resources/data/SauceDemo/login-data.json`
- **Product Data:** `src/test/resources/data/SauceDemo/products-data.json`
- **Checkout Data:** `src/test/resources/data/SauceDemo/checkout-data.json`

### Data Providers

- **`validLoginData`** - Valid username/password combinations
- **`invalidLoginData`** - Invalid credentials scenarios
- **`lockedUserData`** - Locked user account scenarios
- **`singleProductData`** - Individual product test data
- **`multipleProductData`** - Multiple product scenarios
- **`sortData`** - Product sorting test cases
- **`validCheckoutData`** - Valid checkout information
- **`invalidCheckoutData`** - Invalid checkout scenarios

## Configuration

### Browser Configuration
Tests run in Chrome by default. Configuration can be changed in:
- **testng.xml** - TestNG suite parameters
- **BaseTest.java** - WebDriver initialization

### Driver Initialization
The framework uses a robust driver initialization system:
- **ThreadLocal Storage:** Each test thread has its own driver instance
- **Circular Dependency Prevention:** Built-in protection against infinite loops
- **Fallback Initialization:** Automatic driver creation if not properly initialized
- **Automatic Cleanup:** Drivers are properly closed after each test

### Configuración de Ejecución

**Thread-Safety:**
- `ThreadLocal<WebDriver>` en `BaseTest`
- Cada thread tiene su propia instancia de driver
- Navegación controlada en `@BeforeMethod` de cada clase
- Cierre automático de drivers en `@AfterMethod`

**Paralelización:**
- `parallel="classes"` en todos los archivos TestNG
- `thread-count="3"` por defecto
- Ejecución paralela thread-safe garantizada

### Reporting

**Reportes Generados Automáticamente:**

1. **Extent Reports** (Recomendado)
   - Ubicación: `target/extent-reports/ExtentReport_YYYYMMDD_HHMMSS.html`
   - Dashboard interactivo con gráficas
   - Screenshots automáticos en fallos
   - Logs detallados por test

2. **Surefire Reports**
   - Ubicación: `target/surefire-reports/`
   - Reportes XML/HTML de TestNG
   - Útil para integración CI/CD

**Abrir Extent Report:**
```bash
# macOS
open target/extent-reports/ExtentReport_*.html

# Linux
xdg-open target/extent-reports/ExtentReport_*.html

# Windows
start target\extent-reports\ExtentReport_*.html
```

**Ver Screenshots:**
```bash
open target/extent-reports/screenshots/
```

## Best Practices

### Before Running Tests
1. Ensure SauceDemo website is accessible: https://www.saucedemo.com
2. Check network connectivity
3. Verify Chrome browser is updated
4. Run `mvn clean install` si es la primera vez

### Test Execution Tips
1. **Quick feedback:** `mvn clean test -DsuiteXmlFile=testng-smoke.xml -Dheadless=true`
2. **Full validation:** `mvn clean test -DsuiteXmlFile=testng-regression.xml`
3. **CI/CD:** `mvn clean test -DsuiteXmlFile=testng-smoke.xml -Dheadless=true`
4. **Debugging:** Ejecutar test individual con `-Dtest=ClassName#methodName`
5. **Local development:** Usar `-Dheadless=false` para ver el browser

### Troubleshooting

**Common Issues:**

1. **`NullPointerException` en page objects:**
   - **Causa:** `parallel="methods"` en TestNG XML
   - **Solución:** Usar `parallel="classes"` (ya configurado)

2. **`TimeoutException` o elementos no encontrados:**
   - **Causa:** Navegación no ejecutada correctamente
   - **Solución:** Verificar que `navigateToBaseUrl()` esté en `@BeforeMethod`

3. **Tests ejecutan más de lo esperado:**
   - **Causa:** Archivo TestNG incorrecto
   - **Solución:** Usar `-DsuiteXmlFile=testng-smoke.xml` explícitamente

4. **Browser crashes:**
   - **Solución:** Actualizar Chrome y WebDriverManager

5. **Test failures:**
   - **Solución:** Revisar `target/extent-reports/` y `target/surefire-reports/`

## Test Coverage

### Functional Areas Covered

✅ **Authentication**
- Valid login scenarios
- Invalid credentials handling
- Locked user scenarios

✅ **Product Management**
- Product catalog display
- Product information validation
- Product sorting (A-Z, Z-A, price)
- Add to cart functionality
- Remove from cart

✅ **Shopping Cart**
- Cart item management
- Cart navigation
- Item count validation
- Price calculations

✅ **Checkout Process**
- Multi-step checkout flow
- Form validation
- Price calculation verification
- Order completion
- Error handling

✅ **User Experience**
- Logout functionality
- Navigation flows
- Responsive interactions

### Metrics
- **Total Test Cases:** 27
- **Test Categories:** 6
- **Data Providers:** 8
- **Page Objects:** 6
- **Expected Coverage:** ~85% of user-facing functionality

## Contributing

When adding new tests:

1. Follow existing naming conventions
2. Add appropriate tags: `@Test(groups = {"smoke", "regression", "SauceDemo"})`
3. Include data providers for parameterized tests
4. Update this README with new test information
5. Ensure tests are independent and repeatable
6. Agregar `navigateToBaseUrl()` en `@BeforeMethod` si es necesario
7. Usar `getDriver()` para obtener WebDriver thread-safe
8. Probar en paralelo: `mvn clean test -DsuiteXmlFile=testng-regression.xml`

## Support

For issues or questions:
1. Check test logs in `target/surefire-reports/`
2. Review ExtentReports for detailed execution information
3. Verify test data configuration
4. Check browser compatibility

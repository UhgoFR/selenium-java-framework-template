# 🚀 Guía de Ejecución de Tests por Niveles

Esta guía documenta cómo ejecutar diferentes niveles de pruebas usando grupos de TestNG.

---

## 📋 Niveles de Prueba Disponibles

### 1️⃣ **SMOKE TESTS** (Pruebas de Humo)
- **Propósito:** Validación rápida de funcionalidad crítica
- **Tiempo estimado:** 2-5 minutos
- **Cuándo ejecutar:** Después de cada build, antes de deployment
- **Cobertura:** Tests marcados con `@Test(groups = {"smoke"})`

### 2️⃣ **REGRESSION TESTS** (Pruebas de Regresión)
- **Propósito:** Verificar toda la funcionalidad después de cambios
- **Tiempo estimado:** 10-20 minutos
- **Cuándo ejecutar:** Antes de releases mayores, builds nocturnos
- **Cobertura:** Tests marcados con `@Test(groups = {"regression"})`

### 3️⃣ **NEGATIVE TESTS** (Pruebas Negativas)
- **Propósito:** Validar manejo de errores e inputs inválidos
- **Tiempo estimado:** 5-10 minutos
- **Cuándo ejecutar:** Durante ciclos de QA, antes de releases
- **Cobertura:** Tests marcados con `@Test(groups = {"negative"})`

---

## 🎯 Comandos de Ejecución

### **Archivos TestNG Disponibles**

El framework incluye los siguientes archivos de configuración TestNG:

| Archivo | Descripción | Tests Incluidos | Paralelización |
|---------|-------------|-----------------|----------------|
| `testng-smoke.xml` | Tests de humo (críticos) | 6 tests específicos | `parallel="classes"` |
| `testng-regression.xml` | Suite de regresión completa | Todos con grupo `regression` | `parallel="classes"` |
| `testng-negative.xml` | Tests de manejo de errores | Todos con grupo `negative` | `parallel="classes"` |
| `testng-web.xml` | Todos los tests Web | 5 clases de tests Web | `parallel="classes"` |
| `testng-api.xml` | Todos los tests API | 1 clase de tests API | `parallel="classes"` |
| `testng.xml` | Suite completa (Web + API) | Todos los tests | `parallel="classes"` |

> **Nota Importante:** Todos los archivos usan `parallel="classes"` con `thread-count="3"` para ejecución paralela thread-safe.

---

### **Opción 1: Usando archivos XML separados (RECOMENDADO)**

#### Ejecutar SMOKE Tests (6 tests específicos)
```bash
mvn clean test -DsuiteXmlFile=testng-smoke.xml -Dbrowser=chrome -Dheadless=false
```

#### Ejecutar REGRESSION Tests (por grupo)
```bash
mvn clean test -DsuiteXmlFile=testng-regression.xml -Dbrowser=chrome -Dheadless=false
```

#### Ejecutar NEGATIVE Tests (por grupo)
```bash
mvn clean test -DsuiteXmlFile=testng-negative.xml -Dbrowser=chrome -Dheadless=false
```

#### Ejecutar SOLO Tests WEB
```bash
mvn clean test -DsuiteXmlFile=testng-web.xml -Dbrowser=chrome -Dheadless=false
```

#### Ejecutar SOLO Tests API
```bash
mvn clean test -DsuiteXmlFile=testng-api.xml
```

#### Ejecutar TODOS los Tests (Web + API)
```bash
mvn clean test -DsuiteXmlFile=testng.xml -Dbrowser=chrome -Dheadless=false
```

---

### **Opción 2: Usando grupos con Maven (Alternativa)**

#### Ejecutar solo tests SMOKE
```bash
mvn clean test -Dgroups=smoke -Dbrowser=chrome -Dheadless=false
```

#### Ejecutar solo tests REGRESSION
```bash
mvn clean test -Dgroups=regression -Dbrowser=chrome -Dheadless=false
```

#### Ejecutar solo tests NEGATIVE
```bash
mvn clean test -Dgroups=negative -Dbrowser=chrome -Dheadless=false
```

#### Ejecutar múltiples grupos
```bash
# Smoke + Regression
mvn clean test -Dgroups="smoke,regression" -Dbrowser=chrome -Dheadless=false

# Negative + Regression
mvn clean test -Dgroups="negative,regression" -Dbrowser=chrome -Dheadless=false
```

---

## 🌐 Ejecución en Diferentes Navegadores

### Chrome (por defecto)
```bash
mvn clean test -DsuiteXmlFile=testng-smoke.xml -Dbrowser=chrome -Dheadless=false
```

### Firefox
```bash
mvn clean test -DsuiteXmlFile=testng-smoke.xml -Dbrowser=firefox -Dheadless=false
```

### Headless Mode (Sin interfaz gráfica)
```bash
mvn clean test -DsuiteXmlFile=testng-smoke.xml -Dbrowser=chrome -Dheadless=true
```

---

## 📊 Generación de Reportes

Todos los comandos anteriores generan automáticamente:

### **Extent Report**
- **Ubicación:** `target/extent-reports/ExtentReport_YYYYMMDD_HHMMSS.html`
- **Contenido:** Dashboard interactivo con resultados, gráficas, logs y screenshots

### **Surefire Reports**
- **Ubicación:** `target/surefire-reports/`
- **Contenido:** Reportes XML y TXT de TestNG

### **Abrir el Extent Report**
```bash
# En macOS
open target/extent-reports/ExtentReport_*.html

# En Linux
xdg-open target/extent-reports/ExtentReport_*.html

# En Windows
start target/extent-reports/ExtentReport_*.html
```

---

## ⚙️ Configuración de Paralelización

### **Configuración Actual (Thread-Safe)**

Todos los archivos XML están configurados con:
- **Modo paralelo:** `parallel="classes"` ✅
- **Thread count:** `thread-count="3"`
- **Estrategia:** Las clases se ejecutan en paralelo, los métodos dentro de cada clase en serie

> **Importante:** Se cambió de `parallel="methods"` a `parallel="classes"` para garantizar que `BaseTest.setUp()` se ejecute antes que los `@BeforeMethod` de las clases hijas.

### Ajustar el número de threads:

Edita el archivo XML correspondiente y modifica:
```xml
<suite name="..." parallel="classes" thread-count="5">
```

**Recomendaciones:**
- **Máquina local:** 3-4 threads
- **CI/CD con más recursos:** 5-8 threads
- **Debugging:** 1 thread o `parallel="false"`

### **Por qué `parallel="classes"`?**

- ✅ Garantiza orden correcto de `@BeforeMethod` en herencia
- ✅ Evita `NullPointerException` en inicialización de page objects
- ✅ Mantiene paralelización efectiva entre clases de tests
- ✅ Thread-safe con `ThreadLocal<WebDriver>`

---

## 🔧 Ejemplos de Uso Común

### 1. Quick Smoke Test antes de commit
```bash
mvn clean test -DsuiteXmlFile=testng-smoke.xml -Dbrowser=chrome -Dheadless=true
```

### 2. Full Regression antes de release
```bash
mvn clean test -DsuiteXmlFile=testng-regression.xml -Dbrowser=chrome -Dheadless=false
```

### 3. Negative Tests en Firefox
```bash
mvn clean test -DsuiteXmlFile=testng-negative.xml -Dbrowser=firefox -Dheadless=false
```

### 4. CI/CD Pipeline (Headless)
```bash
mvn clean test -DsuiteXmlFile=testng-regression.xml -Dbrowser=chrome -Dheadless=true
```

---

## 📝 Estructura de Archivos TestNG

```
proyecto/
├── testng.xml              # Suite completa (Web + API)
├── testng-smoke.xml        # 6 tests smoke específicos
├── testng-regression.xml   # Tests con grupo @Test(groups={"regression"})
├── testng-negative.xml     # Tests con grupo @Test(groups={"negative"})
├── testng-web.xml          # Todos los tests Web (5 clases)
└── testng-api.xml          # Todos los tests API (1 clase)
```

### **Diferencias Clave:**

**testng-smoke.xml:**
- Lista métodos específicos con `<include name="testMethodName"/>`
- Ejecución rápida: ~15-20 segundos
- 6 tests críticos seleccionados manualmente

**testng-regression.xml / testng-negative.xml:**
- Usa filtros de grupos `<include name="regression"/>`
- Ejecuta todos los tests marcados con ese grupo
- Más flexible pero depende de anotaciones correctas

**testng-web.xml / testng-api.xml:**
- Separa tests por tipo (Web vs API)
- Útil para ejecutar solo un tipo de pruebas
- Web requiere browser, API no

---

## 🏷️ Grupos Disponibles en el Framework

| Grupo | Descripción | Ejemplo de Tests |
|-------|-------------|------------------|
| `smoke` | Tests críticos de funcionalidad básica | Login válido, navegación principal |
| `regression` | Tests completos de todas las features | Todos los flujos de usuario |
| `negative` | Tests de manejo de errores | Login inválido, datos incorrectos |
| `SauceDemo` | Tests específicos de SauceDemo | Todos los tests de la aplicación |

---

## 🎯 Estrategia de Testing Recomendada

### **Durante Desarrollo:**
```bash
# Ejecutar smoke tests frecuentemente
mvn test -DsuiteXmlFile=testng-smoke.xml -Dheadless=true
```

### **Antes de Pull Request:**
```bash
# Ejecutar regression completa
mvn clean test -DsuiteXmlFile=testng-regression.xml -Dheadless=false
```

### **En CI/CD Pipeline:**
```bash
# Stage 1: Smoke tests (rápido)
mvn test -DsuiteXmlFile=testng-smoke.xml -Dheadless=true

# Stage 2: Regression tests (completo)
mvn test -DsuiteXmlFile=testng-regression.xml -Dheadless=true

# Stage 3: Negative tests (validación de errores)
mvn test -DsuiteXmlFile=testng-negative.xml -Dheadless=true
```

---

## 🐛 Troubleshooting

### Problema: Tests no se ejecutan con grupos
**Solución:** Usa la opción 1 (archivos XML separados) en lugar de `-Dgroups`

### Problema: Se ejecutan más tests de los esperados
**Causa:** Anteriormente el pom.xml tenía configurado `testng.xml` por defecto, ignorando `-DsuiteXmlFile`.
**Solución:** Ahora el pom.xml usa `${suiteXmlFile}` dinámicamente, por lo que `-DsuiteXmlFile=testng-smoke.xml` ejecutará solo los 6 tests de smoke configurados.

### Problema: Tests fallan con `NullPointerException` en paralelo
**Causa:** `parallel="methods"` no garantiza que `BaseTest.setUp()` se ejecute antes que `@BeforeMethod` de clases hijas.
**Solución:** Se cambió a `parallel="classes"` en todos los archivos TestNG.

### Problema: `TimeoutException` o elementos no encontrados
**Causa:** `BaseTest.setUp()` no navegaba a la URL base correctamente en paralelo.
**Solución:** Se movió `navigateToBaseUrl()` a los `@BeforeMethod` de cada clase de test.

### Problema: Extent Report no se genera
**Solución:** Asegúrate de usar archivos XML con listeners configurados

### Problema: Tests fallan en paralelo
**Solución:** Reduce `thread-count` a 1 o usa `parallel="false"` para debugging

---

## 📚 Referencias

- **TestNG Groups:** https://testng.org/doc/documentation-main.html#test-groups
- **Maven Surefire:** https://maven.apache.org/surefire/maven-surefire-plugin/
- **Extent Reports:** https://www.extentreports.com/

---

---

## 🔄 Cambios Recientes (2026-03-29)

### **Mejoras en Paralelización:**
- ✅ Cambiado de `parallel="methods"` a `parallel="classes"` en todos los archivos TestNG
- ✅ Navegación movida de `BaseTest.setUp()` a `@BeforeMethod` de clases hijas
- ✅ Eliminados problemas de `NullPointerException` y `TimeoutException` en ejecución paralela

### **Nuevos Archivos:**
- ✅ `testng-web.xml` - Solo tests Web
- ✅ `testng-api.xml` - Solo tests API

### **Configuración:**
- ✅ `pom.xml` usa `${suiteXmlFile}` dinámicamente
- ✅ Todos los archivos usan `thread-count="3"` con `parallel="classes"`

---

**Última actualización:** 2026-03-29

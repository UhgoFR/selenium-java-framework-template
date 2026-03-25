package com.automation.base;

import com.automation.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.*;

public class BaseAPITest {
    // Thread-safe para ejecución en paralelo
    private static ThreadLocal<RequestSpecification> requestSpecThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<ResponseSpecification> responseSpecThreadLocal = new ThreadLocal<>();
    
    // Variables de instancia para compatibilidad
    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    /**
     * Configura el entorno de Rest Assured para todas las pruebas API de la suite.
     * 
     * <p>Este método se ejecuta una sola vez al inicio de todas las pruebas API y se encarga de:
     * <ul>
     *   <li>Establecer la URI base desde la configuración centralizada</li>
     *   <li>Configurar headers estándar para todas las solicitudes (JSON)</li>
     *   <li>Activar logging automático de solicitudes y respuestas</li>
     *   <li>Establecer validaciones globales de respuesta</li>
     *   <li>Aplicar la configuración a RestAssured globalmente</li>
     * </ul>
     * 
     * <p>Configuración aplicada:
     * <ul>
     *   <li><strong>Base URI:</strong> Obtenida desde ConfigManager</li>
     *   <li><strong>Accept:</strong> application/json</li>
     *   <li><strong>Content-Type:</strong> application/json</li>
     *   <li><strong>Response Time:</strong> Menor a 30 segundos</li>
     *   <li><strong>Logging:</strong> Solicitudes y respuestas en consola</li>
     * </ul>
     */
    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        System.out.println("BaseAPITest @BeforeSuite - Configuring RestAssured for parallel execution");
        // Configuración global básica
        RestAssured.baseURI = ConfigManager.getApiBaseUrl();
        System.out.println("RestAssured.baseURI set to: " + RestAssured.baseURI);
    }
    
    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        System.out.println("BaseAPITest @BeforeClass for thread: " + Thread.currentThread().getId());
        initializeSpecifications();
    }
    
    @BeforeMethod(alwaysRun = true)
    public void setUpMethod() {
        System.out.println("BaseAPITest @BeforeMethod for thread: " + Thread.currentThread().getId());
        // Asegurar que las especificaciones estén configuradas para este thread
        if (requestSpecThreadLocal.get() == null) {
            initializeSpecifications();
        }
        // Actualizar variables de instancia para compatibilidad
        requestSpec = requestSpecThreadLocal.get();
        responseSpec = responseSpecThreadLocal.get();
    }

    /**
     * Inicializa las especificaciones de Request y Response para el thread actual.
     * Este método es thread-safe y crea instancias separadas para cada thread.
     */
    private void initializeSpecifications() {
        System.out.println("Initializing specifications for thread: " + Thread.currentThread().getId());
        
        // Crear RequestSpecification específica para este thread
        RequestSpecification threadRequestSpec = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getApiBaseUrl())
                .setAccept("application/json")
                .setContentType("application/json")
                .addFilter(RequestLoggingFilter.logRequestTo(System.out))
                .addFilter(ResponseLoggingFilter.logResponseTo(System.out))
                .build();
        
        // Crear ResponseSpecification específica para este thread
        ResponseSpecification threadResponseSpec = new ResponseSpecBuilder()
                .expectResponseTime(Matchers.lessThan(30000L))
                .build();
        
        // Almacenar en ThreadLocal
        requestSpecThreadLocal.set(threadRequestSpec);
        responseSpecThreadLocal.set(threadResponseSpec);
        
        // Actualizar variables de instancia
        requestSpec = threadRequestSpec;
        responseSpec = threadResponseSpec;
        
        System.out.println("Specifications initialized for thread: " + Thread.currentThread().getId());
    }
    
    /**
     * Limpia las especificaciones del thread actual después de cada test.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDownMethod() {
        System.out.println("BaseAPITest @AfterMethod for thread: " + Thread.currentThread().getId());
        // Limpiar ThreadLocals para evitar memory leaks
        requestSpecThreadLocal.remove();
        responseSpecThreadLocal.remove();
    }
    
    /**
     * Limpia las especificaciones después de cada clase.
     */
    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        System.out.println("BaseAPITest @AfterClass for thread: " + Thread.currentThread().getId());
        requestSpecThreadLocal.remove();
        responseSpecThreadLocal.remove();
    }
    
    /**
     * Proporciona acceso a la especificación de solicitud configurada para el thread actual.
     * 
     * @return RequestSpecification thread-safe configurada con headers y filtros base
     */
    protected RequestSpecification getRequestSpecification() {
        RequestSpecification spec = requestSpecThreadLocal.get();
        if (spec == null) {
            initializeSpecifications();
            spec = requestSpecThreadLocal.get();
        }
        return spec;
    }

    /**
     * Proporciona acceso a la especificación de respuesta configurada.
     * 
     * <p>Este método devuelve la ResponseSpecification que contiene las validaciones
     * globales aplicadas a todas las respuestas HTTP incluyendo:
     * <ul>
     *   <li>Validación de tiempo de respuesta (menor a 30 segundos)</li>
     *   <li>Configuración base para validaciones adicionales</li>
     *   <li>Criterios de respuesta estándar</li>
     * </ul>
     * 
     * <p>Uso típico cuando se necesita agregar validaciones específicas a una prueba:
     * <pre>
     * given()
     *     .when()
     *     .get("/users/1")
     *     .then()
     *     .spec(getResponseSpecification())
     *     .statusCode(200)
     *     .body("name", Matchers.notNullValue())
     *     .body("email", Matchers.containsString("@"));
     * </pre>
     * 
     * <p>Nota: Esta especificación se aplica automáticamente a todas las llamadas
     * mediante RestAssured.responseSpecification, pero el método permite acceso
     * directo si se necesita personalización.
     * 
     * @return ResponseSpecification configurada con validaciones globales
     */
    protected ResponseSpecification getResponseSpecification() {
        return responseSpec;
    }
}

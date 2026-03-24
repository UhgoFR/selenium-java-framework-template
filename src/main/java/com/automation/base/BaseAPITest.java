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
import org.testng.annotations.BeforeSuite;

public class BaseAPITest {
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
    @BeforeSuite
    public void setUp() {
        RestAssured.baseURI = ConfigManager.getApiBaseUrl();

        requestSpec = new RequestSpecBuilder()
                .setAccept("application/json")
                .setContentType("application/json")
                .addFilter(RequestLoggingFilter.logRequestTo(System.out))
                .addFilter(ResponseLoggingFilter.logResponseTo(System.out))
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectResponseTime(Matchers.lessThan(30000L))
                .build();

        RestAssured.requestSpecification = requestSpec;
        RestAssured.responseSpecification = responseSpec;
    }

    /**
     * Proporciona acceso a la especificación de solicitud configurada.
     * 
     * <p>Este método devuelve la RequestSpecification que contiene toda la configuración
     * base para las solicitudes HTTP incluyendo:
     * <ul>
     *   <li>Headers predefinidos (Accept, Content-Type)</li>
     *   <li>Filtros de logging para debugging</li>
     *   <li>Configuración de base URI</li>
     * </ul>
     * 
     * <p>Uso típico cuando se necesita personalizar una solicitud específica:
     * <pre>
     * RequestSpecification customSpec = getRequestSpecification()
     *     .header("Authorization", "Bearer token")
     *     .pathParam("userId", 123);
     * 
     * given()
     *     .spec(customSpec)
     *     .when()
     *     .get("/users/{userId}")
     *     .then()
     *     .statusCode(200);
     * </pre>
     * 
     * @return RequestSpecification configurada con headers y filtros base
     */
    protected RequestSpecification getRequestSpecification() {
        return requestSpec;
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

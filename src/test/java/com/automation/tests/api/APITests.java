package com.automation.tests.api;

import com.automation.base.BaseAPITest;
import com.automation.models.User;
import com.automation.models.Post;
import com.automation.models.Address;
import com.automation.models.Geo;
import com.automation.models.Company;
import com.automation.utils.DataLoader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class APITests extends BaseAPITest {

    @Test(description = "Ejemplo de prueba GET básica", groups = "ApiTest")
    public void basicGetTest() {
        Response response = given()
                .when()
                .get("/users/1")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "El código de estado no es 200");
        
        System.out.println("Prueba GET completada exitosamente");
        System.out.println("Response: " + response.asString());
    }

    @Test(description = "Prueba POST usando modelo User y archivo JSON", groups = "ApiTest")
    public void createUserTest() {
        // Cargar datos desde archivo JSON usando modelo User
        User user = DataLoader.loadJson("data/user.json", User.class);
        
        System.out.println("Usuario creado desde JSON: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Dirección: " + user.getAddress().getCity());
        System.out.println("Compañía: " + user.getCompany().getName());

        Response response = given()
                .body(user)  // El objeto User se serializa automáticamente a JSON
                .when()
                .post("/users")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 201, "El código de estado no es 201");
        
        System.out.println("Prueba POST completada exitosamente");
        System.out.println("Response: " + response.asString());
    }

    @Test(description = "Prueba POST usando archivo JSON como string", groups = "ApiTest")
    public void createPostTest() {
        // Cargar JSON como string para casos donde se necesita el JSON crudo
        String postJson = DataLoader.loadJsonAsString("data/post.json");
        
        System.out.println("Post JSON cargado: " + postJson);

        Response response = given()
                .body(postJson)  // Enviar JSON como string
                .when()
                .post("/posts")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 201, "El código de estado no es 201");
        
        System.out.println("Prueba POST completada exitosamente");
        System.out.println("Response: " + response.asString());
    }

    @Test(description = "Prueba POST usando modelo Post", groups = "ApiTest")
    public void createPostModelTest() {
        // Crear objeto Post usando Builder pattern
        Post post = Post.builder()
                .userId(1)
                .title("Nuevo Post de Prueba")
                .body("Este es el contenido del post creado desde el modelo Post")
                .build();
        
        System.out.println("Post creado desde modelo: " + post.getTitle());

        Response response = given()
                .body(post)  // El objeto Post se serializa automáticamente
                .when()
                .post("/posts")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 201, "El código de estado no es 201");
        
        System.out.println("Prueba POST completada exitosamente");
        System.out.println("Response: " + response.asString());
    }

    @Test(description = "Prueba con validación de respuesta usando modelo User", groups = "ApiTest")
    public void getUserAndValidateTest() {
        Response response = given()
                .when()
                .get("/users/1")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "El código de estado no es 200");
        
        // Deserializar respuesta a modelo User
        User user = response.as(User.class);
        
        System.out.println("Usuario obtenido desde API: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        Assert.assertNotNull(user.getId(), "El ID del usuario no debería ser nulo");
        Assert.assertNotNull(user.getName(), "El nombre del usuario no debería ser nulo");
        Assert.assertTrue(user.getEmail().contains("@"), "El email debería contener @");
    }

    @Test(description = "Prueba con JSONPlaceholder API", groups = "ApiTest")
    public void jsonPlaceholderTest() {
        given()
                .baseUri("https://jsonplaceholder.typicode.com")
        .when()
                .get("/posts/1")
        .then()
                .statusCode(200)
                .log().all();
        
        System.out.println("Prueba con JSONPlaceholder completada exitosamente");
    }

    @Test(description = "Prueba PUT usando modelo User", groups = "ApiTest")
    public void updateUserTest() {
        // Crear usuario usando Builder pattern
        User user = User.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .username("johnupdated")
                .phone("1-770-736-8031")
                .website("johnupdated.org")
                .address(Address.builder()
                        .street("123 Updated St")
                        .city("Updated City")
                        .zipcode("10002")
                        .geo(Geo.builder()
                                .lat("40.7129")
                                .lng("-74.0061")
                                .build())
                        .build())
                .company(Company.builder()
                        .name("Updated Enterprises")
                        .catchPhrase("Updated solutions")
                        .bs("updated consulting")
                        .build())
                .build();
        
        System.out.println("Usuario actualizado: " + user.getName());

        Response response = given()
                .body(user)
                .when()
                .put("/users/1")
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "El código de estado no es 200");
        
        System.out.println("Prueba PUT completada exitosamente");
        System.out.println("Response: " + response.asString());
    }
}

import config.Config;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DummyTest {

    public static RequestSpecification request(String cookie) {
        RequestSpecification spec = given()
                .log().ifValidationFails().
                baseUri(Config.BASE_URL).
                contentType(ContentType.JSON);

        if(cookie != null) {
            spec.cookie("session", cookie);
        }
            return spec;
    }

    String username = "test1";
    String password = "1234";

    String login(String username, String password) {
        return request(null)
                .body("""
                        {
                        "username": "%s",
                        "password": "%s"
                        }
                        """.formatted(username, password))
                .log().all()
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .log().all()
                .extract()
                .cookie("session");
    }

    ValidatableResponse getCategories(String cookie) {
        return request(cookie)
                .log().all()
                .when()
                .get("/api/categories")
                .then()
                .log().all();
    }

    @Test
    void loggingIn() {
        System.out.println(login(username, password));
    }

    @Test
    void getCategoriesWithValidCookie() {
        getCategories(login(username, password))
                .statusCode(200);
    }

    @Test
    void getCategoriesWithInvalidCookie() {
        getCategories("")
                .statusCode(401)
                .body("error", equalTo("Not logged in."));
    }

    @Test
    void getCategoriesAndValidateId() {
        //ValidatableResponse performs checks straight on the values
        getCategories(login(username, password))
                .statusCode(200)
                .body("find { it.id == 'study' }.id", equalTo("study"));
    }

    @Test
    void getCategoriesAndExtractName() {
        //ExtractableResponse is used to take out a single value out of the response
        String name = getCategories(login(username, password))
                .statusCode(200)
                .extract()
                .path("find { it.id == 'study'}.name");

        assertEquals("Study", name);
    }

    @Test
    void getCategoryResponse() {
        //Response contains the whole response, status code, header and the body
        Response categoryResponse = getCategories(login(username, password))
                .statusCode(200)
                .extract()
                .response();

        System.out.println("---------------------------------");
        System.out.println(categoryResponse.statusCode());
        System.out.println(categoryResponse.contentType());
        System.out.println(categoryResponse.body().asString());
    }

    @Test
    void getNumberOfCategoriesWithMoreThanFiftySessions() {
        Response response = getCategories(login(username, password)).extract().response();

        int numberOfCategoriesWithMoreThanFiftySessions = response.path("findAll { it.sessionCount > 50 }.size()");
        assertEquals(2, numberOfCategoriesWithMoreThanFiftySessions);

        System.out.println("----------------------");
        Object one = response.path("findAll { it.sessionCount > 50 }");
        Object two = response.path("findAll { it.sessionCount > 50 }.sessionCount");
        Object three = response.path("findAll { it.sessionCount > 50 }.size()");
        Object four = response.path("findAll { it.sessionCount > 50 }.size");
        System.out.println(one);
        System.out.println(two);
        System.out.println(three);
        System.out.println(four);
    }

}

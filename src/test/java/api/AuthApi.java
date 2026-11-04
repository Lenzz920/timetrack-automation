package api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static api.BaseRequest.request;
import static io.restassured.RestAssured.given;

public class AuthApi {

    public static String login(String username, String password) {
        return loginResponse(username, password)
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().cookie("session");
    }

    public static Response loginResponse(String username, String password) {
        return request(null)
                .body("""
                        {
                        "username": "%s",
                        "password": "%s"
                        }
                        """.formatted(username, password))
                .when()
                .post("/api/auth/login");
    }
}
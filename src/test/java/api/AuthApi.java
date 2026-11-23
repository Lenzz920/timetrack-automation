package api;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static api.BaseRequest.request;

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

    public static ValidatableResponse logout(String cookie) {
        return request(cookie)
                .when()
                .post("/api/auth/logout")
                .then()
                .log().ifValidationFails();
    }

}
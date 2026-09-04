package api;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

public class AuthApi {

    public static String login(String username, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
{
"username": "%s",
"password": "%s"
}
""".formatted(username, password))
                .when()
                .post("http://localhost:3000/api/auth/login")
                .then()
                .statusCode(200)
                .extract().cookie("session");
    }

}

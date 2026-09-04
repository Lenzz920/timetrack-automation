package api;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class SessionsApi {

    String cookie;

    public SessionsApi(String cookie) {
        this.cookie = cookie;
    }

    public String createSession() {
        return given()
                .contentType(ContentType.JSON)
                .cookie("session", cookie)
                .body("""
{
"date":"2026-09-01",
"start":"09:00",
"end":"10:00",
"categoryName":"study",
"note":"to be deleted"
}
""")
                .post("http://localhost:3000/api/sessions")
                .then()
                .statusCode(200)
                .extract().path("id");
    }

    public ValidatableResponse getSession(String id) {
        return given()
                .cookie("session", cookie)
                .get("http://localhost:3000/api/sessions/" + id)
                .then();
    }

}

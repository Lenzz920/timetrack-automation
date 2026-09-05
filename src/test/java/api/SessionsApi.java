package api;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class SessionsApi {

    String cookie;
    private static final String BASE = "http://localhost:3000";
    private static final String SESSIONS = BASE + "/api/sessions/";

    public SessionsApi(String cookie) {
        this.cookie = cookie;
    }

    public String createSession() {
        return request()
                .contentType(ContentType.JSON)
                .body("""
{
"date":"2026-09-05",
"start":"09:00",
"end":"10:00",
"categoryName":"study",
"note":"to be deleted"
}
""")
                .post(BASE + "/api/sessions")
                .then()
                .statusCode(200)
                .extract().path("id");
    }

    public ValidatableResponse deleteSession(String id) {
        return request()
                .delete(SESSIONS  + id)
                .then();
    }

    public ValidatableResponse getSession(String id) {
        return request()
                .get(SESSIONS + id)
                .then();
    }

    public ValidatableResponse getAllSessions() {
        return request()
                .get( BASE + "/api/data")
                .then();
    }

    public RequestSpecification request() {
        return given().cookie("session", cookie);
    }

}

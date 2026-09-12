package api;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class SessionsApi {

    private String cookie;
    private static final String BASE = "http://localhost:3000";
    private static final String SESSIONS = BASE + "/api/sessions/";
    private String toJson(SessionData sessionData) {
        return """
{
"date":"%s",
"start":"%s",
"end":"%s",
"note":"%s",
"categoryName":"%s"
}
""".formatted(sessionData.date(), sessionData.start(), sessionData.end(), sessionData.note(), sessionData.categoryName());
    }

    public SessionsApi(String cookie) {
        this.cookie = cookie;
    }



    public String createSession(SessionData sessionData) {
        return postSession(sessionData)
                .statusCode(200)
                .extract().path("id");
    }

    public String createSession() {
        return request()
                .contentType(ContentType.JSON)
                .body("""
{
"date":"2026-09-05",
"start":"09:00",
"end":"10:00",
"note":"to be deleted",
"categoryName":"study"
}
""")
                .post(BASE + "/api/sessions")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().path("id");
    }

    public ValidatableResponse postSession(SessionData sessionData) {
        return request()
                .contentType(ContentType.JSON)
                .body(toJson(sessionData))
                .post(SESSIONS)
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse deleteSession(String id) {
        return request()
                .delete(SESSIONS  + id)
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse getSession(String id) {
        return request()
                .get(SESSIONS + id)
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse getAllSessions() {
        return request()
                .get( BASE + "/api/data")
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse updateSession(String id, SessionData sessionData) {
        return request()
                .contentType(ContentType.JSON)
                .body(toJson(sessionData))
                .put(SESSIONS + id)
                .then()
                .log().ifValidationFails();
    }

    public RequestSpecification request() {
        return given().cookie("session", cookie);
    }

}

package api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class SessionsApi {

    private final String cookie;
    private static final String BASE = "http://localhost:3000";
    private static String toJson(SessionData sessionData) {
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
                .post("/api/sessions")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().path("id");
    }

    public ValidatableResponse postSession(SessionData sessionData) {
        return postSessionRaw(toJson(sessionData));
    }

    public ValidatableResponse postSessionRaw(String body) {
        return request()
                .contentType(ContentType.JSON)
                .body(body)
                .post("/api/sessions/")
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse deleteSession(String id) {
        return request()
                .delete("/api/sessions/{id}", id)
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse getSession(String id) {
        return request()
                .get("/api/sessions/{id}", id)
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse getAllSessions() {
        return request()
                .get( "/api/data")
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse updateSession(String id, SessionData sessionData) {
        return updateSessionRaw(id, toJson(sessionData));
    }

    public ValidatableResponse updateSessionRaw(String id, String body) {
        return request()
                .contentType(ContentType.JSON)
                .body(body)
                .put("/api/sessions/{id}", id)
                .then()
                .log().ifValidationFails();
    }

    public Response whoAmI() {
        return request()
                .get("/api/auth/me");
    }

        public RequestSpecification request () {
            RequestSpecification spec = given().baseUri(BASE);
            if (cookie != null) {
                spec.cookie("session", cookie);
            }
            return spec;
        }
    }

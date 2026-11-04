package api;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static api.BaseRequest.request;

public class SessionsApi {

    private final String COOKIE;

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
        this.COOKIE = cookie;
    }

    public String createSession(SessionData sessionData) {
        return postSession(sessionData)
                .statusCode(200)
                .extract().path("id");
    }

    public String createSession() {
        return request(COOKIE)
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
        return request(COOKIE)
                .body(body)
                .post("/api/sessions/")
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse deleteSession(String id) {
        return request(COOKIE)
                .delete("/api/sessions/{id}", id)
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse getSession(String id) {
        return request(COOKIE)
                .get("/api/sessions/{id}", id)
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse getAllSessions() {
        return request(COOKIE)
                .get("/api/data")
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse updateSession(String id, SessionData sessionData) {
        return updateSessionRaw(id, toJson(sessionData));
    }

    public ValidatableResponse updateSessionRaw(String id, String body) {
        return request(COOKIE)
                .body(body)
                .put("/api/sessions/{id}", id)
                .then()
                .log().ifValidationFails();
    }

    public Response whoAmI() {
        return request(COOKIE)
                .get("/api/auth/me");
    }
}

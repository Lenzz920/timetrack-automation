package api;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class WeekNotesApi {

    private String cookie;
    private static final String BASE = "http://localhost:3000/api/week-notes/";

    public WeekNotesApi(String cookie) {
        this.cookie = cookie;
    }

    public ValidatableResponse getWeekNotes(String date) {
        return given()
                .cookie("session", cookie)
                .get(BASE + date)
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse updateWeekNotes(String date, String noteText) {
        return given()
                .cookie("session", cookie)
                .contentType(ContentType.JSON)
                .body("""
                        {
                        "text": "%s"
                        }
                        """.formatted(noteText))
                .put(BASE + date)
                .then()
                .log().ifValidationFails();
    }

}

package api;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

public class WeekNotesApi {

    private final String COOKIE;

    public WeekNotesApi(String cookie) {
        this.COOKIE = cookie;
    }

    public ValidatableResponse getWeekNotes(String date) {
        return BaseRequest.request(COOKIE)
                .get("/api/week-notes/{weekStart}", date)
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse updateWeekNotes(String date, String noteText) {
        return BaseRequest.request(COOKIE)
                .contentType(ContentType.JSON)
                .body("""
                        {
                        "text": "%s"
                        }
                        """.formatted(noteText))
                .put("/api/week-notes/{weekStart}", date)
                .then()
                .log().ifValidationFails();
    }

}
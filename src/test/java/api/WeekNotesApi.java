package api;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

public class WeekNotesApi {

    private String cookie;

    public WeekNotesApi(String cookie) {
        this.cookie = cookie;
    }

    public ValidatableResponse getWeekNotes(String date) {
        return BaseRequest.request(cookie)
                .get("/api/week-notes/{weekStart}", date)
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse updateWeekNotes(String date, String noteText) {
        return BaseRequest.request(cookie)
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
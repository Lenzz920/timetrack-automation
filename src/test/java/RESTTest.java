import api.AuthApi;
import api.SessionData;
import api.SessionsApi;
import api.WeekNotesApi;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class RESTTest {

    SessionsApi owner;
    SessionsApi nonOwner;
    private static final ResponseSpecification jsonResponse = new ResponseSpecBuilder().expectContentType(ContentType.JSON).build();

    @BeforeAll
    static void specs() {
        RestAssured.responseSpecification = jsonResponse;
    }

    @BeforeEach
    void setUp() {
        owner = new SessionsApi(AuthApi.login("test1", "1234"));
        nonOwner = new SessionsApi(AuthApi.login("test2", "1234"));
    }

    @ParameterizedTest(name = "{1} to {2} -> {3}")
    @CsvSource({
            "2026-09-09, 09:00, 10:00, 200",
            "2026-09-09, 10:00, 09:00, 400",
            "2026-09-09, 10:00, 10:00, 400"
    })
    void createSessionHonoursTimeRule(String date, String startTime, String endTime, int expectedStatusCode) {
        SessionData sessionData = new SessionData(date, startTime, endTime, "param test", "Study");
        owner.postSession(sessionData)
                .statusCode(expectedStatusCode);
    }

    @Test
    void getSessionReturnsCreatedFields() {
        SessionData ownerSessionData = new SessionData("2026-09-07", "09:00", "10:00", "dynamic test", "Study");
        String id = owner.createSession(ownerSessionData);
        assertSessionMatches(owner.getSession(id), id, ownerSessionData);
    }

    @Test
    void getSessionThatWasDeleted() {
        String id = owner.createSession();
        owner.getSession(id)
        .statusCode(200);
        owner.deleteSession(id);
        owner.getSession(id)
                .statusCode(404);
    }

    @Test
    void createdSessionAppearsInGetAll() {
        String id = owner.createSession();
        List<String> ids = owner.getAllSessions().extract().path("sessions.id");
        assertTrue(ids.contains(id));
    }

    @Test
    void deletedSessionIsGoneFromGetAll() {
        String id = owner.createSession();
        owner.deleteSession(id);
        List<String> ids = owner.getAllSessions().extract().path("sessions.id");
        assertFalse(ids.contains(id));
    }

    @Test
    void cannotDeleteSessionAsANonOwner() {
       String id = owner.createSession();
       nonOwner.deleteSession(id)
               .statusCode(404);
       owner.getSession(id)
               .statusCode(200);
    }

    @Test
    void cannotReadSessionAsANonOwner() {
        String id = owner.createSession();
        nonOwner.getSession(id)
                .statusCode(404);
    }

    @Test
    void cannotUpdateSessionAsANonOwner() {
        SessionData ownerSessionData = new SessionData("2026-09-07", "09:00", "10:00", "shouldn't be changed", "Study");
        SessionData nonOwnerSessionData = new SessionData("2026-10-06", "07:30", "12:40", "nonOwner", "Work");

        String id = owner.createSession(ownerSessionData);
        nonOwner.updateSession(id, nonOwnerSessionData)
                .statusCode(404);

        assertSessionMatches(owner.getSession(id), id, ownerSessionData);
    }

    @Test
    void canCreateWeekNoteAndReadItAsOwner() {
        String date = "2026-09-14";
        String note = "this is a note generated from test";
        WeekNotesApi owner = new WeekNotesApi(AuthApi.login("test1", "1234"));

        owner.updateWeekNotes(date, note);
        owner.getWeekNotes(date)
                .statusCode(200)
                .body("text", equalTo(note));
    }

    @Test
    void sessionGetsUpdated() {
        SessionData originalSessionData = new SessionData("2026-09-07", "09:00", "10:00", "dynamic test", "Study");
        SessionData updatedSessionData = new SessionData("2026-10-12", "12:34", "13:45", "new note", "Work");

        String id = owner.createSession(originalSessionData);
        owner.updateSession(id, updatedSessionData)
                .statusCode(200);

        assertSessionMatches(owner.getSession(id), id, updatedSessionData);
    }

    private void assertSessionMatches(ValidatableResponse response, String id, SessionData expected) {
        var body = response.statusCode(200).extract();
        assertEquals(id, body.path("id"));
        assertEquals(expected.date(), body.path("date"));
        assertEquals(expected.start(), body.path("start"));
        assertEquals(expected.end(), body.path("end"));
        assertEquals(expected.note(), body.path("note"));
        assertEquals(expected.categoryName(), body.path("categoryName"));
    }

}

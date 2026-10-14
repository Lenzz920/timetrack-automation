import api.AuthApi;
import api.SessionData;
import api.SessionsApi;
import api.WeekNotesApi;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static api.AuthApi.login;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Random.class)
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
        owner = new SessionsApi(login("test1", "1234"));
        nonOwner = new SessionsApi(login("test2", "1234"));
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

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "invalid cookie, invalidCookie",
            "no cookie,"
    })
    void invalidOrMissingCookieIsNotAccepted(String caseName, String cookie) {
        SessionsApi notLoggedInUser = new SessionsApi(cookie);
        Response response = notLoggedInUser.whoAmI();
        assertEquals(401, response.getStatusCode());
        assertEquals("Not logged in.", response.body().path("error"));
    }

    @Test
    void updateIsIdempotent() {
        SessionData sessionData = new SessionData("2026-09-14", "10:00", "11:00", "idempotent", "Work");
        String id = owner.createSession();

        owner.updateSession(id, sessionData).statusCode(200);
        String firsUpdate = owner.getSession(id).extract().body().asString();

        owner.updateSession(id, sessionData).statusCode(200);
        String secondUpdate = owner.getSession(id).extract().body().asString();
        assertEquals(firsUpdate, secondUpdate);
    }

    @Test
    void deleteIsIdempotent() {
        String id = owner.createSession();
        owner.deleteSession(id).statusCode(200);
        owner.getSession(id).statusCode(404);
        owner.deleteSession(id).statusCode(404);
        owner.getSession(id).statusCode(404);
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

    @ParameterizedTest
    @CsvSource({
            "invalidUser, 1234",
            "test1, 2345",
            "'' , ''"
    })
    void negativeLoginTestsFail(String username, String password) {
        Response loginResponse = AuthApi.loginResponse(username, password);
        assertEquals(401, loginResponse.getStatusCode());
        assertEquals("Invalid username or password.", loginResponse.path("error"));

    }

    @Test
    void positiveLoginTestSavesCookie() {
        Response loginResponse = AuthApi.loginResponse("test1", "1234");
        assertEquals(200, loginResponse.getStatusCode());
        assertNotNull(loginResponse.cookie("session"));

        SessionsApi validUser = new SessionsApi(loginResponse.cookie("session"));
        Response me = validUser.whoAmI();
        assertEquals(200, me.getStatusCode());
        assertEquals("test2", me.path("username"));
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
        WeekNotesApi owner = new WeekNotesApi(login("test1", "1234"));

        owner.updateWeekNotes(date, note);
        owner.getWeekNotes(date)
                .statusCode(200)
                .body("text", equalTo(note));
    }

    @Test
    void weekNoteStartMustBeAMonday() {
        String date = "2026-09-16";
        String note = "negative test " + UUID.randomUUID();
        WeekNotesApi owner = new WeekNotesApi(login("test1", "1234"));

        owner.updateWeekNotes(date, note).statusCode(400);
        assertNotEquals(note, owner.getWeekNotes(date).extract().body().path("text"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2026-13-01",
            "2026-01-32",
            "2026-02-30",
            "2026-00-01",
            "2026-01-00"
    })
    void weekStartMustBeAValidDate(String invalidDate) {
        String note = "negative test " + UUID.randomUUID();
        WeekNotesApi owner = new WeekNotesApi(login("test1", "1234"));

        owner.updateWeekNotes(invalidDate, note).statusCode(400);
        assertNotEquals(note, owner.getWeekNotes(invalidDate).extract().body().path("text"));
    }

    @Test
    void everyOwnerCanSeeAndUpdateOnlyTheirWeekNotes() {
        String ownerNote = "owner " + UUID.randomUUID();
        String nonOwnerNote = "non owner " + UUID.randomUUID();
        String date = "2026-10-05";

        WeekNotesApi owner = new WeekNotesApi(login("test1", "1234"));
        WeekNotesApi nonOwner = new WeekNotesApi(login("test2", "1234"));

        owner.updateWeekNotes(date, ownerNote).statusCode(200);
        nonOwner.updateWeekNotes(date, nonOwnerNote).statusCode(200);

        assertEquals(ownerNote, owner.getWeekNotes(date).extract().body().path("text"));
        assertEquals(nonOwnerNote, nonOwner.getWeekNotes(date).extract().body().path("text"));
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

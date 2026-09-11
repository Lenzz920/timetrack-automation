import api.AuthApi;
import api.SessionData;
import api.SessionsApi;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RESTTest {

    SessionsApi owner;
    SessionsApi nonOwner;

    @BeforeEach
    void setUp() {
        owner = new SessionsApi(AuthApi.login("test1", "1234"));
        nonOwner = new SessionsApi(AuthApi.login("test2", "1234"));
    }

//    @Disabled("not migrated to SessionData")
//    @ParameterizedTest(name = "{1} to {2} -> {3}")
//    @CsvSource({
//            "2026-09-09, 09:00, 10:00, 200",
//            "2026-09-09, 10:00, 09:00, 400",
//            "2026-09-09, 10:00, 10:00, 400"
//    })
//    void createSessionHonoursTimeRule(String date, String startTime, String endTime, int expectedStatusCode) {
//        owner.postSession(date, startTime, endTime, "param test", "Study")
//                .statusCode(expectedStatusCode);
//    }

//    @Disabled("is not migrated to SessionData")
//    @Test
//    void getSessionReturnsCreatedFields() {
//        String sessionDate = "2026-09-07";
//        String startTime = "09:00";
//        String endTime = "10:00";
//        String note = "dynamic test";
//        String categoryName = "Study";
//
//        String id = owner.createSession();
//        owner.getSession(id)
//                .statusCode(200)
//                .body("id", equalTo(id))
//                .body("date", equalTo(sessionDate))
//                .body("start", equalTo(startTime))
//                .body("end", equalTo(endTime))
//                .body("note", equalTo(note))
//                .body("categoryName", equalTo(categoryName));
//    }

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

//    @Disabled("not migrated to SessionData")
//    @Test
//    void sessionGetsUpdated() {
//        String sessionDate = "2026-09-07";
//        String startTime = "09:00";
//        String endTime = "10:00";
//        String note = "dynamic test";
//        String categoryName = "Study";
//
//        String updatedSessionDate = "2026-10-12";
//        String updatedStartTime = "12:34";
//        String updatedEndTime = "13:45";
//        String updatedNote = "new note";
//        String updatedCategoryName = "Work";
//
//        String id = owner.createSession(sessionDate, startTime, endTime, note, categoryName);
//        owner.updateSession(id, updatedSessionDate, updatedStartTime, updatedEndTime, updatedNote, updatedCategoryName)
//                .statusCode(200);
//
//        owner.getSession(id)
//                .statusCode(200)
//                .body("id", equalTo(id))
//                .body("date", equalTo(updatedSessionDate))
//                .body("start", equalTo(updatedStartTime))
//                .body("end", equalTo(updatedEndTime))
//                .body("note", equalTo(updatedNote))
//                .body("categoryName", equalTo(updatedCategoryName));
//    }

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

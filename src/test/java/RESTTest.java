import api.AuthApi;
import api.SessionsApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RESTTest {

    SessionsApi owner;
    SessionsApi nonOwner;

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
        owner.postSession(date, startTime, endTime, "param test", "Study")
                .statusCode(expectedStatusCode);
    }

    @Test
    void getSessionReturnsCreatedFields() {
        String sessionDate = "2026-09-07";
        String startTime = "09:00";
        String endTime = "10:00";
        String note = "dynamic test";
        String categoryName = "Study";

        String id = owner.createSession(sessionDate, startTime, endTime, note, categoryName);
        owner.getSession(id)
                .statusCode(200)
                .body("id", equalTo(id))
                .body("date", equalTo(sessionDate))
                .body("start", equalTo(startTime))
                .body("end", equalTo(endTime))
                .body("note", equalTo(note))
                .body("categoryName", equalTo(categoryName));
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
        String sessionDate = "2026-09-07";
        String startTime = "09:00";
        String endTime = "10:00";
        String note = "shouldn't be changed";
        String categoryName = "Study";

        String nonOwnerSessionDate = "2026-10-06";
        String nonOwnerStartTime = "07:30";
        String nonOwnerEndTime = "12:40";
        String nonOwnerNote = "nonOwner";
        String nonOwnerCategoryName = "Work";

        String id = owner.createSession(sessionDate, startTime, endTime, note, categoryName);
        nonOwner.updateSession(id, nonOwnerSessionDate, nonOwnerStartTime, nonOwnerEndTime, nonOwnerNote, nonOwnerCategoryName)
                .statusCode(404);

        owner.getSession(id)
                .statusCode(200)
                .body("id", equalTo(id))
                .body("date", equalTo(sessionDate))
                .body("start", equalTo(startTime))
                .body("end", equalTo(endTime))
                .body("note", equalTo(note))
                .body("categoryName", equalTo(categoryName));
    }

    @Test
    void sessionGetsUpdated() {
        String sessionDate = "2026-09-07";
        String startTime = "09:00";
        String endTime = "10:00";
        String note = "dynamic test";
        String categoryName = "Study";

        String updatedSessionDate = "2026-10-12";
        String updatedStartTime = "12:34";
        String updatedEndTime = "13:45";
        String updatedNote = "new note";
        String updatedCategoryName = "Work";

        String id = owner.createSession(sessionDate, startTime, endTime, note, categoryName);
        owner.updateSession(id, updatedSessionDate, updatedStartTime, updatedEndTime, updatedNote, updatedCategoryName)
                .statusCode(200);

        owner.getSession(id)
                .statusCode(200)
                .body("id", equalTo(id))
                .body("date", equalTo(updatedSessionDate))
                .body("start", equalTo(updatedStartTime))
                .body("end", equalTo(updatedEndTime))
                .body("note", equalTo(updatedNote))
                .body("categoryName", equalTo(updatedCategoryName));
    }

}

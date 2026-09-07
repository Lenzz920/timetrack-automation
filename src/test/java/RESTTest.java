import api.AuthApi;
import api.SessionsApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RESTTest {

    SessionsApi sessions;

    @BeforeEach
    void setUp() {
        String cookie = AuthApi.login("test1", "1234");
        sessions = new SessionsApi(cookie);
    }

    @Test
    void getSessionReturnsCreatedFields() {
        String sessionDate = "2026-09-07";
        String startTime = "09:00";
        String endTime = "10:00";
        String note = "dynamic test";
        String categoryName = "Study";

        String id = sessions.createSession(sessionDate, startTime, endTime, note, categoryName);
        sessions.getSession(id)
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
        String id = sessions.createSession();
        sessions.getSession(id)
        .statusCode(200);
        sessions.deleteSession(id);
        sessions.getSession(id)
                .statusCode(404);
    }

    @Test
    void createdSessionAppearsInGetAll() {
        String id = sessions.createSession();
        List<String> ids = sessions.getAllSessions().extract().path("sessions.id");
        assertTrue(ids.contains(id));
    }

    @Test
    void deletedSessionIsGoneFromGetAll() {
        String id = sessions.createSession();
        sessions.deleteSession(id);
        List<String> ids = sessions.getAllSessions().extract().path("sessions.id");
        assertFalse(ids.contains(id));
    }

}

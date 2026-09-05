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
        String id = sessions.createSession();
        sessions.getSession(id)
                .statusCode(200)
                .body("id", equalTo(id))
                .body("start", equalTo("09:00"));
    }

    @Test
    void getSessionThatWasDeleted() {
        String id = sessions.createSession();
        sessions.getSession(id)
        .statusCode(200)
                .body("id", equalTo(id))
                .body("start", equalTo("09:00"));
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

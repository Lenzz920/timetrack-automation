import api.AuthApi;
import api.SessionsApi;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

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

}

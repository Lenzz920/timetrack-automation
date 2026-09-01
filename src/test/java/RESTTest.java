import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class RESTTest {

    String sessionValue;

    @BeforeEach
    void getCookie() {
        sessionValue = login();
        // clearSessionsFromPreviousRun();
    }

    @Test
    void getAllSessions() {
        given()
                .cookie("session", sessionValue)
                .get("http://localhost:3000/api/data")
                .then()
                .log().all()
                .statusCode(200)
                .body("sessions", notNullValue());
    }

    @Test
    void getSession() {
        String id = createSession();
        getSession(id);
    }

    String login() {
        return given()
                .contentType(ContentType.JSON)
                .body("""
{
"username": "test1",
"password": "1234"
}
""")
                .when()
                .post("http://localhost:3000/api/auth/login")
                .then()
                .statusCode(200)
                .extract().cookie("session");
    }

    void getSession(String id) {
        given()
                .cookie("session", sessionValue)
                .get("http://localhost:3000/api/sessions/" + id)
                .then()
                .log().all();
    }

    String createSession() {
        return given()
                .cookie("session", sessionValue)
                .contentType(ContentType.JSON)
                .body("""
{
"date":"2026-09-01",
"start":"18:00",
"end":"19:00",
"categoryName":"study",
"note":"to be deleted"
}
""")
                .post("http://localhost:3000/api/sessions")
                .then()
                .statusCode(200)
                .extract().path("id");
    }

/*    void clearSessionsFromPreviousRun() {
        List<String> ids = given()
                .cookie("session", sessionValue)
                .get("http://localhost:3000/api/data")
                .then()
                .statusCode(200)
                .extract().path("sessions.id");

        for (String id : ids) {
            given()
                    .cookie("session", sessionValue)
                    .delete("http://localhost:3000/api/sessions/" + id)
                    .then()
                    .statusCode(200);
        }
    }*/

}

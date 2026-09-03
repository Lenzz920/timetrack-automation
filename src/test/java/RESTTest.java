import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RESTTest {

    String sessionValue;

    @BeforeEach
    void getCookie() {
        sessionValue = login("test1", "1234");
        // clearSessionsFromPreviousRun();
    }


    @Test
    void getSessionReturnsCreatedFields() {
        String id = createSession();
        getSession(id)
                .statusCode(200)
                .body("id", equalTo(id))
                .body("start", equalTo("09:00"));
    }

    String login(String username, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body("""
{
"username": "%s",
"password": "%s"
}
""".formatted(username, password))
                .when()
                .post("http://localhost:3000/api/auth/login")
                .then()
                .statusCode(200)
                .extract().cookie("session");
    }

    ValidatableResponse getSession(String id) {
       return given()
                .cookie("session", sessionValue)
                .get("http://localhost:3000/api/sessions/" + id)
                .then();
    }

    String createSession() {
        return given()
                .cookie("session", sessionValue)
                .contentType(ContentType.JSON)
                .body("""
{
"date":"2026-09-01",
"start":"09:00",
"end":"10:00",
"categoryName":"study",
"note":"to be deleted"
}
""")
                .post("http://localhost:3000/api/sessions")
                .then()
                .statusCode(200)
                .extract().path("id");
    }


}

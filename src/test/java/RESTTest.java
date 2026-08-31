import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class RESTTest {


    String sessionValue;

    @BeforeEach
    void getCookie() {
        sessionValue = login();
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

    @Test
    void createSession() {
        given()
                .cookie("session", sessionValue)
                .contentType(ContentType.JSON)
                .body("""
{
"date":"2026-08-31",
"start":"16:00",
"end":"17:00",
"categoryName":"study",
"note":"to be deleted"
}
""")
                .post("http://localhost:3000/api/sessions")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue());
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

}

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class RESTtests {

    @Test
    void createSession() {
        given()
                .contentType(ContentType.JSON)
                .body("""
{
"date":"2026-08-29",
"start":"16:00",
"end":"17:00",
"categoryId":"study",
"note":"to be deleted"
}
""")
                .post("http://localhost:3000/api/sessions")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    void getSession() {
        given()
                .get("http://localhost:3000/api/data")
                .then()
                .log().all()
                .statusCode(200)
                .body("sessions", notNullValue());
    }

}

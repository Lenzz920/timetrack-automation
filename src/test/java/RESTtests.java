import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class RESTtests {

    @Test
    void createASession() {
        given()
                .contentType(ContentType.JSON)
                .body("""
{
    "date": "2026-08-29",
    "start": "14:00",
    "end": "15:00",
    "categoryId": "study",
    "note": "123"
}
                        """)
                .when()
                .post("http://localhost:3000/api/sessions")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

}

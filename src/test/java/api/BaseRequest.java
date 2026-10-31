package api;

import config.Config;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BaseRequest {

    private BaseRequest() {
    }

    public static RequestSpecification request(String cookie) {
        RequestSpecification spec = given().baseUri(Config.BASE_URL);
        if (cookie != null) {
            spec.cookie("session", cookie);
        }
        return spec;
    }

}
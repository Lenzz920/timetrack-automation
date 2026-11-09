package api;

import io.restassured.response.ValidatableResponse;

import static api.BaseRequest.request;

public class CategoriesApi {

    private final String COOKIE;

    public CategoriesApi(String cookie) {
        this.COOKIE = cookie;
    }

    public ValidatableResponse createCategory(String name) {
        return request(COOKIE)
                .body("""
                        {
                        "name": "%s"
                        }
                        """.formatted(name))
                .post("/api/categories")
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse getCategories() {
        return request(COOKIE)
                .get("/api/categories")
                .then()
                .log().ifValidationFails();
    }

    public ValidatableResponse deleteCategory(String name) {
        return request(COOKIE)
                .delete("/api/categories/{id}", name)
                .then()
                .log().ifValidationFails();
    }

}

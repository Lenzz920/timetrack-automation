package api;

import io.restassured.response.ValidatableResponse;

import static api.BaseRequest.request;

public class CategoriesApi {

    private final String COOKIE;

    private static String toJson(String updatedName) {
        return """
                        {
                        "name": "%s"
                        }
                        """.formatted(updatedName);
    }

    public CategoriesApi(String cookie) {
        this.COOKIE = cookie;
    }

    public ValidatableResponse createCategory(String name) {
        return request(COOKIE)
                .body(toJson(name))
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

    public ValidatableResponse updateCategory(String categoryId, String updatedName) {
        return request(COOKIE)
                .body(toJson(updatedName))
                .put("/api/categories/{categoryId}", categoryId)
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

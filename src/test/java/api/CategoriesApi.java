package api;

import io.restassured.response.ValidatableResponse;

import java.util.HashMap;
import java.util.Map;

import static api.BaseRequest.request;

public class CategoriesApi {

    private final String COOKIE;

    private Map<String, String> categoryBody(String name, String color) {
        Map<String, String> categoryRequestBody = new HashMap<>();
        categoryRequestBody.put("name", name);
        if (color != null) {
            categoryRequestBody.put("color", color);
        }
        return categoryRequestBody;
    }

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
        return createCategory(name, null);
    }

    public ValidatableResponse createCategory(String name, String color) {
        return request(COOKIE)
                .body(categoryBody(name, color))
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

    public ValidatableResponse updateCategory(String categoryId, String updateName) {
        return updateCategory(categoryId, updateName, null);
    }

    public ValidatableResponse updateCategory(String categoryId, String updatedName, String color) {
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

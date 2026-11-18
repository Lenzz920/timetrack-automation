import api.CategoriesApi;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static api.AuthApi.login;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static testdata.Accounts.OWNER_PASSWORD;
import static testdata.Accounts.OWNER_USERNAME;

public class CategoriesTest {
    String name;
    String updatedName;
    String secondCategoryName;
    String id;
    String updatedId;
    String secondCategoryId;
    String userIdTest1;
    CategoriesApi owner;

    @BeforeEach
    void setUp() {
        name = "Category" + UUID.randomUUID();
        updatedName = "UpdatedName" + UUID.randomUUID();
        secondCategoryName = "SecondCategory" + UUID.randomUUID();
        id = name.toLowerCase(Locale.ROOT);
        updatedId = updatedName.toLowerCase(Locale.ROOT);
        secondCategoryId = secondCategoryName.toLowerCase(Locale.ROOT);
        userIdTest1 = "umth9ptqactwxdo";
        owner = new CategoriesApi(login(OWNER_USERNAME, OWNER_PASSWORD));
    }

    @Test
    void canCreateAndRetrieveCategory() {
        owner.createCategory(name)
                .statusCode(200);

        assertCategoryIsReturned(name, 0);

        owner.deleteCategory(id)
                .statusCode(200);
    }

    @Test
    void cantDuplicateCategoryWithUppercase() {
        owner.createCategory(name)
                .statusCode(200);

        assertCategoryIsReturned(name, 0);

        owner.createCategory(name.toUpperCase(Locale.ROOT))
                .statusCode(400)
                .body("error", containsString("You already have a category named"));

        assertCategoryIsReturned(name, 0);

        owner.deleteCategory(id)
                .statusCode(200);
    }

    @Test
    void canUpdateCategory() {
        owner.createCategory(name)
                .statusCode(200);

        owner.updateCategory(id, updatedName)
                .statusCode(200);

        assertCategoryIsReturned(updatedName, 0);
        assertCategoryIsNotReturned(name);

        owner.deleteCategory(updatedId)
                .statusCode(200);
    }

    @Test
    void cantUpdateCategoryWithNameThatAlreadyExists() {
        owner.createCategory(name)
                .statusCode(200);

        owner.createCategory(secondCategoryName)
                .statusCode(200);

        owner.updateCategory(id, secondCategoryName)
                .statusCode(400)
                .body("error", containsString("You already have a category named"));

        assertCategoryIsReturned(name, 0);
        assertCategoryIsReturned(secondCategoryName, 0);

        owner.deleteCategory(id)
                .statusCode(200);

        owner.deleteCategory(secondCategoryId)
                .statusCode(200);
    }

    @Test
    void canDeleteCategory() {
        owner.createCategory(name)
                .statusCode(200);

        assertCategoryIsReturned(name, 0);

        owner.deleteCategory(id)
                .statusCode(200);

        assertCategoryIsNotReturned(name);
    }

    @Test
    void deletingCategoryThatDoesntExistDoesntIntroduceChangesToExistingCategories() {
        List<String> currentCategoryList = new ArrayList<>(getCategoryList());

        owner.deleteCategory(name)
                .statusCode(404)
                .body("error", containsString("Category not found."));

        assertEquals(currentCategoryList, getCategoryList());
    }

    @Test
    void getFullListOfCategory() {
        owner.createCategory(name)
                .statusCode(200);

        String category = "find { it.id == '" + id + "' }";

        owner.getCategories()
                .statusCode(200)
                .body(category + ".keySet()",
                        containsInAnyOrder("id", "userId", "name", "color", "sessionCount"))
                .body(category + ".id", isA(String.class))
                .body(category + ".userId", isA(String.class))
                .body(category + ".name", isA(String.class))
                .body(category + ".color", isA(String.class))
                .body(category + ".sessionCount", isA(Integer.class));

        owner.deleteCategory(id)
                .statusCode(200);
    }

    @Test
    void cantGetCategoriesWithoutCookie() {
        CategoriesApi noCookie = new CategoriesApi(null);

        noCookie.getCategories()
                .statusCode(401)
                .body("error", containsString("Not logged in."));
    }

    private void assertCategoryIsReturned(String name, int sessions) {
        String id = name.toLowerCase(Locale.ROOT);
        String category = "findAll { it.id == '" + id + "' }";
        owner.getCategories()
                .statusCode(200)
                .body(category + ".name", contains(name))
                .body(category + ".sessionCount", contains(sessions));
    }

    private void assertCategoryIsNotReturned(String name) {
        String id = name.toLowerCase(Locale.ROOT);
        String category = "findAll { it.id == '" + id + "' }";
        owner.getCategories()
                .statusCode(200)
                .body(category, empty());
    }

    private List<String> getCategoryList() {
        return owner.getCategories()
                .statusCode(200)
                .extract()
                .path("id");
    }

}

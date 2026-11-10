import api.CategoriesApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.UUID;

import static api.AuthApi.login;
import static org.hamcrest.Matchers.*;
import static testdata.Accounts.OWNER_PASSWORD;
import static testdata.Accounts.OWNER_USERNAME;

public class CategoriesTest {
    String name;
    String id;
    CategoriesApi owner;

    @BeforeEach
    void setUp() {
        name = "category" + UUID.randomUUID();
        id = name.toLowerCase(Locale.ROOT);
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

    private void assertCategoryIsReturned(String name, int sessions) {
        String id = name.toLowerCase(Locale.ROOT);
        String category = "findAll { it.id == '" + id + "' }";
        owner.getCategories()
                .statusCode(200)
                .body(category + ".name", contains(name))
                .body(category + ".sessionCount", contains(sessions));
    }

}

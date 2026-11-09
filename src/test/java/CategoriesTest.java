import api.CategoriesApi;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.UUID;

import static api.AuthApi.login;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.find;
import static org.hamcrest.Matchers.*;
import static testdata.Accounts.OWNER_PASSWORD;
import static testdata.Accounts.OWNER_USERNAME;

public class CategoriesTest {

    @Test
    void canCreateAndRetrieveCategory() {
        String name = "category" + UUID.randomUUID();
        String id = name.toLowerCase(Locale.ROOT);

        CategoriesApi owner = new CategoriesApi(login(OWNER_USERNAME, OWNER_PASSWORD));
        owner.createCategory(name)
                .statusCode(200);

        owner.getCategories()
                .statusCode(200)
                .body("find { it.id == '" + id + "' }.name", equalTo(name))
                .body("find { it.id == '" + id + "' }.sessionCount", equalTo(0));

        owner.deleteCategory(id)
                .statusCode(200);
    }

}

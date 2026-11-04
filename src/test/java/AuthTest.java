import api.AuthApi;
import api.SessionsApi;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static testdata.Accounts.OWNER_PASSWORD;
import static testdata.Accounts.OWNER_USERNAME;

public class AuthTest {

    @ParameterizedTest
    @CsvSource({
            "invalidUser, 1234",
            OWNER_USERNAME + ", 2345",
            "'' , ''"
    })
    void negativeLoginTestsFail(String username, String password) {
        Response loginResponse = AuthApi.loginResponse(username, password);
        assertEquals(401, loginResponse.getStatusCode());
        assertEquals("Invalid username or password.", loginResponse.path("error"));

    }

    @Test
    void positiveLoginTestSavesCookie() {
        Response loginResponse = AuthApi.loginResponse(OWNER_USERNAME, OWNER_PASSWORD);
        assertEquals(200, loginResponse.getStatusCode());
        assertNotNull(loginResponse.cookie("session"));

        SessionsApi validUser = new SessionsApi(loginResponse.cookie("session"));
        Response me = validUser.whoAmI();
        assertEquals(200, me.getStatusCode());
        assertEquals(OWNER_USERNAME, me.path("username"));
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "invalid cookie, invalidCookie",
            "no cookie,"
    })
    void invalidOrMissingCookieIsNotAccepted(String caseName, String cookie) {
        SessionsApi notLoggedInUser = new SessionsApi(cookie);
        Response response = notLoggedInUser.whoAmI();
        assertEquals(401, response.getStatusCode());
        assertEquals("Not logged in.", response.body().path("error"));
    }

}

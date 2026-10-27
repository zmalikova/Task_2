
package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class LoginUserTest {

    private static final String BASE_URL =
            "https://qa-stellarburgers.education-services.ru";

    private final String email =
            "student_" + UUID.randomUUID() + "@yandex.ru";
    private final String password = "TestPassword123";
    private final String name = "Test Student";

    @Test
    void loginExistingUserReturnsSuccess() {
        Response registration = createUser();

        registration.then()
                .statusCode(200);

        String accessToken =
                registration.jsonPath().getString("accessToken");

        try {
            loginUser(email, password)
                    .then()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("accessToken", notNullValue());
        } finally {
            deleteUser(accessToken);
        }
    }

    @Test
    void loginWithIncorrectPasswordReturnsError() {
        Response registration = createUser();

        registration.then()
                .statusCode(200);

        String accessToken =
                registration.jsonPath().getString("accessToken");

        try {
            loginUser(email, "WrongPassword123")
                    .then()
                    .statusCode(401)
                    .body("success", equalTo(false));
        } finally {
            deleteUser(accessToken);
        }
    }

    @Step("Создать пользователя")
    private Response createUser() {
        Map<String, String> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);
        user.put("name", name);

        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(user)
                .post("/api/auth/register");
    }

    @Step("Авторизоваться с email {email}")
    private Response loginUser(String email, String password) {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(credentials)
                .post("/api/auth/login");
    }

    @Step("Удалить пользователя")
    private void deleteUser(String accessToken) {
        given()
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .delete("/api/auth/user");
    }
}

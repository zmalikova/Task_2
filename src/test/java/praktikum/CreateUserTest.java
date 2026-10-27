
package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class CreateUserTest {

    private static final String BASE_URL =
            "https://qa-stellarburgers.education-services.ru";

    private String accessToken;

    private final String email =
            "student_" + UUID.randomUUID() + "@yandex.ru";

    private final String password = "TestPassword123";
    private final String name = "Test Student";

    @Test
    void createUniqueUserReturnsSuccess() {
        Response response = createUser();

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", notNullValue());

        accessToken = response.jsonPath().getString("accessToken");
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
                .when()
                .post("/api/auth/register");
    }

    @AfterEach
    @Step("Удалить созданного пользователя")
    void deleteUser() {
        if (accessToken != null) {
            given()
                    .baseUri(BASE_URL)
                    .header("Authorization", accessToken)
                    .when()
                    .delete("/api/auth/user");
        }
    }
}

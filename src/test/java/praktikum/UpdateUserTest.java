
package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class UpdateUserTest {

    private static final String BASE_URL =
            "https://qa-stellarburgers.education-services.ru";

    private final String email =
            "student_" + UUID.randomUUID() + "@yandex.ru";
    private final String password = "TestPassword123";
    private final String name = "Test Student";

    @Test
    void updateEmailWithAuthorizationReturnsSuccess() {
        String token = registerUser();

        try {
            String newEmail = "updated_" + UUID.randomUUID() + "@yandex.ru";

            updateUser(token, "email", newEmail)
                    .then()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("user.email", equalTo(newEmail));
        } finally {
            deleteUser(token);
        }
    }

    @Test
    void updateNameWithAuthorizationReturnsSuccess() {
        String token = registerUser();

        try {
            updateUser(token, "name", "Updated Student")
                    .then()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("user.name", equalTo("Updated Student"));
        } finally {
            deleteUser(token);
        }
    }

    @Test
    void updateEmailWithoutAuthorizationReturnsError() {
        Map<String, String> data = new HashMap<>();
        data.put("email", "updated_" + UUID.randomUUID() + "@yandex.ru");

        given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(data)
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .body("success", equalTo(false));
    }

    @Test
    void updateNameWithoutAuthorizationReturnsError() {
        Map<String, String> data = new HashMap<>();
        data.put("name", "Updated Student");

        given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(data)
                .patch("/api/auth/user")
                .then()
                .statusCode(401)
                .body("success", equalTo(false));
    }

    @Step("Зарегистрировать пользователя")
    private String registerUser() {
        Map<String, String> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);
        user.put("name", name);

        Response response = given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(user)
                .post("/api/auth/register");

        response.then().statusCode(200);

        return response.jsonPath().getString("accessToken");
    }

    @Step("Изменить данные пользователя")
    private Response updateUser(String token, String field, String value) {
        Map<String, String> data = new HashMap<>();
        data.put(field, value);

        return given()
                .baseUri(BASE_URL)
                .header("Authorization", token)
                .contentType("application/json")
                .body(data)
                .patch("/api/auth/user");
    }

    @Step("Удалить пользователя")
    private void deleteUser(String token) {
        given()
                .baseUri(BASE_URL)
                .header("Authorization", token)
                .delete("/api/auth/user");
    }
}

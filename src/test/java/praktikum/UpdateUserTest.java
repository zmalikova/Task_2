
package praktikum;

import io.qameta.allure.Step;
import praktikum.client.UserClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Description;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

class UpdateUserTest {

    private final UserClient userClient = new UserClient();

    private final String email =
            "student_" + UUID.randomUUID() + "@yandex.ru";
    private final String password = "TestPassword123";
    private final String name = "Test Student";

    private String accessToken;

    @BeforeEach
    @Step("Создать пользователя перед тестом")
    void setUp() {
        Response registration =
                userClient.createUser(email, password, name);

        registration.then().statusCode(200);

        accessToken = registration.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Изменение email с авторизацией")
    @Description("Проверка успешного изменения email авторизованным пользователем")
    void updateEmailWithAuthorizationReturnsSuccess() {
        String newEmail = "updated_" + UUID.randomUUID() + "@yandex.ru";

        userClient.updateUser(accessToken, "email", newEmail)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail));
    }

    @Test
    @DisplayName("Изменение имени с авторизацией")
    @Description("Проверка успешного изменения имени авторизованным пользователем")
    void updateNameWithAuthorizationReturnsSuccess() {
        userClient.updateUser(accessToken, "name", "Updated Student")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo("Updated Student"));
    }

    @Test
    @DisplayName("Изменение email без авторизации")
    @Description("Проверка ошибки при попытке изменить email без авторизации")
    void updateEmailWithoutAuthorizationReturnsError() {
        userClient.updateUser(null, "email",
                        "updated_" + UUID.randomUUID() + "@yandex.ru")
                .then()
                .statusCode(401)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Изменение имени без авторизации")
    @Description("Проверка ошибки при попытке изменить имя без авторизации")
    void updateNameWithoutAuthorizationReturnsError() {
        userClient.updateUser(null, "name", "Updated Student")
                .then()
                .statusCode(401)
                .body("success", equalTo(false));
    }

    @AfterEach
    @Step("Удалить созданного пользователя")
    void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}

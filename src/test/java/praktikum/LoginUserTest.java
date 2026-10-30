
package praktikum;

import io.qameta.allure.Step;
import praktikum.client.UserClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class LoginUserTest {

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
    @DisplayName("Авторизация существующего пользователя")
    @Description("Проверка успешной авторизации зарегистрированного пользователя")
    void loginExistingUserReturnsSuccess() {
        userClient.loginUser(email, password)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    @Description("Проверка ошибки авторизации при вводе неверного пароля")
    void loginWithIncorrectPasswordReturnsError() {
        userClient.loginUser(email, "WrongPassword123")
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

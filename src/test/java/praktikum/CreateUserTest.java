
package praktikum;

import io.qameta.allure.Step;
import praktikum.client.UserClient;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class CreateUserTest {

    private final UserClient userClient = new UserClient();

    private final String email =
            "student_" + UUID.randomUUID() + "@yandex.ru";

    private final String password = "TestPassword123";
    private final String name = "Test Student";

    private String accessToken;

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания нового уникального пользователя")
    void createUniqueUserReturnsSuccess() {
        Response response = userClient.createUser(email, password, name);

        accessToken = response.jsonPath().getString("accessToken");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    @Description("Проверка ошибки при повторной регистрации пользователя с теми же данными")
    void createAlreadyRegisteredUserReturnsError() {
        Response firstResponse =
                userClient.createUser(email, password, name);

        accessToken = firstResponse.jsonPath().getString("accessToken");

        firstResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));

        Response secondResponse =
                userClient.createUser(email, password, name);

        secondResponse.then()
                .statusCode(403)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка ошибки при создании пользователя без обязательного поля email")
    void createUserWithoutEmailReturnsError() {
        Response response =
                userClient.createUserWithoutEmail(password, name);

        response.then()
                .statusCode(403)
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

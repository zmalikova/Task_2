
package praktikum;

import io.qameta.allure.Step;
import praktikum.client.UserClient;
import praktikum.client.OrderClient;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

class GetUserOrdersTest {

    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();

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
    @DisplayName("Получение заказов авторизованным пользователем")
    @Description("Проверка получения списка заказов авторизованным пользователем")
    void getOrdersWithAuthorizationReturnsSuccess() {
        orderClient.getUserOrders(accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получение заказов без авторизации")
    @Description("Проверка ошибки при получении заказов без авторизации")
    void getOrdersWithoutAuthorizationReturnsError() {
        orderClient.getUserOrders(null)
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

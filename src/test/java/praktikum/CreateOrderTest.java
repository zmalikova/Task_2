
package praktikum;

import praktikum.client.UserClient;
import praktikum.client.OrderClient;
import praktikum.client.IngredientClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class CreateOrderTest {

    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private final IngredientClient ingredientClient = new IngredientClient();

    private final String email =
            "alien_burger_" + UUID.randomUUID() + "@yandex.ru";
    private final String password = "SpaceBurger2026";
    private final String name = "Captain Cheeseburger";

    private String accessToken;

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Проверка успешного создания заказа авторизованным пользователем")
    void createOrderWithAuthorizationAndIngredientsReturnsSuccess() {
        registerUser();

        List<String> ingredients = getValidIngredients();

        orderClient.createOrder(accessToken, ingredients)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка возможности создать заказ без авторизации")
    void createOrderWithoutAuthorizationReturnsSuccess() {
        List<String> ingredients = getValidIngredients();

        orderClient.createOrder(null, ingredients)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка ошибки при создании заказа без ингредиентов")
    void createOrderWithoutIngredientsReturnsError() {
        orderClient.createOrder(null, List.of())
                .then()
                .statusCode(400)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента")
    @Description("Проверка ошибки при передаче неверного хеша ингредиента")
    void createOrderWithInvalidIngredientHashReturnsError() {
        orderClient.createOrder(null, List.of("invalid_hash"))
                .then()
                .statusCode(400)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов с авторизацией")
    @Description("Проверка ошибки при создании заказа без ингредиентов авторизованным пользователем")
    void createOrderWithAuthorizationWithoutIngredientsReturnsError() {
        registerUser();

        orderClient.createOrder(accessToken, List.of())
                .then()
                .statusCode(400)
                .body("success", equalTo(false));
    }

    @Step("Зарегистрировать пользователя")
    private void registerUser() {
        Response response =
                userClient.createUser(email, password, name);

        response.then().statusCode(200);

        accessToken = response.jsonPath().getString("accessToken");
    }

    @Step("Получить валидные ингредиенты")
    private List<String> getValidIngredients() {
        Response response = ingredientClient.getIngredients();

        response.then().statusCode(200);

        List<String> ingredients =
                response.jsonPath().getList("data._id", String.class);

        return ingredients.subList(0, 2);
    }

    @AfterEach
    @Step("Удалить созданного пользователя")
    void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}

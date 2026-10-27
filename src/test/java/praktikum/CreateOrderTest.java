
package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class CreateOrderTest {

    private static final String BASE_URL =
            "https://qa-stellarburgers.education-services.ru";

    private final String email = "alien_burger_" + UUID.randomUUID() + "@yandex.ru";
    private final String password = "SpaceBurger2026";
    private final String name = "Captain Cheeseburger";

    private final List<String> ingredients = List.of(
            "6acef97852135b001b21a332",
            "6acef97852135b001b21a32f"
    );

    @Test
    void createOrderWithAuthorizationAndIngredientsReturnsSuccess() {
        String token = registerUser();

        try {
            createOrder(token, ingredients)
                    .then()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("order", notNullValue());
        } finally {
            deleteUser(token);
        }
    }

    @Test
    void createOrderWithoutAuthorizationReturnsSuccess() {
        createOrder(null, ingredients)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order", notNullValue());
    }

    @Test
    void createOrderWithoutIngredientsReturnsError() {
        createOrder(null, List.of())
                .then()
                .statusCode(400)
                .body("success", equalTo(false));
    }

    @Test
    void createOrderWithInvalidIngredientHashReturnsError() {
        createOrder(null, List.of("invalid_hash"))
                .then()
                .statusCode(400)
                .body("success", equalTo(false));
    }

    @Test
    void createOrderWithAuthorizationWithoutIngredientsReturnsError() {
        String token = registerUser();

        try {
            createOrder(token, List.of())
                    .then()
                    .statusCode(400)
                    .body("success", equalTo(false));
        } finally {
            deleteUser(token);
        }
    }

    @Step("Создать заказ")
    private Response createOrder(String token, List<String> ingredients) {
        Map<String, Object> order = new HashMap<>();
        order.put("ingredients", ingredients);

        var request = given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(order);

        if (token != null) {
            request.header("Authorization", token);
        }

        return request
                .when()
                .post("/api/orders");
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

    @Step("Удалить пользователя")
    private void deleteUser(String token) {
        given()
                .baseUri(BASE_URL)
                .header("Authorization", token)
                .delete("/api/auth/user");
    }
}

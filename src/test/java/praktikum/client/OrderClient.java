
package praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String BASE_URL =
            "https://qa-stellarburgers.education-services.ru";

    @Step("Создать заказ")
    public Response createOrder(String token, List<String> ingredients) {
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

    @Step("Получить заказы пользователя")
    public Response getUserOrders(String token) {
        var request = given()
                .baseUri(BASE_URL);

        if (token != null) {
            request.header("Authorization", token);
        }

        return request
                .get("/api/orders");
    }
}

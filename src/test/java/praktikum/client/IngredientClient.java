
package praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientClient {

    private static final String BASE_URL =
            "https://qa-stellarburgers.education-services.ru";

    @Step("Получить список ингредиентов")
    public Response getIngredients() {
        return given()
                .baseUri(BASE_URL)
                .get("/api/ingredients");
    }
}

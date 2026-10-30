
package praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final String BASE_URL =
            "https://qa-stellarburgers.education-services.ru";

    @Step("Создать пользователя")
    public Response createUser(String email, String password, String name) {
        Map<String, String> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);
        user.put("name", name);

        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(user)
                .post("/api/auth/register");
    }

    @Step("Создать пользователя без email")
    public Response createUserWithoutEmail(String password, String name) {
        Map<String, String> user = new HashMap<>();
        user.put("password", password);
        user.put("name", name);

        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(user)
                .post("/api/auth/register");
    }

    @Step("Авторизоваться под пользователем")
    public Response loginUser(String email, String password) {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(credentials)
                .post("/api/auth/login");
    }

    @Step("Изменить данные пользователя")
    public Response updateUser(String token, String field, String value) {
        Map<String, String> data = new HashMap<>();
        data.put(field, value);

        var request = given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(data);

        if (token != null) {
            request.header("Authorization", token);
        }

        return request
                .patch("/api/auth/user");
    }

    @Step("Удалить пользователя")
    public Response deleteUser(String token) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", token)
                .delete("/api/auth/user");
    }
}

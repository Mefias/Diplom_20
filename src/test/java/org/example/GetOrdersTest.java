package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;

public class GetOrdersTest extends ApiHandleTest {
    public GetOrdersTest() {
        super("/api/orders");
    }

    @BeforeClass
    public static void beforeClass() {
        user = getRandomUser();
        authorisationResponse = user.register();
        Ingredient.IngredientListRequest request = new Ingredient.IngredientListRequest(Ingredient.getList()[0]._id);
        new ApiHandleTest("/api/orders").getPostResponse(request, authorisationResponse);
    }

    @AfterClass
    public static void afterClass() {
        if (user != null && authorisationResponse != null)
            user.delete(authorisationResponse);
    }

    @Test
    @DisplayName("GET /api/orders Получение заказов c авторизацией")
    public void getOrders() {
        Response response = getGetResponse(authorisationResponse);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 200);
        compareResponseBodyPartToTarget(response, "success", equalTo(true), "true");
        compareResponseBodyPartToTarget(response, "orders", notNullValue(), "not empty");
        compareResponseBodyPartToTarget(response, "total", notNullValue(), "not empty");
        compareResponseBodyPartToTarget(response, "totalToday", notNullValue(), "not empty");
    }

    @Test
    @DisplayName("GET /api/orders Получение заказов без авторизациеи")
    public void getOrdersWithoutCredentials() {
        Response response = getGetResponse();
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 401);
        compareResponseBodyPartToTarget(response, "success", equalTo(false), "false");
        compareResponseMessageToTarget(response, "You should be authorised");
    }
}

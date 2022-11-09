package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.example.Ingredient.IngredientListRequest;

import java.util.List;

import static org.hamcrest.Matchers.*;

public class PostOrdersTest extends ApiHandleTest {
    public static List<Ingredient> buns;
    public static List<Ingredient> mains;
    public static List<Ingredient> sauces;

    public PostOrdersTest() {
        super("/api/orders");
    }

    @BeforeClass
    public static void beforeClass() {
        user = getRandomUser();
        authorisationResponse = user.register();

        buns = Ingredient.getList(Ingredient.IngredientType.bun);
        mains = Ingredient.getList(Ingredient.IngredientType.main);
        sauces = Ingredient.getList(Ingredient.IngredientType.sauce);
    }

    @AfterClass
    public static void afterClass() {
        if (user != null && authorisationResponse != null)
            user.delete(authorisationResponse);
    }

    @Test
    @DisplayName("POST /api/orders Создание заказа c авторизацией и ингридиентами")
    public void createOrderWithCredentialsAndIngredients() {
        IngredientListRequest request = new IngredientListRequest(buns.get(0)._id, mains.get(0)._id, buns.get(0)._id);
        Response response = getPostResponse(request, authorisationResponse);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 200);
        compareResponseBodyPartToTarget(response, "success", equalTo(true), "true");
        compareResponseBodyPartToTarget(response, "name", not(emptyOrNullString()), "not empty");
        compareResponseBodyPartToTarget(response, "order.number", not(emptyOrNullString()), "not empty");
    }

    @Test
    @DisplayName("POST /api/orders Создание заказа c авторизацией без ингридиентов")
    public void createOrderWithCredentialsAndNoIngredients() {
        IngredientListRequest request = new IngredientListRequest();
        Response response = getPostResponse(request, authorisationResponse);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 400);
        compareResponseBodyPartToTarget(response, "success", equalTo(false), "false");
        compareResponseMessageToTarget(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("POST /api/orders Создание заказа c авторизацией с  неверным ингридиентом")
    public void createOrderWithCredentialsAndInvalidIngredient() {
        IngredientListRequest request = new IngredientListRequest(buns.get(0)._id, "nonExistingIngredient", buns.get(0)._id);
        Response response = getPostResponse(request, authorisationResponse);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 500);
    }


    @Test
    @DisplayName("POST /api/orders Создание заказа без авторизации")
    public void createOrderWithoutCredentials() {
        IngredientListRequest request = new IngredientListRequest(buns.get(0)._id, mains.get(0)._id, buns.get(0)._id);
        Response response = getPostResponse(request);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 200);
        compareResponseBodyPartToTarget(response, "success", equalTo(true), "true");
        compareResponseBodyPartToTarget(response, "name", not(emptyOrNullString()), "not empty");
        compareResponseBodyPartToTarget(response, "order.number", not(emptyOrNullString()), "not empty");
    }
}

package org.example;

import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class Ingredient extends ApiHandle {
    public String _id;
    public String name;
    public IngredientType type;
    public enum IngredientType {
        bun,
        main,
        sauce
    }
    public static class IngredientListResponse {
        boolean success;
        Ingredient[] data;
    }
    public static class IngredientListRequest {
        String[] ingredients;
        public IngredientListRequest(String ... args) {
            ingredients = args;
        }
    }
    public static Ingredient[] ingredients;
    public static Ingredient[] getList() {
        Response res = given()
                .header("Content-type", "application/json")
                .get("/api/ingredients");
        return gson.fromJson(res.body().asString(), IngredientListResponse.class).data;
    }
    public static List<Ingredient> getList(IngredientType type) {
        if (ingredients == null)
            ingredients = Ingredient.getList();
        ArrayList<Ingredient> res = new ArrayList<Ingredient>();
        for (Ingredient i: ingredients)
            if (i.type.equals(type))
                res.add(i);

        return res;
    }
}

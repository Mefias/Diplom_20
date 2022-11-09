package org.example;

import com.google.gson.Gson;
import io.restassured.RestAssured;

public class ApiHandle {
    public static final String baseURI = "https://stellarburgers.nomoreparties.site";
    public static final Gson gson = new Gson();
    public ApiHandle() {
        RestAssured.baseURI= baseURI;
    }
}

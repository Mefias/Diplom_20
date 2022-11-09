package org.example;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class User extends ApiHandle {
    public String email;
    public String password;
    public String name;
    public User() {
    }
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    public AuthorisationResponse register() {
        Response res = given()
                .header("Content-type", "application/json")
                .and()
                .body(this)
                .post("/api/auth/register");
        return gson.fromJson(res.body().asString(), AuthorisationResponse.class);
    }
    public AuthorisationResponse login() {
        Response res = given()
                .header("Content-type", "application/json")
                .and()
                .body(this)
                .post("/api/auth/login");
        return gson.fromJson(res.body().asString(), AuthorisationResponse.class);
    }
    public void delete(AuthorisationResponse authorisationResponse) {
        Response res = given()
                .header("Content-type", "application/json")
                .header("Authorization", authorisationResponse.accessToken)
                .delete("/api/auth/user");
        // res.body().toString()
    }
    public User clone() {
        return new User(email, password, name);
    }
}

package org.example;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;

public class ApiHandleTest extends ApiHandle {
    public static final String baseURI = "https://stellarburgers.nomoreparties.site";
    public static final Faker faker = new Faker();

    public final String handle;
    public static User user = null;
    public static AuthorisationResponse authorisationResponse = null;

    public ApiHandleTest(String handle) {
        this.handle = handle;
    }

    @BeforeClass
    public static void setUpBaseUri() {
        RestAssured.baseURI= baseURI;
    }
    public static User getRandomUser() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password();
        String name = faker.name().firstName();

        return new User(email, password, name);
    }

    @Step("Post request with {body}")
    public Response getPostResponse(Object body) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(body)
                .post(handle);
        return response;
    }
    @Step("Post request with credentials and {body}")
    public Response getPostResponse(Object body, AuthorisationResponse authorisationResponse) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", authorisationResponse.accessToken)
                .body(body)
                .post(handle);
        return response;
    }
    @Step("Get request with no body")
    public Response getGetResponse() {
        Response response = given()
                .header("Content-type", "application/json")
                .get(handle);
        return response;
    }
    @Step("Get request with credentials and no body")
    public Response getGetResponse(AuthorisationResponse authorisationResponse) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", authorisationResponse.accessToken)
                .get(handle);
        return response;
    }
    @Step("Patch request with user")
    public Response getPatchResponse(User user) {
        Response response = given()
            .header("Content-type", "application/json")
            .body(user)
            .patch(handle);
        return response;
    }
    @Step("Patch request with credentials and user")
    public Response getPatchResponse(User user, AuthorisationResponse authorisationResponse) {
        Response response = given()
            .header("Content-type", "application/json")
            .header("Authorization", authorisationResponse.accessToken)
            .body(user)
            .patch(handle);
        return response;
    }
    @Step("Compare response message to {message}")
    public void compareResponseMessageToTarget(Response response, String message) {
        response.then().assertThat().body("message", equalTo(message));
    }
    @Step("Compare response code to {code}")
    public void compareResponseCodeToTarget(Response response, int code) {
        response.then().assertThat().statusCode(code);
    }
    @Step("Check response part '{part}' is {matcherDescription}")
    public void compareResponseBodyPartToTarget(Response response, String part, Matcher matcher, String matcherDescription) {
        response.then().assertThat().body(part, matcher);
    }
    @Step("Print response code and body to console")
    public void printResponseToConsole(Response response) {
        System.out.println(response.statusCode() +": "+response.body().asString());
    }
    @Step("Save authorisation response for future requests")
    public void saveAuthorisationResponse(Response response) {
        AuthorisationResponse ar = gson.fromJson(response.body().asString(), AuthorisationResponse.class);
        if (ar.success)
            authorisationResponse = authorisationResponse;
    }

    @Step("Check correct authorisation or login response")
    public void rightAuthorisationOrLoginResponseCheck(User user, Response response) {
        compareResponseCodeToTarget(response, 200);
        compareResponseBodyPartToTarget(response, "success", Matchers.equalTo(true), "true");
        compareResponseBodyPartToTarget(response, "accessToken", startsWith("Bearer "), "has access token");
        compareResponseBodyPartToTarget(response, "refreshToken", not(emptyOrNullString()), "has refresh token");
        compareResponseBodyPartToTarget(response, "user.email", Matchers.equalTo(user.email), "equals to source");
        compareResponseBodyPartToTarget(response, "user.name", Matchers.equalTo(user.name), "equals to source");
    }
}

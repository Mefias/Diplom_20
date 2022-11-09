package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class PostAuthLoginTest extends ApiHandleTest {

    public PostAuthLoginTest() {
        super("/api/auth/login");
    }

    @BeforeClass
    public static void beforeClass() {
        user = getRandomUser();
        authorisationResponse = user.register();
    }

    @AfterClass
    public static void afterClass() {
        if (user != null && authorisationResponse != null)
            user.delete(authorisationResponse);
    }

    @Test
    @DisplayName("POST /api/auth/login Логин под существующим пользователем")
    public void loginWithRightCredentials() {
        Response response = getPostResponse(user);
        printResponseToConsole(response);

        rightAuthorisationOrLoginResponseCheck(user, response);
    }

    @Test
    @DisplayName("POST /api/auth/login Логин с неверным логином")
    public void loginWithNoLogin() {
        User wrongUser = user.clone();
        wrongUser.name = null;
        wrongUser.email = null;
        loginWithWrongCredentials(wrongUser);
    }

    @Test
    @DisplayName("POST /api/auth/login Логин с неверным паролем")
    public void loginWithNoPassword() {
        User wrongUser = user.clone();
        wrongUser.name = null;
        wrongUser.password = null;
        loginWithWrongCredentials(wrongUser);
    }

    @Test
    @DisplayName("POST /api/auth/login Логин с неверным логином и паролем")
    public void loginWithWrongNorLoginNorPassword() {
        User wrongUser = user.clone();
        wrongUser.name = null;
        wrongUser.email = null;
        wrongUser.password = null;
        loginWithWrongCredentials(wrongUser);
    }

    public void loginWithWrongCredentials(User user) {
        Response response = getPostResponse(user);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 401);
        compareResponseBodyPartToTarget(response, "success", equalTo(false), "false");
        compareResponseMessageToTarget(response, "email or password are incorrect");
    }
}

package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.*;


public class PostAuthRegisterTest extends ApiHandleTest {
    User user = null;

    public PostAuthRegisterTest() {
        super("/api/auth/register");
    }

    @After
    public void after() {
        if (user != null && authorisationResponse != null)
            user.delete(authorisationResponse);
    }

    @Test
    @DisplayName("POST /api/auth/register Создать уникального пользователя")
    public void uniqueUserCreation() {
        user = getRandomUser();
        Response response = getPostResponse(user);
        printResponseToConsole(response);
        saveAuthorisationResponse(response);

        rightAuthorisationOrLoginResponseCheck(user, response);
    }

    @Test
    @DisplayName("POST /api/auth/register Создать пользователя, который уже зарегистрирован")
    public void nonUniqueUserCreation() {
        user = getRandomUser();
        Response createResponse = getPostResponse(user);
        saveAuthorisationResponse(createResponse);
        Response response = getPostResponse(user);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 403);
        compareResponseBodyPartToTarget(response, "success", equalTo(false), "false");
        compareResponseMessageToTarget(response, "User already exists");
    }

    @Test
    @DisplayName("POST /api/auth/register Создать пользователя и не заполнить логин")
    public void uniqueUserCreationWithoutRequiredParameterEmail() {
        user = getRandomUser();
        user.email = null;
        uniqueUserCreationWithoutRequiredParameter(user);
    }

    @Test
    @DisplayName("POST /api/auth/register Создать пользователя и не заполнить логин")
    public void uniqueUserCreationWithoutRequiredParameterPassword() {
        user = getRandomUser();
        user.password = null;
        uniqueUserCreationWithoutRequiredParameter(user);
    }

    @Test
    @DisplayName("POST /api/auth/register Создать пользователя и не заполнить логин")
    public void uniqueUserCreationWithoutRequiredParameterName() {
        user = getRandomUser();
        user.name = null;
        uniqueUserCreationWithoutRequiredParameter(user);
    }

    public void uniqueUserCreationWithoutRequiredParameter(User user) {
        Response response = getPostResponse(user);
        saveAuthorisationResponse(response);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 403);
        compareResponseBodyPartToTarget(response, "success", equalTo(false), "false");
        compareResponseMessageToTarget(response, "Email, password and name are required fields");
    }
}

package org.example;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class PatchAuthUserTest extends ApiHandleTest {
    public PatchAuthUserTest() {
        super("/api/auth/user");
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
    @DisplayName("PATCH /api/auth/user Изменение почты c авторизацией")
    public void changeLogin() {
        User changedUser = user.clone();
        changedUser.email = getRandomUser().email;

        Response response = getPatchResponse(changedUser, authorisationResponse);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 200);
        compareResponseBodyPartToTarget(response, "success", equalTo(true), "true");
        compareResponseBodyPartToTarget(response, "user.email", equalTo(changedUser.email), "changed");
    }

    @Test
    @DisplayName("PATCH /api/auth/user Изменение пароля c авторизацией")
    public void changePassword() {
        User changedUser = user.clone();
        changedUser.password = getRandomUser().password;

        Response response = getPatchResponse(changedUser, authorisationResponse);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 200);
        compareResponseBodyPartToTarget(response, "success", equalTo(true), "true");

        AuthorisationResponse changedLoginResponse = changedUser.login();
        Assert.assertEquals(changedLoginResponse.success, true);
    }

    @Test
    @DisplayName("PATCH /api/auth/user Изменение имени c авторизацией")
    public void changeName() {
        User changedUser = user.clone();
        changedUser.name = getRandomUser().name;

        Response response = getPatchResponse(changedUser, authorisationResponse);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 200);
        compareResponseBodyPartToTarget(response, "success", equalTo(true), "true");
        compareResponseBodyPartToTarget(response, "user.name", equalTo(changedUser.name), "changed");
    }

    @Test
    @DisplayName("PATCH /api/auth/user Изменение почты на уже существующую у ранее зарегистрированного пользователя")
    public void changeLoginToExisting() {
        User otherUser = getRandomUser();
        AuthorisationResponse otherAuthorisation = otherUser.register();
        try {
            User changedUser = user.clone();
            changedUser.email = otherUser.email;
            Response response = getPatchResponse(changedUser, authorisationResponse);
            printResponseToConsole(response);

            compareResponseCodeToTarget(response, 403);
            compareResponseBodyPartToTarget(response, "success", equalTo(false), "false");
            compareResponseMessageToTarget(response, "User with such email already exists");
        } finally {
            otherUser.delete(otherAuthorisation);
        }
    }

    @Test
    @DisplayName("PATCH /api/auth/user Изменение почты без авторизации")
    public void changeLoginWithNoCredentialLeadsError() {
        User changedUser = user.clone();
        changedUser.email = getRandomUser().email;
        changeRequestWithWrongCredentialsLeadsError(user, changedUser);
    }

    @Test
    @DisplayName("PATCH /api/auth/user Изменение пароля без авторизации")
    public void changePasswordWithNoCredentialLeadsError() {
        User changedUser = user.clone();
        changedUser.password = getRandomUser().password;
        changeRequestWithWrongCredentialsLeadsError(user, changedUser);
    }

    @Test
    @DisplayName("PATCH /api/auth/user Изменение имени без авторизации")
    public void changeNameWithNoCredentialLeadsError() {
        User changedUser = user.clone();
        changedUser.name = getRandomUser().name;
        changeRequestWithWrongCredentialsLeadsError(user, changedUser);
    }

    public void changeRequestWithWrongCredentialsLeadsError(User originalUser, User changedUser) {
        Response response = getPatchResponse(changedUser);
        printResponseToConsole(response);

        compareResponseCodeToTarget(response, 401);
        compareResponseBodyPartToTarget(response, "success", equalTo(false), "false");
        compareResponseMessageToTarget(response, "You should be authorised");
    }
}

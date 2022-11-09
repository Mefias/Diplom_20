package org.example;

public class AuthorisationResponse {
    public boolean success;
    public User user;
    public String accessToken;
    public String refreshToken;

    public AuthorisationResponse() {
    }
}

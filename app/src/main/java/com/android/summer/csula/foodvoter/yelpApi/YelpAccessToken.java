package com.android.summer.csula.foodvoter.yelpApi;


public class YelpAccessToken {

    private String accessToken;
    private String tokenType;
    private int expiresIn;

    public YelpAccessToken(String accessToken, String tokenType, int expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String toString() {
        return "{access_token: " + accessToken + ", token_type: " + tokenType + ", expires_in: " + expiresIn + "}";
    }
}



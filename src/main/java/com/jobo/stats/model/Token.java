package com.jobo.stats.model;

public class Token {
    public static String getAccessToken;
    private static String accessToken; // Make this static
    private static String refreshToken; // Make this static

    public static String getAccessToken() {
        return accessToken;
    }

    public static void setAccessToken(String token) {
        accessToken = token;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static void setRefreshToken(String token) {
        refreshToken = token;
    }
}


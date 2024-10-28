package com.jobo.stats.client;

import com.jobo.stats.model.Token;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.Base64;

public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    public static void spotifyAuth(String code) throws IOException, ParseException {
        String clientId = "b4cda3d2795543e3977998f5d982bfc4";
        String clientSecret = "01b2415dd16b4b54b85980fb4cc7cc73";
        String redirectUri = "http://localhost:8080/callback";

        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");
        post.setHeader("Authorization", "Basic " + encodedAuth);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        String body = "grant_type=authorization_code"
                + "&code=" + code
                + "&redirect_uri=" + redirectUri;
        post.setEntity(new StringEntity(body));

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            int statusCode = response.getCode();
            String jsonResponse = EntityUtils.toString(response.getEntity());

            logger.debug("Status Code: {}", statusCode);
            logger.debug("Response: {}", jsonResponse);

            if (statusCode == 200) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                Token.setAccessToken(jsonObject.get("access_token").getAsString());
                Token.setRefreshToken(jsonObject.has("refresh_token") ? jsonObject.get("refresh_token").getAsString() : null);

                logger.debug("Access token saved: {}", Token.getAccessToken());
            } else {
                logger.error("Error obtaining token: {}", jsonResponse);
            }
        }
    }

    public static void fetchTopArtists(int limit) {
        String accessToken = Token.getAccessToken();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet getRequest = new HttpGet("https://api.spotify.com/v1/me/top/artists?time_range=long_term&limit=" + limit);
            getRequest.setHeader("Authorization", "Bearer " + accessToken);

            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                int statusCode = response.getCode();
                String jsonResponse = EntityUtils.toString(response.getEntity());

                logger.debug("Status Code: {}", statusCode);
                logger.debug("Response: {}", jsonResponse);

                if (statusCode == 200) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                    JsonArray items = jsonObject.getAsJsonArray("items");

                    int displayedCount = 0;
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject artist = items.get(i).getAsJsonObject();
                        String name = artist.get("name").getAsString();

                        displayedCount++;
                        logger.info("{}: \"{}\"", displayedCount, name);
                    }
                } else {
                    logger.error("Error obteniendo top artistas: {}", jsonResponse);
                }
            }
        } catch (IOException | ParseException e) {
            logger.error("Exception occurred while fetching top artists", e);
        }
    }

    public static void fetchTopSongs(int limit) {
        String accessToken = Token.getAccessToken();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet getRequest = new HttpGet("https://api.spotify.com/v1/me/top/tracks?time_range=long_term&limit=" + limit);
            getRequest.setHeader("Authorization", "Bearer " + accessToken);

            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                int statusCode = response.getCode();
                String jsonResponse = EntityUtils.toString(response.getEntity());

                logger.debug("Status Code: {}", statusCode);
                logger.debug("Response: {}", jsonResponse);

                if (statusCode == 200) {
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                    JsonArray items = jsonObject.getAsJsonArray("items");

                    for (int i = 0; i < items.size(); i++) {
                        JsonObject track = items.get(i).getAsJsonObject();
                        String trackName = track.get("name").getAsString();
                        JsonArray artists = track.getAsJsonArray("artists");
                        String artistName = artists.get(0).getAsJsonObject().get("name").getAsString();
                        logger.info("{}: \"{}\" - {}", (i + 1), trackName, artistName);
                    }
                } else {
                    logger.error("Error obteniendo top canciones: {}", jsonResponse);
                }
            }
        } catch (IOException | ParseException e) {
            logger.error("Exception occurred while fetching top songs", e);
        }
    }
}

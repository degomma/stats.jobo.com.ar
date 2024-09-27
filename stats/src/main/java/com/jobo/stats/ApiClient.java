package com.jobo.stats;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

public class ApiClient {

    public void fetchTopArtists(int limit) {
        String accessToken = StatsApplication.getAccessToken(); // Get the access token

        // crear HTTP client y GET request
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet getRequest = new HttpGet("https://api.spotify.com/v1/me/top/artists?time_range=long_term&limit=" + limit);
            getRequest.setHeader("Authorization", "Bearer " + accessToken);

            //ejecutar request y manejar la respuesta
            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                int statusCode = response.getCode();
                String jsonResponse = EntityUtils.toString(response.getEntity());

                System.out.println("Status Code: " + statusCode);
                System.out.println("Response: " + jsonResponse);

                if (statusCode == 200) {
                    // parsear la respuesta JSON
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                    JsonArray items = jsonObject.getAsJsonArray("items");

                    // printear top artistas
                    int displayedCount = 0;
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject artist = items.get(i).getAsJsonObject();
                        String name = artist.get("name").getAsString();

                        displayedCount++;
                        System.out.println(displayedCount + ") \"" + name + "\"");
                    }
                } else {
                    System.out.println("Error obteniendo top artistas: " + jsonResponse);
                }

            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    // metodo para obtener top canciones
    public void fetchTopSongs(int limit) {
        String accessToken = StatsApplication.getAccessToken(); // Get the access token

        //crear HTTP client y GET request
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet getRequest = new HttpGet("https://api.spotify.com/v1/me/top/tracks?time_range=long_term&limit=" + limit);
            getRequest.setHeader("Authorization", "Bearer " + accessToken);

            //ejecutar request y manejar la respuesta
            try (CloseableHttpResponse response = httpClient.execute(getRequest)) {
                int statusCode = response.getCode();
                String jsonResponse = EntityUtils.toString(response.getEntity());

                System.out.println("Status Code: " + statusCode);
                System.out.println("Response: " + jsonResponse);

                if (statusCode == 200) {
                    // parsear la respuesta JSON
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                    JsonArray items = jsonObject.getAsJsonArray("items");

                    // printear top canciones
                    for (int i = 0; i < items.size(); i++) {
                        JsonObject track = items.get(i).getAsJsonObject();
                        String trackName = track.get("name").getAsString();

                        // obtener nombre del artista
                        JsonArray artists = track.getAsJsonArray("artists");
                        String artistName = artists.get(0).getAsJsonObject().get("name").getAsString();
                        System.out.println((i + 1) + ") \"" + trackName + "\" - " + artistName);
                    }
                } else {
                    System.out.println("Error obteniendo top canciones: " + jsonResponse);
                }
            }
            System.exit(0);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
package com.jobo.stats.service;

import com.jobo.stats.client.ApiClient;
import com.jobo.stats.model.Token;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@Service
public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    private final ApiClient apiClient;

    public ApiService() {
        this.apiClient = new ApiClient();
    }

    public RedirectView getLoginRedirect() {
        logger.info("Redirecting to Spotify login.");
        String clientId = "b4cda3d2795543e3977998f5d982bfc4";
        String redirectUri = "http://localhost:8080/callback";
        String scope = "user-read-private user-read-email user-top-read";
        String authUrl = "https://accounts.spotify.com/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=" + scope;
        return new RedirectView(authUrl);
    }

    public void handleCallback(String code) {
        try {
            logger.debug("Handling callback with code: {}", code);
            ApiClient.spotifyAuth(code);
            ApiClient.fetchTopArtists(10);
            ApiClient.fetchTopSongs(10);

        } catch (IOException | ParseException e) {
            logger.error("Error handling callback", e);
        }
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            logger.error("InterruptedException during sleep", e);
            throw new RuntimeException(e);
        }
        logger.debug("Exiting application after callback processing.");
        System.exit(0);
    }

    public String getTokenInfo() {
        String accessToken = Token.getAccessToken();
        logger.debug("Fetching token info.");
        return (accessToken != null) ? "Token: " + accessToken : "No se guardó el token.";
    }
}

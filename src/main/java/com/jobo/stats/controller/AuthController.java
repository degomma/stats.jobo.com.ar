package com.jobo.stats.controller;

import com.jobo.stats.model.Token;
import com.jobo.stats.service.ApiService;
import com.jobo.stats.client.ApiClient;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@Controller
public class AuthController {
    private final ApiService apiService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/login")
    public RedirectView login() {
        return apiService.getLoginRedirect();
    }

    @GetMapping("/callback")
    public RedirectView callback(@RequestParam("code") String code) throws IOException, ParseException {
        ApiClient.spotifyAuth(code);
        String accessToken = Token.getAccessToken();

        // Redirige al frontend con el accessToken
        String frontendUrl = "http://localhost:3000/home?accessToken=" + accessToken; // Asegúrate de que la ruta sea /home
        return new RedirectView(frontendUrl);
    }



    @GetMapping("/sanityCheck")
    @ResponseBody
    public String sanityCheck() {
        return "El servicio anda 100% barrani.";
    }

    @GetMapping("/getToken")
    @ResponseBody
    public String getToken() {
        return apiService.getTokenInfo();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/topSongs")
    public String getTopSongs(@RequestParam(defaultValue = "10") int limit) {
        try {
            ApiClient.fetchTopSongs(limit);
            return "Top songs fetched successfully!";
        } catch (Exception e) {
            logger.error("Error fetching top songs", e);
            return "Failed to fetch top songs.";
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/topArtists")
    public String getTopArtists(@RequestParam(defaultValue = "10") int limit) {
        try {
            ApiClient.fetchTopArtists(limit);
            return "Top artists fetched successfully!";
        } catch (Exception e) {
            logger.error("Error fetching top artists", e);
            return "Failed to fetch top artists.";
        }
    }
}
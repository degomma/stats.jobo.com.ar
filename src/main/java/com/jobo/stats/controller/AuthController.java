package com.jobo.stats.controller;

import com.jobo.stats.service.ApiService;
import com.jobo.stats.client.ApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public RedirectView callback(@RequestParam("code") String code) {
        apiService.handleCallback(code);
        return new RedirectView("/");
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

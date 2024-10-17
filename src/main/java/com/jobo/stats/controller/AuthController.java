package com.jobo.stats.controller;

import com.jobo.stats.service.ApiService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthController {
    private final ApiService apiService;

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
}

package com.multitenant.template.controller;

import com.example.api.WelcomeApi;
import com.example.model.WelcomeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ApiController implements WelcomeApi {
    @Override
    public ResponseEntity<WelcomeResponse> showWelcomeMessage(String name) {
        WelcomeResponse welcomeResponse = new WelcomeResponse();
        welcomeResponse.message(name);
        return ResponseEntity.ok(welcomeResponse);
    }
}

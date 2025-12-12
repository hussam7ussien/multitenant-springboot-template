package com.multitenant.menu.controller.restaurant;

import com.multitenant.menu.api.AuthApi;
import com.multitenant.menu.model.LoginRequest;
import com.multitenant.menu.model.LoginResponse;
import com.multitenant.menu.controller.AbstractController;
import com.multitenant.menu.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController extends AbstractController implements AuthApi {

    private final JwtService jwtService;

    @Override
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        @SuppressWarnings("unused")
        String password = loginRequest.getPassword();

        // TODO: Validate credentials against database
        // For now, accept any username/password for testing
        // In production, use UserService to validate

        String token = jwtService.generateToken(username);
        
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setType("Bearer");
        
        return ResponseEntity.ok(response);
    }
}


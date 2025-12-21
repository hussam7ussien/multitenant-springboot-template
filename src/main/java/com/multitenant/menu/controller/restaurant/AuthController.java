package com.multitenant.menu.controller.restaurant;

import com.multitenant.menu.api.AuthApi;
import com.multitenant.menu.controller.AbstractController;
import com.multitenant.menu.entity.sql.UserEntity;
import com.multitenant.menu.model.*;
import com.multitenant.menu.services.JwtService;
import com.multitenant.menu.services.OtpService;
import com.multitenant.menu.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AuthController extends AbstractController implements AuthApi {

    private final JwtService jwtService;
    private final OtpService otpService;
    private final UserService userService;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
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

    @Override
    public ResponseEntity<OtpRequestResponse> requestOtp(String xTenantID, OtpRequest otpRequest) {
        try {
            logInfo("OTP request for phone: " + otpRequest.getPhone() + " (tenant: " + xTenantID + ")");
            
            if (otpRequest.getPhone() == null || otpRequest.getPhone().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Generate OTP and return session token
            String sessionToken = otpService.generateOtp(otpRequest.getPhone());
            
            OtpRequestResponse response = new OtpRequestResponse();
            response.setSessionToken(sessionToken);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logError("Error generating OTP", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<OtpVerifyResponse> verifyOtp(String xTenantID, OtpVerifyRequest otpVerifyRequest) {
        try {
            logInfo("OTP verification attempt (tenant: " + xTenantID + ")");
            
            // Validate session token
            if (!jwtService.validateTempSessionToken(otpVerifyRequest.getSessionToken())) {
                log.warn("Invalid or expired session token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Verify OTP
            String phone = otpService.verifyOtp(otpVerifyRequest.getSessionToken(), otpVerifyRequest.getOtp());
            if (phone == null) {
                log.warn("OTP verification failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Find or create user
            UserEntity user = userService.findOrCreateByPhone(phone, xTenantID);
            
            // Generate access and refresh tokens
            String accessToken = jwtService.generateAccessToken(
                    user.getId(),
                    user.getUsername(),
                    user.getPhone(),
                    xTenantID
            );
            String refreshToken = jwtService.generateRefreshToken(
                    user.getId(),
                    user.getUsername(),
                    xTenantID
            );

            // Build response
            OtpVerifyResponse response = new OtpVerifyResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setTokenType("Bearer");
            
            // Map user to UserResponse
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId().intValue());
            userResponse.setUsername(user.getUsername());
            userResponse.setEmail(user.getEmail());
            userResponse.setName(user.getName());
            userResponse.setPhone(user.getPhone());
            userResponse.setVerified(user.getVerified() != null && user.getVerified());
            
            response.setUser(userResponse);
            
            logInfo("OTP verified successfully for user: " + user.getId() + " (tenant: " + xTenantID + ")");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logError("Error verifying OTP", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<RefreshTokenResponse> refreshToken(String xTenantID, RefreshTokenRequest refreshTokenRequest) {
        try {
            logInfo("Token refresh request (tenant: " + xTenantID + ")");
            
            // Validate refresh token
            if (!jwtService.validateRefreshToken(refreshTokenRequest.getRefreshToken())) {
                log.warn("Invalid or expired refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Extract user info from refresh token
            Long userId = jwtService.extractUserId(refreshTokenRequest.getRefreshToken());
            String tokenTenantId = jwtService.extractTenantId(refreshTokenRequest.getRefreshToken());
            
            // Validate tenant matches
            if (!xTenantID.equals(tokenTenantId)) {
                log.warn("Tenant mismatch in refresh token");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Get user from database
            UserEntity user = userService.findByUsername(jwtService.extractUsername(refreshTokenRequest.getRefreshToken()))
                    .orElse(null);
            
            if (user == null) {
                log.warn("User not found for refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Generate new access token
            String accessToken = jwtService.generateAccessToken(
                    user.getId(),
                    user.getUsername(),
                    user.getPhone(),
                    xTenantID
            );

            RefreshTokenResponse response = new RefreshTokenResponse();
            response.setAccessToken(accessToken);
            response.setTokenType("Bearer");
            
            logInfo("Token refreshed successfully for user: " + user.getId() + " (tenant: " + xTenantID + ")");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logError("Error refreshing token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

